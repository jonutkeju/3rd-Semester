#!/usr/bin/env bash
# provision.sh - Bab 5: Setup tools web reconnaissance di Kali VM
# Dijalankan otomatis oleh Vagrant, atau manual dengan:
#   chmod +x provision.sh && sudo ./provision.sh

set -e

echo "[*] Update package list..."
apt-get update -y

echo "[*] Memasang tools content discovery (ffuf, gobuster, feroxbuster)..."
apt-get install -y ffuf gobuster feroxbuster

echo "[*] Memasang whatweb (fingerprinting CLI)..."
apt-get install -y whatweb

echo "[*] Memasang SecLists (koleksi wordlist)..."
apt-get install -y seclists
# SecLists biasanya terpasang di /usr/share/seclists/ di Kali

echo "[*] Memastikan Burp Suite Community sudah ada (biasanya bawaan Kali)..."
if ! command -v burpsuite &> /dev/null; then
    apt-get install -y burpsuite
fi

echo "[*] Memasang OWASP ZAP sebagai alternatif open-source penuh..."
apt-get install -y zaproxy

echo "[*] Memasang Docker Engine + Compose plugin..."
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

echo "[*] Membuat struktur folder catatan lab Bab 5..."
sudo -u vagrant mkdir -p /home/vagrant/Lab-Notes/Bab-05-Web-Recon
sudo -u vagrant touch /home/vagrant/Lab-Notes/Bab-05-Web-Recon/00-scope-dan-tujuan.md
sudo -u vagrant touch /home/vagrant/Lab-Notes/Bab-05-Web-Recon/01-observasi-manual.md

echo ""
echo "[*] Selesai! Jalankan 'docker compose up -d' lalu buka Burp Suite/ZAP untuk mulai."
