# 🇻🇳 Việt Phục Remix

**Việt Phục Remix** là ứng dụng Android hướng đến việc khám phá, phối trang phục truyền thống Việt Nam và tìm hiểu giá trị văn hóa – di sản của các vùng miền. Dự án kết hợp trải nghiệm thời trang với nội dung văn hóa trong một giao diện hiện đại, giúp người dùng tiếp cận Việt phục theo cách trực quan và gần gũi hơn.

## ✨ Giới thiệu

Việt Phục Remix được xây dựng với định hướng đưa trang phục truyền thống Việt Nam đến gần hơn với người dùng thông qua trải nghiệm số. Ứng dụng tổ chức các nội dung về Việt phục, hỗ trợ tạo và lưu những bộ phối đồ yêu thích, đồng thời cung cấp khu vực khám phá di sản và bản đồ văn hóa Việt Nam.

## 🚀 Chức năng chính

- **Khám phá Việt phục:** duyệt và tìm hiểu các lựa chọn trang phục truyền thống.
- **Phối trang phục:** kết hợp các món đồ để tạo phong cách cá nhân.
- **Lookbook cá nhân:** xem, lưu và quản lý những bộ phối đồ yêu thích.
- **So sánh phong cách:** hỗ trợ xem và đối chiếu các lựa chọn phối đồ.
- **Khám phá văn hóa:** tìm hiểu thông tin di sản và kiến thức văn hóa Việt Nam.
- **Bản đồ di sản Việt Nam:** hiển thị dữ liệu bản đồ và thông tin theo tỉnh/thành.
- **Quét và kiểm tra văn hóa:** có các thành phần xử lý kết quả kiểm tra văn hóa và quét bằng camera.
- **Hỗ trợ AI:** tích hợp nền tảng Firebase AI; dự án có giao diện tạo prompt AI phục vụ trải nghiệm ứng dụng.

> Lưu ý: chức năng thực tế có thể phụ thuộc vào cấu hình dịch vụ, API và mức độ hoàn thiện của từng màn hình trong phiên bản hiện tại.

## 🛠️ Công nghệ sử dụng

| Công nghệ | Vai trò |
|---|---|
| Kotlin | Ngôn ngữ lập trình Android |
| Jetpack Compose | Xây dựng giao diện người dùng |
| Material 3 | Thành phần và phong cách giao diện |
| Android Jetpack / ViewModel | Quản lý trạng thái và vòng đời giao diện |
| Navigation Compose | Điều hướng giữa các màn hình |
| Room | Lưu trữ dữ liệu cục bộ |
| Firebase AI | Tích hợp các khả năng AI |
| Google Services / Firebase App Check | Hỗ trợ cấu hình và bảo vệ dịch vụ Firebase |
| Retrofit, OkHttp, Moshi | Hỗ trợ giao tiếp HTTP và xử lý dữ liệu |
| Kotlin Coroutines | Xử lý tác vụ bất đồng bộ |
| SVG và JSON | Lưu trữ dữ liệu bản đồ, tỉnh/thành và nội dung liên quan |

## 📁 Cấu trúc dự án

```text
Viet-Phuc-Remix/
├── app/
│   └── src/
│       └── main/
│           ├── assets/
│           │   ├── provinces_master.json
│           │   └── vietnam-34-provinces.svg
│           ├── java/com/example/
│           │   ├── data/
│           │   │   ├── local/       # Room và dữ liệu lưu cục bộ
│           │   │   ├── model/       # Các mô hình dữ liệu
│           │   │   └── repository/  # Xử lý và cung cấp dữ liệu
│           │   ├── ui/
│           │   │   ├── components/  # Thành phần giao diện dùng chung
│           │   │   ├── screens/     # Các màn hình ứng dụng
│           │   │   └── theme/       # Màu sắc, kiểu chữ và giao diện
│           │   └── MainActivity.kt
│           └── res/                 # Tài nguyên Android
├── .env.example                    # Mẫu biến môi trường
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## ⚙️ Cài đặt và chạy ứng dụng

### Yêu cầu

- Android Studio có hỗ trợ dự án Android Gradle.
- JDK tương thích với Gradle của dự án.
- Thiết bị Android hoặc trình giả lập chạy **Android 7.0 (API 24) trở lên**.
- Tài khoản và cấu hình Firebase nếu sử dụng các chức năng cần dịch vụ Firebase/AI.

### Các bước thực hiện

1. **Clone repository**

   ```bash
   git clone <URL_REPOSITORY>
   cd Viet-Phuc-Remix
   ```

   Thay `<URL_REPOSITORY>` bằng đường dẫn repository GitHub của bạn.

2. **Mở dự án**

   Mở thư mục dự án bằng Android Studio và chờ Gradle Sync hoàn tất.

5. **Chạy ứng dụng**

   Chọn thiết bị Android hoặc emulator trong Android Studio, sau đó nhấn **Run**.

## 🔐 Bảo mật

- Không đưa API key, mật khẩu, keystore hoặc thông tin xác thực vào repository công khai.
- Chỉ sử dụng `.env.example` để chia sẻ tên biến và giá trị mẫu.
- Hạn chế quyền truy cập API key và cấu hình giới hạn sử dụng phù hợp.
- Kiểm tra cấu hình Firebase và các dịch vụ bên ngoài trước khi phát hành ứng dụng.

## 🧭 Định hướng phát triển

- Hoàn thiện trải nghiệm phối và lưu trang phục.
- Bổ sung nội dung thuyết minh, nguồn tham khảo và thông tin lịch sử cho từng loại Việt phục.
- Tiếp tục hoàn thiện bản đồ di sản và nội dung theo tỉnh/thành.
- Nâng cao trải nghiệm quét, kiểm tra và gợi ý nội dung bằng AI.
- Tối ưu giao diện, hiệu năng và khả năng tương thích trên nhiều thiết bị Android.

## 🎯 Mục tiêu dự án

Việt Phục Remix hướng đến việc kết hợp **công nghệ, thời trang và văn hóa Việt Nam**, tạo ra một không gian số để người dùng khám phá Việt phục, thử nghiệm cách phối đồ và tìm hiểu thêm về di sản dân tộc.

---

<p align="center">
  <strong>Việt Phục Remix — Khám phá bản sắc Việt theo cách mới.</strong>
</p>
