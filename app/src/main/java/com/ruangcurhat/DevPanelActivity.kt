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
import com.ruangcurhat.api.ApiClient
import com.ruangcurhat.api.ApiResponse
import com.ruangcurhat.api.CounselorDto
import com.ruangcurhat.api.CounselorRequest
import com.ruangcurhat.api.ListResponse
import com.ruangcurhat.api.RegisterRequest
import com.ruangcurhat.api.UserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DevPanelActivity : AppCompatActivity() {

    private lateinit var layoutDevConsListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_panel)

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDev)
        val spinnerRel = findViewById<Spinner>(R.id.spinnerReligion)

        val etConsName = findViewById<EditText>(R.id.etDevConsName)
        val etConsRank = findViewById<EditText>(R.id.etDevConsRank)
        val etConsNrp = findViewById<EditText>(R.id.etDevConsNrp)
        val etConsJob = findViewById<EditText>(R.id.etDevConsJob)
        val etConsUnit = findViewById<EditText>(R.id.etDevConsUnit)
        val etConsTelegram = findViewById<EditText>(R.id.etDevConsTelegram)
        val btnSaveCons = findViewById<Button>(R.id.btnRegisterNewCounselor)

        val etUserEmail = findViewById<EditText>(R.id.etDevUserEmail)
        val etUserPassword = findViewById<EditText>(R.id.etDevUserPassword)
        val etUserName = findViewById<EditText>(R.id.etDevUserName)
        val etUserRank = findViewById<EditText>(R.id.etDevUserRank)
        val etUserNrp = findViewById<EditText>(R.id.etDevUserNrp)
        val btnSaveUser = findViewById<Button>(R.id.btnRegisterNewUser)

        layoutDevConsListContainer = findViewById(R.id.layoutDevConsListContainer)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("Islam", "Kristen", "Katolik", "Hindu", "Buddha", "Konghucu"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRel.adapter = adapter

        btnBack.setOnClickListener { finish() }

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

            btnSaveCons.isEnabled = false
            btnSaveCons.text = "MENYIMPAN..."

            val request = CounselorRequest(name, rank.uppercase(), nrp, job, unit, telegram, religion)
            ApiClient.service.createCounselor(request).enqueue(object : Callback<ApiResponse<CounselorDto>> {
                override fun onResponse(call: Call<ApiResponse<CounselorDto>>, response: Response<ApiResponse<CounselorDto>>) {
                    btnSaveCons.isEnabled = true
                    btnSaveCons.text = "Simpan & Daftarkan Konselor"

                    val body = response.body()
                    if (!response.isSuccessful || body?.success != true) {
                        Toast.makeText(this@DevPanelActivity, body?.message ?: "Gagal menyimpan konselor.", Toast.LENGTH_LONG).show()
                        return
                    }

                    etConsName.text.clear()
                    etConsRank.text.clear()
                    etConsNrp.text.clear()
                    etConsJob.text.clear()
                    etConsUnit.text.clear()
                    etConsTelegram.text.clear()

                    Toast.makeText(this@DevPanelActivity, "Konselor baru berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    renderCounselorList()
                }

                override fun onFailure(call: Call<ApiResponse<CounselorDto>>, t: Throwable) {
                    btnSaveCons.isEnabled = true
                    btnSaveCons.text = "Simpan & Daftarkan Konselor"
                    Toast.makeText(this@DevPanelActivity, "Gagal terhubung ke server: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }

        btnSaveUser.setOnClickListener {
            val email = etUserEmail.text.toString().trim()
            val password = etUserPassword.text.toString()
            val name = etUserName.text.toString().trim()
            val rank = etUserRank.text.toString().trim()
            val nrp = etUserNrp.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || rank.isEmpty() || nrp.isEmpty()) {
                Toast.makeText(this, "Semua kolom anggota dinas wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSaveUser.isEnabled = false
            btnSaveUser.text = "MENYIMPAN..."

            val request = RegisterRequest(
                email = email,
                password = password,
                name = name,
                pangkat = rank.uppercase(),
                nrp = nrp,
                jabatan = "Prajurit Dinas",
                kesatuan = "Lanud Abdulrachman Saleh",
                telegram = "+62...",
                role = "user"
            )

            ApiClient.service.register(request).enqueue(object : Callback<ApiResponse<UserDto>> {
                override fun onResponse(call: Call<ApiResponse<UserDto>>, response: Response<ApiResponse<UserDto>>) {
                    btnSaveUser.isEnabled = true
                    btnSaveUser.text = "Tambah & Aktifkan Anggota"

                    val body = response.body()
                    if (!response.isSuccessful || body?.success != true) {
                        Toast.makeText(this@DevPanelActivity, body?.message ?: "Gagal membuat akun dinas.", Toast.LENGTH_LONG).show()
                        return
                    }

                    etUserEmail.text.clear()
                    etUserPassword.text.clear()
                    etUserName.text.clear()
                    etUserRank.text.clear()
                    etUserNrp.text.clear()

                    Toast.makeText(this@DevPanelActivity, "Akun dinas berhasil diaktifkan.", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<ApiResponse<UserDto>>, t: Throwable) {
                    btnSaveUser.isEnabled = true
                    btnSaveUser.text = "Tambah & Aktifkan Anggota"
                    Toast.makeText(this@DevPanelActivity, "Gagal terhubung ke server: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }

        renderCounselorList()
    }

    private fun renderCounselorList() {
        showAdminListMessage("Memuat daftar konselor...")

        ApiClient.service.getCounselors().enqueue(object : Callback<ListResponse<CounselorDto>> {
            override fun onResponse(call: Call<ListResponse<CounselorDto>>, response: Response<ListResponse<CounselorDto>>) {
                layoutDevConsListContainer.removeAllViews()

                if (!response.isSuccessful || response.body()?.success != true) {
                    showAdminListMessage("Gagal memuat data konselor.")
                    return
                }

                val allCounselors = response.body()?.data.orEmpty()
                if (allCounselors.isEmpty()) {
                    showAdminListMessage("Belum ada konselor terdaftar.")
                    return
                }

                for (c in allCounselors) {
                    val view = LayoutInflater.from(this@DevPanelActivity).inflate(R.layout.item_counselor_card, layoutDevConsListContainer, false)

                    view.findViewById<TextView>(R.id.tvConsEmoji).text = c.emoji ?: ""
                    view.findViewById<TextView>(R.id.tvConsName).text = c.name
                    view.findViewById<TextView>(R.id.tvConsRank).text = "Pangkat: ${c.pangkat}"
                    view.findViewById<TextView>(R.id.tvConsNrp).text = "NRP/NIP: ${c.nrp}"
                    view.findViewById<TextView>(R.id.tvConsJob).text = c.jabatan ?: "-"
                    view.findViewById<TextView>(R.id.tvConsUnit).text = c.kesatuan ?: "-"

                    val btnDelete = view.findViewById<AppCompatButton>(R.id.btnContactTelegram)
                    btnDelete.text = "Hapus Konselor (Admin)"
                    btnDelete.supportBackgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@DevPanelActivity, android.R.color.holo_red_dark))
                    btnDelete.setOnClickListener { deleteCounselor(c) }

                    layoutDevConsListContainer.addView(view)
                }
            }

            override fun onFailure(call: Call<ListResponse<CounselorDto>>, t: Throwable) {
                showAdminListMessage("Gagal terhubung ke server.")
            }
        })
    }

    private fun deleteCounselor(counselor: CounselorDto) {
        ApiClient.service.deleteCounselor(counselor.id).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@DevPanelActivity, "Gagal menghapus konselor.", Toast.LENGTH_LONG).show()
                    return
                }

                Toast.makeText(this@DevPanelActivity, "Konselor ${counselor.name} berhasil dihapus!", Toast.LENGTH_SHORT).show()
                renderCounselorList()
            }

            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                Toast.makeText(this@DevPanelActivity, "Gagal terhubung ke server: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showAdminListMessage(message: String) {
        layoutDevConsListContainer.removeAllViews()
        val tvMessage = TextView(this).apply {
            text = message
            textSize = 11f
            setTextColor(ContextCompat.getColor(this@DevPanelActivity, R.color.brand_subtext))
        }
        layoutDevConsListContainer.addView(tvMessage)
    }
}
