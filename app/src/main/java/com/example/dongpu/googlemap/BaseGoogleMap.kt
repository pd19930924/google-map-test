package com.example.dongpu.googlemap

import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.*

/**
 * Created by dong.pu on 2019/6/26.
 */
class BaseGoogleMap {

    private lateinit var mMap : GoogleMap

    private lateinit var markerList : ArrayList<Marker>  //it is used to remove markers, hide markers

    private lateinit var hideMarkerList : ArrayList<Marker>  //it is used to storage hide markers in case that we need to show some hide markers

    constructor(mMap: GoogleMap){
        this.mMap = mMap
        this.markerList = ArrayList<Marker>()
        this.hideMarkerList = ArrayList<Marker>()
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
        var marker = mMap.addMarker(markerOptions)
        markerList.add(marker)
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
        var marker = mMap.addMarker(markerOptions)
        markerList.add(marker)
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
            var marker = mMap.addMarker(markerOptions)
            markerList.add(marker)
        }
    }

    /**
     * show hide marker if we have hide it
     * @param latLng
     */
    fun showMarker(latLng: LatLng){
        var currentMarker : Marker? = null
        for(marker in hideMarkerList){
            var mLatLng = marker.position
            if(mLatLng.equals(latLng)){
                currentMarker = marker
                break
            }
        }
        if(currentMarker == null)Log.e(TAG, LATLNG_NOT_EXIST_ERROR)
        else{
            currentMarker.isVisible = true  //show the marker
            hideMarkerList.remove(currentMarker!!)   //we have restore the marker, so we delete the marker in hideMarkerList
        }
    }

    /**
     * show, if we know this is which market we add, we can move it dirrectly
     * and we can avoid searching all markers
     * @param index it is which index of our marker
     */
    fun showMarker(index : Int){
        if(markerList.size < index){
            Log.e(TAG, INDEX_OUT_OF_RANGE_ERROR)
            return
        }
        var marker = markerList.get(index)
        marker.isVisible = true
        hideMarkerList.remove(marker)   //set hidemMarkerList
    }

    /**
     * @param marker we use exist marker to show marker
     */
    fun showMarker(marker: Marker){
        if(!hideMarkerList.remove(marker)) Log.e(TAG, MARKET_NOT_HIDDEN)
    }

    /**
     * hide the marker, we need to search all markers to hide the marker
     * @param latLng
     */
    fun hideMarker(latLng: LatLng){
        var currentMarker : Marker? = null
        for(marker in markerList){
            var mLatLng = marker.position  //get latLng
            if(mLatLng.equals(latLng)){
                currentMarker = marker
                break
            }
        }
        //if currentMarker is null , it means that we does not have the marker
        if(currentMarker == null)Log.e(TAG, LATLNG_NOT_EXIST_ERROR)
        else{
            currentMarker.isVisible = false
            hideMarkerList.add(currentMarker)  //set hidemMarkerList
        }
    }

    /**
     * hide the market, if we know this is which market we add, we can move it dirrectly
     * and we can avoid searching all markers
     * @param index it is which index of our marker
     */
    fun hideMarker(index : Int){
        if(markerList.size < index){
            Log.e(TAG, INDEX_OUT_OF_RANGE_ERROR)
            return
        }
        var marker = markerList.get(index)
        marker.isVisible = false
        hideMarkerList.add(marker)   //set hidemMarkerList
    }

    /**
     * @param marker we use exist marker to hide the marker
     */
    fun hideMarker(marker: Marker){
        if(!markerList.contains(marker)) Log.e(TAG, MARKET_NOT_EXIST_ERROR)
        else hideMarkerList.add(marker)
    }

    /**
     * remove existing marker
     * @param latLng
     */
    fun removeMarker(latLng : LatLng){
        var currentMarker : Marker? = null
        for(marker in markerList){
            var mLatLng = marker.position  //get latLng
            if(mLatLng.equals(latLng)){
                currentMarker = marker
                break
            }
        }
        //if currentMarker is null , it means that we does not have the marker
        if(currentMarker == null)Log.e(TAG, LATLNG_NOT_EXIST_ERROR)
        else {
            currentMarker.remove()
            markerList.remove(currentMarker)
        }
    }

    /**
     * remove existing marker
     * @param index
     */
    fun removeMarker(index: Int){
        if(markerList.size < index){
            Log.e(TAG, INDEX_OUT_OF_RANGE_ERROR)
            return
        }
        var marker = markerList.get(index)
        marker.remove()
        markerList.remove(marker)
    }

    /**
     * @param marker we use exist marker to remove the marker
     */
    fun removeMarker(marker: Marker){
        if(!markerList.contains(marker)) Log.e(TAG, MARKET_NOT_EXIST_ERROR)
        else{
            marker.remove()
            markerList.remove(marker)
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

    /**
     * we can get the screen cooprates of (southwest and northeast)
     * but before we use it, at first it will show (0, 0) and (0, 0), but after movement, it will show correct bounds
     * @return 2 latlngBounds
     */
    fun getMapBounds() : LatLngBounds{
        return mMap.projection.visibleRegion.latLngBounds
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

    companion object {
        val TAG = "BaseGoogleMap"
        val LATLNG_NOT_EXIST_ERROR = "The latitude and longtitude is not exisiting"
        val MARKET_NOT_EXIST_ERROR = "The marker is not existing"
        val INDEX_OUT_OF_RANGE_ERROR = "The index is out of range"
        val MARKET_NOT_HIDDEN = "The marker has not hidden"
    }
}