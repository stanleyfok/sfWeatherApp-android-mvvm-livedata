package com.example.sfweather.features.weatherHistory.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfweather.R
import com.example.sfweather.models.SearchHistory
import kotlinx.android.synthetic.main.weather_history_item.view.*

class WeatherHistoryRecycleViewAdapter(
    private val onClickListener: View.OnClickListener,
    private val onDeleteListener: View.OnClickListener
) : RecyclerView.Adapter<WeatherHistoryRecycleViewAdapter.WeatherHistoryViewHolder>() {

    var isEdit:Boolean = false
    var searchHistories:List<SearchHistory>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHistoryViewHolder {
        return WeatherHistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.weather_history_item, parent, false)
        )
    }

    override fun getItemCount():Int {
        searchHistories?.let {
            return it.count()
        }

        return 0
    }

    override fun onBindViewHolder(holder: WeatherHistoryViewHolder, position: Int) {
        val searchHistory = searchHistories?.get(position)

        if (searchHistory != null) {
            holder.cityNameLabel.text = searchHistory.cityName
            holder.dateLabel.text = DateFormat.format("yyyy-MM-dd hh:mm:ss", searchHistory.timestamp * 1000L).toString()
            holder.deleteBtn.visibility = if (isEdit) View.VISIBLE else View.GONE

            with(holder.itemView) {
                tag = position
                setOnClickListener(onClickListener)
            }

            with(holder.deleteBtn) {
                tag = position

                setOnClickListener(onDeleteListener)
            }
        }
    }

    inner class WeatherHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cityNameLabel: TextView = itemView.cityNameLabel
        val dateLabel: TextView = itemView.dateLabel
        var deleteBtn: ImageView = itemView.deleteBtn
    }
}
