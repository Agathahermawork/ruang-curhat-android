package com.ruangcurhat

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class DevPanelActivity : AppCompatActivity() {

    private lateinit var layoutDevConsListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_panel)

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDev)
        val spinnerRel = findViewById<Spinner>(R.id.spinnerReligion)

        // Form 1: Konselor
        val etConsName = findViewById<EditText>(R.id.etDevConsName)
        val etConsRank = findViewById<EditText>(R.id.etDevConsRank)
        val etConsNrp = findViewById<EditText>(R.id.etDevConsNrp)
        val etConsJob = findViewById<EditText>(R.id.etDevConsJob)
        val etConsUnit = findViewById<EditText>(R.id.etDevConsUnit)
        val etConsTelegram = findViewById<EditText>(R.id.etDevConsTelegram)
        val btnSaveCons = findViewById<Button>(R.id.btnRegisterNewCounselor)

        // Form 2: Anggota
        val etUserEmail = findViewById<EditText>(R.id.etDevUserEmail)
        val etUserName = findViewById<EditText>(R.id.etDevUserName)
        val etUserRank = findViewById<EditText>(R.id.etDevUserRank)
        val etUserNrp = findViewById<EditText>(R.id.etDevUserNrp)
        val btnSaveUser = findViewById<Button>(R.id.btnRegisterNewUser)

        // List 3: Kelola
        layoutDevConsListContainer = findViewById(R.id.layoutDevConsListContainer)

        // Setup Opsi Dropdown Kategori Agama
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("Islam", "Kristen", "Katolik", "Hindu", "Buddha"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRel.adapter = adapter

        btnBack.setOnClickListener { finish() }

        // 1. Logika Daftarkan Konselor Baru
        btnSaveCons.setOnClickListener {
            val name = etConsName.text.toString().trim()
            val rank = etConsRank.text.toString().trim()
            val nrp = etConsNrp.text.toString().trim()
            val job = etConsJob.text.toString().trim()
            val unit = etConsUnit.text.toString().trim()
            val telegram = etConsTelegram.text.toString().trim()
            val religion = spinnerRel.selectedItem.toString()

            if (name.isEmpty() || rank.isEmpty() || nrp.isEmpty() || telegram.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua kolom pendaftaran!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emoji = when (religion) {
                "Islam" -> "👳‍♂️"
                "Kristen" -> "👨‍💼"
                "Katolik" -> "⛪"
                "Hindu" -> "🕉️"
                else -> "☸️"
            }

            val newCons = DataStoreManager.Counselor(name, rank.uppercase(), nrp, job, unit, telegram, religion, emoji)
            DataStoreManager.addCounselor(newCons)

            // Bersihkan Inputan
            etConsName.text.clear()
            etConsRank.text.clear()
            etConsNrp.text.clear()
            etConsJob.text.clear()
            etConsUnit.text.clear()
            etConsTelegram.text.clear()

            Toast.makeText(this, "Konselor baru berhasil disimpan!", Toast.LENGTH_SHORT).show()
            renderCounselorList() // Refresh List Kelola
        }

        // 2. Logika Tambah Anggota Dinas Baru (BISA DIPAKAI LOGIN NYATA!)
        btnSaveUser.setOnClickListener {
            val email = etUserEmail.text.toString().trim()
            val name = etUserName.text.toString().trim()
            val rank = etUserRank.text.toString().trim()
            val nrp = etUserNrp.text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || rank.isEmpty() || nrp.isEmpty()) {
                Toast.makeText(this, "Semua kolom anggota dinas wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Daftarkan akun baru ke Database Memori
            val newUser = DataStoreManager.UserAccount(email, name, rank.uppercase(), nrp, "Prajurit Dinas", "Lanud Abdulrachman Saleh", "+62...", "user")
            DataStoreManager.addUserAccount(newUser)

            // Bersihkan inputan
            etUserEmail.text.clear()
            etUserName.text.clear()
            etUserRank.text.clear()
            etUserNrp.text.clear()

            Toast.makeText(this, "Akun dinas anggota berhasil diaktifkan! Silakan coba login kembali.", Toast.LENGTH_LONG).show()
        }

        // Render List Kelola Awal
        renderCounselorList()
    }

    // 3. Logika Render & Hapus Konselor Dinamis Lintas Agama
    private fun renderCounselorList() {
        layoutDevConsListContainer.removeAllViews()
        val allCounselors = DataStoreManager.getAllCounselors()

        if (allCounselors.isEmpty()) {
            val tvEmpty = TextView(this).apply {
                text = "Belum ada konselor terdaftar."
                textSize = 11f
                setTextColor(ContextCompat.getColor(this@DevPanelActivity, R.color.brand_subtext))
            }
            layoutDevConsListContainer.addView(tvEmpty)
            return
        }

        for (c in allCounselors) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_counselor_card, layoutDevConsListContainer, false)

            view.findViewById<TextView>(R.id.tvConsEmoji).text = c.emoji
            view.findViewById<TextView>(R.id.tvConsName).text = c.name
            view.findViewById<TextView>(R.id.tvConsRank).text = "Pangkat: ${c.pangkat}"
            view.findViewById<TextView>(R.id.tvConsNrp).text = "NRP/NIP: ${c.nrp}"
            view.findViewById<TextView>(R.id.tvConsJob).text = c.jabatan
            view.findViewById<TextView>(R.id.tvConsUnit).text = c.kesatuan

            // Menggunakan AppCompatButton agar serasi dengan perubahan tipe di XML
            val btnDelete = view.findViewById<AppCompatButton>(R.id.btnContactTelegram)
            btnDelete.text = "Hapus Konselor (Admin)"

            // Mengubah warna latar menggunakan ColorStateList (Aman untuk Material Components & menjaga sudut rounded tetap utuh)
            val redColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            btnDelete.supportBackgroundTintList = ColorStateList.valueOf(redColor)

            btnDelete.setOnClickListener {
                DataStoreManager.deleteCounselor(c.nrp)
                Toast.makeText(this, "Konselor ${c.name} berhasil dihapus!", Toast.LENGTH_SHORT).show()
                renderCounselorList() // Refresh List Kelola
            }

            layoutDevConsListContainer.addView(view)
        }
    }
}