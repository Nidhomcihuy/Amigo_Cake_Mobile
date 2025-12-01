package com.example.admin_mobile_amigo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin_mobile_amigo.databinding.ActivityFinanceManagementBinding
import java.text.NumberFormat
import java.util.*

// Model Pengeluaran
data class Expense(
    val id: Int,
    val date: String,
    val description: String,
    val amount: Double,
    val category: String
)

class FinanceManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinanceManagementBinding
    private val expensesList = mutableListOf<Expense>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinanceManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        loadFinanceData()
        loadDummyExpenses() // Memuat data dummy yang bervariasi
        setupExpensesRecyclerView()

        // Listener FAB
        binding.fabAddExpense.setOnClickListener {
            openAddExpenseForm()
        }
    }

    private fun loadFinanceData() {
        // Data Keuangan Dummy (Seharusnya diambil dari database/API)
        val income = 20000000.0
        val expense = 7500000.0
        val netProfit = income - expense

        // Format mata uang Rupiah Indonesia
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        binding.tvIncome.text = formatter.format(income)
        binding.tvExpense.text = formatter.format(expense)
        binding.tvNetProfit.text = formatter.format(netProfit)
    }

    private fun loadDummyExpenses() {
        // Hapus data lama (jika ada) dan tambahkan data dummy yang lebih bervariasi
        expensesList.clear()
        expensesList.add(Expense(1, "25 Nov 2025", "Pembayaran Listrik Toko", 350000.0, "Operasional"))
        expensesList.add(Expense(2, "20 Nov 2025", "Gaji Karyawan Produksi", 4000000.0, "Gaji"))
        expensesList.add(Expense(3, "15 Nov 2025", "Sewa Tempat Bulan November", 3000000.0, "Operasional"))
        expensesList.add(Expense(4, "10 Nov 2025", "Pembelian Terigu & Gula", 1200000.0, "Bahan Baku"))
        expensesList.add(Expense(5, "05 Nov 2025", "Perbaikan Oven", 800000.0, "Perawatan"))
        expensesList.add(Expense(6, "01 Nov 2025", "Biaya Pemasaran Digital", 500000.0, "Pemasaran"))
        expensesList.add(Expense(7, "28 Okt 2025", "Tagihan Air & Internet", 250000.0, "Operasional"))
        expensesList.add(Expense(8, "22 Okt 2025", "Pembelian Susu dan Telur", 700000.0, "Bahan Baku"))
        expensesList.add(Expense(9, "18 Okt 2025", "Ongkos Kirim Bahan Baku", 150000.0, "Transportasi"))
    }

    private fun setupExpensesRecyclerView() {
        // IMPLEMENTASI ADAPTER BARU
        // CATATAN: Pastikan ExpenseAdapter.kt sudah ada dan diimpor
        val adapter = ExpenseAdapter(expensesList) { expense ->
            // Ketika item diklik, buka form edit pengeluaran
            openEditExpenseForm(expense)
        }

        binding.recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewExpenses.adapter = adapter
    }

    private fun openAddExpenseForm() {
        Toast.makeText(this, "Membuka formulir input Pengeluaran...", Toast.LENGTH_SHORT).show()
        // TODO: Intent ke AddEditExpenseActivity
    }

    private fun openEditExpenseForm(expense: Expense) {
        Toast.makeText(this, "Mengedit Pengeluaran: ${expense.description}", Toast.LENGTH_SHORT).show()
        // TODO: Intent ke AddEditExpenseActivity dengan data expense
    }
}
// PASTIKAN: ExpenseAdapter.kt, item_expense_list.xml, dan semua file Drawable terkait sudah ada.