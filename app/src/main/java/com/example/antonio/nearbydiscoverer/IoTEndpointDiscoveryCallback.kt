package com.example.antonio.nearbydiscoverer

import android.util.Log
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback

class IoTEndpointDiscoveryCallback(var mainActivity: MainActivity) : EndpointDiscoveryCallback() {

    private val TAG=javaClass.name;

    override fun onEndpointFound(p0: String?, p1: DiscoveredEndpointInfo?) {
        Log.d(TAG,"On Nearby Found with name:${p1?.endpointName} (id:$p0)")
        Log.d(TAG,p1?.endpointName)
        mainActivity.onNearbyEndpointFound(p0!!,p1?.endpointName!!)
    }

    override fun onEndpointLost(p0: String?) {
        Log.d(TAG,"On Nearby Lost (id:$p0)")
        mainActivity.onNearbyEndpointLost(p0!!)
    }
}