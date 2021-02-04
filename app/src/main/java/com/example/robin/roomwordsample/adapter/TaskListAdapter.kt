package com.example.robin.roomwordsample.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.activity.MainActivity
import com.example.robin.roomwordsample.data.Task
import com.example.robin.roomwordsample.databinding.RowLayoutBinding
import com.example.robin.roomwordsample.fragments.MainFragment
import com.example.robin.roomwordsample.fragments.TaskDetailsBottomSheetFragment
import com.example.robin.roomwordsample.utils.Utils
import java.io.Serializable

class TaskListAdapter(val listActionPerformer: ListActionPerformer<Action>) :
    ListAdapter<Task, TaskListAdapter.ViewHolder>(TaskDiffCallbacks()) {

    var tasks = ArrayList<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Task) {
            binding.task.text = item.word
            if (item.isComplete) {
                binding.task.setTextColor(binding.root.context.resources.getColor(R.color.colorAccent))
                binding.task.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.task.setTextColor(binding.root.context.resources.getColor(R.color.textColor))
                binding.task.paintFlags =
                    binding.task.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            binding.time.text = item.time
            binding.TxtImg.setText(item.word.toCharArray()[0] + "")
            binding.TxtImg.avatarBackgroundColor = Color.parseColor(item.color)
            binding.completionToggle.setOnCheckedChangeListener(null)
            binding.completionToggle.isChecked = item.isComplete
            binding.completionToggle.setOnCheckedChangeListener { _, isChecked ->
                toggleCompletion(item.id, isChecked)
                Log.e("TAG", "" + item.id)
            }

            //  Setting up the alarm icon
            if (item.tag == " "){
                binding.imgAlarm.visibility = View.INVISIBLE
            }else{
                binding.imgAlarm.visibility = View.VISIBLE
            }

            binding.relcard.setOnClickListener {
                listActionPerformer.performAction(OpenTaskDetailsBottomSheet(item))
            }

        }

        private fun toggleCompletion(id: Int, mark: Boolean) {
            listActionPerformer.performAction(ToggleTaskStatus(id, mark))
        }

    }

    fun removeTask(position: Int, context: Context) {
        listActionPerformer.performAction(RemoveTask(tasks[position]))
        WorkManager.getInstance(context).cancelAllWorkByTag(tasks[position].tag)
        notifyDataSetChanged()
    }

    fun restoreTask(task: Task, position: Int) {
        listActionPerformer.performAction(RestoreTask(task))
        notifyItemRangeChanged(position, tasks.size)
    }

    class TaskDiffCallbacks : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

}