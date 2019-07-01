package com.example.dongpu.googlemap

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*

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

        /*
        //Add markers and move the camera
        when(coordinates.size){
            0 -> nonMarker(mMap)
            else -> addMarkersToMap(coordinates)
        }*/
        mMap.addMarker(MarkerOptions().position(LatLng(36.5535809156,116.7519861043)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fuel_station)))
        mMap.addMarker(MarkerOptions().position(LatLng(38.0427810026,114.5143212580)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fuel_price)))

        //mMap.addMarker(MarkerOptions().position(LatLng(39.9939544339,117.1005165346)).icon(BitmapDescriptorFactory.fromResource(R.drawable.t34)))
        //mMap.addMarker(MarkerOptions().position(LatLng(40.1095820808,117.7404697928)).icon(BitmapDescriptorFactory.fromResource(R.drawable.test)))
        //mMap.addMarker(MarkerOptions().position(LatLng(39.5188913704,115.5157698773)).icon(BitmapDescriptorFactory.fromResource(R.drawable.test1)))
        //mMap.addMarker(MarkerOptions().position(LatLng(40.1179761492,115.6915092499)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ttt)))
        //mMap.addMarker(MarkerOptions().position(LatLng(39.0618318074,115.6530580736)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pot5)))

        mMap.addMarker(MarkerOptions().position(LatLng(38.3044167994,116.8386942891)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray)))
        mMap.addMarker(MarkerOptions().position(LatLng(39.9047253699,116.4072154982)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_yellow)))
        mMap.addMarker(MarkerOptions().position(LatLng(39.0850853357,117.1993482089)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_red)))
        mMap.addMarker(MarkerOptions().position(LatLng(40.8244345101,114.8875440254)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_green)))


    }

    //it is suitable for 0 points
    private fun nonMarker(mMap : GoogleMap){
        //we only make a circle and move camera to current location
        //there is nothing to show
        if(currentLatLng != null){
            mMap.addCircle(drawCircle(currentLatLng!!))
            moveCameraAtBeginning(currentLatLng!!)
            limitMoveCamera(currentLatLng!!)
        }


    }

    //it is suitable for several points ( >0)
    private fun addMarkersToMap(coordinates : ArrayList<LatLng>){
        //center of camera
        var centerX = -1.0
        var centerY = -1.0
        for(coordinate in coordinates){
            var markerOptions = MarkerOptions()
            markerOptions.position(coordinate)
            addIcon(markerOptions)
            mMap.addMarker(markerOptions)
            centerX += coordinate.latitude
            centerY += coordinate.longitude
        }
        centerX = centerX/coordinates.size
        centerY = centerY/coordinates.size
        var centerLatLng = LatLng(centerX, centerY)
        mMap.addCircle(drawCircle(centerLatLng))


        mMap.addMarker(MarkerOptions().position(centerLatLng))

        var newLatLng = LatLng(centerLatLng.latitude - 1.7, centerLatLng.longitude)
        moveCameraAtBeginning(newLatLng)

        limitMoveCamera(centerLatLng)
        markerClickListener()
        cameraChangedListener(centerLatLng)
    }

    private fun addDrawableResourceToMarkerOption(latLng: LatLng, drawableResource : Int) : MarkerOptions{
        var markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(drawableResource))
        return markerOptions
    }

    //we need special icon, which can not be made from icon(like show name in marker)
    //@Param latlng
    //@Param layoutView  a view , we should use a initialized view, we should use "var layoutView = LayoutInflater.from(context).inflater(R.layout.xx, null)"
    private fun addLayoutResourceToMarkerOption(latLng: LatLng, layoutView : View) : MarkerOptions{
        var markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, layoutView)))
        return markerOptions
    }

    //paint a circle to surround all latlng
    private fun drawCircle(centerLatLng : LatLng) : CircleOptions{
        var circleOptions = CircleOptions()
        circleOptions.center(centerLatLng)
        circleOptions.radius(circlRadius)
        circleOptions.strokeColor(resources.getColor(R.color.color_dog_blue))
        return circleOptions
    }

    //add icon for map mark
    private fun addIcon(markerOptions: MarkerOptions, type : Int = FUEL_STATION){
        when(type){
            FUEL_STATION->markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fuel_station1))
            FUEL_PRICE->{
                var fuel_view = LayoutInflater.from(applicationContext).inflate(R.layout.fuel_price_layout, null)
                fuel_view.findViewById<TextView>(R.id.price).text = "123"
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, fuel_view)))
            }
            PARKING_GREEN->markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_green1))
            PARKING_RED->markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_red1))
            PARKING_YELLOW->markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_yellow1))
            PARKING_GRAY->markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_gray1))
        }

    }

    //paint a special pic for marker icon(we need a new pic)
    private fun createDrawableFromView(context : Context, view : View) : Bitmap{
        var displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0,0 , displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        var bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    //we must make sure if we rotate our screen
    //at first we need to move camera at the beginning at first
    private fun moveCameraAtBeginning(centerLatLng : LatLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLng))  //move camera to top and center
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomRateInVerticalScreen))  //zoom in camera
        /**
        when(this.resources.configuration.orientation){
            //horizon screen
            Configuration.ORIENTATION_LANDSCAPE->{
                mMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLng))  //move camera to top and center
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomRateInHorizonScreen))  //zoom in camera
            }
            //vertical screen
            Configuration.ORIENTATION_PORTRAIT->{
                var newLatLng = LatLng(centerLatLng.latitude - 1.7, centerLatLng.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng ))  //move camera to top and center
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomRateInVerticalScreen))  //zoom in camera
            }
        }*/
    }

    //when we change camera, we use this function
    private fun moveCamera(centerLatLng : LatLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLng))  //move camera to top and center
        /**
        when(this.resources.configuration.orientation){
        //horizon screen
        Configuration.ORIENTATION_LANDSCAPE->{
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerLatLng))  //move camera to top and center
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomRateInHorizonScreen))  //zoom in camera
        }
        //vertical screen
        Configuration.ORIENTATION_PORTRAIT->{
        var newLatLng = LatLng(centerLatLng.latitude - 1.7, centerLatLng.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng ))  //move camera to top and center
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomRateInVerticalScreen))  //zoom in camera
        }
        }*/
    }

    //use bound to forbid the movement of camera
    private fun limitMoveCamera(centerLatLng: LatLng){
        var newLatLng = LatLng(centerLatLng.latitude - 1.7, centerLatLng.longitude)
        var mapBounds = LatLngBounds(newLatLng, newLatLng)  //we set a bound to make sure that our map will never change

        /*
        var leftLatLng = LatLng(centerLatLng.latitude - 1.7, centerLatLng.longitude)
        var rightLatLng = LatLng(centerLatLng.latitude, centerLatLng.longitude)
        var mapBounds = LatLngBounds(leftLatLng, rightLatLng)*/
        mMap.setLatLngBoundsForCameraTarget(mapBounds)
    }

    //use bound to forbid the movement of camera
    private fun limitMoveCamera(leftLatLng: LatLng, rightLatLng : LatLng){
        var mapBounds = LatLngBounds(leftLatLng, rightLatLng)
        mMap.setLatLngBoundsForCameraTarget(mapBounds)
    }

    //set bounds for camera move when we move camera
    private fun cameraChangedListener(centerLatLng: LatLng){

        //listen zoom in and zoom out
        mMap.setOnCameraChangeListener {
            //if zoom <= 6, it meas that our camera is far away, so we fix our camera
            if(it.zoom <= 6){
                limitMoveCamera(centerLatLng)
            }else{   //if the camera is closer and closer
                var rate = 0.15 * (it.zoom - 6)  //we need a rate to make sure that the space we can move contains circle
                var leftLatLng = LatLng(centerLatLng.latitude - it.zoom * rate, centerLatLng.longitude - it.zoom * rate)
                var rightLatLng = LatLng(centerLatLng.latitude + it.zoom * rate, centerLatLng.longitude + it.zoom * rate)
                limitMoveCamera(leftLatLng, rightLatLng)
            }
        }
    }

    private fun markerClickListener(){
        //click function
        mMap.setOnMarkerClickListener {
            true
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
