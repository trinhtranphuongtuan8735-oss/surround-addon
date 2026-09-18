# Surround Addon (Meteor Client, MC 1.21.1)

Addon mẫu chứa module **Firework Surround**: tự động bắn (dùng) firework
rocket từ hotbar liên tục theo chu kỳ. Cơ chế: crystal chỉ đặt được vào ô
trống không có entity nào chiếm chỗ - dùng firework rocket sẽ luôn spawn ra
một entity firework tại vị trí bạn trong vài tick trước khi nổ, dù bạn có
đang bay Elytra hay không. Spam liên tục sẽ khiến đối thủ khó/không thể đặt
crystal sát người bạn. Module không đặt block nào và không yêu cầu bay.

## Build (không cần cài gì trên máy — dùng GitHub Actions)

1. Tạo repo GitHub mới, upload toàn bộ nội dung thư mục này lên (repo có sẵn
   file `.github/workflows/build.yml`).
2. Vào tab **Actions** của repo → workflow "Build" sẽ tự chạy sau khi push
   (hoặc bấm "Run workflow" để chạy tay).
3. Đợi build xong (khoảng 1-2 phút) → mở lần chạy đó → mục **Artifacts** ở
   dưới cùng → tải `surround-addon-jar` về, giải nén ra là file `.jar`.
4. Bỏ `.jar` đó vào thư mục `mods/` của Minecraft.

## Build thủ công trên máy (cần cài Gradle)

Vì project này không kèm sẵn Gradle Wrapper, nếu build tay bạn cần cài
Gradle 8.10+ trước (https://gradle.org/install), rồi chạy:

```
gradle build
```

Jar xuất ra tại `build/libs/`. Bỏ vào thư mục `mods/` cùng với Fabric Loader,
Fabric API và Meteor Client.

## Việc bạn cần tự kiểm tra lại trước khi build

Meteor Client cập nhật API khá thường xuyên giữa các bản. Trước khi build,
mở project trong IDE (IntelliJ khuyến nghị) và để Gradle tải dependency, sau đó
kiểm tra 2 chỗ hay đổi chữ ký hàm nhất:

1. **`gradle.properties`** — giá trị `meteor_version`, `yarn_mappings`,
   `fabric_version` cần khớp với bản Meteor Client 1.21.1 mới nhất trên
   `https://maven.meteordev.org`. Vào trang đó xem tag chính xác.
2. **`InvUtils.findInHotbar(...)` / `InvUtils.swap(...)`** trong
   `SurroundFireworksModule.java` — nếu IDE báo lỗi tham số hoặc không tìm
   thấy hàm, mở class `InvUtils` (Ctrl+click) để xem chữ ký thật của bản bạn
   đang dùng và sửa lại lời gọi cho khớp. Phần logic xung quanh (khi nào bắn,
   điều kiện phát hiện địch) không cần đổi.

## Cấu trúc

```
src/main/java/com/example/surroundaddon/
├── SurroundAddon.java              # entrypoint, đăng ký category + module
└── modules/
    └── SurroundFireworksModule.java
```

## Cài đặt trong module (trong game)

- **delay**: số tick chờ giữa các lần bắn firework.
- **keep-in-reserve**: giữ lại tối thiểu bao nhiêu quả, không dùng hết sạch.
- **only-when-enemy-nearby / enemy-range**: chỉ tự bắn khi có người chơi khác
  trong tầm này (mặc định bật, tránh tốn firework khi không giao chiến).
- **silent-swap**: swap slot hotbar ngầm rồi trả lại slot cũ ngay sau khi bắn.

## Lưu ý

- Module chỉ tự động hoá thao tác dùng firework rocket - hành vi hoàn toàn
  giống việc bạn tự tay bấm chuột phải liên tục, chỉ nhanh và đều hơn.
- Kết quả có chặn được crystal hay không phụ thuộc vào cách server đó xử lý
  va chạm entity khi đặt crystal (một số anti-cheat/plugin có thể vẫn cho
  đặt được, hoặc phát hiện hành vi spam item bất thường và cảnh báo/ban tuỳ
  luật server). Nên test trên server cho phép hack trước khi dùng thật.
