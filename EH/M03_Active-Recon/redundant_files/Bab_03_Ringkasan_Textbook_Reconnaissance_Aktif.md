# Bab 3 — Reconnaissance Aktif: DNS, Port & Service Enumeration

Kalau Bab 2 kemarin ibarat mengintip rumah dari kejauhan, bab ini adalah momen kamu akhirnya jalan mendekat, ketuk pintu, dan mulai bertanya-tanya langsung ke sistem target: "Pintu mana saja yang terbuka di sini? Siapa yang menjaga tiap pintunya? Versi berapa gembok yang dipakai?"

Ini titik krusial, karena mulai bab ini, aktivitasmu **sudah bisa terdeteksi** oleh target. Setiap paket yang kamu kirim berpotensi tercatat di log mereka. Makanya penting banget kamu paham betul apa yang sebenarnya terjadi "di balik layar" saat sebuah tool seperti Nmap kamu jalankan — bukan cuma hafal command-nya doang.

## 3.1 Fondasi yang Sering Dilewatkan: TCP/IP dan Three-Way Handshake

Saya sering ketemu orang yang sudah lancar pakai Nmap tapi giliran ditanya "kenapa SYN scan lebih stealthy dari scan biasa?", jawabannya cuma "soalnya kata tutorial gitu". Nah, di sinilah pentingnya paham fondasi, bukan cuma menghafal command.

Setiap koneksi TCP normal dimulai dengan proses yang disebut **three-way handshake**: klien kirim paket **SYN** (sinyal "saya mau connect"), server balas dengan **SYN-ACK** (sinyal "oke, saya terima, kamu juga terima ya"), lalu klien kirim **ACK** untuk menyelesaikan koneksi. Setelah handshake ini selesai barulah data sungguhan mulai dikirim.

Kenapa ini penting buat pentester? Karena seluruh logika port scanning — termasuk kenapa ada beberapa "jenis" scan yang berbeda — berakar dari sini. Kita bisa memanfaatkan tahapan handshake yang belum selesai untuk mengintip status sebuah port, tanpa perlu benar-benar menyelesaikan koneksi.

## 3.2 Port, Service, dan Banner: Tiga Serangkai yang Wajib Kamu Pahami

**Port** itu sekadar nomor "pintu" — angka 0 sampai 65535, sebagian di antaranya sudah punya konvensi umum (port 80 untuk HTTP, 22 untuk SSH, dan seterusnya), meski konvensi ini bisa saja diubah sesuka admin server.

**Service** adalah program yang sebenarnya "berjaga" di balik port tersebut — misalnya Apache atau Nginx di balik port 80, OpenSSH di balik port 22.

**Banner** adalah informasi identitas yang kadang secara sukarela "diumumkan" oleh service tersebut begitu ada yang connect — semacam service bilang "halo, saya OpenSSH versi 7.2p2 di sini". Banner inilah yang sering jadi harta karun buat pentester: kalau kamu tahu persis versi service-nya, kamu bisa langsung cek apakah versi tersebut punya kerentanan yang sudah didokumentasikan publik.

## 3.3 DNS Enumeration: Lebih dari Sekadar `nslookup`

Kalau di Bab 2 kita cari subdomain lewat sumber pasif (crt.sh, theHarvester), di sini kita mulai "bertanya langsung" ke server DNS target.

**Zone transfer** adalah mekanisme yang harusnya cuma dipakai antar DNS server resmi (primary ke secondary) untuk menyinkronkan seluruh isi zona DNS mereka. Masalahnya, banyak admin yang lupa membatasi siapa saja yang boleh meminta zone transfer ini — kalau salah konfigurasi, siapapun (termasuk kamu) bisa minta `AXFR` dan mendapat **seluruh daftar record DNS** organisasi tersebut sekaligus, lengkap dengan subdomain internal yang harusnya tidak pernah bocor ke publik.

```bash
dig axfr @<dns-server-target> <domain>
```

Kalau zone transfer ditolak (yang mana memang seharusnya begitu di konfigurasi yang benar), kita beralih ke **brute-force subdomain** menggunakan wordlist — mencoba ribuan kemungkinan nama subdomain umum (`mail`, `dev`, `staging`, `vpn`, dst.) dan melihat mana saja yang benar-benar merespons. Tools populer untuk ini: **dnsenum**, **amass**, dan **dnsrecon** — masing-masing punya kelebihan sendiri, tapi prinsip kerjanya sama: brute-force + validasi.

## 3.4 Nmap: Lebih dari Sekadar "Scan Aja"

Nmap itu ibarat pisau lipat Swiss Army buat pentester — satu tool, puluhan mode operasi. Tapi justru karena fleksibel, banyak yang cuma pakai `nmap -A target` tanpa paham apa yang sebenarnya terjadi.

**TCP Connect Scan (`-sT`)** menyelesaikan three-way handshake secara penuh — sederhana, tapi lebih mudah terdeteksi karena koneksi benar-benar "resmi" terjalin dan pasti tercatat di log aplikasi target.

**SYN Scan (`-sS`)**, sering disebut *half-open scan*, mengirim SYN lalu begitu dapat SYN-ACK, langsung kirim RST (reset) alih-alih menyelesaikan handshake dengan ACK. Hasilnya tetap bisa menyimpulkan status port (terbuka jika dapat SYN-ACK), tapi karena koneksi nggak pernah benar-benar "resmi" terbentuk, jejaknya di level aplikasi jadi lebih minim — meski tetap terekam di level jaringan kalau ada firewall/IDS yang jeli.

**Service/Version Detection (`-sV`)** mencoba lebih jauh dari sekadar "port ini terbuka" — Nmap akan mengirim serangkaian probe untuk memancing banner atau respons khas dari service tersebut, lalu mencocokkannya dengan database signature yang dimilikinya.

**OS Fingerprinting (`-O`)** menebak sistem operasi target berdasarkan karakteristik unik implementasi stack TCP/IP-nya — setiap OS punya "gaya" sedikit berbeda dalam merespons paket-paket tertentu (misalnya TTL default, ukuran window TCP), dan Nmap punya database fingerprint dari ribuan kombinasi ini.

**NSE (Nmap Scripting Engine)** adalah fitur yang mengubah Nmap dari sekadar port scanner jadi platform enumerasi (bahkan sedikit eksploitasi ringan) yang jauh lebih powerful, lewat ribuan script komunitas yang bisa kamu panggil dengan `--script`.

## 3.5 Enumerasi Layanan Spesifik

Setelah tahu port mana yang terbuka, saatnya "masuk lebih dalam" ke masing-masing service.

**SMB (Server Message Block)**, biasanya di port 139/445, adalah protokol berbagi file khas Windows (meski Linux juga bisa menjalankannya lewat Samba). Enumerasi SMB bisa mengungkap nama share yang tersedia, user yang terdaftar, bahkan kadang policy password organisasi — semua tanpa autentikasi sama sekali kalau konfigurasinya ceroboh. Tool klasik untuk ini: `enum4linux` dan `smbclient`.

**SMTP (Simple Mail Transfer Protocol)**, port 25, punya command lawas bernama `VRFY` dan `EXPN` yang aslinya dirancang untuk verifikasi alamat email — tapi kalau tidak dinonaktifkan, command ini bisa dipakai untuk mengonfirmasi apakah sebuah username/email valid di server tersebut, berguna banget untuk menyusun daftar target phishing (nanti dibahas lebih lanjut di Bab 8) atau password spraying.

**SNMP (Simple Network Management Protocol)**, biasanya port 161/UDP, dipakai untuk monitoring perangkat jaringan. Masalah klasiknya: banyak perangkat masih memakai **community string** default seperti `public` atau `private` yang berfungsi sebagai "password" sangat lemah — kalau masih default, siapapun bisa membaca (bahkan di beberapa kasus, menulis) konfigurasi perangkat tersebut.

## 3.6 Teknik Stealth dan Kesadaran akan Deteksi

Nmap punya beberapa **timing template** (`-T0` sampai `-T5`) yang mengatur seberapa agresif dan cepat scan dilakukan. `-T0` ("paranoid") mengirim paket sangat lambat untuk menghindari threshold deteksi IDS/IPS berbasis rate-limiting, sementara `-T4`/`-T5` mengutamakan kecepatan dengan risiko lebih mudah terdeteksi.

Ada juga teknik **fragmentasi paket** (`-f`) yang memecah paket menjadi potongan lebih kecil, kadang cukup untuk melewati firewall/IDS lawas yang tidak melakukan reassembly paket sebelum inspeksi.

Penting digarisbawahi: teknik-teknik ini bukan jaminan "tidak terdeteksi sama sekali" — anggap saja sebagai upaya mengurangi *noise*, bukan invisibility cloak. Trade-off-nya jelas: semakin stealthy, semakin lama waktu yang dibutuhkan, dan dalam engagement dengan batas waktu terbatas, kamu perlu menimbang mana yang lebih penting: kecepatan atau kesenyapan.

## 3.7 Jangan Lupakan UDP

Ini kesalahan klasik pemula: scan cuma fokus TCP, lupa sama sekali soal UDP. Padahal banyak service penting justru berjalan di atas UDP — DNS (port 53), SNMP (161), bahkan beberapa layanan VPN.

Masalahnya, scan UDP itu inherently lebih lambat dan kurang reliable dibanding TCP, karena UDP tidak punya mekanisme handshake — Nmap harus menunggu response (atau ketiadaan response, yang juga informasi) untuk menyimpulkan status port, dan beberapa firewall justru mem-block ICMP "port unreachable" yang dibutuhkan Nmap untuk menyimpulkan port tertutup, sehingga hasil scan UDP sering penuh status "open|filtered" yang ambigu.

## 3.8 Automasi: AutoRecon dan Pentingnya Validasi Manual

Kalau kamu berhadapan dengan banyak host sekaligus, menjalankan semua tool di atas satu-satu jelas nggak efisien. **AutoRecon** (open-source) mengotomasi rangkaian enumerasi dasar — scan port, lanjut enumerasi service yang relevan berdasarkan port yang ditemukan — dan menyimpan hasilnya secara terstruktur per host.

Tapi ingat prinsip yang sudah kita bahas di Bab 1: automasi mempercepat kerja, bukan menggantikan pemahaman. Selalu luangkan waktu memvalidasi manual output automasi, terutama untuk temuan yang akan jadi dasar keputusan eksploitasi selanjutnya — automasi bisa salah baca konteks atau melewatkan hal yang di luar skenario yang diprogramkan.

## Rangkuman Bab

- Paham three-way handshake bikin kamu ngerti *kenapa* teknik scanning bekerja, bukan cuma hafal syntax.
- DNS enumeration (zone transfer + brute force) bisa membongkar subdomain yang tidak pernah muncul di riset pasif.
- Nmap punya banyak mode: TCP connect, SYN scan, version detection, OS fingerprinting, NSE — masing-masing punya trade-off berbeda.
- Enumerasi SMB, SMTP, dan SNMP masing-masing punya "celah kebiasaan" khasnya sendiri (share tanpa auth, VRFY/EXPN, community string default).
- UDP sering diabaikan padahal menyimpan banyak service penting.
- Automasi (AutoRecon) mempercepat kerja tapi tetap butuh validasi manual.

## Checkpoint Mandiri

1. Bisakah kamu jelaskan kenapa SYN scan dianggap lebih "senyap" dibanding TCP connect scan, sampai ke level paket yang dikirim?
2. Kalau zone transfer DNS berhasil kamu lakukan terhadap sebuah domain, apa risiko keamanan langsung yang muncul dari situ?
3. Kenapa hasil scan UDP sering menunjukkan status "open|filtered" alih-alih kepastian "open" atau "closed"?

## Tantangan Mastery

Terhadap satu mesin **Metasploitable2** yang sudah kamu deploy, hasilkan peta layanan lengkap — mencakup seluruh port TCP terbuka beserta versi service-nya, hasil enumerasi DNS (termasuk percobaan zone transfer terhadap service DNS yang berjalan di mesin tersebut), dan status community string SNMP — semuanya **hanya lewat command line**, tanpa bantuan tool GUI apapun.

## Sumber Bacaan Lanjutan

- Nmap Reference Guide resmi: https://nmap.org/book/man.html
- RFC 793 — Transmission Control Protocol (untuk pemahaman mendalam soal handshake)
- Dokumentasi resmi AutoRecon: https://github.com/Tib3rius/AutoRecon
- Dokumentasi enum4linux: https://github.com/CiscoCXSecurity/enum4linux
