package com.example.robin.roomwordsample


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.robin.roomwordsample.data.Task
import com.example.robin.roomwordsample.data.TaskViewModel
import com.example.robin.roomwordsample.databinding.FragmentAddTaskBinding
import com.example.robin.roomwordsample.utils.Notify
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */

@AndroidEntryPoint
class NewTaskFragment : Fragment() {

    private var checked: Boolean = false
    private var year = 0
    private var month = 0
    private var day = 0
    private var hr = 0
    private var min = 0
    private val formatter by lazy { SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.US) }
    val calendar: Calendar by lazy { Calendar.getInstance() }
    private val colors by lazy { resources.getStringArray(R.array.colors) }
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentAddTaskBinding>(
            inflater,
            R.layout.fragment_add_task, container, false
        )

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).actionBar?.title = ""

        sharedElementEnterTransition = MaterialContainerTransform()

        binding.toolbar.setNavigationIcon(R.drawable.ic_cancel)

        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.HasRemind.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                binding.EnterDateTime.visibility = View.INVISIBLE
                checked = false
            } else {
                binding.EnterDateTime.visibility = View.VISIBLE
                checked = true
            }
        }

        binding.EnterTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _, i, i1 ->
                    binding.EnterTime.setText(
                        String.format(resources.getString(R.string.selected_time), i, i1)
                    )
                    hr = i
                    min = i1
                }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.EnterDate.setOnClickListener {
            val mYear = calendar.get(Calendar.YEAR)
            val mMonth = calendar.get(Calendar.MONTH)
            val mDay = calendar.get(Calendar.DAY_OF_MONTH)
            val mDatePicker = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, selectedyear, selectedmonth, selectedday ->
                    binding.EnterDate.setText(
                        String.format(
                            resources.getString(R.string.selected_date), selectedday,
                            selectedmonth + 1, selectedyear
                        )
                    )
                    year = selectedyear
                    month = selectedmonth
                    day = selectedday
                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        binding.submit.setOnClickListener {
            if (TextUtils.isEmpty(binding.task.text)) {
                Snackbar.make(
                    binding.submit, "Task field cannot be empty",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                val task = binding.task.text.toString()
                val description = binding.description.text.toString()
                val tm = formatter.format(Date(System.currentTimeMillis()))
                var tag = " "
                if (checked) {
                    val c = Calendar.getInstance()
                    c.set(year, month, day, hr, min)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    val data = Data.Builder()
                    data.putString("Task Name", binding.task.text.toString())

                    val notifyManager = OneTimeWorkRequest.Builder(Notify::class.java)
                        .setInputData(data.build())
                        .setInitialDelay(
                            c.timeInMillis - System.currentTimeMillis(),
                            TimeUnit.MILLISECONDS
                        )
                        .addTag(c.timeInMillis.toString())
                        .build()
                    tag = c.timeInMillis.toString()
                    WorkManager.getInstance(requireContext()).enqueue(notifyManager)
                }
                taskViewModel.insert(
                    Task(
                        task, tm, tag, false, description,
                        colors[Random.nextInt(0, colors.size - 1)]
                    )
                )
                it.findNavController()
                    .navigate(NewTaskFragmentDirections.actionNewTaskFragmentToMainFragment())
            }
        }

        return binding.root
    }

}
