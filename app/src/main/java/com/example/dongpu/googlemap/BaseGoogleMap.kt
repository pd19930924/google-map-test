package com.example.dongpu.googlemap

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.View
import com.example.dongpu.googlemap.cluster_test.MyItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlin.collections.ArrayList

/**
 * Created by dong.pu on 2019/6/26.
 */
class BaseGoogleMap : Cloneable {

    private lateinit var mMap : GoogleMap

    private lateinit var markerList : ArrayList<Marker>  //it is used to remove markers, hide markers
    private lateinit var markerOptionsList : ArrayList<MarkerOptions>  //it is the same with marker , but it's duty is to burden other works, like get icon in makrer

    private lateinit var clusterManger: ClusterManager<MyItem>  //it is used to storage clusterManger

    private var isForbidOrLimitCameraMovement : Boolean = false  //it is used to judge whether we have use limitCameraMove or forbidCameraMove
    private var isStartCluster : Boolean = false //it is used to judge whether we have start cluster

    constructor(mMap: GoogleMap){
        this.mMap = mMap
        this.markerList = ArrayList<Marker>()
        this.markerOptionsList = ArrayList<MarkerOptions>()
    }

    /**
     * This is used to add a marker to Map
     * @param point the marker that we need to add
     * @param title if title exists, there will be a title if we click marker
     * @param drawableResouce if drawableResouce exists, marker icon will be replaced to our pic
     * it is an example : addMarkerToMap(Latlng(31.1, 32.3), "str" , resouce.getDrawable(R.drawable.pic))
     */
    @JvmOverloads
    fun addMarkerToMap(point : LatLng, title : String? = null, snippet : String? = null, drawableResouce : Int? = null) : Marker{
        var bitmapDescriptor : BitmapDescriptor? = null
        if(drawableResouce != null){
            bitmapDescriptor = BitmapDescriptorFactory.fromResource(drawableResouce)
        }
        var markerOptions = createMarkerOptions(point, title, snippet, bitmapDescriptor)
        var marker = createMarker(markerOptions)
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
    fun addMarkerToMap(point : LatLng, title : String? = null, snippet : String? = null, view : View) : Marker{
        var bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(createBitmapFromView(view))
        var markerOptions = createMarkerOptions(point, title, snippet, bitmapDescriptor)
        var marker = createMarker(markerOptions)
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
            var markerOptions = createMarkerOptions(point)
            createMarker(markerOptions)
        }
        return markerList
    }

    @JvmOverloads
    private fun createMarkerOptions(position : LatLng, title : String? = null, snippet: String? = null, icon : BitmapDescriptor? = null) : MarkerOptions{
        var markerOptions = MarkerOptions()
        markerOptions.position(position)
        markerOptions.title(title)
        markerOptions.snippet(snippet)
        markerOptions.icon(icon)
        markerOptionsList.add(markerOptions)
        return markerOptions
    }

    private fun createMarker(markerOptions: MarkerOptions) : Marker{
        var marker = mMap.addMarker(markerOptions)
        markerList.add(marker)
        needClusterWhenAddMarker(marker ,markerOptions)
        return marker
    }

    private fun needClusterWhenAddMarker(marker : Marker, markerOptions: MarkerOptions){
        //if we open the state that we need cluster map, then we will hide marker and add item to clusterManager
        if(isStartCluster){
            marker.isVisible = false  //hide marker
            var myItem = MyItem(markerList.size - 1, markerOptions)
            clusterManger.addItem(myItem)
        }
    }

    /**
     * show hide markers if we have hide it
     * @param latLng
     */
    fun showMarker(latLng: LatLng){
        var currentMarker : Marker? = null
        var currentMarkerOptions : MarkerOptions? = null
        var index = 0
        for(marker in markerList){
            var mLatLng = marker.position
            if(mLatLng.equals(latLng)){
                currentMarker = marker
                currentMarkerOptions = markerOptionsList.get(index)

                break
            }
            index++
        }
        if(currentMarker == null)Log.e(TAG, LATLNG_NOT_EXIST_ERROR)
        else{
            if(isStartCluster)needClusterWhenShowMarker(index, currentMarker, currentMarkerOptions!!)
            else{
                currentMarkerOptions?.visible(true)
                currentMarker.isVisible = true
            }
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
        var currentMarker = markerList.get(index)
        var currentMarkerOptions = markerOptionsList.get(index)

        if(isStartCluster)needClusterWhenShowMarker(index, currentMarker, currentMarkerOptions!!)
        else{
            currentMarkerOptions.visible(true)
            currentMarker.isVisible = true
        }
    }

    /**
     * @param marker we use exist marker to show marker
     */
    fun showMarker(marker: Marker){
        var currentMarker : Marker? = null
        var currentMarkerOptions : MarkerOptions? = null
        var index = 0
        for(cMarker in markerList){
            if(cMarker.equals(marker)){
                currentMarker = cMarker
                currentMarkerOptions = markerOptionsList.get(index)
                break;
            }
            index++
        }
        if(currentMarker == null) {
            Log.e(TAG, MARKET_NOT_EXIST_ERROR)
        }else{
            if(isStartCluster)needClusterWhenShowMarker(index, currentMarker, currentMarkerOptions!!)
            else{
                currentMarkerOptions?.visible(true)
                currentMarker.isVisible = true
            }
        }
    }

    private fun needClusterWhenShowMarker(index : Int, marker: Marker, markerOptions: MarkerOptions){
        if(clusterManger.markerCollection.markers.contains(marker))return
        var myItem = MyItem(index, markerOptions)
        clusterManger.addItem(myItem)
    }

    /**
     * hide the marker, we need to search all markers to hide the marker
     * @param latLng
     */
    fun hideMarker(latLng: LatLng) : Marker?{
        var currentMarker : Marker? = null
        var index = 0
        for(marker in markerList){
            var mLatLng = marker.position  //get latLng
            if(mLatLng.equals(latLng)){
                currentMarker = marker
                var currentMarkerOptions = markerOptionsList.get(index)
                currentMarkerOptions.visible(false)
                break
            }
            index++
        }
        //if currentMarker is null , it means that we does not have the marker
        if(currentMarker == null){
            Log.e(TAG, LATLNG_NOT_EXIST_ERROR)
            return null
        }
        else currentMarker.isVisible = false
        return currentMarker
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
        var currentMarker = markerList.get(index)
        var currentMarkerOptions = markerOptionsList.get(index)
        currentMarker.isVisible = false
        currentMarkerOptions.visible(false)
        return currentMarker
    }

    /**
     * @param marker we use exist marker to hide the marker
     */
    fun hideMarker(marker: Marker) : Marker?{
        var currentMarker : Marker? = null
        var index = 0
        for(cMarker in markerList){
            if(cMarker.equals(marker)){
                currentMarker = cMarker
                var currentMarkerOptions = markerOptionsList.get(index)
                currentMarkerOptions.visible(false)
                break;
            }
            index++
        }
        if(currentMarker == null) {
            Log.e(TAG, MARKET_NOT_EXIST_ERROR)
            return null
        }
        else currentMarker.isVisible = true
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
        markerOptionsList.removeAt(index)
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
                markerOptionsList.removeAt(index)
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
                markerOptionsList.removeAt(index)
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
     * we will forbidCemera in current place
     */
    fun forbidCameraMove(){
        var centerLatLng = getCameraLatLng()
        forbidCameraMove(centerLatLng)
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
        markerOptionsList.clear()
    }

    /**
     * clear all datas on map(markers, circles , lines), we can use mMap.clear() to remove all markers
     * but also ,we need to clear our map list
     */
    fun clearAll(){
        markerList.clear()
        markerOptionsList.clear()
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
     * @param onMarkerClickListener what do you want to do when you click marker
     */
    @JvmOverloads
    fun startCluster(context : Context, showAnimation : Boolean = false,
                     onClusterClickListener: ClusterManager.OnClusterClickListener<MyItem>? = null,
                     onMarkerClickListener: GoogleMap.OnMarkerClickListener? = null){
        if(isStartCluster)return   //if we have started cluster, will will never start it again
        clusterManger = ClusterManager<MyItem>(context, getGoogleMap())
        clusterManger.setAnimation(showAnimation)

        var myItemRenderer = MyItemRenderer(context, mMap, clusterManger)
        markerList.forEachIndexed { index, marker ->
            if(!marker.isVisible)return@forEachIndexed  //if marker is not visible, we will not add marker to clusterManager
            var markerOptions = markerOptionsList.get(index)
            var myItem = MyItem(index, markerOptions)
            clusterManger.addItem(myItem)
            hideMarkerWhenClustering(index)   //hide the marker
        }
        clusterManger.renderer = myItemRenderer
        mMap.setOnCameraIdleListener(clusterManger)
        mMap.setOnMarkerClickListener(clusterManger)

        slightlyMoveMent()

        clusterManger.setOnClusterClickListener(onClusterClickListener)
        clusterManger.markerCollection.setOnMarkerClickListener(onMarkerClickListener)
        //with a small movement, we will make the cluster begin to work

        isStartCluster = true
    }

    /**
     * a slightly movement when we want to refresh our cluster
     */
    private fun slightlyMoveMent(){
        setCameraZoom(getZoom() + 0.0001F)
        setCameraZoom(getZoom() - 0.0001F)
    }

    private fun hideMarkerWhenClustering(index: Int){
        var marker = markerList.get(index)
        marker.isVisible = false
    }

    /**
     * close our cluster function
     */
    fun stopCluster(){
        if(isStartCluster == false)return  //if we have closed cluster, will will never close it again
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

    /**
     * This is used to start our night mode, when we open night mode or mode auto, the map will change
     * @param context
     */
    fun startDayNightMode(context: Context){
        var isNight = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES  //it is used to judge it is day or night
        if(isNight){
            //our resource located at res/raw/map_night_mode_style.xml
            changeToNightMode(context)
        }
    }

    /**
     * This is used to change our map into dark night
     * @param context
     */
    fun changeToNightMode(context: Context){
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_night_mode_style))
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


    override fun toString(): String {
        return super.toString()
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
        val LATLNG_NOT_EXIST_ERROR = "The position is not exisiting"
        val MARKET_NOT_EXIST_ERROR = "The marker is not existing"
        val INDEX_OUT_OF_RANGE_ERROR = "The index is out of range"
        val MARKET_NOT_HIDDEN = "The marker has not hidden"
    }

    class MyItem : ClusterItem {

        private var mPosition : LatLng? = null
        private var mSnippet : String? = null
        private var mTitle : String? = null

        var markerOptions : MarkerOptions? = null

        //This id is used to store the index of our maker
        //we can use the id to restore our marker(after stop cluster)
        var id = 0

        @JvmOverloads
        constructor(id : Int, markerOptions: MarkerOptions){
            this.id = id
            this.mPosition = markerOptions.position
            this.mTitle = markerOptions.title
            this.mSnippet = markerOptions.snippet
            this.markerOptions = markerOptions
        }

        override fun getPosition(): LatLng {
            return mPosition!!
        }

        override fun getSnippet(): String? {
            return mSnippet
        }

        override fun getTitle(): String? {
            return mTitle
        }

    }

    class MyItemRenderer : DefaultClusterRenderer<MyItem> {

        constructor( context : Context, mMap: GoogleMap, clusterManager: ClusterManager<MyItem>) : super(context,mMap, clusterManager)

        override fun onBeforeClusterItemRendered(item: MyItem?, markerOptions: MarkerOptions?) {
            var myMarkerOptions = item!!.markerOptions!!
            markerOptions!!.title(myMarkerOptions.title)
            markerOptions!!.snippet(myMarkerOptions.snippet)
            markerOptions!!.icon(myMarkerOptions.icon)
        }

        override fun onBeforeClusterRendered(cluster: Cluster<MyItem>?, markerOptions: MarkerOptions?) {
            super.onBeforeClusterRendered(cluster, markerOptions)  //default pic
        }

        override fun shouldRenderAsCluster(cluster: Cluster<MyItem>?): Boolean {
            return cluster!!.size > 1
        }
    }
}