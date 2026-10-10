# CampHub

Aplikasi Android direktori bootcamp teknologi.

- **Teknologi:** Jetpack Compose, Material 3, MVVM, Retrofit
- **Versi Android:** minimal Android 9 (API 28)
- **Backend:** repository [CampHubAPI](https://github.com/ixlumine/CampHubAPI)

## Struktur

```
app/src/main/java/com/camphub/app/
├── data/             Data layer
│   ├── container/    Pembuatan Retrofit dan data login (token, user, role)
│   ├── interceptor/  Menambahkan header Authorization: Bearer <token>
│   ├── dto/          Data Transfer Object untuk request dan response API
│   ├── service/      Daftar endpoint API (interface Retrofit)
│   └── repository/   Mengambil data dari API dan mengubahnya menjadi model
└── ui/               UI layer
    ├── model/        Model data untuk ditampilkan
    ├── state/        UI state (Loading, Success, Error)
    ├── route/        Route dan NavHost
    ├── theme/        Warna dan tipografi Material 3
    ├── view/         Layar dan komponen Composable
    └── viewmodel/    Menyimpan dan menyediakan state untuk UI
```

## Fitur

- **Beranda**: peringkat bootcamp.
  - Hanya bootcamp dengan minimal 3 ulasan.
  - Urutan: rata-rata rating tertinggi (dibulatkan 1 desimal), lalu jumlah ulasan terbanyak.
- **Katalog**: daftar bootcamp, diurutkan berdasarkan nama (A–Z).
  - Detail bootcamp: profil, program, ringkasan ulasan, dan daftar ulasan.
  - Detail program: harga, durasi, silabus, dan keterangan pendaftaran dibuka atau ditutup.
- **Forum**: pertanyaan dan komentar.

Tombol tambah, ubah, dan hapus tampil sesuai role dan pemilik data.

## Prasyarat

- Android Studio.
- Backend [CampHubAPI](https://github.com/ixlumine/CampHubAPI) sudah berjalan (lihat [README](https://github.com/ixlumine/CampHubAPI/blob/main/README.md)).
- Emulator, atau HP Android yang terhubung lewat USB dengan USB debugging aktif.

## Konfigurasi

Alamat backend diatur di `local.properties` (folder utama project). File ini dibuat Android Studio dan tidak di-commit.

| Menjalankan di | Isi `camphub.baseUrl` | Keterangan |
|---|---|---|
| Emulator | Tidak perlu diisi (default `http://10.0.2.2:8080/`) | `10.0.2.2` adalah alamat laptop dari dalam emulator |
| HP via USB | `http://127.0.0.1:8080/` | `adb reverse` meneruskan port 8080 di HP ke laptop |

Nilai harus diakhiri `/`.

Contoh `local.properties` untuk HP via USB (baris `sdk.dir` dibiarkan apa adanya):
```
sdk.dir=...
camphub.baseUrl=http://127.0.0.1:8080/
```

Setelah menambah atau menghapus baris `camphub.baseUrl`, klik **Sync Project with Gradle Files**.

## Menjalankan

**Emulator**
1. Buka project di Android Studio, tunggu Gradle sync selesai.
2. Pilih emulator, klik **Run**.

**HP via USB**
1. Jalankan `adb reverse tcp:8080 tcp:8080`.
2. Pastikan `local.properties` sudah berisi `camphub.baseUrl` (lihat Konfigurasi).
3. Pilih HP, klik **Run**.

## Akun contoh

Dibuat oleh seed data backend saat database masih kosong. Hanya untuk development.

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@example.com | nilai `ADMIN_PASSWORD` di backend (default `admin_dev_password`) |
| PROVIDER | kodenusantara@example.com,<br>rintis@example.com | password123 |
| USER | rina@example.com,<br>bima@example.com,<br>sekar@example.com | password123 |

## Jika gagal terhubung

- Pastikan log backend sudah menampilkan `Started CampHubApiApplicationKt`.
- Buka `http://10.0.2.2:8080/api/auth/login` di browser pada emulator. Jika muncul teks "Method tidak didukung", backend bisa dijangkau.
- `targetSdk` harus 36. Dengan nilai 37, Android 17 memblokir koneksi aplikasi ke backend di laptop.
- Emulator lambat atau macet: Device Manager → **Cold Boot Now**.
