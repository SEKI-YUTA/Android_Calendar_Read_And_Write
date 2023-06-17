package com.example.calendarproviderdemo

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.calendarproviderdemo.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var  binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission.launch(arrayOf(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR))
    }

    override fun onResume() {
        super.onResume()
        binding.btnDemo.setOnClickListener {
            readCalendarEvents()
        }

        binding.btnInsert.setOnClickListener {
           val id = addEvent()
            Log.d("id", id.toString())
        }
    }

    private fun addEvent(
    ): Long {
        val calId = 3
        val startMilli = Date().time
        val endMilli = Date().time + 1000 * 60 * 60

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMilli)
            put(CalendarContract.Events.DTEND, endMilli)
            put(CalendarContract.Events.TITLE, "Some title")
            put(CalendarContract.Events.DESCRIPTION, "this is description text")
            put(CalendarContract.Events.CALENDAR_ID, calId)
            put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Tokyo")
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri?.lastPathSegment?.toLong() ?: -1
    }

    private fun readCalendarEvents()
    {
        val titleCol = CalendarContract.Events.TITLE
        val startDateCol = CalendarContract.Events.DTSTART
        val endDateCol = CalendarContract.Events.DTEND

        val projection = arrayOf(titleCol, startDateCol, endDateCol)
        val selection = CalendarContract.Events.DELETED + " != 1"

        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection, selection, null, null
        )

        val titleColIdx = cursor!!.getColumnIndex(titleCol)
        val startDateColIdx = cursor.getColumnIndex(startDateCol)
        val endDateColIdx = cursor.getColumnIndex(endDateCol)

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

        while (cursor.moveToNext()) {
            val title = cursor.getString(titleColIdx)
            val startDate = formatter.format(Date(cursor.getLong(startDateColIdx)))
            val endDate = formatter.format(Date(cursor.getLong(endDateColIdx)))

            Log.d("MY_APP", "$title $startDate $endDate")
        }

        cursor.close()
    }


    val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var granted = true
        val values = it.values
        values.forEach {
            if(!it) granted = false
        }
    }
}
