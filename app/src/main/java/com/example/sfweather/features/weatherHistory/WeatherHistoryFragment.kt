package com.example.sfweather.features.weatherHistory

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.sfweather.MainActivity
import com.example.sfweather.R
import com.example.sfweather.features.weatherHistory.adapters.WeatherHistoryRecycleViewAdapter
import com.example.sfweather.models.SearchHistory
import com.example.sfweather.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_weather_history.*
import kotlinx.android.synthetic.main.fragment_weather_history.view.*

class WeatherHistoryFragment : Fragment(), View.OnClickListener {
    private lateinit var viewModel: WeatherHistoryViewModel
    private lateinit var recycleViewAdapter: WeatherHistoryRecycleViewAdapter

    //region life cycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_weather_history, container, false)

        this.viewModel = ViewModelProviders.of(this).get(WeatherHistoryViewModel::class.java)

        this.viewModel.searchHistories.observe(this, Observer {
            this.reloadRecyclerView(it)
        })

        this.viewModel.selectedSearchHistory.observe(this, Observer {
            this.onSelectSearchHistory(it)
        })

        this.viewModel.isEdit.observe(this, Observer {
            this.updateEditMode(it)
        })

        this.recycleViewAdapter = WeatherHistoryRecycleViewAdapter(this.viewModel)
        this.setupRecyclerView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.editButton.setOnClickListener(this)

        this.viewModel.loadSearchHistories()
    }
    //endregion

    //region private method
    private fun setupRecyclerView(view: View) {
        view.weatherHistoryRecycleView.apply {
            setHasFixedSize(true)
            adapter = recycleViewAdapter
        }

        val swipeHandler = object : SwipeToDeleteCallback(activity!!.applicationContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.removeSearchHistoryAtPosition(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(view.weatherHistoryRecycleView)
    }
    //endregion

    //region ui methods
    private fun reloadRecyclerView(searchHistories: List<SearchHistory>) {
        recycleViewAdapter.notifyDataSetChanged()

        val count = searchHistories.count()

        if (count == 0) {
            this.emptyResultText.visibility = View.VISIBLE
            this.weatherHistoryRecycleView.visibility = View.GONE
        } else {
            this.emptyResultText.visibility = View.GONE
            this.weatherHistoryRecycleView.visibility = View.VISIBLE
        }
    }

    private fun onSelectSearchHistory(searchHistory: SearchHistory) {
        targetFragment?.let {
            val intent = Intent(context, WeatherHistoryFragment::class.java)
            intent.putExtra("cityId", searchHistory.cityId);

            it.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent);
        }

        (activity as MainActivity).popStack()
    }

    private fun updateEditMode(bool: Boolean) {
        if (bool) {
            this.editButton.text = resources.getString(R.string.WEATHER_HISTORY_TOOLBAR_BUTTON_DONE)
        } else {
            this.editButton.text = resources.getString(R.string.WEATHER_HISTORY_TOOLBAR_BUTTON_EDIT)
        }

        recycleViewAdapter.notifyDataSetChanged()
    }
    //endregion

    //region click listener
    override fun onClick(view: View) {
        when (view.id) {
            R.id.editButton -> {
                this.viewModel.toggleIsEdit()
            }
        }
    }
    //endregion
}
