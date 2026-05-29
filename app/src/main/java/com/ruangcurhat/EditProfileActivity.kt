package com.ruangcurhat

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ruangcurhat.api.ApiClient
import com.ruangcurhat.api.ApiResponse
import com.ruangcurhat.api.UpdateProfileRequest
import com.ruangcurhat.api.UserDto
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val etPassword = findViewById<EditText>(R.id.etEditPassword)
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
            val password = etPassword.text.toString()

            if (name.isEmpty() || rank.isEmpty() || nrp.isEmpty()) {
                Toast.makeText(this, "Nama, Pangkat, dan NRP wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isNotBlank() && password.length < 6) {
                Toast.makeText(this, "Password baru minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = sharedPref.getString("TOKEN", null)
            if (token.isNullOrBlank()) {
                Toast.makeText(this, "Sesi login tidak valid. Silakan login ulang.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Menyimpan..."

            val request = UpdateProfileRequest(
                name = name,
                pangkat = rank.uppercase(),
                nrp = nrp,
                jabatan = job,
                kesatuan = unit,
                telegram = telegram,
                password = password.takeIf { it.isNotBlank() }
            )

            ApiClient.service.updateProfile("Bearer $token", request).enqueue(object : Callback<ApiResponse<UserDto>> {
                override fun onResponse(call: Call<ApiResponse<UserDto>>, response: Response<ApiResponse<UserDto>>) {
                    btnSave.isEnabled = true
                    btnSave.text = "Simpan Perubahan"

                    val body = response.body()
                    val user = body?.data
                    if (!response.isSuccessful || body?.success != true || user == null) {
                        val message = body?.message ?: parseErrorMessage(response) ?: "Gagal menyimpan perubahan profil."
                        Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_LONG).show()
                        return
                    }

                    sharedPref.edit().apply {
                        putString("NAME", user.name)
                        putString("PANGKAT", user.pangkat ?: "")
                        putString("NRP", user.nrp ?: "")
                        putString("JABATAN", user.jabatan ?: "")
                        putString("KESATUAN", user.kesatuan ?: "")
                        putString("TELEGRAM", user.telegram ?: "")
                        apply()
                    }
                    etPassword.text.clear()

                    Toast.makeText(this@EditProfileActivity, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onFailure(call: Call<ApiResponse<UserDto>>, t: Throwable) {
                    btnSave.isEnabled = true
                    btnSave.text = "Simpan Perubahan"
                    Toast.makeText(this@EditProfileActivity, "Gagal terhubung ke server: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun parseErrorMessage(response: Response<ApiResponse<UserDto>>): String? {
        val rawError = response.errorBody()?.string().orEmpty()
        if (rawError.isBlank()) {
            return "Gagal menyimpan perubahan profil. HTTP ${response.code()}"
        }

        return runCatching {
            val json = JSONObject(rawError)
            val validationErrors = json.optJSONObject("errors")
            if (validationErrors != null && validationErrors.keys().hasNext()) {
                val key = validationErrors.keys().next()
                val firstError = validationErrors.optJSONArray(key)?.optString(0)
                if (!firstError.isNullOrBlank()) {
                    return@runCatching firstError
                }
            }

            val message = json.optString("message")
            if (message.isNotBlank()) message else "Gagal menyimpan perubahan profil. HTTP ${response.code()}"
        }.getOrElse {
            "Gagal menyimpan perubahan profil. HTTP ${response.code()}"
        }
    }
}
