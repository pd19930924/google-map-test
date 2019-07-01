package com.example.dongpu.googlemap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

/**
 * Created by dong.pu on 2019/6/25.
 */
class TestAcitivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var baseGoogleMap = BaseGoogleMap(mMap)
        var coordinates = ArrayList<LatLng>()
        coordinates.add(LatLng(39.9047253699,116.4072154982))
        coordinates.add(LatLng(39.0850853357,117.1993482089))
        baseGoogleMap.addMarkersToMap(coordinates)
        baseGoogleMap.moveCamera(LatLng(39.9047253699,116.4072154982))
    }


}