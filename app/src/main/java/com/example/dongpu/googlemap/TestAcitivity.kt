package com.example.dongpu.googlemap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Created by dong.pu on 2019/6/25.
 */
class TestAcitivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var addDefaultMarkerBtn : Button
    private lateinit var addDrawableMarkerBtn : Button
    private lateinit var addLayoutMarkerBtn : Button

    private lateinit var mMap: GoogleMap
    private lateinit var baseGoogleMap: BaseGoogleMap

    private var latLngList = ArrayList<LatLng>()

    private var lastMarker : Marker? = null

    private var markerType : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        initView()
        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    private fun initView(){
        addDefaultMarkerBtn = findViewById(R.id.add_default_marker)
        addDrawableMarkerBtn = findViewById(R.id.add_drawable_marker)
        addLayoutMarkerBtn = findViewById(R.id.add_layout_marker)

        addDefaultMarkerBtn.setOnClickListener {
            lastMarker = null
            baseGoogleMap.clear()
            markerType = DEFAULT
            addDefaulMarkers() }
        addDrawableMarkerBtn.setOnClickListener {
            lastMarker = null
            baseGoogleMap.clear()
            markerType = DRAWABLE
            addDrawableMarkers() }
        addLayoutMarkerBtn.setOnClickListener {
            lastMarker = null
            baseGoogleMap.clear()
            markerType = LAYOUT
            addLayoutMarkers() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        baseGoogleMap = BaseGoogleMap(mMap)

        initLatLng()
        initMap()
        initMarkerClick()
    }

    private fun initMap(){
        baseGoogleMap.moveCamera(LatLng(40.721270, -73.982380))  //at first, we move our camera to 40.721270, -73.982380(in New York)
        baseGoogleMap.setCameraZoom(6F)   //at first, we set zoom at the level of continent
    }

    private fun initLatLng(){
        var latLng1 = LatLng(40.8447820000,-73.8648270000)   //New York
        var latLng2 = LatLng(40.732830,-74.171754)   //Newark
        var latLng3 = LatLng(39.9525840000,-75.1652220000)    //Philadelphia
        var latLng4 = LatLng(40.1274710000,-75.0069600000)  //Edison
        latLngList.add(latLng1)
        latLngList.add(latLng2)
        latLngList.add(latLng3)
        latLngList.add(latLng4)
    }

    private fun initMarkerClick(){
        mMap.setOnMarkerClickListener {
            when(markerType){
                DEFAULT -> clickDefaultMarker(it)
                DRAWABLE -> clickDrawableMarker(it)
                LAYOUT -> clickLayoutMarker(it)
            }
            true
        }
    }

    private fun addDefaulMarkers(){
        for(latLng in latLngList){
            baseGoogleMap.addMarkerToMap(latLng)
        }
    }

    private fun clickDefaultMarker(marker: Marker){
        if(lastMarker == null){
            lastMarker = marker
        }else{
            if(lastMarker!!.equals(marker)) return
            lastMarker!!.zIndex = 0f
        }
        marker.zIndex = 2f
        lastMarker = marker
    }

    private fun addDrawableMarkers(){
        for(latLng in latLngList){
            baseGoogleMap.addMarkerToMap(latLng, "", R.drawable.ic_parking_green)
        }
    }

    private fun clickDrawableMarker(marker: Marker){
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

    private fun addLayoutMarkers(){
        //here we use marker.title to store our data in view
        for(latLng in latLngList){
            var view = LayoutInflater.from(applicationContext).inflate(R.layout.fuel_price_layout, null)
            var value = view.findViewById<TextView>(R.id.value)
            var background = view.findViewById<LinearLayout>(R.id.background)
            value.text = "value"
            var marker = baseGoogleMap.addMarkerToMap(latLng, "", view)
            marker.title = "value"
        }
    }

    private fun clickLayoutMarker(marker: Marker){
        if(lastMarker == null){
            lastMarker = marker
        }else{
            if(lastMarker!!.equals(marker)) return
            lastMarker!!.zIndex = 0f
            var view = LayoutInflater.from(applicationContext).inflate(R.layout.fuel_price_layout, null)
            var value = view.findViewById<TextView>(R.id.value)
            var background = view.findViewById<LinearLayout>(R.id.background)
            value.text = lastMarker!!.title  //because we build a new view, so we need to use our title to restore the value
            value.textSize = 14f
            background.setBackgroundResource(R.drawable.ic_fuel_price)
            lastMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(baseGoogleMap.createBitmapFromView(view)))
            lastMarker = marker
        }
        var view = LayoutInflater.from(applicationContext).inflate(R.layout.fuel_price_layout, null)
        var value = view.findViewById<TextView>(R.id.value)
        var background = view.findViewById<LinearLayout>(R.id.background)
        value.text = marker.title
        value.textSize = 16f
        background.setBackgroundResource(R.drawable.ic_fuel_price_big)
        marker.zIndex = 2f
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(baseGoogleMap.createBitmapFromView(view)))
    }

    companion object {
        //which kind of marker we are shoing
        val DEFAULT = 0
        val DRAWABLE = 1
        val LAYOUT = 2
    }
}