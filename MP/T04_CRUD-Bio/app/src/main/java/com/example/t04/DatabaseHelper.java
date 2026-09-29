package com.example.t04;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mahasiswa.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MHS = "mhs";
    public static final String COLUMN_NRP = "nrp";
    public static final String COLUMN_NAMA = "nama";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE " + TABLE_MHS + " (" +
                COLUMN_NRP + " TEXT PRIMARY KEY, " +
                COLUMN_NAMA + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MHS);
        onCreate(db);
    }

    public boolean insertMahasiswa(String nrp, String nama) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NRP, nrp);
        values.put(COLUMN_NAMA, nama);

        long result = db.insert(TABLE_MHS, null, values);
        return result != -1;
    }

    public boolean updateMahasiswa(String nrp, String nama) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA, nama);

        int result = db.update(
                TABLE_MHS,
                values,
                COLUMN_NRP + "=?",
                new String[]{nrp}
        );

        return result > 0;
    }

    public boolean deleteMahasiswa(String nrp) {
        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_MHS,
                COLUMN_NRP + "=?",
                new String[]{nrp}
        );

        return result > 0;
    }
}
