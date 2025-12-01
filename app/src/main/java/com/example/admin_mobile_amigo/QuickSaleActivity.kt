package com.example.admin_mobile_amigo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin_mobile_amigo.databinding.ActivityQuickSaleBinding
import java.text.NumberFormat
import java.util.*

// Model Data untuk Item Penjualan Cepat (diperlukan untuk Adapter)
data class SaleItem(
    var productId: Int? = null,
    var productName: String = "",
    var quantity: Double = 1.0,
    var pricePerUnit: Double = 0.0,
    var subtotal: Double = 0.0
)

// Data Produk Dummy (seharusnya diambil dari database/API)
// Kita gunakan data ini untuk mengisi AutoCompleteTextView
data class ProductData(val id: Int, val name: String, val price: Double)
val dummyProducts = listOf(
    ProductData(1, "Red Velvet Tart Premium", 250000.0),
    ProductData(2, "Cheesecake Mini", 35000.0),
    ProductData(3, "Brownies Panggang Kotak", 120000.0),
    ProductData(4, "Kue Sus Buah (per box)", 65000.0)
)

class QuickSaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuickSaleBinding
    private lateinit var adapter: SaleItemAdapter
    private val saleItemsList = mutableListOf<SaleItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.appBarLayout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.appBarLayout.toolbar.setNavigationOnClickListener { finish() }

        // Inisialisasi daftar item dengan satu baris kosong
        saleItemsList.add(SaleItem())

        setupRecyclerView()
        setupListeners()
        updateGrandTotal()
    }

    private fun setupRecyclerView() {
        adapter = SaleItemAdapter(
            saleItemsList,
            dummyProducts, // Teruskan data produk untuk AutoComplete
            onTotalChanged = { updateGrandTotal() },
            onRemoveClicked = { position -> removeItem(position) }
        )

        binding.recyclerViewSaleItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSaleItems.adapter = adapter
    }

    private fun setupListeners() {
        // Listener untuk tombol Tambah Item
        binding.btnAddItem.setOnClickListener {
            addItem()
        }

        // Listener untuk tombol Selesaikan Transaksi
        binding.btnCompleteSale.setOnClickListener {
            completeSale()
        }
    }

    private fun addItem() {
        saleItemsList.add(SaleItem())
        adapter.notifyItemInserted(saleItemsList.size - 1)
        binding.recyclerViewSaleItems.scrollToPosition(saleItemsList.size - 1)
    }

    private fun removeItem(position: Int) {
        if (saleItemsList.size > 1) {
            saleItemsList.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, saleItemsList.size)
            updateGrandTotal()
        } else {
            Toast.makeText(this, "Minimal harus ada satu item penjualan.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk memperbarui total pembayaran
    private fun updateGrandTotal() {
        val total = saleItemsList.sumOf { it.subtotal }
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        binding.tvGrandTotal.text = formatter.format(total)
    }

    private fun completeSale() {
        // VALIDASI: Pastikan ada item dan semua produk terpilih
        if (saleItemsList.any { it.productId == null || it.quantity <= 0 }) {
            Toast.makeText(this, "Pastikan semua item produk sudah terpilih dan kuantitas valid.", Toast.LENGTH_LONG).show()
            return
        }

        val totalTransaksi = saleItemsList.sumOf { it.subtotal }
        // TODO: Simpan data ke database/API (saleItemsList, totalTransaksi, Catatan)

        Toast.makeText(this, "Transaksi Offline Rp ${totalTransaksi} berhasil dicatat!", Toast.LENGTH_LONG).show()

        // Bersihkan form setelah sukses
        saleItemsList.clear()
        saleItemsList.add(SaleItem())
        adapter.notifyDataSetChanged()
        binding.etNotes.setText("")
        updateGrandTotal()
    }
}