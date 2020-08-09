package com.example.robin.roomwordsample.adapter

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
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.data.Task
import com.example.robin.roomwordsample.databinding.RowLayoutBinding
import com.example.robin.roomwordsample.utils.Utils

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

            binding.relcard.setOnClickListener {
                val mDialogView =
                    LayoutInflater.from(binding.root.context)
                        .inflate(R.layout.task_description_dialog, null)
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
                    if (item.isComplete) binding.root.context.getString(R.string.completed) else binding.root.context.getString(
                        R.string.not_completed
                    )
                mDialogView.findViewById<TextView>(R.id.status).setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        if (item.isComplete) R.color.colorAccent else R.color.textColor
                    )
                )
                val mBuilder = AlertDialog.Builder(binding.root.context).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.setCancelable(false)

                edit.setOnClickListener {
                    save.visibility = View.VISIBLE
                    edit.visibility = View.GONE
                    name.isEnabled = true
                    desc.isEnabled = true
                    name.requestFocus()
                    Utils.showKeyboard(binding.root.context)
                }
                save.setOnClickListener {
                    listActionPerformer.performAction(
                        UpdateTask(
                            item.id,
                            name.text.toString(),
                            desc.text.toString()
                        )
                    )
                    mAlertDialog.dismiss()
                    Utils.closeKeyboard(binding.root.context)
                }
                mDialogView.findViewById<Button>(R.id.close).setOnClickListener {
                    mAlertDialog.dismiss()
                    Utils.closeKeyboard(binding.root.context)
                }
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