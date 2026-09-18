package com.example.surroundaddon.modules;

import com.example.surroundaddon.SurroundAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/**
 * FireworkSurround
 *
 * Idea: an end crystal can only be placed where there is no entity occupying
 * that space (the game checks for entity collision before allowing the
 * placement). Right-clicking a firework rocket always spawns a firework
 * rocket entity at your position for a few ticks before it explodes -
 * this happens whether or not you're elytra-flying. By repeatedly using
 * firework rockets, you keep an entity "in the way" around yourself, which
 * can stop an opponent's crystal from being placed directly next to you.
 *
 * This module does NOT place any blocks and does NOT require flying - it
 * just automates spamming firework rockets from your hotbar on a timer,
 * optionally only while an enemy is nearby (so you're not burning through
 * your whole stack outside of combat).
 *
 * NOTE: InvUtils method names (findInHotbar / swap) match a recent Meteor
 * Client release. If your version's API differs slightly, open the class in
 * your IDE (Ctrl+click) and adjust the calls - the rest of the logic doesn't
 * need to change.
 */
public class SurroundFireworksModule extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Ticks to wait between firework uses.")
        .defaultValue(2)
        .range(0, 20)
        .sliderMin(0)
        .sliderMax(20)
        .build()
    );

    private final Setting<Integer> keepInReserve = sgGeneral.add(new IntSetting.Builder()
        .name("keep-in-reserve")
        .description("Stop auto-using fireworks once your total count drops to this or below (0 = use all).")
        .defaultValue(0)
        .range(0, 32)
        .sliderMin(0)
        .sliderMax(32)
        .build()
    );

    private final Setting<Boolean> onlyWhenEnemyNearby = sgGeneral.add(new BoolSetting.Builder()
        .name("only-when-enemy-nearby")
        .description("Only spam fireworks while an enemy player is within range (saves your stock outside of fights).")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> enemyRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("enemy-range")
        .description("Range to check for a nearby enemy player.")
        .defaultValue(6.0)
        .range(1.0, 20.0)
        .sliderMin(1.0)
        .sliderMax(20.0)
        .visible(onlyWhenEnemyNearby::get)
        .build()
    );

    private final Setting<Boolean> silentSwap = sgGeneral.add(new BoolSetting.Builder()
        .name("silent-swap")
        .description("Swap back to your original hotbar slot right after using the firework.")
        .defaultValue(true)
        .build()
    );

    private int timer;

    public SurroundFireworksModule() {
        super(SurroundAddon.CATEGORY, "firework-surround", "Spams firework rockets around you so a crystal can't be placed in your space.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        if (onlyWhenEnemyNearby.get() && !enemyNearby()) return;

        if (timer > 0) {
            timer--;
            return;
        }

        if (tryUseFirework()) timer = delay.get();
    }

    private boolean enemyNearby() {
        double rangeSq = enemyRange.get() * enemyRange.get();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (mc.player.squaredDistanceTo(player) <= rangeSq) return true;
        }

        return false;
    }

    private boolean tryUseFirework() {
        FindItemResult firework = InvUtils.findInHotbar(Items.FIREWORK_ROCKET);
        if (!firework.found()) return false;
        if (firework.count() <= keepInReserve.get()) return false;

        // InvUtils.swap(slot, silent): when silent=true, Meteor handles switching
        // back to your original hotbar slot on its own after this tick - no
        // extra call needed here.
        InvUtils.swap(firework.slot(), silentSwap.get());
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.swingHand(Hand.MAIN_HAND);

        return true;
    }
}
