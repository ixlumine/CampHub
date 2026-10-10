# CampHub

Aplikasi Android untuk mencari dan membandingkan bootcamp. Tugas ALP Visual Programming, Universitas Ciputra.
Backend ada di repository `CampHubAPI`.

## Cara menjalankan

1. Jalankan backend `CampHubAPI` (lihat README-nya). Tunggu sampai muncul `Started CampHubApiApplicationKt`, lalu beri jeda beberapa detik agar data contoh selesai diisi.
2. Buka project ini di Android Studio, tunggu Gradle sync selesai.
3. Pilih emulator, klik **Run**.

### HP lewat kabel USB

1. Jalankan `adb reverse tcp:8080 tcp:8080`.
2. Tambahkan baris `camphub.baseUrl=http://127.0.0.1:8080/` di file `local.properties` (folder utama project). File ini tidak di-commit.
3. Klik **Sync Project with Gradle Files**, lalu **Run**.

Untuk kembali ke emulator, hapus baris tersebut, lalu Sync.

## Akun contoh

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@example.com | nilai `ADMIN_PASSWORD` di backend (default `admin_dev_password`) |
| PROVIDER | kodenusantara@example.com | password123 |
| PROVIDER | rintis@example.com | password123 |
| USER | rina@example.com | password123 |
| USER | bima@example.com | password123 |
| USER | sekar@example.com | password123 |

## Jika gagal terhubung

- Pastikan backend sudah berjalan.
- Buka `http://10.0.2.2:8080/api/auth/login` di Chrome emulator. Jika muncul teks "Method tidak didukung", backend bisa dijangkau.
- Pastikan `targetSdk = 36` di `app/build.gradle.kts`. Dengan nilai 37, Android 17 memblokir koneksi aplikasi ke backend di laptop.
- Emulator lambat atau macet: buka Device Manager, pilih **Cold Boot Now**.
