package com.example.hitungluas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hitungluas.ui.theme.HitungLuasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HitungLuasTheme {
                HitungLuas()
            }
        }
    }
}

@Composable
fun HitungLuas() {

    var angka1 by remember { mutableStateOf("") }
    var angka2 by remember { mutableStateOf("") }
    var hasil by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Kalkulator Sederhana"
        )

        OutlinedTextField(
            value = angka1,
            onValueChange = { angka1 = it },
            label = { Text("Angka pertama") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = angka2,
            onValueChange = { angka2 = it },
            label = { Text("Angka kedua") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {
                    val a = angka1.toDoubleOrNull()
                    val b = angka2.toDoubleOrNull()

                    if (a != null && b != null) {
                        hasil = (a + b).toString()
                    } else {
                        hasil = "Masukkan angka yang valid"
                    }
                }
            ) {
                Text("+")
            }

            Button(
                onClick = {
                    val a = angka1.toDoubleOrNull()
                    val b = angka2.toDoubleOrNull()

                    if (a != null && b != null) {
                        hasil = (a - b).toString()
                    } else {
                        hasil = "Masukkan angka yang valid"
                    }
                }
            ) {
                Text("-")
            }

            Button(
                onClick = {
                    val a = angka1.toDoubleOrNull()
                    val b = angka2.toDoubleOrNull()

                    if (a != null && b != null) {
                        hasil = (a * b).toString()
                    } else {
                        hasil = "Masukkan angka yang valid"
                    }
                }
            ) {
                Text("×")
            }

            Button(
                onClick = {
                    val a = angka1.toDoubleOrNull()
                    val b = angka2.toDoubleOrNull()

                    if (a != null && b != null) {
                        if (b != 0.0) {
                            hasil = (a / b).toString()
                        } else {
                            hasil = "Tidak bisa dibagi 0"
                        }
                    } else {
                        hasil = "Masukkan angka yang valid"
                    }
                }
            ) {
                Text("÷")
            }
        }

        Text(
            text = "Hasil: $hasil",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}