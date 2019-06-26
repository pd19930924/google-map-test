package com.example.dongpu.googlemap

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

/**
 * Created by dong.pu on 2019/6/26.
 */
class BaseGoogleMap {

    private lateinit var mMap : GoogleMap

    constructor(mMap: GoogleMap){
        this.mMap = mMap
    }

    /**
     * This is used to add a marker to Map
     * @param point the marker that we need to add
     * @param title if title exists, there will be a title if we click marker
     * @param drawableResouce if drawableResouce exists, marker icon will be replaced to our pic
     * it is an example : addMarkerToMap(Latlng(31.1, 32.3), "str" , resouce.getDrawable(R.drawable.pic))
     */
    @JvmOverloads
    fun addMarkerToMap(point : LatLng, title : String? = null, drawableResouce : Int? = null){
        var markerOptions = MarkerOptions()
        markerOptions.position(point)
        if(title != null){
            markerOptions.title(title)
        }
        if(drawableResouce != null){
            var bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(drawableResouce)
            markerOptions.icon(bitmapDescriptorFactory)
        }
        mMap.addMarker(markerOptions)
    }

    /**
     * This is used to add a marker to Map, and the diffenrence between this and another "addMarkerToMap" is we used a view to replace our marker pic
     * @param point the marker that we need to add
     * @param title if title exists, there will be a title if we click marker
     * @param context it is come from our activity, we need it to load our view
     * @param view marker icon will be replaced to our pic
     * it is an example :
     * var view = Inflater.from(context).inflater(R.layout.main, null)  //There is a key point, we should initialize our view at first
     * addMarkerToMap(Latlng(31.1, 32.3), "str" , this, view)
     */
    fun addMarkerToMap(point : LatLng, title : String? = null, context : Context, view : View){
        var markerOptions = MarkerOptions()
        markerOptions.position(point)
        if(title != null){
            markerOptions.title(title)
        }
        var bitmapDescriptorFactory = BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, view))
        markerOptions.icon(bitmapDescriptorFactory)
        mMap.addMarker(markerOptions)
    }

    //paint a special bitmap pic form layout for marker icon
    private fun createDrawableFromView(context : Context, view : View) : Bitmap {
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

    /**
     * This is function that we can use it to set markers on Map
     * @param points it is points that we need to add on Map (point =  LatLng(latitude, longtitude))
     */
    fun addMarkersToMap(points : ArrayList<LatLng>){
        if(points.size == 0){
            return
        }
        for(point in points){
            var markerOptions = MarkerOptions()
            markerOptions.position(point)
            mMap.addMarker(markerOptions)
        }
    }

    /**
     * This is used to draw a circle to our map, but it only draw a circle, it has an attribute color
     * and default color of our circle is black
     * @param centerLatLng it is the center of our circle
     * @param radius it is radius (unit : meter)
     * @param color if color exists, our circle color will changed
     */
    @JvmOverloads
    fun drawCircleOnMap(centerLatLng: LatLng, radius : Double, color : Int? = null){
        var circleOptions = CircleOptions()
        circleOptions.center(centerLatLng)
        circleOptions.radius(radius)
        if(color != null) circleOptions.strokeColor(color)
        mMap.addCircle(circleOptions)
    }

    /**
     * we can move camera to a assign location(latitude, longtitude)
     * @param latLng
     */
    fun moveCamera(latLng: LatLng){
        var cameraUpdateFactory = CameraUpdateFactory.newLatLng(latLng)
        mMap.moveCamera(cameraUpdateFactory)
    }

    /**
     * we can move camera to a assign location(cameraPosition)
     * @param cameraPosition
     */
    fun moveCamera(cameraPosition: CameraPosition){
        var cameraUpdateFactory = CameraUpdateFactory.newCameraPosition(cameraPosition)
        mMap.moveCamera(cameraUpdateFactory)
    }

    /**
     * it will change the zoom of camera
     * @param zoomValue it is the zoom that we want to go
     */
    fun setCameraZoom(zoomValue : Float){
        /*There are some standard value of zoom
        1: World
        5: Landmass/continent
        10: City
        15: Streets
        20: Buildings
         */
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomValue))
    }

    /**
     * This is used to limit the movement of camera when we try to make our camera can not go far away
     * This will make a rectangle(?), and we can only move in the rectangle
     * @param leftTopLatLng left top point
     * @param rightBottomLatLng right bottom point
     */
    fun limitCameraMove(leftTopLatLng : LatLng, rightBottomLatLng : LatLng){
        var mapBounds = LatLngBounds(leftTopLatLng, rightBottomLatLng)
        mMap.setLatLngBoundsForCameraTarget(mapBounds)
    }

    /**
     * We used it to fix our cameral, and our cameral will never moved if we try to move our camera
     * @param latLng This will be our screen center
     */
    fun fixCamera(latLng: LatLng){
        var mapBounds = LatLngBounds(latLng, latLng)
        mMap.setLatLngBoundsForCameraTarget(mapBounds)
    }

    /**
     * we can use this function to get current zoom of Map
     * @return zoom value
     */
    fun getZoom() : Float{
        return mMap.cameraPosition.zoom
    }

    /**
     * we can get camera position
     * @return our camera position
     */
    fun getCameraPosition() : CameraPosition{
        return mMap.cameraPosition
    }
}