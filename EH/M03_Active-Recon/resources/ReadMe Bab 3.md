Isi paketnya:

- **`Bab_03_Ringkasan_Textbook_Reconnaissance_Aktif.md`** — teori TCP/IP handshake, port/service/banner, DNS enumeration (zone transfer + brute force), Nmap mendalam (SYN vs connect scan, version detection, OS fingerprinting, NSE), enumerasi SMB/SMTP/SNMP, teknik stealth, pentingnya UDP, dan automasi via AutoRecon.
- **`Bab_03_Modul_Praktikum_Active_Recon.md`** — praktikum lengkap terhadap **Metasploitable2**, dari full port scan sampai zone transfer DNS, enumerasi SMB/SMTP/SNMP.
- **`resources/`**:
    - **Dua opsi target** karena Metasploitable2 resminya distribusi VM, bukan Docker:
        - _Opsi A (direkomendasikan)_: `import_metasploitable2.sh` — otomatis bikin VM VirtualBox dari file `.vmdk` resmi yang kamu unduh sendiri dari SourceForge (nggak saya redistribusikan langsung karena masalah lisensi/ukuran file).
        - _Opsi B (ringan)_: `docker-compose.yml` pakai image komunitas, untuk laptop terbatas — dengan disclaimer jujur bahwa ini bukan image resmi.
    - `Vagrantfile` + `provision.sh` — Kali attacker dengan nmap, dnsenum, dnsrecon, enum4linux, smbclient, snmp, AutoRecon.
    - `active_recon.sh` — automasi full scan → version detection → DNS AXFR → SMB → SNMP, semua tersimpan rapi.
    - `template_enumeration_table.md` — template dokumentasi.
    - `README.md` — panduan lengkap kedua opsi.
