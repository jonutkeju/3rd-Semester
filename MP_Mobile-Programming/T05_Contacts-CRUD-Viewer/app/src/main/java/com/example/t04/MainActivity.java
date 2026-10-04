package com.example.t04;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements KontakAdapter.OnActionClickListener {

    private ListView lvKontak;
    private KontakAdapter kAdapter;
    private SQLiteDatabase dbku;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvKontak = findViewById(R.id.lvKontak);
        Button btnTambah = findViewById(R.id.btnTambah);

        ArrayList<Kontak> listKontak = new ArrayList<>();
        kAdapter = new KontakAdapter(this, 0, listKontak, this);
        lvKontak.setAdapter(kAdapter);

        dbHelper = new DbHelper(this);
        dbku = dbHelper.getWritableDatabase();

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDialogTambah();
            }
        });

        // Klik item untuk opsi Ubah / Hapus / Forward
        lvKontak.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Kontak dipilih = kAdapter.getItem(position);
                if (dipilih != null) {
                    tampilkanDialogOpsi(dipilih);
                }
            }
        });

        lvKontak.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Kontak dipilih = kAdapter.getItem(position);
                if (dipilih != null) {
                    tampilkanDialogHapus(dipilih);
                }
                return true;
            }
        });

        muatDataDariDatabase();
    }

    @Override
    public void onEditClick(Kontak kontak) {
        tampilkanDialogUbah(kontak);
    }

    @Override
    public void onDeleteClick(Kontak kontak) {
        tampilkanDialogHapus(kontak);
    }

    @Override
    public void onPesanClick(Kontak kontak) {
        forwardKePesan(kontak);
    }

    private void forwardKePesan(Kontak kontak) {
        try {
            Uri uri = Uri.parse("smsto:" + kontak.getNoHp());
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", "Halo " + kontak.getNama() + ", ");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membuka aplikasi Pesan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void tampilkanDialogTambah() {
        View viewInput = LayoutInflater.from(this).inflate(R.layout.add_kontak, null);
        final EditText etNama = viewInput.findViewById(R.id.etNama);
        final EditText etNoHp = viewInput.findViewById(R.id.etNoHp);
        final EditText etNoHp2 = viewInput.findViewById(R.id.etNoHp2);
        final EditText etAlamat = viewInput.findViewById(R.id.etAlamat);
        final EditText etPekerjaan = viewInput.findViewById(R.id.etPekerjaan);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Kontak");
        builder.setView(viewInput);
        builder.setCancelable(false);

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nama = etNama.getText().toString().trim();
                String noHp = etNoHp.getText().toString().trim();
                String noHp2 = etNoHp2.getText().toString().trim();
                String alamat = etAlamat.getText().toString().trim();
                String pekerjaan = etPekerjaan.getText().toString().trim();

                if (!nama.isEmpty() && !noHp.isEmpty()) {
                    simpanKontak(nama, noHp, noHp2, alamat, pekerjaan);
                } else {
                    Toast.makeText(MainActivity.this, "Nama dan No HP Utama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void tampilkanDialogOpsi(final Kontak kontak) {
        CharSequence[] pilihan = {"Ubah (Edit)", "Hapus", "Kirim Pesan (Forward)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Aksi untuk " + kontak.getNama());
        builder.setItems(pilihan, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    tampilkanDialogUbah(kontak);
                } else if (which == 1) {
                    tampilkanDialogHapus(kontak);
                } else if (which == 2) {
                    forwardKePesan(kontak);
                }
            }
        });
        builder.show();
    }

    private void tampilkanDialogUbah(final Kontak kontak) {
        View viewInput = LayoutInflater.from(this).inflate(R.layout.add_kontak, null);
        final EditText etNama = viewInput.findViewById(R.id.etNama);
        final EditText etNoHp = viewInput.findViewById(R.id.etNoHp);
        final EditText etNoHp2 = viewInput.findViewById(R.id.etNoHp2);
        final EditText etAlamat = viewInput.findViewById(R.id.etAlamat);
        final EditText etPekerjaan = viewInput.findViewById(R.id.etPekerjaan);

        etNama.setText(kontak.getNama());
        etNoHp.setText(kontak.getNoHp());
        etNoHp2.setText(kontak.getNoHp2());
        etAlamat.setText(kontak.getAlamat());
        etPekerjaan.setText(kontak.getPekerjaan());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah / Edit Kontak");
        builder.setView(viewInput);
        builder.setCancelable(false);

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String namaBaru = etNama.getText().toString().trim();
                String noHpBaru = etNoHp.getText().toString().trim();
                String noHp2Baru = etNoHp2.getText().toString().trim();
                String alamatBaru = etAlamat.getText().toString().trim();
                String pekerjaanBaru = etPekerjaan.getText().toString().trim();

                if (!namaBaru.isEmpty() && !noHpBaru.isEmpty()) {
                    ubahKontak(kontak, namaBaru, noHpBaru, noHp2Baru, alamatBaru, pekerjaanBaru);
                } else {
                    Toast.makeText(MainActivity.this, "Nama dan No HP Utama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void tampilkanDialogHapus(final Kontak kontak) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Kontak");
        builder.setMessage("Apakah Anda yakin ingin menghapus kontak " + kontak.getNama() + "?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hapusKontak(kontak);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void simpanKontak(String nama, String noHp, String noHp2, String alamat, String pekerjaan) {
        ContentValues nilai = new ContentValues();
        nilai.put("nama", nama);
        nilai.put("nohp", noHp);
        nilai.put("nohp2", noHp2);
        nilai.put("alamat", alamat);
        nilai.put("pekerjaan", pekerjaan);
        long id = dbku.insert("kontak", null, nilai);

        if (id != -1) {
            kAdapter.add(new Kontak(id, nama, noHp, noHp2, alamat, pekerjaan));
            Toast.makeText(this, "Kontak berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal menambahkan kontak", Toast.LENGTH_SHORT).show();
        }
    }

    private void ubahKontak(Kontak kontak, String namaBaru, String noHpBaru, String noHp2Baru, String alamatBaru, String pekerjaanBaru) {
        ContentValues nilai = new ContentValues();
        nilai.put("nama", namaBaru);
        nilai.put("nohp", noHpBaru);
        nilai.put("nohp2", noHp2Baru);
        nilai.put("alamat", alamatBaru);
        nilai.put("pekerjaan", pekerjaanBaru);

        int rows = dbku.update("kontak", nilai, "_id = ?", new String[]{String.valueOf(kontak.getId())});
        if (rows > 0) {
            kontak.setNama(namaBaru);
            kontak.setNoHp(noHpBaru);
            kontak.setNoHp2(noHp2Baru);
            kontak.setAlamat(alamatBaru);
            kontak.setPekerjaan(pekerjaanBaru);
            kAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Kontak berhasil diperbarui", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal memperbarui kontak", Toast.LENGTH_SHORT).show();
        }
    }

    private void hapusKontak(Kontak kontak) {
        int rows = dbku.delete("kontak", "_id = ?", new String[]{String.valueOf(kontak.getId())});
        if (rows > 0) {
            kAdapter.remove(kontak);
            Toast.makeText(this, "Kontak berhasil dihapus", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal menghapus kontak", Toast.LENGTH_SHORT).show();
        }
    }

    private void muatDataDariDatabase() {
        kAdapter.clear();
        Cursor cursor = dbku.rawQuery("SELECT * FROM kontak", null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
                String noHp = cursor.getString(cursor.getColumnIndexOrThrow("nohp"));
                String noHp2 = cursor.getString(cursor.getColumnIndexOrThrow("nohp2"));
                String alamat = cursor.getString(cursor.getColumnIndexOrThrow("alamat"));
                String pekerjaan = cursor.getString(cursor.getColumnIndexOrThrow("pekerjaan"));
                kAdapter.add(new Kontak(id, nama, noHp, noHp2, alamat, pekerjaan));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbku != null && dbku.isOpen()) {
            dbku.close();
        }
    }
}
