package com.example.stations.ui.distance.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stations.databinding.StationListItemBinding
import com.example.stations.domain.models.Station

class StationsListAdapter(private val clickListener: StationListener) :
    ListAdapter<Station, StationsListAdapter.StationViewHolder>(StationDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        return StationViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = getItem(position)
        holder.bind(station, clickListener)
    }

    companion object StationDiffCallback : DiffUtil.ItemCallback<Station>() {
        override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem == newItem
        }
    }

    class StationViewHolder(private var binding: StationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(station: Station, clickListener: StationListener) {
            binding.apply {
                binding.station = station
                binding.onClickListener = clickListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): StationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StationListItemBinding.inflate(layoutInflater, parent, false)
                return StationViewHolder(binding)
            }
        }
    }

    class StationListener(val clickListener: (station: Station) -> Unit) {
        fun onClick(station: Station) = clickListener(station)
    }
}