package com.example.t04;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText edtNrp;
    private EditText edtNama;

    private Button btnSimpan;
    private Button btnCari;
    private Button btnUpdate;
    private Button btnHapus;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "DataMahasiswa";
    private static final String KEY_NRP_TERAKHIR = "nrp_terakhir";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNrp = findViewById(R.id.edtNrp);
        edtNama = findViewById(R.id.edtNama);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnCari = findViewById(R.id.btnCari);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHapus = findViewById(R.id.btnHapus);

        databaseHelper = new DatabaseHelper(this);

        sharedPreferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        // Load the last NRP that was successfully searched.
        String nrpTerakhir = sharedPreferences.getString(
                KEY_NRP_TERAKHIR,
                ""
        );

        if (!nrpTerakhir.isEmpty()) {
            edtNrp.setText(nrpTerakhir);
        }

        // CREATE
        btnSimpan.setOnClickListener(v -> {
            String nrp = edtNrp.getText().toString().trim();
            String nama = edtNama.getText().toString().trim();

            if (nrp.isEmpty() || nama.isEmpty()) {
                showToast("NRP dan Nama harus diisi!");
                return;
            }

            boolean berhasil = databaseHelper.insertMahasiswa(nrp, nama);

            if (berhasil) {
                showToast("Data mahasiswa berhasil disimpan");
            } else {
                showToast("Gagal menyimpan. NRP mungkin sudah ada.");
            }
        });

        // READ / SEARCH
        btnCari.setOnClickListener(v -> {
            String nrp = edtNrp.getText().toString().trim();

            if (nrp.isEmpty()) {
                showToast("Masukkan NRP terlebih dahulu");
                return;
            }

            SQLiteDatabase db = databaseHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_MHS,
                    new String[]{
                            DatabaseHelper.COLUMN_NRP,
                            DatabaseHelper.COLUMN_NAMA
                    },
                    DatabaseHelper.COLUMN_NRP + "=?",
                    new String[]{nrp},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                String nama = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_NAMA
                        )
                );

                edtNama.setText(nama);

                // Persist the last NRP searched.
                sharedPreferences.edit()
                        .putString(KEY_NRP_TERAKHIR, nrp)
                        .apply();

                showToast("Data ditemukan");
            } else {
                edtNama.setText("");
                showToast("Data tidak ditemukan");
            }

            cursor.close();
            db.close();
        });

        // UPDATE
        btnUpdate.setOnClickListener(v -> {
            String nrp = edtNrp.getText().toString().trim();
            String nama = edtNama.getText().toString().trim();

            if (nrp.isEmpty() || nama.isEmpty()) {
                showToast("NRP dan Nama harus diisi!");
                return;
            }

            boolean berhasil = databaseHelper.updateMahasiswa(nrp, nama);

            if (berhasil) {
                showToast("Data berhasil di-update");
            } else {
                showToast("Data dengan NRP tersebut tidak ditemukan");
            }
        });

        // DELETE
        btnHapus.setOnClickListener(v -> {
            String nrp = edtNrp.getText().toString().trim();

            if (nrp.isEmpty()) {
                showToast("Masukkan NRP terlebih dahulu");
                return;
            }

            boolean berhasil = databaseHelper.deleteMahasiswa(nrp);

            if (berhasil) {
                edtNrp.setText("");
                edtNama.setText("");
                showToast("Data berhasil dihapus");
            } else {
                showToast("Data dengan NRP tersebut tidak ditemukan");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
