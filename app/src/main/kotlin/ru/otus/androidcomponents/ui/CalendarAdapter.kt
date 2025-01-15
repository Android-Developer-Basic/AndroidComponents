package ru.otus.androidcomponents.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.otus.androidcomponents.data.CalendarData
import ru.otus.androidcomponents.databinding.ItemCalendarBinding

class CalendarAdapter : ListAdapter<CalendarData, CalendarAdapter.CalendarHolder>(CalendarDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CalendarHolder(
        ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CalendarHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarHolder(private val binding: ItemCalendarBinding) : ViewHolder(binding.root) {
        fun bind(calendar: CalendarData) = with(binding) {
            color.setBackgroundColor(calendar.color)
            displayName.text = calendar.displayName
            account.text = calendar.account
        }
    }
}

private object CalendarDiff : DiffUtil.ItemCallback<CalendarData>() {
    override fun areItemsTheSame(oldItem: CalendarData, newItem: CalendarData): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: CalendarData, newItem: CalendarData): Boolean {
        return oldItem == newItem
    }
}