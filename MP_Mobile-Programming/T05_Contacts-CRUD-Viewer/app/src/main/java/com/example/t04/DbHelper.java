package com.example.t04;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, "kontak.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS kontak (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nama TEXT, " +
                "nohp TEXT, " +
                "nohp2 TEXT, " +
                "alamat TEXT, " +
                "pekerjaan TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS kontak;");
        onCreate(db);
    }
}
