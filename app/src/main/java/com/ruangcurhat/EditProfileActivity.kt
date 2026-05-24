package com.ruangcurhat

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ruangcurhat.R

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromEdit)
        val etName = findViewById<EditText>(R.id.etEditName)
        val etRank = findViewById<EditText>(R.id.etEditRank)
        val etNrp = findViewById<EditText>(R.id.etEditNrp)
        val etJob = findViewById<EditText>(R.id.etEditJob)
        val etUnit = findViewById<EditText>(R.id.etEditUnit)
        val etTelegram = findViewById<EditText>(R.id.etEditTelegram)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)

        // Muat data lama untuk diedit
        val sharedPref = getSharedPreferences("SESSION", Context.MODE_PRIVATE)
        etName.setText(sharedPref.getString("NAME", ""))
        etRank.setText(sharedPref.getString("PANGKAT", ""))
        etNrp.setText(sharedPref.getString("NRP", ""))
        etJob.setText(sharedPref.getString("JABATAN", ""))
        etUnit.setText(sharedPref.getString("KESATUAN", ""))
        etTelegram.setText(sharedPref.getString("TELEGRAM", ""))

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val rank = etRank.text.toString().trim()
            val nrp = etNrp.text.toString().trim()
            val job = etJob.text.toString().trim()
            val unit = etUnit.text.toString().trim()
            val telegram = etTelegram.text.toString().trim()

            if (name.isEmpty() || rank.isEmpty() || nrp.isEmpty()) {
                Toast.makeText(this, "Nama, Pangkat, dan NRP wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan pembaruan ke SharedPreferences sesi
            sharedPref.edit().apply {
                putString("NAME", name)
                putString("PANGKAT", rank.uppercase())
                putString("NRP", nrp)
                putString("JABATAN", job)
                putString("KESATUAN", unit)
                putString("TELEGRAM", telegram)
                apply()
            }

            Toast.makeText(this, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}