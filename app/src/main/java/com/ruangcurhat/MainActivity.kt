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
import com.ruangcurhat.api.ApiClient
import com.ruangcurhat.api.LoginRequest
import com.ruangcurhat.api.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // 4. Submit Login ke API Laravel
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

                    getSharedPreferences("SESSION", Context.MODE_PRIVATE).edit().apply {
                        putString("TOKEN", body.token)
                        putString("ROLE", user.role ?: "user")
                        putString("NAME", user.name)
                        putString("PANGKAT", user.pangkat ?: "")
                        putString("NRP", user.nrp ?: "")
                        putString("JABATAN", user.jabatan ?: "")
                        putString("KESATUAN", user.kesatuan ?: "")
                        putString("TELEGRAM", user.telegram ?: "")
                        apply()
                    }

                    Toast.makeText(this@MainActivity, "Autentikasi Berhasil!", Toast.LENGTH_SHORT).show()
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
