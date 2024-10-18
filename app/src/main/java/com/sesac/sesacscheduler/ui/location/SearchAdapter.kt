package com.sesac.sesacscheduler.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.ui.location.model.PoiItem

class SearchAdapter(
    private val onPlaceClick: (PoiItem) -> Unit
) : ListAdapter<PoiItem,SearchAdapter.SearchViewHolder>(PlaceDiffCallback()) {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlaceName: TextView = itemView.findViewById(R.id.tvPlaceName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return SearchViewHolder(view)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val place = getItem(position)
        holder.tvPlaceName.text = place.name

        // 아이템 클릭 시 경도와 위도를 전달
        holder.itemView.setOnClickListener {
            onPlaceClick(place)
        }
    }

    class PlaceDiffCallback : DiffUtil.ItemCallback<PoiItem>() {
        override fun areItemsTheSame(oldItem: PoiItem, newItem: PoiItem): Boolean {
            return oldItem.noorLat == newItem.noorLat
        }

        override fun areContentsTheSame(oldItem: PoiItem, newItem: PoiItem): Boolean {
            return oldItem == newItem
        }
    }
}