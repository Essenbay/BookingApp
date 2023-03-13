package com.example.bookingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.databinding.FragmentHomeEstablishmentItemBinding
import com.google.firebase.Timestamp
import java.util.*

class EstablishmentsAdapter(
    private val establishments: List<Establishment>,
    private val onEstablishmentClicked: (
        establishment: Establishment,
        tableID: Int,
        date: Timestamp
    ) -> Unit
) : RecyclerView.Adapter<EstablishmentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentHomeEstablishmentItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = establishments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(establishments[position])
        holder.itemView.setOnClickListener {
            val tableID = 1
            val date = Timestamp.now()
            onEstablishmentClicked(establishments[position], tableID, date)
        }
    }

    class ViewHolder(private val binding: FragmentHomeEstablishmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(establishment: Establishment) {
            binding.establishmentName.text = establishment.name
            binding.establishmentDescription.text = establishment.description
            binding.establishmentAddress.text = establishment.address
        }
    }
}