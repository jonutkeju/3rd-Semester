#!/usr/bin/env bash
# import_metasploitable2.sh - Bab 3, Opsi A
#
# Membantu membuat VM VirtualBox dari file .vmdk resmi Metasploitable2
# yang sudah kamu unduh manual (karena lisensi/hosting file besar tidak
# bisa didistribusikan ulang di paket ini).
#
# LANGKAH SEBELUM MENJALANKAN SCRIPT INI:
#   1. Unduh "Metasploitable2.zip" dari halaman resmi SourceForge Rapid7:
#      https://sourceforge.net/projects/metasploitable/files/Metasploitable2/
#   2. Ekstrak zip tersebut, cari file "Metasploitable.vmdk"
#   3. Catat path lengkap file .vmdk tersebut
#
# Cara pakai:
#   chmod +x import_metasploitable2.sh
#   ./import_metasploitable2.sh /path/ke/Metasploitable.vmdk

set -e

VMDK_PATH="$1"
VM_NAME="Bab03-Metasploitable2"
HOSTONLY_IP="192.168.56.13"

if [ -z "$VMDK_PATH" ] || [ ! -f "$VMDK_PATH" ]; then
    echo "Penggunaan: ./import_metasploitable2.sh /path/ke/Metasploitable.vmdk"
    echo "Pastikan file .vmdk sudah diunduh dan diekstrak terlebih dahulu."
    exit 1
fi

echo "[*] Membuat VM baru: ${VM_NAME}..."
VBoxManage createvm --name "$VM_NAME" --ostype "Ubuntu_64" --register

echo "[*] Mengatur RAM dan network host-only..."
VBoxManage modifyvm "$VM_NAME" --memory 1024 --cpus 1
VBoxManage modifyvm "$VM_NAME" --nic1 hostonly --hostonlyadapter1 vboxnet0

echo "[*] Menyalin file .vmdk ke folder VM (agar tidak menimpa file asli)..."
VM_FOLDER=$(VBoxManage showvminfo "$VM_NAME" --machinereadable | grep ^CfgFile= | cut -d'"' -f2 | xargs dirname)
cp "$VMDK_PATH" "${VM_FOLDER}/Metasploitable.vmdk"

echo "[*] Menambahkan storage controller dan attach disk..."
VBoxManage storagectl "$VM_NAME" --name "SATA Controller" --add sata --controller IntelAhci
VBoxManage storageattach "$VM_NAME" --storagectl "SATA Controller" --port 0 --device 0 \
    --type hdd --medium "${VM_FOLDER}/Metasploitable.vmdk"

echo ""
echo "[*] VM '${VM_NAME}' berhasil dibuat."
echo "[*] Sebelum menyalakan VM, buka VirtualBox GUI, pastikan adapter network"
echo "    'vboxnet0' (host-only) sudah ada di menu File > Host Network Manager."
echo "[*] Setelah VM menyala, login default: msfadmin / msfadmin"
echo "[*] Set IP static ${HOSTONLY_IP} di dalam VM jika diperlukan (opsional, DHCP host-only juga bisa)."
echo ""
echo "Untuk menyalakan VM dari command line:"
echo "    VBoxManage startvm '${VM_NAME}' --type headless"
