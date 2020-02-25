package com.example.hw2

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var show = false
        plus_button.setOnClickListener{
            if(!show){
                show = true
                map_button.show()
                map_button.animate().translationY(-resources.getDimension(R.dimen.standard_66))
                time_button.show()
                time_button.animate().translationY(-resources.getDimension(R.dimen.standard_132))
            }else{
                show = false
                map_button.hide()
                map_button.animate().translationY(0f)
                time_button.hide()
                time_button.animate().translationY(0f)
            }
        }

        time_button.setOnClickListener{
            val intent = Intent(applicationContext, TimeActivity::class.java)
            startActivity(intent)
        }

        map_button.setOnClickListener{
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }

        list.onItemClickListener = AdapterView.OnItemClickListener{_,_,position, _ ->
            val selected = list.adapter.getItem(position) as Reminder
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Delete reminder")
                .setMessage(selected.message)
                .setPositiveButton("Delete"){ _,_ ->
                    if(selected.time != null) {
                        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(this@MainActivity, ReminderReceiver::class.java)
                        val pending = PendingIntent.getBroadcast(this@MainActivity, selected.uid!!, intent, PendingIntent.FLAG_ONE_SHOT)
                        manager.cancel(pending)
                    }

                    doAsync {
                        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders").build()
                        db.reminderDao().delete(selected.uid!!)
                        db.close()

                        refreshList()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }
    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList(){
        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders").build()
            val reminders = db.reminderDao().getReminders()
            db.close()

            uiThread {
                if(reminders.isNotEmpty()) {
                    val adapter = ReminderAdapter(applicationContext, reminders)
                    list.adapter = adapter
                } else {
                    list.adapter = null
                    toast("No reminders yet")
                }
            }
        }
    }

    companion object {
        val CHANNEL_ID="REMINDER_CHANNEL_1D"
        var notificationId = 1567
        fun showNotification(context: Context, message:String){
            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_24px)
                .setContentTitle(context?.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }
            val notification = notificationId + Random(notificationId).nextInt(1,30)
            notificationManager.notify(notification, notificationBuilder.build())

        }
    }
}
