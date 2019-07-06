package com.example.dongpu.googlemap

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.dongpu.googlemap.R
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var currentLatLng : LatLng? = null  //get current location then we use current location to choose datas in database

    private var cameraPosition : CameraPosition? = null

    private var savedInstanceState : Bundle? = null
    //standard
    /*
    1: World
    5: Landmass/continent
    10: City
    15: Streets
    20: Buildings
     */
    private val zoomRateInVerticalScreen = 6f  //amplification multiple
    private val zoomRateInHorizonScreen = 5.7f  //amplification multiple

    private var mClusterManager : ClusterManager<MyItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        this.savedInstanceState = savedInstanceState

        var metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

    }

    fun setCurLatLng(curLatLng: LatLng){
        this.currentLatLng = curLatLng
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var coordinates = ArrayList<LatLng>()
        coordinates.add(LatLng(39.9047253699,116.4072154982))
        coordinates.add(LatLng(39.0850853357,117.1993482089))

        //mMap.addMarker(MarkerOptions().position(LatLng(36.5535809156,116.7519861043)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fuel_station)))
        //mMap.addMarker(MarkerOptions().position(LatLng(38.0427810026,114.5143212580)).icon(BitmapDescriptorFactory.fromResource(R.drawable.fuel)))

        //mMap.addMarker(MarkerOptions().position(LatLng(39.9939544339,117.1005165346)).icon(BitmapDescriptorFactory.fromResource(R.drawable.t34)))
        //mMap.addMarker(MarkerOptions().position(LatLng(40.1095820808,117.7404697928)).icon(BitmapDescriptorFactory.fromResource(R.drawable.test)))
        //mMap.addMarker(MarkerOptions().position(LatLng(39.5188913704,115.5157698773)).icon(BitmapDescriptorFactory.fromResource(R.drawable.test1)))
        //mMap.addMarker(MarkerOptions().position(LatLng(40.1179761492,115.6915092499)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ttt)))
        //mMap.addMarker(MarkerOptions().position(LatLng(39.0618318074,115.6530580736)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pot5)))

        //mMap.addMarker(MarkerOptions().position(LatLng(38.3044167994,116.8386942891)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pot_1)))
        //mMap.addMarker(MarkerOptions().position(LatLng(39.9047253699,116.4072154982)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pot_2)))
        //mMap.addMarker(MarkerOptions().position(LatLng(39.0850853357,117.1993482089)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pot_3)))
        //mMap.addMarker(MarkerOptions().position(LatLng(40.8244345101,114.8875440254)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_yellow)))

        /*
        mMap.setOnMarkerClickListener {
            it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_bigger))
            true
        }*/

        /*
        var baseGoogleMap = BaseGoogleMap(mMap)

        var latLngList = ArrayList<LatLng>()
        latLngList.add(LatLng(36.5535809156,116.7519861043))
        latLngList.add(LatLng(38.0427810026,114.5143212580))
        latLngList.add(LatLng(38.3044167994,116.8386942891))
        latLngList.add(LatLng(39.9047253699,116.4072154982))
        latLngList.add(LatLng(39.0850853357,117.1993482089))
        latLngList.add(LatLng(40.8244345101,114.8875440254))


        baseGoogleMap.addMarkersToMap(latLngList)

        baseGoogleMap.moveCamera(LatLng(36.5535809156,116.7519861043))
        baseGoogleMap.setCameraZoom(6f)

        Handler().postDelayed(object : Runnable{
            override fun run() {
                Log.d("pudong","1")
                var latLng = LatLng(38.3044167994,116.8386942891)
                baseGoogleMap.removeMarker(latLng)
            }
        },2000)

        Handler().postDelayed(object : Runnable{
            override fun run() {
                Log.d("pudong","2")
                var latLng = LatLng(38.3044167994,116.8386942891)
                baseGoogleMap.removeMarker(latLng)
            }
        },4000)


        /*
        mClusterManager = ClusterManager<MyItem>(this, mMap)
        mMap.setOnCameraIdleListener(mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)
        //addItems()
        addItems(latLngList)*/*/
    }

    private fun addItems(){
        var lat = 51.503186
        var lng = -0.126446
        var i = 0
        while(i<10){
            var offset = i/60.0
            lat = lat+offset
            lng = lng+offset
            var myItem = MyItem(LatLng(lat, lng))
            mClusterManager!!.addItem(myItem)
            i++
        }
    }

    private fun addItems(latLngList : List<LatLng>){
        for(latLng in latLngList){
            var myItem = MyItem(latLng)
            mClusterManager!!.addItem(myItem)
        }
    }


    companion object {
        val FUEL_STATION = 0
        val FUEL_PRICE = 1
        val PARKING_RED = 2
        val PARKING_YELLOW = 3
        val PARKING_GREEN = 4
        val PARKING_GRAY = 5

        val circlRadius : Double = 257495.04 // unit : kilometers(160miles = 257.49504 kilometers)
    }

}
