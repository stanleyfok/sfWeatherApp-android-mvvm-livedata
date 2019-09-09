package com.example.sfweather.features.weatherHistory.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sfweather.R
import com.example.sfweather.features.weatherHistory.WeatherHistoryViewModel
import kotlinx.android.synthetic.main.weather_history_item.view.*

class WeatherHistoryRecycleViewAdapter(
    private val viewModel: WeatherHistoryViewModel
) : RecyclerView.Adapter<WeatherHistoryRecycleViewAdapter.WeatherHistoryViewHolder>() {
    private val onClickListener: View.OnClickListener
    private val onDeleteListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val position = v.tag as Int

            this.viewModel.selectSearchHistoryAtPosition(position)
        }

        onDeleteListener = View.OnClickListener { v ->
            val position = v.tag as Int

            this.viewModel.removeSearchHistoryAtPosition(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHistoryViewHolder {
        return WeatherHistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.weather_history_item, parent, false)
        )
    }

    override fun getItemCount() = this.viewModel.getSearchHistoryCount()

    override fun onBindViewHolder(holder: WeatherHistoryViewHolder, position: Int) {
        val searchHistory = this.viewModel.getSearchHistoryAtPosition(position)

        if (searchHistory != null) {
            holder.cityNameLabel.text = searchHistory.cityName
            holder.dateLabel.text = DateFormat.format("yyyy-MM-dd hh:mm:ss", searchHistory.timestamp * 1000L).toString()
            holder.deleteBtn.visibility = if (this.viewModel.isEdit.value!!) View.VISIBLE else View.GONE

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
