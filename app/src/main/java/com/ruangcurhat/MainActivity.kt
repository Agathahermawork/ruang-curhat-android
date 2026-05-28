package com.ruangcurhat

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ruangcurhat.api.ApiClient
import com.ruangcurhat.api.LoginRequest
import com.ruangcurhat.api.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var isPasswordVisible = false
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
        val btnSubmit = findViewById<Button>(R.id.btnSubmitLogin)

        // 1. Toggling Mode Dinas vs Tamu dengan Warna Dinamis Hijau Aman #A7DDC4
        btnTabDinas.setOnClickListener {
            activeRole = "dinas"

            // Atur background tab Dinas menjadi aktif hijau (#A7DDC4)
            btnTabDinas.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A7DDC4"))
            btnTabDinas.setTextColor(Color.parseColor("#000000"))

            // Atur background tab Tamu menjadi transparan (disabled)
            btnTabTamu.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            btnTabTamu.setTextColor(Color.parseColor("#C7C7CC"))

            tvLabelEmail.text = "E-MAIL DINAS / USERNAME"
            etEmail.hint = "Masukkan e-mail dinas..."
        }

        btnTabTamu.setOnClickListener {
            activeRole = "tamu"

            // Atur background tab Tamu menjadi aktif hijau (#A7DDC4)
            btnTabTamu.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A7DDC4"))
            btnTabTamu.setTextColor(Color.parseColor("#000000"))

            // Atur background tab Dinas menjadi transparan (disabled)
            btnTabDinas.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            btnTabDinas.setTextColor(Color.parseColor("#C7C7CC"))

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

        // 3. Submit Login ke API Laravel
        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Kolom login wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSubmit.isEnabled = false
            btnSubmit.text = "MEMERIKSA..."

            ApiClient.service.login(LoginRequest(email, password)).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Autentikasi Masuk Aman"

                    val body = response.body()
                    val user = body?.data

                    if (!response.isSuccessful || body?.success != true || user == null || body.token.isNullOrBlank()) {
                        Toast.makeText(this@MainActivity, body?.message ?: "Email atau password tidak sesuai.", Toast.LENGTH_LONG).show()
                        return
                    }

                    // Penanganan Null Safety dengan Elvis Operator (?: "") agar tidak crash jika ada properti null
                    getSharedPreferences("SESSION", Context.MODE_PRIVATE).edit().apply {
                        putString("TOKEN", body.token)
                        putString("ROLE", user.role ?: "user")
                        putString("NAME", user.name ?: "")
                        putString("PANGKAT", user.pangkat ?: "")
                        putString("NRP", user.nrp ?: "")
                        putString("JABATAN", user.jabatan ?: "")
                        putString("KESATUAN", user.kesatuan ?: "")
                        putString("TELEGRAM", user.telegram ?: "")
                        apply()
                    }

                    Toast.makeText(this@MainActivity, "Autentikasi Berhasil!", Toast.LENGTH_SHORT).show()

                    // Pindah ke HomeActivity
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "Autentikasi Masuk Aman"
                    Toast.makeText(this@MainActivity, "Gagal terhubung ke server: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}