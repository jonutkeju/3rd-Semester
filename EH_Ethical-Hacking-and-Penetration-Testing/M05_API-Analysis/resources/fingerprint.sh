#!/usr/bin/env bash
# fingerprint.sh - Bab 5: Automasi fingerprinting teknologi dengan whatweb
#
# Cara pakai:
#   chmod +x fingerprint.sh
#   ./fingerprint.sh <url>

URL="$1"
OUTDIR="$HOME/Lab-Notes/Bab-05-Web-Recon"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")

if [ -z "$URL" ]; then
    echo "Penggunaan: ./fingerprint.sh <url>"
    exit 1
fi

mkdir -p "$OUTDIR"

echo "[*] Menjalankan WhatWeb terhadap ${URL} ..."
whatweb -v "$URL" | tee "${OUTDIR}/whatweb-${TIMESTAMP}.txt"

echo ""
echo "[*] Mengecek response header mentah (curl -I) ..."
curl -sI "$URL" | tee "${OUTDIR}/headers-${TIMESTAMP}.txt"

echo ""
echo "[*] Mengecek kemungkinan spesifikasi API yang bocor ..."
for path in "api-docs" "swagger.json" "openapi.json" "graphql"; do
    CODE=$(curl -s -o /dev/null -w "%{http_code}" "${URL}/${path}")
    echo "    ${URL}/${path} -> HTTP ${CODE}"
done

echo ""
echo "[*] Selesai. Jangan lupa cek juga hasil ekstensi Wappalyzer di browser."
