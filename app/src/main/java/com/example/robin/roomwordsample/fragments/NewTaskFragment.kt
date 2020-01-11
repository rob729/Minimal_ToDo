package com.example.robin.roomwordsample


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.robin.roomwordsample.Data.Word
import com.example.robin.roomwordsample.Data.WordViewModel
import com.example.robin.roomwordsample.Utils.notify
import com.example.robin.roomwordsample.Utils.utils
import com.example.robin.roomwordsample.databinding.FragmentNewTaskBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class NewTaskFragment : Fragment() {

    var checked: Boolean = false
    var year = 0
    var month = 0
    var day = 0
    var hr = 0
    var min = 0
    val formatter by lazy { SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.US) }
    private val colors by lazy { resources.getStringArray(R.array.colors) }
    private val wordViewModel: WordViewModel by lazy {
        ViewModelProviders.of(this).get(WordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentNewTaskBinding>(
            inflater,
            R.layout.fragment_new_task, container, false
        )

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
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _, i, i1 ->
                    binding.EnterTime.setText("$i:$i1")
                    hr = i
                    min = i1
                }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.EnterDate.setOnClickListener {
            val mcurrentDate = Calendar.getInstance()
            val mYear = mcurrentDate.get(Calendar.YEAR)
            val mMonth = mcurrentDate.get(Calendar.MONTH)
            val mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH)
            val myFormat = "dd MMM, yyyy"
            val mDatePicker = DatePickerDialog(
                this.context!!,
                DatePickerDialog.OnDateSetListener { _, selectedyear, selectedmonth, selectedday ->
                    binding.EnterDate.setText(selectedday.toString() + "/" + (selectedmonth + 1) + "/" + selectedyear)
                    year = selectedyear
                    month = selectedmonth
                    day = selectedday
                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        binding.submit.setOnClickListener {
            if (TextUtils.isEmpty(binding.task.text)) {
                val snackbar = Snackbar.make(
                    binding.parentLayout,
                    "Task field cannot be empty",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            } else {
                val task = binding.task.text.toString()
                val description = binding.description.text.toString()
                val tm = formatter.format(Date(System.currentTimeMillis()))
                if (checked) {
                    val c = Calendar.getInstance()
                    c.set(year, month, day, hr, min)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    val data = Data.Builder()
                    data.putString("Task Name", binding.task.text.toString())

                    val notifyManager = OneTimeWorkRequest.Builder(notify::class.java)
                        .setInputData(data.build())
                        .setInitialDelay(
                            c.timeInMillis - System.currentTimeMillis(),
                            TimeUnit.MILLISECONDS
                        )
                        .addTag(c.timeInMillis.toString())
                        .build()
                    val tag = c.timeInMillis.toString()
                    WorkManager.getInstance(this.context!!).enqueue(notifyManager)
                }
                wordViewModel.insert(
                    Word(
                        task,
                        tm,
                        tag.toString(),
                        false,
                        description,
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
