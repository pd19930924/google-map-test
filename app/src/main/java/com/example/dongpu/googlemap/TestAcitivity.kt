package com.example.dongpu.googlemap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.example.dongpu.googlemap.cluster_test.ClusterTest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Created by dong.pu on 2019/6/25.
 */
class TestAcitivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

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

    private lateinit var startClusterBtn : Button
    private lateinit var stopClusterBtn : Button

    //for map
    private lateinit var mMap: GoogleMap
    private lateinit var baseGoogleMap: BaseGoogleMap



    private var latLngList = ArrayList<LatLng>()

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
        var clusterLinearLayout = findViewById<LinearLayout>(R.id.cluster_linear_layout)

        linearLayoutList.add(markerLinearLayout)
        linearLayoutList.add(circleLinearLayout)
        linearLayoutList.add(cameraLinearLayout)
        linearLayoutList.add(clusterLinearLayout)

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

    private fun initMarkerTest(){
        markerTest = MarkerTest(baseGoogleMap,applicationContext)
        markerTest!!.latLngList = latLngList

        addDefaultMarkerBtn = findViewById(R.id.add_default_marker)
        addDrawableMarkerBtn = findViewById(R.id.add_drawable_marker)
        addLayoutMarkerBtn = findViewById(R.id.add_layout_marker)

        addDefaultMarkerBtn.setOnClickListener {
            markerTest!!.clearLastMarker()
            baseGoogleMap.clearMarkers()
            markerType = DEFAULT
            markerTest!!.addDefaulMarkers() }
        addDrawableMarkerBtn.setOnClickListener {
            markerTest!!.clearLastMarker()
            baseGoogleMap.clearMarkers()
            markerType = DRAWABLE
            markerTest!!.addDrawableMarkers() }
        addLayoutMarkerBtn.setOnClickListener {
            markerTest!!.clearLastMarker()
            baseGoogleMap.clearMarkers()
            markerType = LAYOUT
            markerTest!!.addLayoutMarkers() }

        markerTest!!.initMarkerClick(this)
    }

    private fun initCircleTest(){
        addDefaultCircleBtn = findViewById(R.id.add_defualt_circle)
        addDrawableCircleBtn = findViewById(R.id.add_drawable_circle)

        var centerLatLng = LatLng(40.721270, -73.982380)

        var circleTest = CircleTest(baseGoogleMap)
        circleTest.centerLatLng = centerLatLng

        addDefaultCircleBtn.setOnClickListener {circleTest.drawDefaultCircle() }

        addDrawableCircleBtn.setOnClickListener {circleTest.drawDrawableCircle(resources.getColor(R.color.color_dog_blue)) }
    }

    private fun initCameraTest(){
        addZoomBtn = findViewById(R.id.add_zoom_1)
        substractZoomBtn = findViewById(R.id.substract_zoom_1)
        forbidCameraMove = findViewById(R.id.forbid_camera_move)
        freeCameraMove = findViewById(R.id.free_camera_move)

        var cameraTest = CameraTest(baseGoogleMap)
        cameraTest.context = applicationContext
        addZoomBtn.setOnClickListener { cameraTest.addZoom() }
        substractZoomBtn.setOnClickListener { cameraTest.subStractZoom() }
        forbidCameraMove.setOnClickListener { cameraTest.forbidCameraMove() }
        freeCameraMove.setOnClickListener { cameraTest.freeCameraMove() }
    }

    private fun initClusterTest(){
        startClusterBtn = findViewById(R.id.start_cluster)
        stopClusterBtn = findViewById(R.id.stop_cluster)
        var clusterTest = ClusterTest(applicationContext, baseGoogleMap)

        startClusterBtn.setOnClickListener {
            baseGoogleMap.clearMarkers()  //clear all markers
            baseGoogleMap.moveCamera(LatLng(40.721270, -73.982380))  //move camera to center
            clusterTest.startCluster()
        }

        stopClusterBtn.setOnClickListener { clusterTest.stopCluster() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        baseGoogleMap = BaseGoogleMap(mMap)

        //init our map info
        initLatLng()
        initMap()

        initMarkerTest()   //this is for marker test
        initCircleTest()   //this is for circle test
        initCameraTest()   //this is for camera test
        initClusterTest()  //this is for cluster test
    }

    private fun initMap(){
        baseGoogleMap.moveCamera(LatLng(40.721270, -73.982380))  //at first, we move our camera to 40.721270, -73.982380(in New York)
        baseGoogleMap.setCameraZoom(5F)   //at first, we set zoom at the level of continent
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

    override fun onMarkerClick(marker: Marker?): Boolean {
        when(markerType){
            DEFAULT -> markerTest!!.clickDefaultMarker(marker!!)
            DRAWABLE -> markerTest!!.clickDrawableMarker(marker!!)
            LAYOUT -> markerTest!!.clickLayoutMarker(marker!!)
        }
        return true
    }

    companion object {
        //which kind of marker we are shoing
        val DEFAULT = 0
        val DRAWABLE = 1
        val LAYOUT = 2
    }
}