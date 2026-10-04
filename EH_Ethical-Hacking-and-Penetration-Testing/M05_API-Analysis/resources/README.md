# Resource Praktikum — Bab 5: Web Reconnaissance & Proxy Intersep

## Isi Paket

| File | Fungsi |
|---|---|
| `docker-compose.yml` | Menjalankan OWASP Juice Shop + OWASP WebGoat (+ WebWolf) |
| `Vagrantfile` | Provisioning VM Kali attacker |
| `provision.sh` | Instalasi ffuf, gobuster, feroxbuster, whatweb, SecLists, Burp Suite, ZAP, Docker |
| `content_discovery.sh` | Automasi content discovery dengan ffuf (umum + pola API) |
| `fingerprint.sh` | Automasi fingerprinting (WhatWeb, header, cek spesifikasi API bocor) |
| `template_peta_serangan.md` | Template dokumentasi peta permukaan serangan |

## Langkah 1 — Setup Kali Attacker

```bash
vagrant up
vagrant ssh
```

## Langkah 2 — Jalankan Target

```bash
docker compose up -d
```

- Juice Shop: `http://localhost:3000`
- WebGoat: `http://localhost:8082/WebGoat` (jika path ini tidak berfungsi, coba `http://localhost:8082` langsung — tergantung versi image, WebGoat kadang menyajikan halaman awal di root)
- WebWolf (companion WebGoat, dipakai di beberapa modul WebGoat lanjutan): `http://localhost:9090/WebWolf`

## Langkah 3 — Setup Proxy Intersep

1. Buka Burp Suite Community (`burpsuite` dari terminal atau menu aplikasi Kali).
2. Pastikan Proxy listener aktif di `127.0.0.1:8080`.
3. Set proxy manual Firefox ke `127.0.0.1:8080`.
4. Akses `http://burp` sambil proxy aktif untuk install sertifikat CA (penting untuk traffic HTTPS).

Kalau ingin pakai OWASP ZAP sebagai alternatif:
```bash
zaproxy
```
Ikuti quick-start wizard bawaan ZAP untuk konfigurasi serupa.

## Langkah 4 — Jalankan Automasi

```bash
chmod +x content_discovery.sh fingerprint.sh
./fingerprint.sh http://localhost:3000
./content_discovery.sh http://localhost:3000
```

## Langkah 5 — Dokumentasikan

```bash
cp template_peta_serangan.md ~/Lab-Notes/Bab-05-Web-Recon/02-peta-serangan.md
```

Lengkapi berdasarkan gabungan hasil observasi manual (Proxy History) dan hasil automasi.

## Catatan Keamanan Lab

- Semua target berjalan lokal di jaringan lab — jangan expose port 3000/8082/9090 ke internet.
- Matikan container setelah selesai:
  ```bash
  docker compose down
  ```
