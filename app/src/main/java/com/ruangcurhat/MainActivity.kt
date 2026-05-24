package com.ruangcurhat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private var isDemoTrayOpen = false
    private var activeRole = "dinas" // dinas atau tamu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)
        val tvLabelEmail = findViewById<TextView>(R.id.tvLabelEmail)

        val btnTabDinas = findViewById<Button>(R.id.btnTabDinas)
        val btnTabTamu = findViewById<Button>(R.id.btnTabTamu)

        val layoutDemoHeader = findViewById<LinearLayout>(R.id.layoutDemoTrayHeader)
        val layoutDemoContent = findViewById<LinearLayout>(R.id.layoutDemoTrayContent)
        val ivDemoArrow = findViewById<ImageView>(R.id.ivDemoTrayArrow)

        val btnDemoAdmin = findViewById<LinearLayout>(R.id.btnDemoAdmin)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitLogin)

        // 1. Toggling Mode Dinas vs Tamu
        btnTabDinas.setOnClickListener {
            activeRole = "dinas"
            btnTabDinas.setBackgroundColor(resources.getColor(android.R.color.white))
            btnTabDinas.setTextColor(resources.getColor(R.color.brand_text))
            btnTabTamu.setBackgroundColor(resources.getColor(android.R.color.transparent))
            btnTabTamu.setTextColor(resources.getColor(R.color.brand_subtext))

            tvLabelEmail.text = "E-MAIL DINAS / USERNAME"
            etEmail.hint = "Masukkan e-mail dinas..."
        }

        btnTabTamu.setOnClickListener {
            activeRole = "tamu"
            btnTabTamu.setBackgroundColor(resources.getColor(android.R.color.white))
            btnTabTamu.setTextColor(resources.getColor(R.color.brand_text))
            btnTabDinas.setBackgroundColor(resources.getColor(android.R.color.transparent))
            btnTabDinas.setTextColor(resources.getColor(R.color.brand_subtext))

            tvLabelEmail.text = "E-MAIL TAMU / NAMA ANONIM"
            etEmail.hint = "Masukkan nama samaran..."
        }

        // 2. Toggling Visibilitas Password
        btnTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_eye_visible)
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_eye_hidden)
            }
            isPasswordVisible = !isPasswordVisible
            etPassword.setSelection(etPassword.text.length)
        }

        // 3. Toggling Accordion Demo Tray
        layoutDemoHeader.setOnClickListener {
            if (isDemoTrayOpen) {
                layoutDemoContent.visibility = View.GONE
                ivDemoArrow.animate().rotation(0f).start()
            } else {
                layoutDemoContent.visibility = View.VISIBLE
                ivDemoArrow.animate().rotation(180f).start()
            }
            isDemoTrayOpen = !isDemoTrayOpen
        }

        // Autofill dari Demo Tray
        btnDemoAdmin.setOnClickListener {
            etEmail.setText("admin@gmail.com")
            etPassword.setText("admin123")
            Toast.makeText(this, "Kredensial Demo Dimuat!", Toast.LENGTH_SHORT).show()
        }

        // 4. Submit Login & Validasi Dinamis Terhadap DataStoreManager!
        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Kolom login wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi Terhadap List Akun Dinamis yang dibuat Admin di Dev Panel!
            val matchedUser = DataStoreManager.validateUserLogin(email)

            if (matchedUser != null) {
                // Berhasil Login! Simpan Sesi dan Berpindah Halaman
                val sharedPref = getSharedPreferences("SESSION", Context.MODE_PRIVATE)
                sharedPref.edit().apply {
                    putString("ROLE", matchedUser.role)
                    putString("NAME", matchedUser.name)
                    putString("PANGKAT", matchedUser.pangkat)
                    putString("NRP", matchedUser.nrp)
                    putString("JABATAN", matchedUser.jabatan)
                    putString("KESATUAN", matchedUser.kesatuan)
                    putString("TELEGRAM", matchedUser.telegram)
                    apply()
                }

                Toast.makeText(this, "Autentikasi Berhasil!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Email tidak terdaftar di sistem dinas!", Toast.LENGTH_LONG).show()
            }
        }
    }
}