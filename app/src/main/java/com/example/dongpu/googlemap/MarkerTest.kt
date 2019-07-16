package com.example.dongpu.googlemap

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Created by dong.pu on 2019/7/8.
 */
class MarkerTest {

    private lateinit var baseGoogleMap : BaseGoogleMap
    private var lastMarker : Marker? = null

    lateinit var latLngList : List<LatLng>

    private var context : Context? = null

    constructor(baseGoogleMap: BaseGoogleMap, context : Context){
        this.baseGoogleMap = baseGoogleMap
        this.context = context
    }

    fun addDefaulMarkers(){
        var index = 0
        for(latLng in latLngList){
            var marker = baseGoogleMap.addMarkerToMap(latLng)
            index++
        }
    }

    fun clickDefaultMarker(marker: Marker){
        if(lastMarker == null){
            lastMarker = marker
        }else{
            if(lastMarker!!.equals(marker)) return
            lastMarker!!.zIndex = 0f
        }
        marker.zIndex = 2f
        lastMarker = marker
    }

    fun addDrawableMarkers(){
        for(latLng in latLngList){
            baseGoogleMap.addMarkerToMap(latLng, null, null, R.drawable.ic_parking_green)
        }
    }

    fun clickDrawableMarker(marker: Marker){
        if(lastMarker == null){
            lastMarker = marker
        }else{
            if(lastMarker!!.equals(marker)) return
            lastMarker!!.zIndex = 0f
            lastMarker!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_green))
        }
        //here we use a big pic to show our map
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_green_big))
        marker.zIndex = 2f
        lastMarker = marker
    }

    fun addLayoutMarkers(){
        //here we use marker.title to store our data in view
        for(latLng in latLngList){
            var view = LayoutInflater.from(context).inflate(R.layout.value_layout, null)
            var value = view.findViewById<TextView>(R.id.value)
            var background = view.findViewById<LinearLayout>(R.id.background)
            value.text = "value"
            var marker = baseGoogleMap.addMarkerToMap(latLng, null, null,view)
            marker.title = "value"
        }
    }

    fun clickLayoutMarker(marker: Marker){
        if(lastMarker == null){
            lastMarker = marker
        }else{
            if(lastMarker!!.equals(marker)) return
            lastMarker!!.zIndex = 0f
            var view = LayoutInflater.from(context).inflate(R.layout.value_layout, null)
            var value = view.findViewById<TextView>(R.id.value)
            var background = view.findViewById<LinearLayout>(R.id.background)
            value.text = lastMarker!!.title  //because we build a new view, so we need to use our title to restore the value
            value.textSize = 14f
            background.setBackgroundResource(R.drawable.ic_fuel_price)
            lastMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(baseGoogleMap.createBitmapFromView(view)))
            lastMarker = marker
        }
        var view = LayoutInflater.from(context).inflate(R.layout.value_layout, null)
        var value = view.findViewById<TextView>(R.id.value)
        var background = view.findViewById<LinearLayout>(R.id.background)
        value.text = marker.title
        value.textSize = 16f
        background.setBackgroundResource(R.drawable.ic_fuel_price_big)
        marker.zIndex = 2f
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(baseGoogleMap.createBitmapFromView(view)))
    }

    fun initMarkerClick(onMarkerClickListener: GoogleMap.OnMarkerClickListener){
        var mMap = baseGoogleMap.getGoogleMap()
        mMap.setOnMarkerClickListener(onMarkerClickListener)
    }

    //if we change our marker type , we need clearing our lastmarker info , or it will break
    fun clearLastMarker(){
        lastMarker = null
    }

    companion object {
        //which kind of marker we are shoing
        val DEFAULT = 0
        val DRAWABLE = 1
        val LAYOUT = 2
    }

}