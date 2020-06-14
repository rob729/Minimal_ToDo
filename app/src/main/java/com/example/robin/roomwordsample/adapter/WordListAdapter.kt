package com.example.robin.roomwordsample.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.robin.roomwordsample.data.Task
import com.example.robin.roomwordsample.data.TaskViewModel
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.utils.Utils
import com.github.abdularis.civ.AvatarImageView

class WordListAdapter internal constructor(
    context: Context?) : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var words = ArrayList<Task>() // Cached copy of words
    private lateinit var ctx: Context
    private lateinit var view: View
    private lateinit var taskViewModel: TaskViewModel

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.task)
        val timeItemView: TextView = itemView.findViewById(R.id.time)
        val avImageView: AvatarImageView = itemView.findViewById(R.id.TxtImg)
        val relcard: RelativeLayout = itemView.findViewById(R.id.relcard)
        val completionToggle: CheckBox = itemView.findViewById(R.id.completionToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.row_layout, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = words[position]
        holder.wordItemView.text = current.word

        if (current.isComplete) {
            holder.wordItemView.setTextColor(ctx.resources.getColor(R.color.colorAccent))
            holder.wordItemView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.wordItemView.setTextColor(ctx.resources.getColor(R.color.textColor))
            holder.wordItemView.paintFlags =
                holder.wordItemView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.timeItemView.text = current.time
        holder.avImageView.setText(current.word.toCharArray()[0] + "")
        holder.avImageView.avatarBackgroundColor = Color.parseColor(current.color)
        holder.completionToggle.isChecked = current.isComplete
        holder.completionToggle.setOnCheckedChangeListener { _, isChecked ->
            toggleCompletion(current.id, isChecked)
        }

        holder.relcard.setOnClickListener {
            val mDialogView =
                LayoutInflater.from(ctx).inflate(R.layout.task_description_dialog, null)
            val edit: TextView = mDialogView.findViewById(R.id.edit)
            val save: Button = mDialogView.findViewById(R.id.save)
            val desc: EditText = mDialogView.findViewById(R.id.description)
            val name: EditText = mDialogView.findViewById(R.id.name)


            name.setText(current.word)
            if (current.description.isNotEmpty()) {
                desc.setText(current.description)
            } else {
                desc.visibility = View.GONE
            }
            mDialogView.findViewById<TextView>(R.id.time).text = current.time
            mDialogView.findViewById<TextView>(R.id.status).text =
                if (current.isComplete) ctx.getString(R.string.completed) else ctx.getString(R.string.not_completed)
            mDialogView.findViewById<TextView>(R.id.status).setTextColor(
                ContextCompat.getColor(
                    ctx,
                    if (current.isComplete) R.color.colorAccent else R.color.textColor
                )
            )
            val mBuilder = AlertDialog.Builder(ctx).setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            //mAlertDialog.setCancelable(false)

            edit.setOnClickListener {
                save.visibility = View.VISIBLE
                edit.visibility = View.GONE
                name.isEnabled = true
                desc.isEnabled = true
                name.requestFocus()
                Utils.showKeyboard(ctx)
            }
            save.setOnClickListener {
                this.taskViewModel.update(current.id, name.text.toString(), desc.text.toString())
                mAlertDialog.dismiss()
                Utils.closeKeyboard(ctx)
            }

            mDialogView.findViewById<Button>(R.id.close).setOnClickListener {
                mAlertDialog.dismiss()
                Utils.closeKeyboard(ctx)
            }
        }
    }

    internal fun setWords(
        tasks: List<Task>,
        ctx: Context?,
        taskViewModel: TaskViewModel,
        view: View
    ) {
        this.words = tasks as ArrayList<Task>
        this.ctx = ctx!!
        this.taskViewModel = taskViewModel
        this.view = view
        notifyDataSetChanged()
    }

    override fun getItemCount() = words.size

    fun getList() = words

    private fun toggleCompletion(id: Int, mark: Boolean) {
        taskViewModel.markAsComplete(id, mark)
    }

    fun removeitem(position: Int) {
        taskViewModel.delete(words[position])
        WorkManager.getInstance().cancelAllWorkByTag(words[position].tag)
        notifyItemRemoved(position)
    }

    fun restoreItem(task: Task, position: Int) {
        words.add(position, task)
        notifyItemChanged(position)
        taskViewModel.insert(task)
    }
}

