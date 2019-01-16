package com.example.robin.roomwordsample

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.*
import com.example.robin.roomwordsample.databinding.ActivityNewWordBinding
import java.util.concurrent.TimeUnit

class NewWordActivity : AppCompatActivity() {

    var checked: Boolean = false
    var year = 0
    var month = 0
    var day = 0
    var hr = 0
    var min = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =  DataBindingUtil.setContentView<ActivityNewWordBinding>(this, R.layout.activity_new_word)
        val cross = resources.getDrawable(R.drawable.ic_cancel)
        cross?.setColorFilter(resources.getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP)

        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(cross)
        }

        val appSharedPrefs = PreferenceManager
            .getDefaultSharedPreferences(this.applicationContext)

        binding.HasRemind.setOnCheckedChangeListener{ _, isChecked ->
            if(!isChecked) {
                binding.EnterDateTime.visibility = View.INVISIBLE
                checked = false
            } else {
                binding.EnterDateTime.visibility = View.VISIBLE
                checked = true
            }
        }

        binding.EnterTime.setOnClickListener{
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { _, i, i1 ->
                    binding.EnterTime.setText(i.toString() + ":" + i1)
                    hr = i
                    min = i1
                }, hour, minute, false
            )
            mTimePicker.show()
        }

        binding.EnterDate.setOnClickListener{
            val mcurrentDate = Calendar.getInstance()
            val mYear = mcurrentDate.get(Calendar.YEAR)
            val mMonth = mcurrentDate.get(Calendar.MONTH)
            val mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH)
            val myFormat = "dd MMM, yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val mDatePicker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, selectedyear, selectedmonth, selectedday ->
                    binding.EnterDate.setText(selectedday.toString() + "/" + (selectedmonth + 1) + "/" + selectedyear)
                    year = selectedyear
                    month = selectedmonth
                    day = selectedday
                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        binding.makeToDoFloatingActionButton.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.userToDoEditText.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val task = binding.userToDoEditText.text.toString()
                var tag = "tag"
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.US)
                val tm = formatter.format(Date(System.currentTimeMillis()))
                replyIntent.putExtra(EXTRA_REPLY, task)
                replyIntent.putExtra("Time",tm)
                if(checked) {
                    val prefsEditor = appSharedPrefs.edit()
                    prefsEditor.putString("Task", binding.userToDoEditText.text.toString())
                    prefsEditor.apply()
                    val c = Calendar.getInstance()
                    c.set(year, month, day, hr, min)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    val notifyManager = OneTimeWorkRequest.Builder(notify::class.java)
                        .setInitialDelay(c.timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                        .addTag(c.timeInMillis.toString())
                        .build()
                    tag = c.timeInMillis.toString()
                    WorkManager.getInstance().enqueue(notifyManager)
                }
                replyIntent.putExtra("tag", tag)
                setResult(Activity.RESULT_OK, replyIntent)

            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}
