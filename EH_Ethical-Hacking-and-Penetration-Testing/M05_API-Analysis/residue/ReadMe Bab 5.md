Isi paketnya:

- **`Bab_05_Ringkasan_Textbook_Fondasi_Keamanan_Aplikasi_Web.md`** — anatomi HTTP request/response, cookie/session, perbedaan server-rendered vs SPA/API-driven (dan implikasinya ke cara kerja pentester), proxy intersep (Burp/ZAP) beserta fitur kuncinya, fingerprinting (Wappalyzer/WhatWeb), content discovery (ffuf/gobuster/feroxbuster + SecLists), enumerasi REST vs GraphQL API, dan sekilas DevSecOps/ZAP baseline scan.
- **`Bab_05_Modul_Praktikum_Web_Recon.md`** — praktikum lengkap terhadap **Juice Shop + WebGoat**: setup proxy dan sertifikat CA, observasi manual traffic, fingerprinting, content discovery, cek kebocoran spesifikasi API.
- **`resources/`**:
    - `docker-compose.yml` — Juice Shop + WebGoat (+ WebWolf sebagai companion).
    - `Vagrantfile` + `provision.sh` — Kali dengan ffuf, gobuster, feroxbuster, whatweb, SecLists, Burp Suite, ZAP.
    - `content_discovery.sh` — automasi ffuf (wordlist umum + pola API).
    - `fingerprint.sh` — automasi WhatWeb + cek header + cek path spesifikasi API yang sering bocor (`api-docs`, `swagger.json`, `graphql`, dll).
    - `template_peta_serangan.md` — template dokumentasi yang akan jadi rujukan Bab 6 & 7.
    - `README.md` — panduan lengkap.
