package com.example.admin_mobile_amigo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_mobile_amigo.databinding.ItemExpenseListBinding
import java.text.NumberFormat
import java.util.*

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // Expense Model (diimpor dari FinanceManagementActivity)
    // data class Expense(...)

    inner class ExpenseViewHolder(private val binding: ItemExpenseListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvExpenseDescription.text = expense.description
            binding.tvExpenseDate.text = expense.date
            binding.tvExpenseCategory.text = expense.category.toUpperCase(Locale.getDefault())

            // Format Harga ke Rupiah
            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvExpenseAmount.text = formatter.format(expense.amount)

            // Set Listener untuk mengedit pengeluaran
            binding.root.setOnClickListener {
                onClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size
}