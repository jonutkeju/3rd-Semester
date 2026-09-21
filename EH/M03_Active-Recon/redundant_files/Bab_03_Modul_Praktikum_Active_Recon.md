# Bab 3 (Praktikum) — Active Recon & Enumeration

Sekarang kita mulai "menyentuh" target sungguhan — bukan lagi cuma bertanya ke sumber pihak ketiga seperti Bab 2 kemarin. Target praktikum kali ini adalah **Metasploitable2**, mesin Linux yang sengaja dibuat penuh kerentanan oleh Rapid7 (perusahaan di balik Metasploit) khusus untuk keperluan latihan seperti ini. Mesin ini sudah jadi semacam "standar industri" buat siapa saja yang belajar pentest — hampir semua orang di komunitas ini pernah mampir ke Metasploitable2 di titik tertentu perjalanan belajarnya.

## 3.1 Menyiapkan Target: Metasploitable2
Metasploitable2 didistribusikan resmi sebagai image virtual disk (`.vmdk`), bukan container resmi dari Rapid7. Kita menyediakan dua opsi setup:
### Opsi A — VirtualBox (Direkomendasikan, Paling Otentik)
1. **Unduh Image Resmi:**  
   Unduh file `Metasploitable2.zip` dari halaman resmi SourceForge Rapid7 (link tersedia di `README.md`).
2. **Ekstrak File:**  
   Ekstrak arsip zip tersebut, pastikan kamu menemukan file `Metasploitable.vmdk` (ukuran ~1,9 GB).  
   > **Catatan Storage:** Sangat disarankan menaruh file ini di partisi disk yang memiliki ruang longgar (misal Drive `D:` atau `E:`), hindari Drive `C:` jika sisa penyimpanan terbatas.
3. **Import ke VirtualBox (Pengguna Windows via GUI):**
   * Buka VirtualBox, klik tombol **New** (Baru).
   * Isi konfigurasi dasar:
     * **Name:** `Metasploitable2`
     * **Folder:** Arahkan ke drive yang lega (misal `E:\VMs`).
     * **Type:** `Linux`
     * **Version:** `Ubuntu (32-bit)` *(Penting: Metasploitable2 menggunakan kernel 32-bit i686; memilih 64-bit dapat memicu Kernel Panic).*
   * **Hardware:** Alokasikan Memory (RAM) `1024 MB` (1 GB) dan `1 CPU`.
   * **Hard Disk:** Pilih **"Use an Existing Virtual Hard Disk File"**, klik ikon folder -> **Add** -> pilih file `Metasploitable.vmdk` -> klik **Choose** -> **Finish**.
4. **Konfigurasi Jaringan (Network) & Storage:**
   * Klik VM `Metasploitable2` -> **Settings** -> menu **Network** -> **Adapter 1**:
     * Ubah *Attached to* menjadi **Host-Only Adapter** (pilih nama adapter yang sama persis dengan yang terpasang di VM Kali Linux kalian, misal `VirtualBox Host-Only Ethernet Adapter`).
   * Masuk ke menu **Storage**:
     * Pastikan `Metasploitable.vmdk` berada di bawah **Controller: IDE**. Jika berada di SATA, pindahkan ke controller IDE untuk menghindari disk failure saat boot.
   * Klik **OK**.
*(Bagi pengguna Linux yang ingin otomasi via CLI, script `import_metasploitable2.sh` tetap dapat digunakan).*
5. **Menyalakan VM & Verifikasi IP:**
   * Klik tombol **Start**.
   * Tunggu hingga proses booting selesai dan muncul prompt login:
     * **Username:** `msfadmin`
     * **Password:** `msfadmin` *(karakter password memang tidak ditampilkan di layar)*.
   * Periksa alamat IP target dengan perintah:
     ```bash
     ifconfig
     ```
   * Catat alamat IP pada interface `eth0` (biasanya `192.168.56.xxx` atau segmen host-only lab kalian).
---
**Opsi B — Docker (Lebih Ringan, untuk Laptop dengan Resource Terbatas)**

Kalau laptopmu terbatas resource-nya, paket ini juga menyediakan `docker-compose.yml` menggunakan image komunitas yang mereplikasi sebagian besar service Metasploitable2. Perlu dicatat: ini bukan image resmi Rapid7, jadi beberapa kerentanan mungkin sedikit berbeda dibanding VM aslinya. Untuk latihan enumerasi dasar di bab ini, perbedaannya tidak signifikan.

Pilih salah satu opsi, lalu catat IP target-nya di catatan lab kamu.

## 3.2 Full Port Scan

Sebelum menjalankan scanning, siapkan terlebih dahulu direktori catatan praktikum di Kali Linux agar file hasil scan dapat tersimpan dengan benar:

```bash
mkdir -p ~/Lab-Notes/Bab-03-Active-Recon
```

Untuk memetakan seluruh 65.535 port TCP terbuka pada target, kalian dapat memilih salah satu dari dua opsi berikut:

### Opsi 1 — Scan Standar (Bawaan Nmap)
```bash
nmap -p- -T4 <IP-target> -oN ~/Lab-Notes/Bab-03-Active-Recon/01-full-portscan.txt
```
* **Kelebihan:** Mengikuti mekanisme adaptif Nmap bawaan secara alami.
* **Catatan:** Jika target memiliki port yang terfilter atau koneksi mengalami retransmisi, proses scan bisa memakan waktu **15–30 menit**.

---

### Opsi 2 — Scan Cepat (Direkomendasikan untuk Praktikum Lab)
```bash
nmap -p- -T4 --min-rate 1000 <IP-target> -oN ~/Lab-Notes/Bab-03-Active-Recon/01-full-portscan.txt
```
* **Kelebihan:** Memaksa Nmap mengirim minimal 1.000 paket per detik, sehingga scan 65.535 port selesai dalam waktu **sekitar 1 menit**.
* **Catatan Etika & Keamanan:** Opsi ini sangat ideal untuk jaringan lokal/lab terisolasi seperti VirtualBox. Namun pada pentest nyata, opsi ini sangat "berisik" (*noisy*) dan dapat langsung memicu alarm deteksi IDS/IPS (Suricata/Snort).

---

**Penjelasan Parameter Tambahan:**
* `-p-` : Memeriksa seluruh 65.535 port TCP (bukan cuma 1.000 port default).
* `-T4` : Mengatur timing template ke level *Aggressive* agar scan lebih cepat.
* `-oN` : Menyimpan output teks normal ke direktori catatan praktikum.


## 3.3 Service & Version Detection + OS Fingerprinting

Setelah mengetahui port mana saja yang terbuka dari hasil scan sebelumnya, lakukan scan lanjutan yang lebih detail hanya ke port-port tersebut.

Kalian bisa menggunakan salah satu dari dua cara berikut:

### Cara A — Ekstraksi Port Otomatis (Direkomendasikan)
Gunakan perintah satu baris ini di Kali Linux untuk mengambil seluruh port yang berstatus `open` secara otomatis dari file hasil scan pertama:

```bash
OPEN_PORTS=$(grep -oP '^\d+(?=/tcp\s+open)' ~/Lab-Notes/Bab-03-Active-Recon/01-full-portscan.txt | paste -sd, -)
nmap -sV -O -p $OPEN_PORTS <IP-target> -oN ~/Lab-Notes/Bab-03-Active-Recon/02-version-os.txt
```

### Cara B — Input Manual Port Populer Metasploitable2
Jika ingin memasukkan port secara manual, gunakan daftar port umum Metasploitable2 berikut:

```bash
nmap -sV -O -p 21,22,23,25,53,80,111,139,445,512,513,514,1099,1524,2049,2121,3306,5432,5900,6000,6667,8009,8180 <IP-target> -oN ~/Lab-Notes/Bab-03-Active-Recon/02-version-os.txt
```

**Penjelasan Parameter:**
* `-sV` : *Service Version Detection* — mengirim serangkaian probe untuk membaca banner dan signature versi aplikasi.
* `-O` : *OS Fingerprinting* — menganalisis respons TCP/IP stack target untuk mendeteksi sistem operasi dan kernel.
* `-p` : Menentukan spesifik port mana saja yang akan diperiksa.

Perhatikan versi tiap service yang muncul (misalnya `vsftpd 2.3.4`, `OpenSSH 4.7p1`, `Apache 2.2.8`). Nanti di Bab 4 dan Bab 13, data versi inilah yang akan kita gunakan untuk mencari exploit yang relevan.


## 3.4 DNS Enumeration Terhadap Metasploitable2

Salah satu service yang sengaja dibuat rentan di Metasploitable2 adalah **BIND DNS server** di port 53. Coba lakukan percobaan zone transfer:

```bash
dig axfr @<IP-target> metasploitable.local
```

(Nama domain bisa bervariasi tergantung versi image — cek dokumentasi resmi Metasploitable2 kalau nama domain di atas tidak berhasil.)

Kalau berhasil, kamu akan melihat langsung mengapa zone transfer yang tidak dibatasi itu berbahaya — kamu dapat seluruh record DNS mesin tersebut hanya dengan satu command.

## 3.5 Enumerasi SMB (Server Message Block)
Protokol SMB (port 139 dan 445) sering kali menyimpan informasi sensitif mengenai nama pengguna, grup, dan folder bersama (*shares*).
1. **Enumerasi Null Session & Informasi Sistem dengan `enum4linux`:**
   ```bash
   enum4linux -a <IP-target> | tee ~/Lab-Notes/Bab-03-Active-Recon/03-smb-enum.txt
   ```
2. **Memeriksa Share Folder Tanpa Password (Anonymous Login):**
   ```bash
   smbclient -L //<IP-target>/ -N
   ```
   * *Opsi `-N`:* Menginstruksikan smbclient untuk tidak meminta password (*no password*).
   * Perhatikan folder share yang terekspos (seperti `tmp` atau `opt`). Celah ini memungkinkan penyerang membaca atau bahkan mengunggah file tanpa autentikasi.
---

## 3.6 Enumerasi SMTP (Simple Mail Transfer Protocol)
Banyak mail server lama yang masih mengaktifkan perintah verifikasi pengguna. Kita dapat menggunakan tool `netcat` untuk menghubungkan diri secara interaktif ke port 25:
```bash
nc <IP-target> 25
```
Setelah banner server SMTP muncul (misalnya `220 metasploitable.localdomain ESMTP Postfix`), ketik perintah berikut satu per satu:
```text
VRFY root
VRFY msfadmin
VRFY user_palsu_12345
QUIT
```
**Analisis Respons Server:**
* **Kode `250 2.1.5`:** Server mengonfirmasi bahwa pengguna tersebut **valid dan terdaftar** di sistem target.
* **Kode `550 5.1.1`:** Server menandakan bahwa pengguna **tidak ditemukan** (*User unknown*).
* Ketik **`QUIT`** untuk keluar dari koneksi netcat.
Teknik ini mendemonstrasikan bagaimana penyerang dapat memetakan daftar username valid di sebuah sistem sebelum melancarkan serangan *password spraying* atau *brute force*.
---


## 3.7 Enumerasi SNMP (Simple Network Management Protocol)

SNMP pada port 161 UDP sering dibiarkan menggunakan kata sandi bawaan (*community string*) default, yaitu `public`. Uji pembacaan informasi sistem menggunakan tool `snmpwalk`:
```bash
# Menampilkan 30 baris pertama informasi sistem:
snmpwalk -v2c -c public <IP-target> | head -n 30
# Menyimpan seluruh dump konfigurasi sistem ke catatan lab:
snmpwalk -v2c -c public <IP-target> > ~/Lab-Notes/Bab-03-Active-Recon/04-snmp-enum.txt
```
Jika community string `public` berhasil, target akan mengekspos ribuan data sensitif mengenai sistem operasi, nama host, daftar proses yang berjalan, kartu jaringan, hingga rute jaringan internal.

## 3.8 Menyusun Tabel Enumerasi Lengkap

Pindahkan seluruh temuan ke `template_enumeration_table.md` yang sudah disediakan. Ini akan jadi rujukan utamamu di Bab 4 (vulnerability scanning) dan seterusnya, jadi usahakan benar-benar lengkap dan rapi.

## Checkpoint Praktikum

- [ ] Metasploitable2 berhasil dijalankan dan bisa dijangkau dari Kali.
- [ ] Full port scan (`-p-`) selesai dan hasilnya tersimpan.
- [ ] Service/version detection + OS fingerprinting selesai untuk semua port terbuka.
- [ ] Percobaan zone transfer DNS sudah dicoba dan hasilnya (berhasil/gagal) tercatat.
- [ ] Enumerasi SMB, SMTP, dan SNMP masing-masing sudah dilakukan dan didokumentasikan.
- [ ] Tabel enumerasi lengkap (`template_enumeration_table.md`) sudah terisi untuk seluruh service yang ditemukan.

## Sumber Daya Pendukung

Semua file teknis — `docker-compose.yml` (opsi ringan), script import VirtualBox untuk image resmi Metasploitable2, provisioning Vagrant untuk Kali dengan tools enumerasi lengkap, script automasi scan, dan template tabel enumerasi — ada di paket resource yang menyertai modul ini. Cek `README.md` untuk instruksi lengkap kedua opsi setup.


## Link Tutorial/Step-by-step Walkthrough

https://youtu.be/14OLaFy1U_w?si=DT_J7sYrIY6335kb

