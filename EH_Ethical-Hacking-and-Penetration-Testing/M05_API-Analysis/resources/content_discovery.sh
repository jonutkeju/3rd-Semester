#!/usr/bin/env bash
# content_discovery.sh - Bab 5: Automasi content discovery dengan ffuf
#
# Cara pakai:
#   chmod +x content_discovery.sh
#   ./content_discovery.sh <base-url>
#
# Contoh:
#   ./content_discovery.sh http://localhost:3000

BASE_URL="$1"
OUTDIR="$HOME/Lab-Notes/Bab-05-Web-Recon"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
WORDLIST_COMMON="/usr/share/seclists/Discovery/Web-Content/common.txt"
WORDLIST_API="/usr/share/seclists/Discovery/Web-Content/api/api-endpoints.txt"

if [ -z "$BASE_URL" ]; then
    echo "Penggunaan: ./content_discovery.sh <base-url>"
    echo "Contoh: ./content_discovery.sh http://localhost:3000"
    exit 1
fi

mkdir -p "$OUTDIR"

echo "[1/2] Content discovery umum terhadap ${BASE_URL} ..."
ffuf -u "${BASE_URL}/FUZZ" -w "$WORDLIST_COMMON" \
    -o "${OUTDIR}/ffuf-common-${TIMESTAMP}.json" -of json

echo ""
echo "[2/2] Content discovery khusus pola API terhadap ${BASE_URL}/api ..."
if [ -f "$WORDLIST_API" ]; then
    ffuf -u "${BASE_URL}/api/FUZZ" -w "$WORDLIST_API" \
        -o "${OUTDIR}/ffuf-api-${TIMESTAMP}.json" -of json
else
    echo "    Wordlist API tidak ditemukan di path default, dilewati."
    echo "    Cek lokasi SecLists yang terpasang: dpkg -L seclists | grep api"
fi

echo ""
echo "[*] Selesai. Hasil tersimpan di: ${OUTDIR}"
echo "[*] Tips: jika banyak hasil false-positive (status 200 untuk semua path),"
echo "    tambahkan filter ukuran response, misal: -fs <ukuran-yang-mau-diabaikan>"
