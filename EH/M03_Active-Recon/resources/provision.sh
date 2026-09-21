#!/usr/bin/env bash
# provision.sh - Bab 3: Setup tools active recon & enumeration di Kali VM
# Dijalankan otomatis oleh Vagrant, atau manual dengan:
#   chmod +x provision.sh && sudo ./provision.sh

set -e

echo "[*] Update package list..."
apt-get update -y

echo "[*] Memastikan Nmap terpasang..."
apt-get install -y nmap

echo "[*] Memasang tools DNS enumeration (dnsenum, dnsrecon, dig)..."
apt-get install -y dnsenum dnsrecon dnsutils

echo "[*] Memasang tools SMB enumeration (enum4linux, smbclient)..."
apt-get install -y enum4linux smbclient

echo "[*] Memasang tools SNMP enumeration (snmp)..."
apt-get install -y snmp

echo "[*] Memasang netcat (untuk enumerasi SMTP manual)..."
apt-get install -y netcat-traditional || apt-get install -y netcat

echo "[*] Memasang AutoRecon (automasi enumerasi)..."
apt-get install -y pipx || apt-get install -y python3-pip
pipx install autorecon 2>/dev/null || pip3 install --break-system-packages autorecon || \
    echo "    Instalasi AutoRecon via pip gagal, install manual: https://github.com/Tib3rius/AutoRecon"

echo "[*] Memasang Docker (untuk opsi target ringan / Opsi B)..."
if ! command -v docker &> /dev/null; then
    apt-get install -y ca-certificates curl gnupg
    install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
    chmod a+r /etc/apt/keyrings/docker.asc
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
      $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
      tee /etc/apt/sources.list.d/docker.list > /dev/null
    apt-get update -y
    apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
fi
usermod -aG docker vagrant || true

echo "[*] Membuat struktur folder catatan lab Bab 3..."
sudo -u vagrant mkdir -p /home/vagrant/Lab-Notes/Bab-03-Active-Recon
sudo -u vagrant touch /home/vagrant/Lab-Notes/Bab-03-Active-Recon/00-scope-dan-tujuan.md

echo "[*] Selesai! Lihat README.md untuk cara menyiapkan target Metasploitable2 (Opsi A/B)."
