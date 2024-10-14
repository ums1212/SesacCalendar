package com.sesac.sesacscheduler.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sesac.sesacscheduler.R
import com.sesac.sesacscheduler.ui.location.model.PoiItem

class SearchAdapter(
    private val places: MutableList<PoiItem>,
    private val onPlaceClick: (PoiItem) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    // ViewHolder 클래스 정의
    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlaceName: TextView = itemView.findViewById(R.id.tvPlaceName)
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return SearchViewHolder(view)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val place = places[position]
        holder.tvPlaceName.text = place.name

        // 아이템 클릭 시 경도와 위도를 전달
        holder.itemView.setOnClickListener {
            onPlaceClick(place)
        }
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int {
        return places.size
    }
}