# Resource Praktikum — Bab 3: Active Recon & Enumeration

## Isi Paket

| File | Fungsi |
|---|---|
| `Vagrantfile` | Provisioning VM Kali attacker dengan tools enumerasi |
| `provision.sh` | Instalasi nmap, dnsenum, dnsrecon, enum4linux, smbclient, snmp, AutoRecon, Docker |
| `import_metasploitable2.sh` | (Opsi A) Membantu import file .vmdk resmi Metasploitable2 ke VirtualBox |
| `docker-compose.yml` | (Opsi B) Menjalankan alternatif ringan Metasploitable2 via Docker |
| `active_recon.sh` | Automasi rangkaian scan & enumerasi (port, DNS, SMB, SNMP) |
| `template_enumeration_table.md` | Template dokumentasi hasil enumerasi |

## Langkah 1 — Setup Kali Attacker

```bash
vagrant up
vagrant ssh
```

(Jika kamu sudah punya Kali VM dari bab sebelumnya, cukup jalankan `sudo ./provision.sh` di dalamnya.)

## Langkah 2 — Setup Target: Pilih Opsi A atau Opsi B

### Opsi A — VirtualBox dengan Image Resmi (Direkomendasikan)

1. Unduh **Metasploitable2.zip** dari halaman resmi:
   https://sourceforge.net/projects/metasploitable/files/Metasploitable2/
2. Ekstrak, cari file `Metasploitable.vmdk`.
3. Jalankan:
   ```bash
   chmod +x import_metasploitable2.sh
   ./import_metasploitable2.sh /path/ke/Metasploitable.vmdk
   ```
4. Pastikan adapter host-only `vboxnet0` sudah dikonfigurasi di VirtualBox (File → Host Network Manager, jika belum ada, buat baru).
5. Nyalakan VM lewat VirtualBox GUI atau:
   ```bash
   VBoxManage startvm "Bab03-Metasploitable2" --type headless
   ```
6. Login default di dalam VM: `msfadmin` / `msfadmin`. Cek IP dengan `ifconfig` di dalam VM tersebut.

### Opsi B — Docker (Lebih Ringan)

```bash
docker compose up -d
```

Target akan bisa diakses di IP host Docker pada port-port yang sudah dipetakan (lihat `docker-compose.yml`).

> **Catatan jujur**: Opsi B pakai image komunitas (bukan resmi Rapid7), jadi ada kemungkinan kecil perilaku beberapa service sedikit berbeda dari VM aslinya — terutama untuk latihan DNS zone transfer (BIND) yang paling otentik justru ada di Opsi A.

## Langkah 3 — Jalankan Automasi Enumerasi

```bash
chmod +x active_recon.sh
./active_recon.sh <IP-target>
```

Script ini akan menjalankan full port scan, version detection, percobaan DNS zone transfer, enumerasi SMB, dan enumerasi SNMP secara berurutan, lalu menyimpan semua hasil ke `~/Lab-Notes/Bab-03-Active-Recon/`.

Untuk enumerasi SMTP, jalankan manual (karena sifatnya interaktif):
```bash
nc <IP-target> 25
```

## Langkah 4 — Dokumentasikan

```bash
cp template_enumeration_table.md ~/Lab-Notes/Bab-03-Active-Recon/06-tabel-enumerasi.md
```

Isi berdasarkan seluruh hasil scan yang sudah kamu kumpulkan.

## Catatan Keamanan Lab

- Jalankan seluruh scan hanya terhadap target di jaringan host-only/internal — jangan pernah arahkan ke IP publik/internet.
- Metasploitable2 sengaja dibuat sangat rentan (termasuk kredensial default `msfadmin/msfadmin`) — jangan pernah menjalankan image ini di jaringan yang terhubung ke internet tanpa isolasi.
- Matikan VM/container setelah selesai praktikum:
  ```bash
  # VirtualBox
  VBoxManage controlvm "Bab03-Metasploitable2" poweroff
  # Docker
  docker compose down
  ```
