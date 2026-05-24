package com.ruangcurhat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvRoleBadge = findViewById<TextView>(R.id.tvRoleBadge)
        val tvUserWelcome = findViewById<TextView>(R.id.tvUserWelcome)
        val btnGoToProfile = findViewById<ImageButton>(R.id.btnGoToProfile)
        val layoutDevTrigger = findViewById<LinearLayout>(R.id.layoutDevTrigger)
        val btnOpenDevPanel = findViewById<Button>(R.id.btnOpenDevPanel)

        // Ambil Data Sesi dari Intent Login atau SharedPreferences
        val sharedPref = getSharedPreferences("SESSION", Context.MODE_PRIVATE)

        // Ambil data kiriman dari login, jika tidak ada, ambil dari SharedPreferences
        val role = intent.getStringExtra("ROLE") ?: sharedPref.getString("ROLE", "user")
        val name = intent.getStringExtra("NAME") ?: sharedPref.getString("NAME", "Anonim")
        val pangkat = intent.getStringExtra("PANGKAT") ?: sharedPref.getString("PANGKAT", "LETDA")
        val nrp = intent.getStringExtra("NRP") ?: sharedPref.getString("NRP", "-")

        // Simpan ke SharedPreferences secara permanen untuk sesi lokal
        sharedPref.edit().apply {
            putString("ROLE", role)
            putString("NAME", name)
            putString("PANGKAT", pangkat)
            putString("NRP", nrp)
            apply()
        }

        // Tampilkan Identitas Sesi di Beranda
        tvUserWelcome.text = name
        if (role == "admin") {
            tvRoleBadge.text = "PENGEMBANG / ADMIN"
            tvRoleBadge.setTextColor(resources.getColor(android.R.color.white))
            tvRoleBadge.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            layoutDevTrigger.visibility = View.VISIBLE
        } else {
            tvRoleBadge.text = "ANGGOTA DINAS"
            tvRoleBadge.setTextColor(resources.getColor(android.R.color.white))
            tvRoleBadge.setBackgroundColor(resources.getColor(android.R.color.holo_blue_dark))
            layoutDevTrigger.visibility = View.GONE
        }

        // Navigasi ke Layar Profil
        btnGoToProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Navigasi ke Panel Pengembang
        btnOpenDevPanel.setOnClickListener {
            startActivity(Intent(this, DevPanelActivity::class.java))
        }

        // Setup Klik Tombol Kategori Agama
        findViewById<LinearLayout>(R.id.btnCategoryIslam).setOnClickListener { openCategory("Islam") }
        findViewById<LinearLayout>(R.id.btnCategoryKristen).setOnClickListener { openCategory("Kristen") }
        findViewById<LinearLayout>(R.id.btnCategoryKatolik).setOnClickListener { openCategory("Katolik") }
        findViewById<LinearLayout>(R.id.btnCategoryHindu).setOnClickListener { openCategory("Hindu") }
        findViewById<LinearLayout>(R.id.btnCategoryBuddha).setOnClickListener { openCategory("Buddha") }
    }

    private fun openCategory(religion: String) {
        val intent = Intent(this, CategoryDetailActivity::class.java)
        intent.putExtra("RELIGION", religion)
        startActivity(intent)
    }
}