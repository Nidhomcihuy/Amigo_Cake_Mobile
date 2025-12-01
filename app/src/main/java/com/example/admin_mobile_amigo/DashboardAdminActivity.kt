package com.example.admin_mobile_amigo

import android.content.Intent // Import ini tidak diperlukan jika hanya menggunakan finish()
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.admin_mobile_amigo.databinding.ActivityCustomerSignupBinding
// import com.google.android.material.textfield.TextInputEditText // Import ini tidak diperlukan dan dihapus

class CustomerSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerSignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi View Binding untuk layout Sign Up
        // PASTIKAN activity_customer_signup.xml sudah dibuat
        binding = ActivityCustomerSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Listener untuk Tombol Sign Up
        binding.btnSignUp.setOnClickListener {
            performSignUp()
        }

        // 3. Listener untuk navigasi kembali ke halaman Login
        // Menggunakan finish() secara efektif menutup Activity Sign Up.
        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun performSignUp() {
        // Mendapatkan teks dari EditText di dalam TextInputLayout
        // Kita menggunakan properti text dari TextInputEditText (etUsername, etSignUpEmail, etSignUpPassword)
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etSignUpEmail.text.toString().trim()
        val password = binding.etSignUpPassword.text.toString().trim()
        val acceptedPolicy = binding.cbAcceptPolicy.isChecked

        // Validasi Sederhana
        if (username.isEmpty()) {
            Toast.makeText(this, "Nama Pengguna harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!acceptedPolicy) {
            Toast.makeText(this, "Anda harus menyetujui kebijakan dan persyaratan.", Toast.LENGTH_SHORT).show()
            return
        }

        // --- Logika Registrasi di sini (misalnya: Panggil API ke server) ---
        // Contoh: RegisterUser(username, email, password)

        // Asumsi Registrasi Berhasil
        Toast.makeText(this, "Registrasi berhasil! Silakan Login.", Toast.LENGTH_LONG).show()

        // Setelah sukses, kembali ke halaman login (menutup Activity ini)
        finish()
    }
}