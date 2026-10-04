# Bab 5 (Praktikum) — Reconnaissance Aplikasi Web & Proxy Intersep

Saatnya kita pindah fokus dari jaringan/service level (Bab 3-4) ke level aplikasi web. Target kita kali ini dua aplikasi latihan yang sangat populer di komunitas: **OWASP Juice Shop** dan **OWASP WebGoat** — keduanya sengaja dibuat penuh kerentanan modern, dan sudah dipelihara komunitas OWASP secara resmi.

## 5.1 Menjalankan Target

```bash
docker compose up -d
```

Ini akan menjalankan:
- **Juice Shop** di `http://localhost:3000`
- **WebGoat** di `http://localhost:8082/WebGoat` (WebGoat butuh registrasi user baru saat pertama akses — silakan buat akun sendiri)

## 5.2 Konfigurasi Proxy Intersep

Kali sudah dilengkapi Burp Suite Community secara default. Kalau kamu ingin memakai OWASP ZAP sebagai alternatif open-source penuh, sudah kami pasangkan juga lewat provisioning.

Langkah setup Burp Suite:
1. Buka Burp Suite, biarkan project sementara ("Temporary project").
2. Pastikan proxy listener aktif di `127.0.0.1:8080` (default).
3. Di browser (gunakan Firefox bawaan Kali), atur proxy manual ke `127.0.0.1:8080`.
4. Akses `http://burp` di browser saat proxy aktif untuk mengunduh dan install sertifikat CA Burp — ini diperlukan supaya traffic HTTPS juga bisa diintersep tanpa banyak warning sertifikat.

## 5.3 Menjelajahi Juice Shop Sambil Mengamati Traffic

Dengan proxy aktif, mulai jelajahi Juice Shop seperti pengguna biasa: lihat produk, coba fitur pencarian, coba login/register. Sambil melakukan ini, perhatikan tab **Proxy History** di Burp — kamu akan melihat setiap request yang terjadi "di belakang layar", termasuk panggilan-panggilan API yang nggak kelihatan cuma dari tampilan browser.

Catat di `01-observasi-manual.md`:
- Endpoint API apa saja yang kamu temukan (biasanya berpola `/rest/...` atau `/api/...` di Juice Shop)?
- Apakah ada informasi menarik di response header?

## 5.4 Fingerprinting Teknologi

```bash
whatweb http://localhost:3000
whatweb http://localhost:8082/WebGoat
```

Bandingkan juga dengan hasil ekstensi Wappalyzer di browser (install dulu dari toko ekstensi kalau belum ada).

## 5.5 Content Discovery dengan ffuf

```bash
ffuf -u http://localhost:3000/FUZZ -w /usr/share/seclists/Discovery/Web-Content/common.txt -o ~/Lab-Notes/Bab-05-Web-Recon/ffuf-juiceshop.json
```

Coba juga wordlist yang lebih spesifik untuk API:

```bash
ffuf -u http://localhost:3000/api/FUZZ -w /usr/share/seclists/Discovery/Web-Content/api/api-endpoints.txt -o ~/Lab-Notes/Bab-05-Web-Recon/ffuf-juiceshop-api.json
```

Perhatikan status code dan panjang response untuk membedakan hasil yang valid dari noise (kadang aplikasi mengembalikan status 200 untuk semua path yang tidak ditemukan — perhatikan pola ini dan sesuaikan filter ffuf jika perlu, misalnya dengan `-fs <ukuran-response-yang-mau-diabaikan>`).

## 5.6 Mengecek Spesifikasi API yang Mungkin Bocor

Coba akses beberapa path umum tempat spesifikasi API sering "bocor":

```bash
curl -s http://localhost:3000/api-docs | head -50
curl -s http://localhost:3000/swagger.json | head -50
```

## 5.7 Menyusun Peta Permukaan Serangan

Sekarang gabungkan semua temuan dari langkah 5.3 sampai 5.6 ke dalam `template_peta_serangan.md`. Ini akan jadi dokumen acuan utama kamu di Bab 6 dan Bab 7 nanti — usahakan selengkap mungkin.

## Checkpoint Praktikum

- [ ] Juice Shop dan WebGoat berhasil dijalankan dan diakses.
- [ ] Proxy intersep (Burp/ZAP) berhasil dikonfigurasi, termasuk sertifikat CA untuk HTTPS.
- [ ] Minimal 5 endpoint API tercatat dari hasil observasi manual di Proxy History.
- [ ] Fingerprinting teknologi sudah dilakukan (whatweb + Wappalyzer).
- [ ] Content discovery dengan ffuf sudah dijalankan terhadap Juice Shop, dengan hasil tersimpan.
- [ ] Peta permukaan serangan (`template_peta_serangan.md`) sudah terisi lengkap.

## Sumber Daya Pendukung

Semua file teknis — `docker-compose.yml` (Juice Shop + WebGoat), provisioning Vagrant untuk Kali dengan ffuf/gobuster/feroxbuster/whatweb/SecLists/ZAP, dan template peta serangan — ada di paket resource yang menyertai modul ini.

## Tutorial / Step-by-Step-by-st

https://youtu.be/dwXLDKlnMX8?si=_tZ6oA3gMQJ325UJ
