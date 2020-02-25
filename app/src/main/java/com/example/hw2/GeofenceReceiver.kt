package com.example.hw2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val geofenceTransition = geofencingEvent.geofenceTransition

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            var uid = intent!!.getIntExtra("uid", 0)
            var text = intent.getStringExtra("message")

            MainActivity.showNotification(context!!, text)
        }
    }

}