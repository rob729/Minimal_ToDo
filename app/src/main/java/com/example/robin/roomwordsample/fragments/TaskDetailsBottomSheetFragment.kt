package com.example.robin.roomwordsample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.data.Task
import com.example.robin.roomwordsample.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.task_description_dialog.*

class TaskDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var mListener: ItemClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_description_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val task = arguments?.getParcelable<Task>(TASK_DETAILS)
        task?.let { setUpViews(it) }
    }

    private fun setUpViews(task: Task) {
        name.setText(task.word)
        if (task.description.isNotEmpty()) {
            description.setText(task.description)
        } else {
            description.visibility = View.GONE
        }
        time.text = task.time
        status.text =
            if (task.isComplete)
                requireContext().getString(R.string.completed)
            else
                requireContext().getString(R.string.not_completed)
        status.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (task.isComplete) R.color.colorAccent else R.color.textColor
            )
        )
        edit.setOnClickListener {
            save.visibility = View.VISIBLE
            edit.visibility = View.GONE
            name.isEnabled = true
            description.isEnabled = true
            name.requestFocus()
            Utils.showKeyboard(requireContext())
        }

        save.setOnClickListener {
            mListener.onTaskUpdate(task.id, name.text.toString(), description.text.toString())
            Utils.closeKeyboard(requireContext())
            dismiss()
        }
    }

    interface ItemClickListener {
        fun onTaskUpdate(
            id: Int,
            name: String,
            descp: String
        )
    }

    companion object {
        const val TASK_DETAILS = "task_details"

        @JvmStatic
        fun newInstance(
            bundle: Bundle,
            mListener: ItemClickListener
        ): TaskDetailsBottomSheetFragment {
            val fragment = TaskDetailsBottomSheetFragment()
            fragment.arguments = bundle
            fragment.mListener = mListener
            return fragment
        }
    }
}