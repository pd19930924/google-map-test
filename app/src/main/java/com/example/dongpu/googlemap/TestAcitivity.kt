package com.example.dongpu.googlemap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.example.dongpu.googlemap.CircleTest.Companion.radius1
import com.example.dongpu.googlemap.CircleTest.Companion.radius2
import com.example.dongpu.googlemap.MarkerTest.Companion.DRAWABLE
import com.example.dongpu.googlemap.MarkerTest.Companion.LAYOUT
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Created by dong.pu on 2019/6/25.
 */
class TestAcitivity : AppCompatActivity(), OnMapReadyCallback{

    //test_activity
    private lateinit var switchTypeBtn : Button
    private lateinit var clearAllBtn : Button   //click this, we can clear all info in our map

    private var currentIndex = 0   //which linearLayout we are showing
    private var linearLayoutList = ArrayList<LinearLayout>()

    //marker_linear_layout
    private lateinit var addDefaultMarkerBtn :  Button
    private lateinit var addDrawableMarkerBtn : Button
    private lateinit var addLayoutMarkerBtn : Button

    //circle_linear_layout
    private lateinit var addDefaultCircleBtn : Button
    private lateinit var addDrawableCircleBtn : Button

    //camera_linear_layout
    private lateinit var addZoomBtn : Button
    private lateinit var substractZoomBtn : Button
    private lateinit var forbidCameraMove : Button
    private lateinit var freeCameraMove : Button

    private lateinit var mMap: GoogleMap
    private lateinit var baseGoogleMap: BaseGoogleMap

    private var latLngList = ArrayList<LatLng>()

    private var lastMarker : Marker? = null

    private var markerType : Int = -1

    private var markerTest : MarkerTest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
        initView() //init test_acitivity
    }

    private fun initView(){
        switchTypeBtn = findViewById(R.id.switch_type)
        clearAllBtn = findViewById(R.id.clear_all)

        var markerLinearLayout = findViewById<LinearLayout>(R.id.marker_linear_layout)
        var circleLinearLayout = findViewById<LinearLayout>(R.id.circle_linear_layout)
        var cameraLinearLayout = findViewById<LinearLayout>(R.id.camera_linear_layout)

        linearLayoutList.add(markerLinearLayout)
        linearLayoutList.add(circleLinearLayout)
        linearLayoutList.add(cameraLinearLayout)

        switchTypeBtn.setOnClickListener {
            linearLayoutList.get(currentIndex).visibility = View.GONE
            if(currentIndex < linearLayoutList.size - 1){
                var nextIndex = currentIndex+1
                linearLayoutList.get(nextIndex).visibility = View.VISIBLE
                currentIndex = nextIndex
            }
            else{
                currentIndex = 0
                linearLayoutList.get(currentIndex).visibility = View.VISIBLE
            }
        }

        clearAllBtn.setOnClickListener {
            baseGoogleMap.clearAll()
        }
    }

    private fun initMarkerView(){
        addDefaultMarkerBtn = findViewById(R.id.add_default_marker)
        addDrawableMarkerBtn = findViewById(R.id.add_drawable_marker)
        addLayoutMarkerBtn = findViewById(R.id.add_layout_marker)

        addDefaultMarkerBtn.setOnClickListener {
            lastMarker = null
            baseGoogleMap.clearMarkers()
            markerType = DEFAULT
            markerTest!!.addDefaulMarkers() }
        addDrawableMarkerBtn.setOnClickListener {
            lastMarker = null
            baseGoogleMap.clearMarkers()
            markerType = DRAWABLE
            markerTest!!.addDrawableMarkers() }
        addLayoutMarkerBtn.setOnClickListener {
            lastMarker = null
            baseGoogleMap.clearMarkers()
            markerType = LAYOUT
            markerTest!!.addLayoutMarkers() }
    }

    private fun initCircleView(){
        addDefaultCircleBtn = findViewById(R.id.add_defualt_circle)
        addDrawableCircleBtn = findViewById(R.id.add_drawable_circle)

        var centerLatLng = LatLng(40.721270, -73.982380)

        var circleTest = CircleTest(baseGoogleMap)

        addDefaultCircleBtn.setOnClickListener {circleTest.drawDefaultCircle() }

        addDrawableCircleBtn.setOnClickListener {circleTest.drawDrawableCircle(resources.getColor(R.color.color_dog_blue)) }
    }

    private fun initCameraView(){
        addZoomBtn = findViewById(R.id.add_zoom_1)
        substractZoomBtn = findViewById(R.id.substract_zoom_1)
        forbidCameraMove = findViewById(R.id.forbid_camera_move)
        freeCameraMove = findViewById(R.id.free_camera_move)

        var cameraTest = CameraTest(baseGoogleMap)
        addZoomBtn.setOnClickListener { cameraTest.addZoom() }
        substractZoomBtn.setOnClickListener { cameraTest.subStractZoom() }
        forbidCameraMove.setOnClickListener { cameraTest.forbidCameraMove() }
        freeCameraMove.setOnClickListener { cameraTest.freeCameraMove() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        baseGoogleMap = BaseGoogleMap(mMap)

        initLatLng()
        initMap()

        //this is for marker test
        initMarkerTest()
        initMarkerView()
        initMarkerClick()

        initCircleView()   //this is for circle test
        initCameraView()   //this is for camera test
    }

    private fun initMap(){
        baseGoogleMap.moveCamera(LatLng(40.721270, -73.982380))  //at first, we move our camera to 40.721270, -73.982380(in New York)
        baseGoogleMap.setCameraZoom(6F)   //at first, we set zoom at the level of continent
    }

    private fun initMarkerTest(){
        markerTest = MarkerTest(baseGoogleMap,applicationContext)
        markerTest!!.latLngList = latLngList
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
                DEFAULT -> markerTest!!.clickDefaultMarker(it)
                DRAWABLE -> markerTest!!.clickDrawableMarker(it)
                LAYOUT -> markerTest!!.clickLayoutMarker(it)
            }
            true
        }
    }

    companion object {
        //which kind of marker we are shoing
        val DEFAULT = 0
        val DRAWABLE = 1
        val LAYOUT = 2
    }
}