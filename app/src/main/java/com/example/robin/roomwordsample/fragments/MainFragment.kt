package com.example.robin.roomwordsample.fragments


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.robin.roomwordsample.Activity.MainActivity
import com.example.robin.roomwordsample.Adapter.TaskListAdapter
import com.example.robin.roomwordsample.Data.Task
import com.example.robin.roomwordsample.Data.TaskViewModel
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.Utils.SwipeToDeleteCallback
import com.example.robin.roomwordsample.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar


class MainFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    lateinit var binding: FragmentMainBinding

    private val mPrefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    val editor: SharedPreferences.Editor by lazy {
        mPrefs.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val nightMode = mPrefs.getBoolean("nightMode", false)
        if(nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).actionBar?.setDisplayShowTitleEnabled(false)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val taskListAdapter = TaskListAdapter(requireContext(), taskViewModel)
        binding.recyclerview.adapter = taskListAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        taskViewModel.allWords.observe(viewLifecycleOwner, Observer { tasks ->
            // Update the cached copy of the words in the adapter.
            if (tasks.isEmpty()) {
                binding.emptyPh.visibility = View.VISIBLE
            } else {
                binding.emptyPh.visibility = View.INVISIBLE
            }

            taskListAdapter.submitList(tasks)
            taskListAdapter.tasks = tasks as ArrayList<Task>
        })

        enableSwipeToDeleteAndUndo(taskListAdapter)

        binding.fab.setOnClickListener {
            val extras = FragmentNavigatorExtras(it to "shared_container")
            it.findNavController()
                .navigate(R.id.action_mainFragment_to_newTaskFragment, null, null, extras)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.nightMode -> {
                val nightMode = mPrefs.getBoolean("nightMode", false)
                val mode =
                    if (nightMode) {
                        AppCompatDelegate.MODE_NIGHT_NO
                    } else {
                        AppCompatDelegate.MODE_NIGHT_YES
                    }

                // Change UI Mode
                editor.putBoolean("nightMode", !nightMode)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(mode)
                true

            }

            else -> view?.let { Navigation.findNavController(it) }?.let {
                NavigationUI.onNavDestinationSelected(
                    item,
                    it
                )
            }!! || super.onOptionsItemSelected(item)
        }
    }

    private fun enableSwipeToDeleteAndUndo(taskListAdapter: TaskListAdapter) {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val item = taskListAdapter.tasks[position]
                taskListAdapter.removeitem(position)
                Snackbar.make(
                        binding.coodLayout, "Item was removed from the list.",
                        Snackbar.LENGTH_LONG
                    )
                    .setActionTextColor(Color.rgb(17, 122, 101))
                    .setAction("UNDO") {
                        taskListAdapter.restoreItem(item, position)
                        binding.recyclerview.scrollToPosition(position)
                    }.show()
            }
        }

        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(binding.recyclerview)
    }
}

