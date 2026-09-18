package com.example.surroundaddon;

import com.example.surroundaddon.modules.SurroundFireworksModule;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventBus;

public class SurroundAddon extends MeteorAddon {

    // A dedicated category so your module shows up in its own tab in the GUI.
    public static final Category CATEGORY = new Category("Surround Addon");

    @Override
    public void onInitialize() {
        Modules.get().add(new SurroundFireworksModule());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.surroundaddon";
    }
}
