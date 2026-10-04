# Bab 5 — Fondasi Keamanan Aplikasi Web & Perangkat Pengujian

Mulai bab ini, kita akan menghabiskan cukup banyak waktu — sekitar lima bab ke depan sebenarnya — di dunia aplikasi web. Ada alasan kenapa porsinya sebesar ini: dari semua permukaan serangan yang bakal kamu temui sebagai pentester, aplikasi web hampir selalu jadi yang paling sering muncul, paling cepat berubah, dan paling beragam variasinya. Server bisa di-patch rutin oleh tim infrastruktur, tapi aplikasi web yang dikembangkan sendiri oleh developer internal organisasi? Itu wilayah yang jauh lebih "liar".

Sebelum kita mulai cari-cari kerentanan spesifik (itu baru mulai Bab 6), bab ini fokus ke dua hal dasar yang wajib banget kamu kuasai dulu: memahami cara kerja komunikasi web itu sendiri, dan menguasai tools yang akan jadi "mata dan tangan" kamu sepanjang pengujian aplikasi web.

## 5.1 Anatomi Komunikasi HTTP: Fondasi yang Sering Dianggap Remeh

Saya sering ketemu orang yang bisa pakai Burp Suite dengan lincah tapi giliran ditanya "kenapa method PUT beda dari POST secara semantik?", jawabannya cuma diam. Padahal pemahaman dasar ini yang bikin kamu bisa membaca traffic dengan cerdas, bukan cuma klak-klik tombol di tool.

Setiap komunikasi web pada dasarnya adalah pertukaran **request** dan **response**. Klien (browser, atau tool seperti curl/Burp) mengirim request yang terdiri dari **method** (GET, POST, PUT, DELETE, dan sebagainya — masing-masing punya makna semantik: GET untuk mengambil data, POST untuk mengirim data baru, PUT untuk update, DELETE ya jelas untuk menghapus), **header** (metadata tambahan seperti jenis konten, autentikasi, cookie), dan kadang **body** (data sungguhan yang dikirim, misalnya isi form).

Server merespons dengan **status code** (200 untuk sukses, 404 untuk tidak ditemukan, 500 untuk error server, dan puluhan kode lain yang masing-masing punya arti spesifik), header response, dan body response (biasanya HTML, JSON, atau format lain tergantung jenis aplikasinya).

**Cookie** dan **session** adalah mekanisme yang dipakai untuk "mengingat" siapa kamu di request-request selanjutnya — karena HTTP itu sendiri sebenarnya *stateless* (tidak punya memori antar request). Server biasanya memberi kamu sebuah session ID lewat cookie setelah login berhasil, dan browser otomatis mengirim ulang cookie itu di setiap request berikutnya supaya server tahu "oh, ini orang yang sama yang tadi sudah login".

## 5.2 Server-Rendered vs SPA/API-Driven: Kenapa Ini Mengubah Cara Kamu Bekerja

Aplikasi web zaman sekarang nggak semuanya dibangun dengan cara yang sama, dan ini punya implikasi langsung ke cara kamu melakukan pengujian.

Model **server-rendered** klasik (pikirkan aplikasi PHP lawas) menghasilkan HTML lengkap di server, lalu mengirimkannya utuh ke browser. Setiap kali kamu klik link atau submit form, seluruh halaman biasanya di-reload dari server.

Model **SPA (Single Page Application)** yang lebih modern (dibangun dengan React, Vue, Angular, dan semacamnya) bekerja beda — browser cuma mengunduh "kerangka" aplikasi sekali, lalu selanjutnya berkomunikasi dengan server lewat panggilan API (biasanya format JSON) di belakang layar, tanpa reload halaman penuh.

Kenapa ini penting buat kamu? Karena permukaan serangan pada aplikasi SPA/API-driven jauh lebih "tersembunyi" dari pandangan sekilas — kalau kamu cuma browsing manual seperti pengguna biasa, kamu akan melewatkan banyak endpoint API yang sebenarnya jadi jantung logika aplikasi tersebut. Di sinilah proxy intersep jadi krusial — dia menangkap semua komunikasi "di belakang layar" itu yang nggak kelihatan kalau cuma lihat tampilan browser.

## 5.3 Proxy Intersep: Mata Kamu untuk Melihat "Di Balik Layar"

**Burp Suite** (versi Community gratis) dan **OWASP ZAP** (sepenuhnya open-source) adalah dua tool proxy intersep paling populer di dunia pentest web. Cara kerjanya sederhana secara konsep: kamu setting browser untuk mengirim semua traffic-nya lewat proxy tool ini dulu, sebelum diteruskan ke server tujuan. Dengan begitu, kamu bisa melihat, menahan, bahkan memodifikasi setiap request sebelum benar-benar terkirim.

Beberapa fitur kunci yang wajib kamu kuasai:

**Proxy History** mencatat semua traffic yang lewat — ini "buku catatan" utama kamu untuk memahami bagaimana aplikasi berkomunikasi dengan server-nya.

**Repeater** memungkinkan kamu mengambil satu request tertentu, memodifikasinya, lalu mengirim ulang berkali-kali untuk melihat bagaimana server merespons variasi input yang berbeda-beda — ini akan jadi tool andalanmu mulai Bab 6 saat mulai mencari kerentanan spesifik.

**Intruder** (di Burp) mengotomasi pengiriman banyak variasi payload sekaligus ke satu titik tertentu dalam request — berguna untuk fuzzing parameter atau brute force sederhana.

## 5.4 Fingerprinting: Kenali Teknologi di Baliknya Sebelum Menyerang

Sebelum menyerang, ada baiknya kamu tahu dulu "senjata" apa yang dipakai musuh. **Wappalyzer** (ekstensi browser) otomatis mendeteksi teknologi yang dipakai sebuah website — mulai dari framework frontend, CMS, analytics tool, sampai kadang versi spesifiknya — hanya dengan menganalisis pola di HTML, header, dan script yang di-load.

**WhatWeb** melakukan hal serupa tapi dari command line, cocok untuk automasi atau saat kamu bekerja tanpa GUI. Kombinasikan dengan analisis header HTTP manual (`curl -I`) untuk konfirmasi tambahan — kadang header `Server` atau `X-Powered-By` secara terang-terangan mengumumkan teknologi backend-nya (meski admin yang lebih hati-hati biasanya menonaktifkan header semacam ini).

## 5.5 Content Discovery: Mencari yang "Tidak Terlihat"

Website yang kamu lihat dengan mata biasa cuma menampilkan link-link yang memang sengaja ditampilkan developer. Tapi seringkali ada halaman, direktori, atau file yang tidak di-link dari manapun, tapi tetap bisa diakses kalau kamu tahu persis URL-nya — entah itu halaman admin lama yang lupa dihapus, file backup, atau endpoint API yang belum didokumentasikan publik.

**Content discovery** adalah teknik untuk menemukan hal-hal tersembunyi ini lewat brute-force sistematis. Tools seperti **gobuster**, **ffuf**, dan **feroxbuster** bekerja dengan cara yang mirip: mencoba ribuan (atau jutaan) kemungkinan nama path dari sebuah wordlist, lalu melihat mana yang memberi response selain 404.

Soal wordlist, kamu nggak perlu bikin sendiri dari nol. **SecLists** adalah koleksi wordlist yang dikurasi komunitas keamanan selama bertahun-tahun — mulai dari daftar nama direktori umum, subdomain, sampai payload untuk berbagai jenis kerentanan. Ini akan jadi salah satu resource paling sering kamu pakai sepanjang buku ini.

## 5.6 Enumerasi API: REST dan GraphQL

Ingat pembahasan soal SPA tadi? Nah, di baliknya biasanya ada API — dan memahami cara enumerasi API adalah skill wajib zaman sekarang.

**REST API** biasanya terstruktur berdasarkan resource (`/api/users`, `/api/users/123`, `/api/orders`) dengan method HTTP yang berbeda menunjukkan aksi berbeda pada resource yang sama. Kalau kamu beruntung, kadang ada spesifikasi **OpenAPI/Swagger** yang "bocor" ke publik (biasanya di path seperti `/api-docs` atau `/swagger.json`) yang secara harfiah memberimu peta lengkap seluruh endpoint yang tersedia — lengkap dengan parameter yang diharapkan.

**GraphQL** bekerja beda — biasanya cuma ada satu endpoint (`/graphql`), tapi klien mengirim query terstruktur yang menentukan data apa saja yang diminta. Banyak implementasi GraphQL yang lupa menonaktifkan fitur **introspection** di lingkungan produksi — fitur ini pada dasarnya membiarkan siapa saja bertanya ke API "hei, skema data apa saja yang kamu punya?" dan mendapat jawaban lengkap. Tools seperti Postman atau Insomnia bisa membantu eksplorasi manual kedua jenis API ini dengan lebih nyaman dibanding curl polos.

## 5.7 Automasi Passive Scanning: Sekilas soal DevSecOps

Sebagai wawasan tambahan, banyak organisasi modern sudah mengintegrasikan scanning keamanan langsung ke pipeline CI/CD mereka — konsep yang sering disebut **DevSecOps**. **ZAP baseline scan**, misalnya, bisa dijalankan otomatis setiap kali ada perubahan kode, melakukan passive scan cepat (tanpa benar-benar menyerang, cuma mengamati traffic normal aplikasi) untuk menangkap masalah keamanan dasar sebelum kode tersebut sampai ke produksi.

Kamu nggak perlu jadi ahli DevSecOps dari bab ini, tapi penting untuk sadar bahwa dunia security testing bergerak ke arah "shift-left" — semakin dini masalah ditemukan dalam siklus development, semakin murah dan mudah diperbaiki dibanding ditemukan setelah aplikasi sudah live di produksi.

## Rangkuman Bab

- Paham method HTTP, header, cookie/session itu fondasi yang bikin kamu bisa membaca traffic dengan cerdas, bukan cuma klak-klik tool.
- Aplikasi SPA/API-driven punya permukaan serangan yang lebih "tersembunyi" — proxy intersep jadi krusial untuk melihatnya.
- Burp Suite/ZAP dengan fitur Proxy History, Repeater, dan Intruder akan jadi tool andalanmu sepanjang buku ini.
- Fingerprinting (Wappalyzer, WhatWeb) membantu kamu kenali teknologi sebelum menyerang lebih jauh.
- Content discovery (gobuster/ffuf/feroxbuster + SecLists) menemukan yang tidak terlihat dari browsing biasa.
- REST dan GraphQL API punya pola enumerasi berbeda — kenali keduanya.

## Checkpoint Mandiri

1. Bisakah kamu menjelaskan kenapa HTTP disebut *stateless*, dan bagaimana cookie/session mengatasi keterbatasan ini?
2. Kenapa aplikasi SPA butuh pendekatan pengujian yang berbeda dibanding aplikasi server-rendered klasik?
3. Apa risiko keamanan dari fitur introspection GraphQL yang masih aktif di lingkungan produksi?

## Tantangan Mastery

Lakukan pemetaan permukaan serangan **selengkap mungkin** terhadap target lab (lihat modul praktikum) — mencakup semua endpoint, parameter, dan teknologi yang terdeteksi — **tanpa melihat kode sumber aplikasinya terlebih dahulu**. Gunakan kombinasi proxy intersep (untuk menangkap traffic natural saat kamu browsing), fingerprinting, dan content discovery. Simpan hasilnya sebagai baseline yang akan kamu pakai terus di Bab 6 dan 7.

## Sumber Bacaan Lanjutan

- PortSwigger Web Security Academy (gratis, sangat direkomendasikan): https://portswigger.net/web-security
- OWASP Web Security Testing Guide, bagian "Information Gathering": https://owasp.org/www-project-web-security-testing-guide/
- Dokumentasi resmi ffuf: https://github.com/ffuf/ffuf
- SecLists repository: https://github.com/danielmiessler/SecLists
