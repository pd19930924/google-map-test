package com.example.dongpu.googlemap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.dongpu.googlemap.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

/**
 * Created by dong.pu on 2019/6/25.
 */
class TestAcitivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var baseGoogleMap: BaseGoogleMap

    private var latLngList = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        baseGoogleMap = BaseGoogleMap(mMap)

        initLatLng()
        initMap()


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
    }
}