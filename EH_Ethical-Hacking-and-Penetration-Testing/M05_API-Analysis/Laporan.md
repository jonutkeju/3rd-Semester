# Peta Permukaan Serangan — Bab 5 (Web Recon & Proxy Intersep)

## 1. Info Target

* **Aplikasi Target 1**: OWASP Juice Shop

  * **URL Dasar**: `http://localhost:3000`

* **Aplikasi Target 2**: OWASP WebGoat

  * **URL Dasar**: `http://localhost:8082/WebGoat`

* **Tanggal Pemetaan**: 2 Oktober 2026

* **Lingkungan Pengujian**: Kali Linux (Docker Environment)

## 2. Fingerprinting Teknologi

| Komponen / Alat | OWASP Juice Shop (`:3000`) | OWASP WebGoat (`:8082`) | 
 | ----- | ----- | ----- | 
| **Platform / Runtime** | Node.js (Express Framework) | Java (Spring Boot Framework) | 
| **Frontend UI** | Angular, Material Design | HTML5, JSP / Thymeleaf, Bootstrap | 
| **Session Tracking** | JSON Web Token (JWT) / `token` | Cookie Servlet `JSESSIONID` | 
| **Header Signifikan** | `Access-Control-Allow-Origin: *`, `X-Content-Type-Options: nosniff` | `X-Frame-Options: DENY`, `Pragma: no-cache` | 

## 3. Daftar Endpoint Ditemukan (Observasi Manual via Proxy History)

| Method | Endpoint | Fungsi (Dugaan) | Butuh Auth? | Catatan Teknis / Respons | 
 | ----- | ----- | ----- | ----- | ----- | 
| `POST` | `/rest/user/login` | Otentikasi pengguna | Tidak | Menerima payload JSON `{email, password}` | 
| `GET` | `/rest/products/search?q=` | Pencarian katalog produk | Tidak | Menerima query parameter, berpotensi input handling issue | 
| `GET` | `/rest/admin/application-configuration` | Pengambilan config aplikasi | Tidak | Membocorkan parameter internal (*Information Disclosure*) | 
| `POST` | `/api/Users/` | Pendaftaran akun baru | Tidak | Menangani registrasi entitas user baru | 
| `GET` | `/api/Challenges/` | Metadata challenge lab | Tidak | Mengembalikan daftar status latihan | 
| `POST` | `/WebGoat/login` | Otentikasi WebGoat | Tidak | Menangani login akun berbasis form session | 
| `GET` | `/WebGoat/start.mvc` | Router utama dashboard | Ya | Mengarahkan ke modul pembelajaran WebGoat | 

## 4. Daftar Endpoint dari Content Discovery (ffuf)

| Path | Status Code | Ukuran Response | Karakteristik / Catatan | 
 | ----- | ----- | ----- | ----- | 
| `/ftp` | `200` / `403` | Bervariasi | Direktori file publik / berpotensi directory listing | 
| `/api-docs` | `200` | Statis | Swagger UI dokumentasi API terekspos | 
| `/assets` | `200` | Statis | Direktori aset statis publik (gambar, css, js) | 
| `/snippets` | `200` | Statis | Direktori penyimpanan kode JavaScript | 

## 5. Spesifikasi API yang Ditemukan

* **Swagger / OpenAPI Bocor?**: Ya, aktif dan dapat diakses pada rute `/api-docs` dan `/swagger.json` pada Juice Shop.

* **GraphQL Endpoint Ditemukan?**: Tidak ditemukan (aplikasi dominan menggunakan RESTful API).

* **Introspection GraphQL Aktif?**: Tidak relevan.

## 6. Ringkasan & Area Prioritas Analisis

Berdasarkan pemetaan permukaan serangan di atas, area yang memiliki prioritas tinggi untuk dianalisis pada modul berikutnya meliputi:

1. **Dokumentasi API Terbuka (`/api-docs`)**: Memberikan visibilitas penuh terhadap semua rute tersembunyi, skema database, dan validasi tipe data backend tanpa otentikasi.

2. **Parameter Input Langsung (`/rest/products/search?q=`)**: Endpoint publik yang memproses input teks langsung dari pengguna dan menjadi kandidat utama evaluasi sanitasi query.

3. **Endpoint Konfigurasi Terbuka (`/rest/admin/application-configuration`)**: Paparan endpoint manajemen tanpa pembatasan hak akses (*Broken Access Control*).