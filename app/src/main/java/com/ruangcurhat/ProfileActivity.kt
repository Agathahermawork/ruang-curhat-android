package com.ruangcurhat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ruangcurhat.api.ApiClient
import com.ruangcurhat.api.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromProfile)
        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvRank = findViewById<TextView>(R.id.tvProfileRank)
        val tvNrp = findViewById<TextView>(R.id.tvProfileNrp)
        val tvJob = findViewById<TextView>(R.id.tvProfileJob)
        val tvUnit = findViewById<TextView>(R.id.tvProfileUnit)
        val tvTelegram = findViewById<TextView>(R.id.tvProfileTelegram)

        val btnEdit = findViewById<Button>(R.id.btnEditProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Muat Data dari SharedPreferences Sesi
        val sharedPref = getSharedPreferences("SESSION", Context.MODE_PRIVATE)
        tvName.text = sharedPref.getString("NAME", "Anonim")
        tvRank.text = sharedPref.getString("PANGKAT", "LETDA")
        tvNrp.text = sharedPref.getString("NRP", "-")
        tvJob.text = sharedPref.getString("JABATAN", "Prajurit Dinas")
        tvUnit.text = sharedPref.getString("KESATUAN", "Lanud Abdulrachman Saleh")
        tvTelegram.text = sharedPref.getString("TELEGRAM", "+62...")

        btnBack.setOnClickListener { finish() }

        btnEdit.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val token = sharedPref.getString("TOKEN", null)
            if (!token.isNullOrBlank()) {
                ApiClient.service.logout("Bearer $token").enqueue(object : Callback<ApiResponse<Unit>> {
                    override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) = Unit
                    override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) = Unit
                })
            }

            sharedPref.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data otomatis jika user baru kembali dari edit profile
        val sharedPref = getSharedPreferences("SESSION", Context.MODE_PRIVATE)
        findViewById<TextView>(R.id.tvProfileName).text = sharedPref.getString("NAME", "Anonim")
        findViewById<TextView>(R.id.tvProfileRank).text = sharedPref.getString("PANGKAT", "LETDA")
        findViewById<TextView>(R.id.tvProfileNrp).text = sharedPref.getString("NRP", "-")
        findViewById<TextView>(R.id.tvProfileJob).text = sharedPref.getString("JABATAN", "Prajurit Dinas")
        findViewById<TextView>(R.id.tvProfileUnit).text = sharedPref.getString("KESATUAN", "Lanud Abdulrachman Saleh")
        findViewById<TextView>(R.id.tvProfileTelegram).text = sharedPref.getString("TELEGRAM", "+62...")
    }
}
