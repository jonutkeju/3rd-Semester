package com.example.tugas3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends Activity {

    TextView txtNama, txtNrp, txtEmail, txtTelepon;
    Button btnTutup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        txtNama = findViewById(R.id.txtNama);
        txtNrp = findViewById(R.id.txtNrp);
        txtEmail = findViewById(R.id.txtEmail);
        txtTelepon = findViewById(R.id.txtTelepon);
        btnTutup = findViewById(R.id.btnTutup);

        String nama = getIntent().getStringExtra("nama");
        String nrp = getIntent().getStringExtra("nrp");
        String email = getIntent().getStringExtra("email");
        String telepon = getIntent().getStringExtra("telepon");

        txtNama.setText(nama);
        txtNrp.setText(nrp);
        txtEmail.setText(email);
        txtTelepon.setText(telepon);

        btnTutup.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("SecondActivity", "SecondActivity telah dihancurkan");
    }
}