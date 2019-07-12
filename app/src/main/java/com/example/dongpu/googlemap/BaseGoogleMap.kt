package com.example.dongpu.googlemap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.View
import com.example.dongpu.googlemap.cluster_test.MyItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import java.util.*

/**
 * Created by dong.pu on 2019/6/26.
 */
class BaseGoogleMap : Cloneable {

    private lateinit var mMap : GoogleMap

    private lateinit var markerList : ArrayList<Marker>  //it is used to remove markers, hide markers

    private lateinit var hideMarkerList : ArrayList<Marker>  //it is used to storage hide markers in case that we need to show some hide markers

    private lateinit var clusterManger: ClusterManager<MyItem>  //it is used to storage clusterManger

    private var isForbidOrLimitCameraMovement : Boolean = false  //it is used to judge whether we have use limitCameraMove or forbidCameraMove
    private var isStartCluster : Boolean = false //it is used to judge whether we have start cluster

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
    fun addMarkerToMap(point : LatLng, title : String? = null, drawableResouce : Int? = null) : Marker{
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
        needClustering(marker)
        return marker
    }

    /**
     * This is used to add a marker to Map, and the diffenrence between this and another "addMarkerToMap" is we used a view to replace our marker pic
     * @param point the marker that we need to add
     * @param title if title exists, there will be a title if we click marker
     * @param context it is come from our activity, we need it to load our view
     * @param view marker icon will be replaced to our pic
     * @return return our maker to help us to do other work to marker
     * it is an example :
     * var view = Inflater.from(context).inflater(R.layout.main, null)  //There is a key point, we should initialize our view at first
     * addMarkerToMap(Latlng(31.1, 32.3), "str" , this, view)
     */
    fun addMarkerToMap(point : LatLng, title : String? = null, view : View) : Marker{
        var markerOptions = MarkerOptions()
        markerOptions.position(point)
        if(title != null){
            markerOptions.title(title)
        }
        var bitmapDescriptorFactory = BitmapDescriptorFactory.fromBitmap(createBitmapFromView(view))
        markerOptions.icon(bitmapDescriptorFactory)
        var marker = mMap.addMarker(markerOptions)
        markerList.add(marker)
        needClustering(marker)
        return marker
    }

    /**
     * This is function that we can use it to set markers on Map
     * @param points it is points that we need to add on Map (point =  LatLng(latitude, longtitude))
     */
    fun addMarkersToMap(points : ArrayList<LatLng>) : List<Marker>{
        if(points.size == 0){
            return markerList
        }
        for(point in points){
            var markerOptions = MarkerOptions()
            markerOptions.position(point)
            var marker = mMap.addMarker(markerOptions)
            markerList.add(marker)
            needClustering(marker)
        }
        return markerList
    }

    private fun needClustering(marker: Marker){
        //if we open the state that we need cluster map, then we will hide marker and add item to clusterManager
        if(isStartCluster){
            marker.isVisible = false  //hide marker
            var myItem = MyItem(marker)
            clusterManger.addItem(myItem)
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
    fun hideMarker(latLng: LatLng) : Marker?{
        var currentMarker : Marker? = null
        for(marker in markerList){
            var mLatLng = marker.position  //get latLng
            if(mLatLng.equals(latLng)){
                currentMarker = marker
                break
            }
        }
        //if currentMarker is null , it means that we does not have the marker
        if(currentMarker == null){
            Log.e(TAG, LATLNG_NOT_EXIST_ERROR)
            return null
        }
        else{
            currentMarker.isVisible = false
            hideMarkerList.add(currentMarker)  //set hidemMarkerList
            return currentMarker
        }
    }

    /**
     * hide the market, if we know this is which market we add, we can move it dirrectly
     * and we can avoid searching all markers
     * @param index it is which index of our marker
     */
    fun hideMarker(index : Int) : Marker?{
        if(markerList.size < index){
            Log.e(TAG, INDEX_OUT_OF_RANGE_ERROR)
            return null
        }
        var marker = markerList.get(index)
        marker.isVisible = false
        hideMarkerList.add(marker)   //set hidemMarkerList
        return marker
    }

    /**
     * @param marker we use exist marker to hide the marker
     */
    fun hideMarker(marker: Marker) : Marker?{
        if(!markerList.contains(marker)) {
            Log.e(TAG, MARKET_NOT_EXIST_ERROR)
            return null
        }
        else hideMarkerList.add(marker)
        return marker
    }

    /**
     * remove existing marker
     * @param index
     * @return remove successfully or not
     */
    fun removeMarker(index: Int) : Boolean{
        if(markerList.size < index){
            Log.e(TAG, INDEX_OUT_OF_RANGE_ERROR)
            return false
        }
        var currentMarker = markerList.get(index)
        currentMarker.remove()
        markerList.removeAt(index)
        return true
    }

    /**
     * remove existing marker
     * @param latLng
     * @return remove successfully or not
     */
    fun removeMarker(latLng : LatLng) : Boolean{
        var currentMarker : Marker? = null
        var index = 0
        for(marker in markerList){
            var mLatLng = marker.position  //get latLng
            if(mLatLng.equals(latLng)){
                currentMarker = marker
                currentMarker.remove()
                //here we don't use markerList.remove,
                //because it will search all datas to judge which data is the marker we need
                markerList.removeAt(index)
                break
            }
            index++
        }
        //if currentMarker is null , it means that we does not have the marker
        if(currentMarker == null){
            Log.e(TAG, LATLNG_NOT_EXIST_ERROR)
            return false
        }
        return true
    }

    /**
     * @param marker we use exist marker to remove the marker
     * @param remove successfully or not
     */
    fun removeMarker(marker: Marker) : Boolean{
        var currentMarker : Marker? = null
        var index = 0
        for(cMarker in markerList){
            if(cMarker.equals(marker)){
                currentMarker = marker
                currentMarker.remove()
                //here we don't use markerLiskt.remove,
                //because it will search all datas to judge which data is the marker we need
                markerList.removeAt(index)
                break
            }
            index++
        }
        if(currentMarker == null){
            Log.e(TAG, MARKET_NOT_EXIST_ERROR)
            return false
        }
        return true
    }

    /**
     * This is used to draw a circle to our map, but it only draw a circle, it has an attribute color
     * and default color of our circle is black
     * @param centerLatLng it is the center of our circle
     * @param radius it is radius (unit : meter)
     * @param color if color exists, our circle color will changed
     */
    @JvmOverloads
    fun drawCircleOnMap(centerLatLng: LatLng, radius : Double, color : Int? = null) : Circle{
        var circleOptions = CircleOptions()
        circleOptions.center(centerLatLng)
        circleOptions.radius(radius)
        if(color != null) circleOptions.strokeColor(color)
        var circle = mMap.addCircle(circleOptions)
        return circle
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
     * This is used to forbid the movement of camera when we try to make our camera can not go far away
     * @param centerLatLng our cameraCenter
     */
    fun forbidCameraMove(centerLatLng: LatLng){
        var mapBounds = LatLngBounds(centerLatLng, centerLatLng)
        mMap.setLatLngBoundsForCameraTarget(mapBounds)
        isForbidOrLimitCameraMovement = true
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
        isForbidOrLimitCameraMovement = true
    }

    /**
     * This is used to free our camera move after using forbidCameraMove or limitCameraMove
     */
    fun freeCameraMove(){
        if(!isForbidOrLimitCameraMovement)return
        mMap.setLatLngBoundsForCameraTarget(null)
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

    fun getCameraLatLng() : LatLng{
        return mMap.cameraPosition.target
    }

    /**
     * we can get the screen cooprates of (southwest and northeast)
     * but before we use it, at first it will show (0, 0) and (0, 0), but after movement, it will show correct bounds
     * @return 2 latlngBounds
     */
    fun getMapBounds() : LatLngBounds{
        return mMap.projection.visibleRegion.latLngBounds
    }

    /**
     * return markerList, to help users do other work
     * @return markList
     */
    fun getMarkerList() : List<Marker>{
        return markerList
    }

    /**
     * clear all markers, ignore the others
     */
    fun clearMarkers(){
        for(marker in markerList){
            marker.remove()
        }
        markerList.clear()
    }

    /**
     * clear all datas on map(markers, circles , lines), we can use mMap.clear() to remove all markers
     * but also ,we need to clear our map list
     */
    fun clearAll(){
        markerList.clear()
        mMap.clear()
    }

    /**
     * return our google map
     * @return googleMap
     */
    fun getGoogleMap() : GoogleMap{
        return mMap
    }

    /**
     * open our cluster function
     * @param context
     * @param showAnimation do you want to see the animation? default value is false
     */
    fun startCluster(context : Context, showAnimation : Boolean = false){
        if(isStartCluster)return
        clusterManger = ClusterManager<MyItem>(context, getGoogleMap())
        clusterManger.setAnimation(showAnimation)
        var index = 0
        for(marker in markerList){
            var myItem = MyItem(marker)
            clusterManger.addItem(myItem)
            hideMarker(index)
            index++
        }
        mMap.setOnCameraIdleListener(clusterManger)
        //with a small movement, we will make the cluster begin to work
        setCameraZoom(getZoom() + 0.0001F)
        setCameraZoom(getZoom() - 0.0001F)
        isStartCluster = true
    }

    /**
     * close our cluster function
     */
    fun stopCluster(){
        if(isStartCluster == false)return
        var index = 0
        for(marker in markerList){
            showMarker(index)
            index++
        }
        clusterManger.clusterMarkerCollection.clear()  //clear all cluster info, we need this ,or the cluster circle will not clear
        clusterManger.clearItems()
        clusterManger.setAnimation(false)
        isStartCluster = false
    }

    override fun clone(): Any {
        var baseGoogleMap : BaseGoogleMap? = null
        try {
            baseGoogleMap = super.clone() as BaseGoogleMap
            return baseGoogleMap
        }catch (e : Exception){
            e.printStackTrace()
            return Any()
        }
    }

    /**
     * paint a special bitmap pic form layout for marker icon
     * @param view our view that we want to show
     */
    fun createBitmapFromView(view : View) : Bitmap {
        val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(measureSpec, measureSpec)

        val measuredWidth = view.measuredWidth
        val measuredHeight = view.measuredHeight

        view.layout(0, 0, measuredWidth, measuredHeight)

        val r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        r.eraseColor(Color.TRANSPARENT)

        val canvas = Canvas(r)
        view.draw(canvas)

        return r
    }

    companion object {
        val TAG = "BaseGoogleMap"
        val LATLNG_NOT_EXIST_ERROR = "The latitude and longtitude is not exisiting"
        val MARKET_NOT_EXIST_ERROR = "The marker is not existing"
        val INDEX_OUT_OF_RANGE_ERROR = "The index is out of range"
        val MARKET_NOT_HIDDEN = "The marker has not hidden"
    }
}