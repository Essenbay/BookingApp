package com.example.bookingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.data.models.ReservationWithEstablishment
import com.example.bookingapp.databinding.FragmentReservationHistoryItemBinding
import com.example.bookingapp.util.formatDate

class ReservationAdapter(
    private val list: List<ReservationWithEstablishment>,
    private val onReservationClicked: (
        reservation: Reservation,
    ) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentReservationHistoryItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onReservationClicked(list[position].reservation)
        }
    }

    class ViewHolder(private val binding: FragmentReservationHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pair: ReservationWithEstablishment) {
            binding.reservationName.text = pair.establishment.name
            val reservationDate =
                formatDate(pair.reservation.fromDate.toDate())
            binding.reservationDate.text = reservationDate
            binding.reservationAddress.text = pair.establishment.address
            binding.tableID.text = pair.reservation.tableID.toString()
        }
    }
}