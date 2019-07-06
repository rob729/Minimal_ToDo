package com.example.robin.roomwordsample.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.robin.roomwordsample.Adapter.WordListAdapter
import com.example.robin.roomwordsample.Data.WordViewModel
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.Utils.SwipeToDeleteCallback
import com.example.robin.roomwordsample.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar


class MainFragment : Fragment() {

    private lateinit var wordViewModel: WordViewModel
    lateinit var binding: FragmentMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main, container, false
        )

        val wordListAdapter = WordListAdapter(context, binding.fab)
        binding.recyclerview.adapter = wordListAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)

        wordViewModel.allWords.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            if (words.isEmpty()) {
                binding.emptyPh.visibility = View.VISIBLE
            } else {
                binding.emptyPh.visibility = View.INVISIBLE
            }
            words?.let { wordListAdapter.setWords(it, context, wordViewModel, binding.recyclerview) }
        })

        enableSwipeToDeleteAndUndo(wordListAdapter)

        binding.fab.setOnClickListener {
            it.findNavController().navigate(MainFragmentDirections.actionMainFragmentToNewToDoFragment())
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return view?.let { Navigation.findNavController(it) }?.let {
            NavigationUI.onNavDestinationSelected(
                item,
                it
            )
        }!! || super.onOptionsItemSelected(item)
    }

    private fun enableSwipeToDeleteAndUndo(wordListAdapter: WordListAdapter) {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {


                val position = viewHolder.adapterPosition
                val item = wordListAdapter.getList()[position]

                wordListAdapter.removeitem(position)


                val snackbar = Snackbar
                    .make(binding.coodLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG)
                snackbar.setAction("UNDO") {
                    wordListAdapter.restoreItem(item, position)
                    binding.recyclerview.scrollToPosition(position)
                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()

            }
        }

        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(binding.recyclerview)
    }


}
