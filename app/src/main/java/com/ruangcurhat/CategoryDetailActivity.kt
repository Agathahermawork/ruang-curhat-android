package com.ruangcurhat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URLEncoder
import com.ruangcurhat.R

class CategoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        val religion = intent.getStringExtra("RELIGION") ?: "Islam"
        findViewById<TextView>(R.id.tvCategoryTitle).text = "Bimbingan Mental $religion"

        findViewById<ImageButton>(R.id.btnBackFromCategory).setOnClickListener { finish() }

        renderCounselors(religion)
    }

    private fun renderCounselors(religion: String) {
        val container = findViewById<LinearLayout>(R.id.layoutConsContainer)
        container.removeAllViews()

        // Ambil data konselor statis + dinamis dari database memori lokal Panel Pengembang
        val counselors = DataStoreManager.getCounselorsByReligion(religion)

        if (counselors.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = "Belum ada pembimbing rohani terdaftar untuk kategori ini."
                textSize = 12f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, 100, 0, 0)
                setTextColor(resources.getColor(R.color.brand_subtext))
            }
            container.addView(emptyView)
            return
        }

        // Inflate view secara dinamis agar 100% kokoh dan tidak memerlukan adapter
        for (c in counselors) {
            val card = LayoutInflater.from(this).inflate(R.layout.item_counselor_card, container, false)

            card.findViewById<TextView>(R.id.tvConsEmoji).text = c.emoji
            card.findViewById<TextView>(R.id.tvConsName).text = c.name
            card.findViewById<TextView>(R.id.tvConsRank).text = "Pangkat: ${c.pangkat}"
            card.findViewById<TextView>(R.id.tvConsNrp).text = "NRP/NIP: ${c.nrp}"
            card.findViewById<TextView>(R.id.tvConsJob).text = c.jabatan
            card.findViewById<TextView>(R.id.tvConsUnit).text = c.kesatuan

            val btnChat = card.findViewById<Button>(R.id.btnContactTelegram)
            btnChat.setOnClickListener {
                triggerTelegramIntent(c.name, c.pangkat, c.telegram)
            }

            container.addView(card)
        }
    }

    private fun triggerTelegramIntent(consName: String, consRank: String, telegram: String) {
        val sharedPref = getSharedPreferences("SESSION", Context.MODE_PRIVATE)
        val uName = sharedPref.getString("NAME", "Anonim")
        val uRank = sharedPref.getString("PANGKAT", "LETDA")
        val uNrp = sharedPref.getString("NRP", "-")
        val uUnit = sharedPref.getString("KESATUAN", "Lanud")

        // Format pesan perkenalan instansional otomatis
        val defaultMessage = "Halo $consRank $consName, saya $uRank $uName (NRP: $uNrp) dari Kesatuan $uUnit ingin berkonsultasi mengenai bimbingan rohani..."

        try {
            val encodedMessage = URLEncoder.encode(defaultMessage, "UTF-8")
            val telegramUri = Uri.parse("https://t.me/$telegram?text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, telegramUri).apply {
                setPackage("org.telegram.messenger") // Memaksa membuka Telegram resmi
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Membuka browser (Aplikasi Telegram tidak terpasang)", Toast.LENGTH_SHORT).show()
            val fallbackUri = Uri.parse("https://t.me/$telegram")
            startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
        }
    }
}