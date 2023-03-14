package com.example.bookingapp.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingapp.data.models.Establishment
import com.example.bookingapp.data.models.Reservation
import com.example.bookingapp.databinding.FragmentHomeEstablishmentItemBinding
import com.example.bookingapp.databinding.FragmentReservationHistoryItemBinding
import com.google.firebase.Timestamp
import java.text.Format

class ReservationAdapter (
    val reservations: List<Reservation>,
    private val onReservationClicked: (
        reservation: Reservation,
    ) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentReservationHistoryItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = reservations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(reservations[position])
        holder.itemView.setOnClickListener {

        }
    }

    class ViewHolder(private val binding: FragmentReservationHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reservation: Reservation) {
            binding.reservationName.text = reservation.establishment.name
            var reservationDate =
                DateFormat.format("dd.MM.yyyy HH:mm", reservation.dateTime.toDate())
            binding.reservationDate.text = reservationDate
            binding.reservationAddress.text = reservation.establishment.address
            binding.tableID.text = reservation.tableID.toString()
        }
    }
}