package com.example.robin.roomwordsample

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.robin.roomwordsample.Data.Word
import com.example.robin.roomwordsample.Data.WordViewModel
import com.github.abdularis.civ.AvatarImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class WordListAdapter internal constructor(
    context: Context, v: FloatingActionButton
) : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var words = emptyList<Word>() // Cached copy of words
    private lateinit var ctx: Context
    private lateinit var view: View
    private val parentView: FloatingActionButton = v
    private lateinit var wordViewModel: WordViewModel
    private val colors: IntArray = intArrayOf( Color.rgb(220,85,31), Color.rgb(17,94,231), Color.rgb(9,187,69), Color.rgb(105,19, 191), Color.rgb(191, 27, 19))

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.task)
        val timeItemView: TextView = itemView.findViewById(R.id.time)
        val avImageView: AvatarImageView = itemView.findViewById(R.id.TxtImg)
        val relcard: RelativeLayout = itemView.findViewById(R.id.relcard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.row_layout, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = words[position]
        holder.wordItemView.text = current.word
        holder.timeItemView.text = current.time
        holder.avImageView.setText(current.word.toCharArray()[0]+"")
        holder.avImageView.avatarBackgroundColor = colors[Random.nextInt(0,5)]
        holder.relcard.setOnLongClickListener{
            wordViewModel.delete(current)
            val snackbar = Snackbar.make(parentView, "Deleted Todo", Snackbar.LENGTH_SHORT)
            snackbar.show()
            WorkManager.getInstance().cancelAllWorkByTag(current.tag)
            return@setOnLongClickListener true
        }
    }

    internal fun setWords(words: List<Word>, ctx: Context, wordViewModel: WordViewModel, view: View) {
        this.words = words
        this.ctx = ctx
        this.wordViewModel = wordViewModel
        this.view = view
        notifyDataSetChanged()
    }

    override fun getItemCount() = words.size
}