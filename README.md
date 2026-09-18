<div align="center">
<h1>Rencang.Ku</h1>

<img src="https://img.shields.io/badge/Android-Native-green?logo=android&style=for-the-badge">
<img src="https://img.shields.io/badge/Kotlin-1.9+-7F52FF?logo=kotlin&style=for-the-badge">
<img src="https://img.shields.io/badge/Machine_Learning-Hugging_Face-orange?logo=huggingface&style=for-the-badge">
<img src="https://img.shields.io/badge/Database-Supabase-emerald?logo=supabase&style=for-the-badge">
<img src="https://img.shields.io/badge/Maps-Google_Maps_SDK-4285F4?logo=googlemaps&style=for-the-badge">

</div>

<br>

## 📌 Gambaran Umum Aplikasi

**Rencangku** (`com.oyn.rencangku`) adalah aplikasi Android native pintar yang berfungsi sebagai **Asisten Rekomendasi Tempat Kuliner**. Aplikasi ini menggabungkan model *Machine Learning* berbasis preferensi personal dengan pemantauan cuaca secara *real-time* untuk memberikan rekomendasi tempat makan (restoran/kafe) yang paling relevan bagi pengguna.

Sistem ini membantu pengguna menemukan tempat kuliner berdasarkan lokasi terkini, preferensi suhu/cuaca tempat makan (misal: makanan hangat untuk cuaca dingin), melihat rincian informasi restoran, mengunggah ulasan bertaraf foto, serta menavigasi langsung ke lokasi tujuan via Google Maps.

---

## 🛠️ Tools & Teknologi yang Digunakan

Proyek ini dibangun menggunakan kumpulan pustaka (*library*), API, dan platform berikut:

| Kategori | Tool / Library / Platform | Kegunaan |
| :--- | :--- | :--- |
| **Language & Platform** | [Kotlin](https://kotlinlang.org/) | Bahasa pemrograman utama untuk pengembangan Android Native. |
| **UI Architecture** | Android Jetpack (ViewBinding, ViewModel, LiveData) | Memisahkan logika bisnis dari antarmuka UI dan mengelola lifecycle. |
| **Asynchronous** | Kotlin Coroutines & Lifecycle Scope | Menangani tugas asynchronous seperti HTTP Request dan I/O. |
| **Networking** | [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp 3](https://square.github.io/okhttp/) | Client HTTP untuk komunikasi dengan ML API, Supabase API, dan OpenWeather API. |
| **Data Parsing** | Gson Converter | Konversi otomatis dari format JSON ke data class Kotlin. |
| **ML Engine** | Hugging Face Space (Xano API) | Hosting Model Machine Learning rekomendasi (*Hybrid/Personalized Filtering*). |
| **Database & Storage** | [Supabase REST API](https://supabase.com/) & Supabase Storage | Penyimpanan metadata lengkap restoran, data favorit, ulasan, serta foto ulasan (`review-images`). |
| **Weather Integration** | [OpenWeatherMap API](https://openweathermap.org/) | Deteksi kondisi cuaca otomatis berdasarkan lokasi pengguna (Latitude/Longitude). |
| **Maps & Location** | Google Maps SDK & Google Places API | Pemetaan lokasi restoran, *marker* peta, dan sistem navigasi. |
| **Session Persistence** | `SharedPreferences` (`SessionManager`) | Menyimpan status login, token pengguna, dan status onboarding. |

---

## 💡 Tujuan Aplikasi

**Rencangku** (Rekan Canggih Penjelajah Kuliner) diciptakan untuk menyelesaikan permasalahan *choice overload* (kebingungan memilih) di tengah melimpahnya pilihan tempat makan dan kafe. Aplikasi ini bertujuan untuk:
* Membantu pengguna menemukan destinasi kuliner yang paling sesuai dengan preferensi selera pribadi secara otomatis.
* Memberikan rekomendasi yang adaptif dan responsif terhadap faktor situasi nyata di lapangan (seperti kondisi cuaca dan jarak lokasi pengguna).
* Menyediakan media informasi kuliner yang interaktif dan transparan melalui ulasan, foto riil, serta peta navigasi.

---

## 🎯 Fungsi Utama Sistem

1. **Pusat Rekomendasi Adaptif (*Context-Aware System*):** Memproses koordinat geografis pengguna dan kondisi cuaca di lokasi terkini untuk menghasilkan saran tempat makan yang pas (misalnya menyarankan tempat berhidangan hangat saat cuaca dingin).
2. **Katalog Kuliner Interaktif:** Menyediakan informasi detail mengenai restoran, mulai dari daftar menu, harga, jam operasional, nomor telepon, hingga ulasan pelanggan.
3. **Penyimpan Preferensi & Favorit Personal:** Menyimpan daftar tempat kuliner kesukaan pengguna yang tersinkronisasi langsung dengan *database* awan (*cloud*).
4. **Petunjuk Arah & Navigasi:** Menghubungkan langsung informasi tempat makan dengan peta lokasi interaktif untuk memudahkan perjalanan.

---

## ✨ Fitur-Fitur Utama

* **Sistem Rekomendasi Pintar (ML & Weather-Aware)**
  * Menampilkan rekomendasi tempat kuliner yang disesuaikan secara otomatis berdasarkan lokasi pengguna, preferensi pribadi, dan kondisi cuaca *real-time*.
  * Mendukung penanganan pengguna baru (*cold-start*) agar tetap mendapatkan rekomendasi terbaik.

* **Detail Restoran Lengkap & Jam Operasional**
  * Menampilkan gambar *header*, alamat lengkap, nomor telepon, estimasi rentang harga, rating, serta kategori cuaca.
  * Menampilkan jam buka/tutup harian yang diformat secara rapi ke dalam bahasa Indonesia.
  * Fitur tempat makan terkait (*related places*) berdasarkan kategori relevan.

* **Sistem Ulasan & Unggah Foto Komunitas**
  * Pengguna yang telah masuk (*login*) dapat memberikan rating bintang dan ulasan teks.
  * **Direct Photo Upload:** Pengguna dapat mengunggah foto ulasan langsung dari galeri ponsel ke *Supabase Storage Bucket*.
  * Fitur untuk mengedit atau menghapus ulasan yang pernah dikirimkan.

* **Daftar Favorit Personal**
  * Menandai dan menyimpan tempat makan favorit dengan satu sentuhan.
  * Mengelola dan menghapus daftar simpanan favorit secara *real-time*.

* **Integrasi Google Maps & Navigasi Lokasi**
  * Pemetaan *marker* lokasi restoran secara visual pada Google Maps SDK.
  * Mengarahkan dan membuka rute navigasi dari titik lokasi pengguna menuju tempat makan tujuan.

* **Autentikasi & Manajemen Sesi Pengguna**
  * Layar *Onboarding* pengenalan aplikasi saat pertama kali digunakan.
  * Pendaftaran akun (*register*), masuk (*login*), edit profil, dan manajemen sesi lokal menggunakan `SharedPreferences`.

---

## 🌟 Manfaat Aplikasi

* **Bagi Pengguna & Wisatawan:**
  * **Menghemat Waktu & Tenaga:** Tidak perlu lagi bingung atau menghabiskan waktu lama untuk memilih tempat makan.
  * **Sesuai Kondisi Fleksibel:** Mendapatkan saran yang tepat sesuai dengan cuaca di sekitar dan jarak yang tidak terlalu jauh.
  * **Informasi Terpercaya:** Dapat melihat ulasan dan foto asli yang diunggah oleh pengguna lain.

* **Bagi Pelaku Usaha Kuliner (UMKM/Restoran):**
  * Membantu mengenalkan tempat makan lokal kepada pengguna dan wisatawan yang berada di sekitar area tersebut.
  * Meningkatkan visibilitas bisnis kuliner secara digital.

---
