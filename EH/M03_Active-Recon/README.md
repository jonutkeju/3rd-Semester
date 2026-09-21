# Tabel Enumerasi Lengkap — Bab 3

## Identitas Diri

* **Nama**: Jonathan Steven Tjahjaputra
* **NRP**: 5027251036
* **Kelas**: Ethical Hacking dan Uji Keamanan Siber A

## Info Target

* **IP Target**: 192.168.56.103
* **Metode deploy**: Tidak disebutkan pada hasil scan
* **Tanggal scan**: 18 September 2026

## Hasil Full Port Scan (TCP)

| Port  | Status | Service     | Versi (jika terdeteksi)             | Catatan                                     |
| ----- | ------ | ----------- | ----------------------------------- | ------------------------------------------- |
| 21    | Open   | FTP         | vsftpd 2.3.4                        | Service FTP terdeteksi                      |
| 22    | Open   | SSH         | OpenSSH 4.7p1 Debian 8ubuntu1       | SSH tersedia                                |
| 23    | Open   | Telnet      | Linux telnetd                       | Telnet tersedia                             |
| 25    | Open   | SMTP        | Postfix smtpd                       | SMTP tersedia                               |
| 53    | Open   | DNS         | ISC BIND 9.4.2                      | DNS tersedia                                |
| 80    | Open   | HTTP        | Apache httpd 2.2.8 (Ubuntu) DAV/2   | Web server tersedia                         |
| 111   | Open   | RPCBind     | RPC #100000                         | RPC service tersedia                        |
| 139   | Open   | NetBIOS/SMB | Samba 3.X - 4.X                     | Workgroup: WORKGROUP                        |
| 445   | Open   | SMB         | Samba 3.X - 4.X                     | SMB service tersedia                        |
| 512   | Open   | rexec       | netkit-rsh rexecd                   | Remote execution service                    |
| 513   | Open   | login       | Tidak terdeteksi                    | Service login tersedia                      |
| 514   | Open   | shell       | Netkit rshd                         | Remote shell service                        |
| 1099  | Open   | Java RMI    | GNU Classpath grmiregistry          | Java RMI registry                           |
| 1524  | Open   | Bindshell   | Metasploitable root shell           | Teridentifikasi sebagai root shell          |
| 2049  | Open   | NFS         | NFS 2-4 (RPC #100003)               | NFS tersedia                                |
| 2121  | Open   | FTP         | Tidak terdeteksi                    | Service teridentifikasi sebagai ccproxy-ftp |
| 3306  | Open   | MySQL       | MySQL 5.0.51a-3ubuntu5              | Database MySQL tersedia                     |
| 3632  | Open   | distccd     | distccd v1 (GNU 4.2.4)              | distccd tersedia                            |
| 5432  | Open   | PostgreSQL  | PostgreSQL 8.3.0 - 8.3.7            | Database PostgreSQL tersedia                |
| 5900  | Open   | VNC         | VNC protocol 3.3                    | Remote graphical access tersedia            |
| 6000  | Open   | X11         | Access denied                       | X11 tersedia tetapi akses ditolak           |
| 6667  | Open   | IRC         | UnrealIRCd                          | IRC service tersedia                        |
| 6697  | Open   | IRC         | UnrealIRCd                          | IRC service tersedia                        |
| 8009  | Open   | AJP13       | Apache Jserv Protocol v1.3          | AJP service tersedia                        |
| 8180  | Open   | HTTP        | Apache Tomcat/Coyote JSP engine 1.1 | Tomcat tersedia                             |
| 8787  | Open   | DRb         | Ruby DRb RMI (Ruby 1.8)             | Ruby DRb tersedia                           |
| 33915 | Open   | Java RMI    | GNU Classpath grmiregistry          | Java RMI registry                           |
| 43324 | Open   | Nlockmgr    | RPC #100021                         | Network lock manager                        |
| 51998 | Open   | Mountd      | RPC #100005                         | NFS mountd tersedia                         |
| 60819 | Open   | Status      | RPC #100024                         | RPC status service                          |

Hasil scan menunjukkan **30 port TCP terbuka**. Selain itu, Nmap mencatat 50.684 port ter-filter dan 14.821 port tertutup.

## DNS Enumeration

* **Zone transfer berhasil?**: Tidak
* **Record yang ditemukan**: Tidak ada

Percobaan AXFR terhadap `metasploitable.local` melalui port 53 gagal.

## SMB Enumeration

* **Share yang ditemukan**:

  * `print$` — Printer Drivers
  * `tmp` — `oh noes!`
  * `opt`
  * `IPC$`
  * `ADMIN$`
* **Bisa diakses tanpa autentikasi?**: Ya, sebagian

  * `tmp` dapat di-mapping dan listing berhasil.
  * `print$`, `opt`, dan `ADMIN$` ditolak.
* **User/group yang ter-enumerate**:

  * User yang ditemukan antara lain `root`, `msfadmin`, `user`, `www-data`, `postgres`, `mysql`, `tomcat55`, `ftp`, `irc`, `proftpd`, `distccd`, dan akun sistem lainnya.
  * Group yang ditemukan antara lain `Domain Admins`, `Domain Users`, dan `Domain Guests`.

Enum4linux juga menunjukkan bahwa server menggunakan **WORKGROUP**, hostname **METASPLOITABLE**, dan Samba **3.0.20-Debian**. Server menerima session menggunakan username dan password kosong pada tahap session check.

Share SMB yang ditemukan dan hasil aksesnya tercatat pada bagian share enumeration.

## SMTP Enumeration

* **VRFY diaktifkan?**: Belum dapat ditentukan
* **Contoh user valid yang berhasil dikonfirmasi**: Belum ada

File hasil scan menyatakan bahwa enumerasi SMTP perlu dilakukan secara manual menggunakan:

`nc 192.168.56.103 25`

Kemudian mencoba `VRFY root` dan `VRFY user_yang_tidak_ada`. Karena langkah tersebut belum dilakukan pada hasil yang diberikan, status VRFY tidak dapat disimpulkan.

## SNMP Enumeration

* **Community string `public` berfungsi?**: Tidak
* **Informasi menarik yang ter-expose**: Tidak ada

Percobaan enumerasi SNMP dengan community string `public` menghasilkan **Timeout: No Response**.

## Ringkasan & Prioritas Investigasi Lanjutan

> Service yang menarik untuk didalami pada Bab 4 antara lain **port 1524, 21, 445, 3306, 3632, 5432, 5900, dan 8180** karena hasil service/version detection menunjukkan service dan versi yang spesifik. Secara khusus, port **1524/tcp** teridentifikasi sebagai **Metasploitable root shell**, sehingga informasi tersebut menjadi temuan yang sangat relevan untuk investigasi vulnerability scanning lebih lanjut. Port 21, 445, 3306, 3632, 5432, 5900, dan 8180 juga dapat diperiksa berdasarkan service dan versi yang terdeteksi.