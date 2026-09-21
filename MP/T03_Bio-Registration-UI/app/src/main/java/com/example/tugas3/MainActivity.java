package com.example.tugas3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    EditText edtNama, edtNrp, edtEmail, edtTelepon;
    Button btnKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNama = findViewById(R.id.edtNama);
        edtNrp = findViewById(R.id.edtNrp);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelepon = findViewById(R.id.edtTelepon);
        btnKirim = findViewById(R.id.btnKirim);

        btnKirim.setOnClickListener(v -> {

            String nama = edtNama.getText().toString();
            String nrp = edtNrp.getText().toString();
            String email = edtEmail.getText().toString();
            String telepon = edtTelepon.getText().toString();

            Intent intent = new Intent(MainActivity.this, SecondActivity.class);

            intent.putExtra("nama", nama);
            intent.putExtra("nrp", nrp);
            intent.putExtra("email", email);
            intent.putExtra("telepon", telepon);

            startActivity(intent);
        });
    }
}