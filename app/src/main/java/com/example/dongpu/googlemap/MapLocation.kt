package com.example.dongpu.googlemap

import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.renderscript.ScriptGroup
import android.util.Log
import android.widget.Toast
import com.example.dongpu.googlemap.R.string.google_maps_key
import org.w3c.dom.Document
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by dong.pu on 2019/7/1.
 */
class MapLocation {

    private var conn: HttpURLConnection? = null

    fun getDatas() {
        Thread(r1).start()

    }

    var r1 = object : Runnable{

        override fun run() {
            Log.d("pudong","here")
            var str_url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=23.055291,113.391802" +
                    "&destination=23.046604,113.397510" +
                    "&key=AIzaSyBLKAq5eIm6IKdbGQBcNtKWcK0a5lI0f-k"  //这里需要新的Key,不能和地图key重复
            var url = URL(str_url)
            var conn = url.openConnection() as HttpURLConnection
            Log.d("pudong","conn = " + conn.responseCode.toString())
            var inputStream : InputStream? = null
            var inputStreamReader: InputStreamReader? = null
            var reader: BufferedReader? = null
            var resultBuffer = StringBuffer()
            var tempLine: String? = null
            inputStream = conn.inputStream
            inputStreamReader = InputStreamReader(inputStream)
            reader = BufferedReader(inputStreamReader)

            tempLine = reader.readLine()
            while (tempLine!=null){
                resultBuffer.append(tempLine)
                tempLine = reader.readLine()
            }

            if(reader!=null)reader.close()
            if(inputStreamReader!=null)inputStreamReader.close()
            if(inputStream!=null)inputStream.close()
            Log.d("pudong",resultBuffer.toString())
        }
    }

    var runnable = object : Runnable{
        override fun run() {
            var str_url = "http://maps.google.com/maps/api/directions/xml?origin=23.055291,113.391802&destination=23.046604,113.397510&sensor=false&mode=driving"
            var url = URL(str_url)

            var conn = url.openConnection() as HttpURLConnection

            var inputStream : InputStream? = null
            var inputStreamReader: InputStreamReader? = null
            var reader: BufferedReader? = null
            var resultBuffer = StringBuffer()
            var tempLine: String? = null

            Log.d("pudong",conn.responseCode.toString())
            inputStream = conn.inputStream
            inputStreamReader = InputStreamReader(inputStream)
            reader = BufferedReader(inputStreamReader)

            tempLine = reader.readLine()
            while (tempLine!=null){
                resultBuffer.append(tempLine)
                tempLine = reader.readLine()
            }

            if(reader!=null)reader.close()
            if(inputStreamReader!=null)inputStreamReader.close()
            if(inputStream!=null)inputStream.close()
            Log.d("pudong",resultBuffer.toString())
        }
    }
}