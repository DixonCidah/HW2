package com.example.hw2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

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

        val data = arrayOf("Oulu", "Helsinki", "Tampere")

        val adapter = ReminderAdapter(applicationContext, data)
        list.adapter = adapter

    }
}
