package com.mobile.amigomobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.TextView
import android.widget.Toast // Untuk menampilkan pesan pop-up
import com.google.android.material.textfield.TextInputEditText

// Asumsi Anda memiliki LoginActivity.kt yang sudah dibuat
// import com.mobile.amigomobile.LoginActivity

class RegisterActivity : AppCompatActivity() {

    // Deklarasi variabel untuk komponen UI
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignUp: TextView
    private lateinit var tvSignIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Inisialisasi (Binding) komponen dari Layout XML
        initializeViews()

        // 2. Listener untuk tombol Sign Up
        btnSignUp.setOnClickListener {
            handleRegistration()
        }

        // 3. Listener untuk kembali ke Login
        tvSignIn.setOnClickListener {
            // Pindah ke Login Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Fungsi untuk menghubungkan variabel dengan ID di XML
     */
    private fun initializeViews() {
        // Menggunakan ID yang sudah kita sepakati di XML sebelumnya
        etUsername = findViewById(R.id.etRegisterUsername)
        etPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnSignUp = findViewById(R.id.buttonSignUp)
        tvSignIn = findViewById(R.id.tvSignIn)
    }

    /**
     * Fungsi untuk memproses pendaftaran dan validasi input
     */
    private fun handleRegistration() {
        // Ambil data dari input
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // A. Validasi Input Kosong
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi.", Toast.LENGTH_SHORT).show()
            return // Hentikan proses jika ada yang kosong
        }

        // B. Validasi Konfirmasi Password
        if (password != confirmPassword) {
            Toast.makeText(this, "Konfirmasi Password tidak cocok.", Toast.LENGTH_SHORT).show()
            etConfirmPassword.error = "Password tidak cocok" // Tampilkan error visual
            return // Hentikan proses
        }

        // C. Validasi Panjang Password (Contoh: minimal 6 karakter)
        if (password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter.", Toast.LENGTH_SHORT).show()
            etPassword.error = "Minimal 6 karakter"
            return // Hentikan proses
        }

        // Jika semua validasi berhasil:
        Toast.makeText(this, "Data Valid. Melanjutkan ke proses Registrasi...", Toast.LENGTH_LONG).show()

        // TODO: Lanjutkan ke Logika Registrasi Firebase atau Backend di sini!
        // Contoh: registerUser(username, password)
    }
}