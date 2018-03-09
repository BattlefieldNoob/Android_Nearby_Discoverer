package com.example.antonio.nearbydiscoverer

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.listView


class MainActivity : AppCompatActivity() {

    val endpointDiscoveryCallback = IoTEndpointDiscoveryCallback(this)

    var ConnectionCallback = IoTNearbyConnectionCallback(this)

    var payloadCallback= IoTPayloadCallback(this)

    lateinit var nearbyClient:ConnectionsClient

    lateinit var eventsListViewAdapter:ArrayAdapter<String>

    lateinit var eventsListView:ListView

    var TAG = javaClass.name

    var MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventsListViewAdapter= ArrayAdapter(this,android.R.layout.simple_list_item_1)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION)
        } else {
            nearbyClient = Nearby.getConnectionsClient(this)
            onGoogleApiResult(true)
        }




        /////////////////////_____________________________________UI__________________________________//////////////////
        constraintLayout {

            eventsListView = listView {
                adapter=eventsListViewAdapter
            }

            applyConstraintSet {
                eventsListView {
                    connect(
                            START to START of PARENT_ID,
                            END to END of PARENT_ID,
                            BOTTOM to BOTTOM of PARENT_ID,
                            TOP to TOP of PARENT_ID
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_COARSE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    nearbyClient = Nearby.getConnectionsClient(this)
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun onGoogleApiResult(success:Boolean){
        if(success) {
            eventsListViewAdapter.add("Google Api Connected!")
            nearbyClient.startDiscovery( "Raspberry", endpointDiscoveryCallback, DiscoveryOptions(Strategy.P2P_CLUSTER)).addOnSuccessListener {
                eventsListViewAdapter.add("Discovery Started Successfully!")
                Log.d(TAG, "Success!")
            }.addOnFailureListener {
                Log.e(TAG, "Error!  ${it.message}")
            }
        }else{
            Log.e(TAG, "Error!")
        }
    }

    fun onNearbyEndpointFound(endpointId:String,endpointName:String){
        eventsListViewAdapter.add("Endpoint found with name:$endpointName (id:$endpointId)")
        nearbyClient.requestConnection("Raspberry",endpointId,ConnectionCallback).addOnSuccessListener {
            eventsListViewAdapter.add("Discovery Started Successfully!")
            Log.d(TAG,"Connected!")
        }.addOnFailureListener {
            Log.d(TAG,"Error!")
        }
    }

    fun onNearbyEndpointLost(endpointId:String){
        eventsListViewAdapter.add("Endpoint lost (id:$endpointId)")
    }

    fun onNearbyEndpointConnection(success: Boolean,endpointId: String){

        if (success) {
            eventsListViewAdapter.add("Connected to endpoint id:$endpointId")
            nearbyClient.sendPayload(endpointId, Payload.fromBytes("Hello!!".toByteArray()))
        }else{
            Log.d(TAG,"Error!")
        }
    }

    fun onPayloadReceived(endpointId: String, message:String){
        eventsListViewAdapter.add("From $endpointId, message:$message")
    }

}