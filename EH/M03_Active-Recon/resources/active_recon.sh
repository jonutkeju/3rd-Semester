#!/usr/bin/env bash
# active_recon.sh - Bab 3: Automasi rangkaian active recon & enumeration
#
# Cara pakai:
#   chmod +x active_recon.sh
#   ./active_recon.sh <IP-target>
#
# Contoh:
#   ./active_recon.sh 192.168.56.13

TARGET="$1"
OUTDIR="$HOME/Lab-Notes/Bab-03-Active-Recon"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")

if [ -z "$TARGET" ]; then
    echo "Penggunaan: ./active_recon.sh <IP-target>"
    exit 1
fi

mkdir -p "$OUTDIR"

echo "=============================================="
echo " Active Recon & Enumeration terhadap: $TARGET"
echo "=============================================="

echo ""
echo "[1/6] Full TCP port scan (semua 65535 port, ini bisa makan waktu)..."
nmap -p- -T4 "$TARGET" -oN "${OUTDIR}/01-full-portscan-${TIMESTAMP}.txt"

OPEN_PORTS=$(grep -oP '^\d+(?=/tcp\s+open)' "${OUTDIR}/01-full-portscan-${TIMESTAMP}.txt" | paste -sd, -)
echo "    Port terbuka terdeteksi: ${OPEN_PORTS:-tidak ada}"

if [ -n "$OPEN_PORTS" ]; then
    echo ""
    echo "[2/6] Service/version detection + OS fingerprinting pada port terbuka..."
    nmap -sV -O -p "$OPEN_PORTS" "$TARGET" -oN "${OUTDIR}/02-version-os-${TIMESTAMP}.txt"
fi

echo ""
echo "[3/6] Mencoba DNS zone transfer (port 53)..."
{
    echo "--- Percobaan AXFR terhadap domain umum Metasploitable ---"
    dig axfr @"$TARGET" metasploitable.local
} | tee "${OUTDIR}/03-dns-axfr-${TIMESTAMP}.txt" 2>&1

echo ""
echo "[4/6] Enumerasi SMB (enum4linux)..."
enum4linux -a "$TARGET" | tee "${OUTDIR}/04-smb-enum-${TIMESTAMP}.txt" 2>&1

echo ""
echo "[5/6] Enumerasi SNMP (community string 'public')..."
snmpwalk -v2c -c public "$TARGET" | tee "${OUTDIR}/05-snmp-enum-${TIMESTAMP}.txt" 2>&1

echo ""
echo "[6/6] Cek manual SMTP diperlukan (interaktif, tidak bisa otomatis penuh)."
echo "    Jalankan manual: nc ${TARGET} 25"
echo "    Lalu coba: VRFY root  dan  VRFY user_yang_tidak_ada"

echo ""
echo "[*] Semua hasil tersimpan di: ${OUTDIR}"
echo "[*] Pindahkan ringkasan ke template_enumeration_table.md"
