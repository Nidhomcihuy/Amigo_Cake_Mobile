package com.example.admin_mobile_amigo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin_mobile_amigo.databinding.ItemProductListBinding
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private val products: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    data class Product(
        val id: Int,
        val name: String,
        val price: Double,
        val productionTimeHours: Int,
        val category: String,
        val isActive: Boolean
    )

    inner class ProductViewHolder(private val binding: ItemProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            // Gambar produk menggunakan placeholder default untuk saat ini
            binding.ivProductImage.setImageResource(R.drawable.ic_cake_placeholder)
            // Atau jika ada URL gambar, gunakan library seperti Glide/Picasso:
            // Glide.with(binding.root.context).load(product.imageUrl).into(binding.ivProductImage)

            binding.tvProductName.text = product.name

            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.tvProductPrice.text = formatter.format(product.price)

            binding.tvProductionTime.text = "${product.productionTimeHours} Jam Produksi"

            // Menentukan Tampilan Status dengan Drawable baru
            if (product.isActive) {
                binding.tvProductStatus.text = "AKTIF"
                binding.tvProductStatus.setBackgroundResource(R.drawable.bg_status_active)
            } else {
                binding.tvProductStatus.text = "NON-AKTIF"
                binding.tvProductStatus.setBackgroundResource(R.drawable.bg_status_inactive) // Menggunakan drawable baru
            }

            binding.root.setOnClickListener {
                onClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}