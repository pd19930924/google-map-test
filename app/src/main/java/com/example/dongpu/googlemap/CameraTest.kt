package com.example.dongpu.googlemap

import android.content.Context
import android.widget.Toast
import com.example.dongpu.googlemap.base_google_map.BaseGoogleMap
import com.google.android.gms.maps.model.LatLng

/**
 * Created by dong.pu on 2019/7/10.
 */
class CameraTest {

    private lateinit var baseGoogleMap : BaseGoogleMap
    lateinit var context: Context

    constructor(baseGoogleMap: BaseGoogleMap){
        this.baseGoogleMap = baseGoogleMap
    }

    fun addZoom(){
        var newZoom = getCurrentZoom() + 1F
        setZoom(newZoom)
        showToast()
    }

    fun subStractZoom(){
        var newZoom = getCurrentZoom() - 1F
        setZoom(newZoom)
        showToast()
    }

    fun forbidCameraMove(){
        baseGoogleMap.forbidCameraMove(getCurrentLatLng())
    }

    fun freeCameraMove(){
        baseGoogleMap.freeCameraMove()
    }

    private fun getCurrentLatLng() : LatLng{
        return baseGoogleMap.getCameraLatLng()
    }

    private fun getCurrentZoom() : Float{
        return baseGoogleMap.getZoom()
    }

    private fun setZoom(zoom : Float){
        baseGoogleMap.setCameraZoom(zoom)
    }

    private fun showToast(){
        Toast.makeText(context,"current zoom = " + getCurrentZoom(), Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TAG = "CameraTest"
    }
}