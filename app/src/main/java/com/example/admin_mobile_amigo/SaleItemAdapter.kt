package com.example.admin_mobile_amigo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_mobile_amigo.databinding.ItemQuickSaleInputBinding

class SaleItemAdapter(
    private val items: MutableList<SaleItem>,
    private val productList: List<ProductData>, // Daftar produk untuk AutoComplete
    private val onTotalChanged: () -> Unit,
    private val onRemoveClicked: (Int) -> Unit
) : RecyclerView.Adapter<SaleItemAdapter.SaleItemViewHolder>() {

    private val productNames = productList.map { it.name } // Hanya nama produk

    inner class SaleItemViewHolder(val binding: ItemQuickSaleInputBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Setup Dropdown/AutoComplete
            val adapterArray = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_dropdown_item_1line,
                productNames
            )
            binding.autoCompleteProduct.setAdapter(adapterArray)

            // Listener ketika item dipilih dari dropdown
            binding.autoCompleteProduct.setOnItemClickListener { parent, view, position, id ->
                val selectedName = parent.getItemAtPosition(position).toString()
                val product = productList.find { it.name == selectedName }
                val currentItem = items[adapterPosition]

                if (product != null) {
                    currentItem.productId = product.id
                    currentItem.productName = product.name
                    currentItem.pricePerUnit = product.price
                    calculateSubtotal(currentItem)
                }
            }

            // Listener perubahan Kuantitas
            binding.etQuantity.doAfterTextChanged { editable ->
                val qty = editable.toString().toDoubleOrNull() ?: 0.0
                val currentItem = items[adapterPosition]
                currentItem.quantity = qty
                calculateSubtotal(currentItem)
            }

            // Listener Hapus Item
            binding.btnRemoveItem.setOnClickListener {
                onRemoveClicked(adapterPosition)
            }
        }

        private fun calculateSubtotal(item: SaleItem) {
            item.subtotal = item.quantity * item.pricePerUnit
            onTotalChanged()
        }

        fun bind(item: SaleItem) {
            // Set data ke view
            binding.autoCompleteProduct.setText(item.productName, false) // false agar tidak memicu click listener
            binding.etQuantity.setText(if (item.quantity > 0) item.quantity.toString() else "")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleItemViewHolder {
        val binding = ItemQuickSaleInputBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}