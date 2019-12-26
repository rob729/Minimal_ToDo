package com.example.robin.roomwordsample.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.robin.roomwordsample.Data.Word
import com.example.robin.roomwordsample.Data.WordViewModel
import com.example.robin.roomwordsample.R
import com.github.abdularis.civ.AvatarImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class WordListAdapter internal constructor(
    context: Context?, v: FloatingActionButton
) : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var words = ArrayList<Word>() // Cached copy of words
    private lateinit var ctx: Context
    private lateinit var view: View
    private val parentView: FloatingActionButton = v
    private lateinit var wordViewModel: WordViewModel
    private val colors: IntArray = intArrayOf(
        Color.rgb(244, 81, 30),
        Color.rgb(17, 94, 231),
        Color.rgb(9, 187, 69),
        Color.rgb(123, 31, 162),
        Color.rgb(191, 27, 19),
        Color.rgb(0, 121, 107),
        Color.rgb(255, 143, 0),
        Color.rgb(216, 27, 96)
    )

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

        if(current.isComplete){
            holder.wordItemView.setTextColor(ctx.resources.getColor(R.color.colorAccent))
            holder.wordItemView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
        else{
            holder.wordItemView.setTextColor(ctx.resources.getColor(R.color.textColor))
            holder.wordItemView.paintFlags = holder.wordItemView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.timeItemView.text = current.time
        holder.avImageView.setText(current.word.toCharArray()[0] + "")
        holder.avImageView.avatarBackgroundColor = colors[Random.nextInt(0, 8)]
        holder.completionToggle.isChecked = current.isComplete
        holder.completionToggle.setOnCheckedChangeListener { _, isChecked ->
            toggleCompletion(current.id, isChecked)
        }
    }

    internal fun setWords(
        words: List<Word>,
        ctx: Context?,
        wordViewModel: WordViewModel,
        view: View
    ) {
        this.words = words as ArrayList<Word>
        this.ctx = ctx!!
        this.wordViewModel = wordViewModel
        this.view = view
        notifyDataSetChanged()
    }

    override fun getItemCount() = words.size

    fun getList() = words

    private fun toggleCompletion(id: Int, mark: Boolean) {
        wordViewModel.markAsComplete(id, mark)
    }

    fun removeitem(position: Int) {
        wordViewModel.delete(words[position])
        WorkManager.getInstance().cancelAllWorkByTag(words[position].tag)
        notifyItemRemoved(position)
    }

    fun restoreItem(word: Word, position: Int) {
        words.add(position, word)
        notifyItemChanged(position)
        wordViewModel.insert(word)
    }
}

