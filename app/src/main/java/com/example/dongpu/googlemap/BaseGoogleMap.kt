package com.example.dongpu.googlemap

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.View
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
    private lateinit var clusterItemList : ArrayList<MyItem?>   //help clusterManger to store item

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
     * @param snippet if snippet exists, there will be a snippet if we click marker
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
     * @param snippet if snippet exists, there will be a snippet if we click marker
     * @param view marker icon will be replaced to our pic
     * @return return our maker to help us to do other work to marker
     * it is an example :
     * var view = Inflater.from(context).inflater(R.layout.main, null)  //There is a key point, we should initialize our view at first
     * addMarkerToMap(Latlng(31.1, 32.3), "str" , null, view)
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

    /**
     * @return our markerOptions
     */
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

    /**
     * here we use markOptions to build a marker
     * @param markerOptions
     * @return marker
     */
    private fun createMarker(markerOptions: MarkerOptions) : Marker{
        var marker = mMap.addMarker(markerOptions)
        markerList.add(marker)
        if(isStartCluster) needClusterWhenAddMarker(marker, markerOptions)
        return marker
    }

    /**
     * if we are in cluster, and we need adding marker, then we need to refresh marker in our cluster
     */
    private fun needClusterWhenAddMarker(marker: Marker, markerOptions: MarkerOptions){
        //if we open the state that we need cluster map, then we will hide marker and add item to clusterManager
        marker.isVisible = false
        var myItem = MyItem(markerOptions)
        clusterItemList.add(myItem)
        clusterManger.addItem(myItem)
        slightlyMoveMent()
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
            if(isStartCluster)needClusterWhenShowMarker(index)
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

        if(isStartCluster)needClusterWhenShowMarker(index)
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
            if(isStartCluster)needClusterWhenShowMarker(index)
            else{
                currentMarkerOptions?.visible(true)
                currentMarker.isVisible = true
            }
        }
    }

    /**
     * if we are in cluster, and we need showing hide marker, we need to refresh marker in our cluster
     */
    private fun needClusterWhenShowMarker(index : Int){
        if(index <= 0 || index >= clusterItemList.size) return
        var currentMyItem = clusterItemList.get(index)
        if(currentMyItem != null) return //if it is not null, it means that we have show the marker
        clusterManger.addItem(currentMyItem)
        slightlyMoveMent()
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
                if(isStartCluster)needClusterWhenHideMarker(index)
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
        if(markerList.size <= index){
            Log.e(TAG, INDEX_OUT_OF_RANGE_ERROR)
            return null
        }
        var currentMarker = markerList.get(index)
        var currentMarkerOptions = markerOptionsList.get(index)
        currentMarker.isVisible = false
        currentMarkerOptions.visible(false)
        if(isStartCluster)needClusterWhenHideMarker(index)
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
                if(isStartCluster)needClusterWhenHideMarker(index)
                break;
            }
            index++
        }
        if(currentMarker == null) {
            Log.e(TAG, MARKET_NOT_EXIST_ERROR)
            return null
        }
        else currentMarker.isVisible = false
        return marker
    }

    /**
     * if we are in cluster, and we need hiding marker, we need to refresh marker in our cluster
     */
    private fun needClusterWhenHideMarker(index : Int){
        var currentMyItem = clusterItemList.get(index)
        if(currentMyItem == null) return   //we have removed the item
        clusterManger.removeItem(currentMyItem)
        clusterItemList.set(index, null)
        slightlyMoveMent()
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
        if(isStartCluster)needClusterWhenRemoveMarker(index)
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
                if(isStartCluster)needClusterWhenRemoveMarker(index)
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
                if(isStartCluster)needClusterWhenRemoveMarker(index)
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
     * if we are in cluster, and we need removing marker, we need to refresh marker in our cluster
     */
    private fun needClusterWhenRemoveMarker(index: Int){
        var currentMyItem = clusterItemList.get(index)
        if(currentMyItem == null) return
        clusterManger.removeItem(currentMyItem)
        clusterItemList.removeAt(index)
        slightlyMoveMent()
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
        if(isStartCluster)return   //if we have started cluster, we will never start it again
        isStartCluster = true

        clusterManger = ClusterManager<MyItem>(context, getGoogleMap())
        clusterManger.setAnimation(showAnimation)

        clusterItemList = ArrayList<MyItem?>()

        var myItemRenderer = MyItemRenderer(context, mMap, clusterManger)
        markerList.forEachIndexed { index, marker ->
            if(!marker.isVisible) {
                clusterItemList.add(null)
                return@forEachIndexed //if marker is not visible, we will not add marker to clusterManager
            }
            var markerOptions = markerOptionsList.get(index)
            var myItem = MyItem(markerOptions)
            clusterManger.addItem(myItem)
            clusterItemList.add(myItem)
            hideMarkerWhenClustering(index)   //hide the marker
        }
        clusterManger.renderer = myItemRenderer
        mMap.setOnCameraIdleListener(clusterManger)
        mMap.setOnMarkerClickListener(clusterManger)

        clusterManger.setOnClusterClickListener(onClusterClickListener)
        clusterManger.markerCollection.setOnMarkerClickListener(onMarkerClickListener)
    }

    /**
     * a slightly movement when we want to refresh our cluster
     */
    private fun slightlyMoveMent(){
        var moveMent = 0.000005F
        var currentZoom = getZoom()
        when(currentZoom){
            mMap.maxZoomLevel -> {
                setCameraZoom(currentZoom - moveMent)
                setCameraZoom(currentZoom + moveMent)
            }
            mMap.minZoomLevel -> {
                setCameraZoom(currentZoom + moveMent)
                setCameraZoom(currentZoom - moveMent)
            }
            else -> {
                setCameraZoom(currentZoom + moveMent)
                setCameraZoom(currentZoom - moveMent)
            }
        }
    }

    /**
     * This is used for special situation , when we start cluster, we need hiding all marker we have put before
     */
    private fun hideMarkerWhenClustering(index: Int){
        var marker = markerList.get(index)
        marker.isVisible = false
    }

    /**
     * close our cluster function
     */
    fun stopCluster(){
        if(isStartCluster == false)return  //if we have closed cluster, will will never close it again
        isStartCluster = false

        clusterItemList.forEachIndexed { index, myItem ->
            if(myItem != null) showMarker(index)
        }
        clusterItemList.clear()
        clusterManger.clusterMarkerCollection.clear()  //clear all cluster info, we need this ,or the cluster circle will not clear
        clusterManger.clearItems()
        clusterManger.setAnimation(false)
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

    fun getIsStartCluster() : Boolean{
        return this.isStartCluster
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

    /**
     * we can cal distance between 2 latlngs
     * @param latLng1
     * @param latLng2
     * @return distance
     */
    fun distanceBetween(latLng1 : LatLng, latLng2: LatLng) : Float{
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        var lat1 = latLng1.latitude
        var lon1 = latLng1.longitude

        var lat2 = latLng1.latitude
        var lon2 = latLng1.longitude

        val MAXITERS = 20
        // Convert lat/long to radians
        lat1 *= Math.PI / 180.0
        lat2 *= Math.PI / 180.0
        lon1 *= Math.PI / 180.0
        lon2 *= Math.PI / 180.0

        val a = 6378137.0 // WGS84 major axis
        val b = 6356752.3142 // WGS84 semi-major axis
        val f = (a - b) / a
        val aSqMinusBSqOverBSq = (a * a - b * b) / (b * b)

        val L = lon2 - lon1
        var A = 0.0
        val U1 = Math.atan((1.0 - f) * Math.tan(lat1))
        val U2 = Math.atan((1.0 - f) * Math.tan(lat2))

        val cosU1 = Math.cos(U1)
        val cosU2 = Math.cos(U2)
        val sinU1 = Math.sin(U1)
        val sinU2 = Math.sin(U2)
        val cosU1cosU2 = cosU1 * cosU2
        val sinU1sinU2 = sinU1 * sinU2

        var sigma = 0.0
        var deltaSigma = 0.0
        var cosSqAlpha = 0.0
        var cos2SM = 0.0
        var cosSigma = 0.0
        var sinSigma = 0.0
        var cosLambda = 0.0
        var sinLambda = 0.0

        var lambda = L // initial guess
        for (iter in 0 until MAXITERS) {
            val lambdaOrig = lambda
            cosLambda = Math.cos(lambda)
            sinLambda = Math.sin(lambda)
            val t1 = cosU2 * sinLambda
            val t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda
            val sinSqSigma = t1 * t1 + t2 * t2 // (14)
            sinSigma = Math.sqrt(sinSqSigma)
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda // (15)
            sigma = Math.atan2(sinSigma, cosSigma) // (16)
            val sinAlpha = if (sinSigma == 0.0)
                0.0
            else
                cosU1cosU2 * sinLambda / sinSigma // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha
            cos2SM = if (cosSqAlpha == 0.0)
                0.0
            else
                cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha // (18)

            val uSquared = cosSqAlpha * aSqMinusBSqOverBSq // defn
            A = 1 + uSquared / 16384.0 * // (3)
                    (4096.0 + uSquared * (-768 + uSquared * (320.0 - 175.0 * uSquared)))
            val B = uSquared / 1024.0 * // (4)
                    (256.0 + uSquared * (-128.0 + uSquared * (74.0 - 47.0 * uSquared)))
            val C = f / 16.0 *
                    cosSqAlpha *
                    (4.0 + f * (4.0 - 3.0 * cosSqAlpha)) // (10)
            val cos2SMSq = cos2SM * cos2SM
            deltaSigma = B * sinSigma * // (6)

                    (cos2SM + B / 4.0 * (cosSigma * (-1.0 + 2.0 * cos2SMSq) - B / 6.0 * cos2SM *
                            (-3.0 + 4.0 * sinSigma * sinSigma) *
                            (-3.0 + 4.0 * cos2SMSq)))

            lambda = L + (1.0 - C) * f * sinAlpha *
                    (sigma + C * sinSigma *
                            (cos2SM + C * cosSigma *
                                    (-1.0 + 2.0 * cos2SM * cos2SM))) // (11)

            val delta = (lambda - lambdaOrig) / lambda
            if (Math.abs(delta) < 1.0e-12) {
                break
            }
        }

        val distance = (b * A * (sigma - deltaSigma)).toFloat()
        return distance
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

        @JvmOverloads
        constructor(markerOptions: MarkerOptions){
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