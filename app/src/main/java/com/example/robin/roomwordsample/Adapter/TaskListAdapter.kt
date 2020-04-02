package com.example.robin.roomwordsample.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.robin.roomwordsample.Data.Task
import com.example.robin.roomwordsample.Data.TaskViewModel
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.Utils.utils
import com.example.robin.roomwordsample.databinding.RowLayoutBinding

class TaskListAdapter(val context: Context?, val taskViewModel: TaskViewModel) :
    ListAdapter<Task, TaskListAdapter.ViewHolder>(TaskDiffCallbacks()) {

    var tasks = ArrayList<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskListAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context, taskViewModel)
    }

    class ViewHolder(val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Task, context: Context?, taskViewModel: TaskViewModel) {
            binding.task.text = item.word
            if (item.isComplete) {
                binding.task.setTextColor(context?.resources?.getColor(R.color.colorAccent)!!)
                binding.task.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.task.setTextColor(context?.resources?.getColor(R.color.textColor)!!)
                binding.task.paintFlags =
                    binding.task.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            binding.time.text = item.time
            binding.TxtImg.setText(item.word.toCharArray()[0] + "")
            binding.TxtImg.avatarBackgroundColor = Color.parseColor(item.color)
            binding.completionToggle.setOnCheckedChangeListener(null)
            binding.completionToggle.isChecked = item.isComplete
            binding.completionToggle.setOnCheckedChangeListener { _, isChecked ->
                toggleCompletion(item.id, isChecked, taskViewModel)
                Log.e("TAG", "" + item.id)
            }

            binding.relcard.setOnClickListener {
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.task_description_dialog, null)
                val edit: TextView = mDialogView.findViewById(R.id.edit)
                val save: Button = mDialogView.findViewById(R.id.save)
                val desc: EditText = mDialogView.findViewById(R.id.description)
                val name: EditText = mDialogView.findViewById(R.id.name)

                name.setText(item.word)
                if (item.description.isNotEmpty()) {
                    desc.setText(item.description)
                } else {
                    desc.visibility = View.GONE
                }
                mDialogView.findViewById<TextView>(R.id.time).text = item.time
                mDialogView.findViewById<TextView>(R.id.status).text =
                    if (item.isComplete) context.getString(R.string.completed) else context.getString(
                        R.string.not_completed
                    )
                mDialogView.findViewById<TextView>(R.id.status).setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (item.isComplete) R.color.colorAccent else R.color.textColor
                    )
                )
                val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.setCancelable(false)

                edit.setOnClickListener {
                    save.visibility = View.VISIBLE
                    edit.visibility = View.GONE
                    name.isEnabled = true
                    desc.isEnabled = true
                    name.requestFocus()
                    utils.showKeyboard(context)
                }
                save.setOnClickListener {
                    taskViewModel.update(item.id, name.text.toString(), desc.text.toString())
                    mAlertDialog.dismiss()
                    utils.closeKeyboard(context)
                }
                mDialogView.findViewById<Button>(R.id.close).setOnClickListener {
                    mAlertDialog.dismiss()
                    utils.closeKeyboard(context)
                }
            }

        }

        private fun toggleCompletion(id: Int, mark: Boolean, taskViewModel: TaskViewModel) {
            taskViewModel.markAsComplete(id, mark)
        }

    }

    fun removeitem(position: Int) {
        taskViewModel.delete(tasks[position])
        WorkManager.getInstance().cancelAllWorkByTag(tasks[position].tag)
        notifyItemRemoved(position)
    }

    fun restoreItem(task: Task, position: Int) {
        tasks.add(position, task)
        notifyItemChanged(position)
        taskViewModel.insert(task)
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