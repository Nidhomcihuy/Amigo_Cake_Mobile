package com.example.admin_mobile_amigo

import android.os.Bundle
import android.view.View // Wajib diimpor untuk menggunakan View.GONE dan View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin_mobile_amigo.databinding.ActivityProductManagementBinding
import com.example.admin_mobile_amigo.ProductAdapter.Product // Import Product Model dari Adapter

class ProductManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductManagementBinding
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        loadDummyProducts()
        setupRecyclerView()

        // Floating Action Button Listener
        binding.fabAddProduct.setOnClickListener {
            openAddProductDialog()
        }
    }

    private fun loadDummyProducts() {
        // Hapus kode loadDummyProducts lama, ganti dengan ini:
        // Coba ganti dengan 'productList.clear()' di sini untuk menguji tampilan kosong
        productList.add(Product(1, "Brownies Choco Fudge", 75000.0, 3, "Brownies", true))
        productList.add(Product(2, "Red Velvet Tart Premium", 250000.0, 6, "Tart", true))
        productList.add(Product(3, "Cookies Oatmeal", 35000.0, 2, "Cookies", false))
        productList.add(Product(4, "Kue Ulang Tahun Custom", 180000.0, 7, "Tart", true))
    }

    private fun setupRecyclerView() {
        val adapter = ProductAdapter(productList) { product ->
            // Ketika item diklik, buka form edit
            Toast.makeText(this, "Mengedit: ${product.name}", Toast.LENGTH_SHORT).show()
            openEditProductForm(product)
        }

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProducts.adapter = adapter

        // PANGGIL FUNGSI EMPTY STATE DI SINI
        checkEmptyState(productList.isEmpty())
    }

    // FUNGSI BARU UNTUK MENGELOLA TAMPILAN EMPTY STATE
    private fun checkEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            // Jika list kosong, tampilkan Empty State dan sembunyikan RecyclerView
            binding.recyclerViewProducts.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            // Jika ada data, tampilkan RecyclerView dan sembunyikan Empty State
            binding.recyclerViewProducts.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
        }
    }

    private fun openAddProductDialog() {
        Toast.makeText(this, "Membuka formulir Tambah Produk...", Toast.LENGTH_SHORT).show()
        // PENTING: Jika produk berhasil ditambahkan, Anda perlu memuat ulang list dan memanggil checkEmptyState() lagi!
    }

    private fun openEditProductForm(product: Product) {
        // Implementasi Intent untuk membuka form edit
    }
}