package com.mobile.amigomobile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.mobile.amigomobile.databinding.ActivityOrderListBinding // PASTIKAN NAMA INI SESUAI DENGAN FILE XML ANDA

class OrderListActivity : AppCompatActivity() {

    // 1. DEKLARASI VIEW BINDING
    private lateinit var binding: ActivityOrderListBinding

    // 2. DEKLARASI PROPERTY YANG HILANG (DIASUMSIKAN)
    private var currentSortType: String = "latest" // Inisialisasi properti yang hilang
    private var currentStatusFilter: String = "all" // Inisialisasi properti yang hilang


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. INFLATE LAYOUT DENGAN VIEW BINDING
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ... (Logika onCreate lainnya)

        setupListeners()
        loadOrderData() // Panggil loadOrderData setelah inisialisasi
    }

    // Fungsi bantuan untuk navigasi (menggantikan goToActivity)
    private fun <T> goToActivity(activityClass: Class<T>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun setupListeners() {
        // PERBAIKAN: Menggunakan binding untuk mengakses Views

        // 3. Listener Navigasi Bawah
        binding.navManualOrderContainer.setOnClickListener { goToActivity(OrderManualActivity::class.java) }
        binding.navHomeContainer.setOnClickListener { goToActivity(HomeActivity::class.java) }
        binding.navReviewContainer.setOnClickListener { goToActivity(OrderRecapActivity::class.java) }
        binding.navTopicContainer.setOnClickListener { goToActivity(TopicActivity::class.java) }

        // 4. Listener Filter Status
        binding.filterStatusButton.setOnClickListener {
            showStatusPopupMenu(it)
        }

        // 5. Listener Filter Sort
        binding.filterSortButton.setOnClickListener {
            showSortPopupMenu(it)
        }
    }

    // Fungsi showSortPopupMenu (ID statis)
    private fun showSortPopupMenu(view: View) {
        // PERBAIKAN: Import PopupMenu dan View sudah ditambahkan di atas
        val popup = PopupMenu(this, view)
        // Gunakan ID statis untuk menu item
        popup.menu.add(0, 1, 0, "Terbaru")
        popup.menu.add(0, 2, 0, "Terdekat (Deadline)")

        popup.setOnMenuItemClickListener { item: MenuItem -> // Menambahkan eksplisit MenuItem
            when (item.itemId) {
                1 -> {
                    currentSortType = "latest"
                    binding.filterSortButton.text = "Terbaru" // Ganti akses view
                    loadOrderData()
                    true
                }
                2 -> {
                    currentSortType = "deadline"
                    binding.filterSortButton.text = "Terdekat" // Ganti akses view
                    loadOrderData()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // Fungsi showStatusPopupMenu (ID statis)
    private fun showStatusPopupMenu(view: View) {
        // PERBAIKAN: Import PopupMenu dan View sudah ditambahkan di atas
        val popup = PopupMenu(this, view)
        // Gunakan ID statis
        popup.menu.add(0, 1, 0, "Semua")
        popup.menu.add(0, 2, 0, "Aktif (Pending)")
        popup.menu.add(0, 3, 0, "Selesai (Completed)")

        popup.setOnMenuItemClickListener { item: MenuItem -> // Menambahkan eksplisit MenuItem
            when (item.itemId) {
                1 -> {
                    currentStatusFilter = "all"
                    binding.filterStatusButton.text = "Semua" // Ganti akses view
                    loadOrderData()
                    true
                }
                2 -> {
                    currentStatusFilter = "Pending"
                    binding.filterStatusButton.text = "Aktif" // Ganti akses view
                    loadOrderData()
                    true
                }
                3 -> {
                    currentStatusFilter = "Completed"
                    binding.filterStatusButton.text = "Selesai" // Ganti akses view
                    loadOrderData()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // Deklarasikan fungsi loadOrderData() agar tidak Unresolved
    private fun loadOrderData() {
        // Implementasi logika pemuatan data pesanan berdasarkan currentSortType dan currentStatusFilter
        // Contoh: Log.d("OrderListActivity", "Loading data. Sort: $currentSortType, Status: $currentStatusFilter")
    }
}