# google-map-test
a google map test , make a new class google map (name : BaseGoogleMap), it simplify our code to use google map for Android.
  ATTENTION: There are 3 files you can use, BaseGoogleMap(This file contains is the basic function), map_night_mode_style.json and not_show_map_poi.json, you can move these 3 files to your project, then you can use BaseGoogleMap to make your code.
  This is an APP for Android, we can use APP to test some function of google map,  add marker to map, draw circle on map, change camera, and clustering.
  The APP contains 2 main button, switch and clear all, switch is used to choose the function at upper right, and clear all will delete all markers, circles we have made on map and stop cluster.
  There have been 4 functions(I divide them into 4 parts, marker test, circle test , cameral test and cluster test), They locate at the upper right, you can use switch to choose functions, and then click button to choose what you want to do.
  Warning, it is neccessary to click stop cluster after you click start cluster and start default cluster.
There is some introduction about BaseGoogleMap
1. BaseGoogleMap is the basic class, it simplify our code, and in BaseGoogleMap, I put 2 list(MarkerList and MarkerOptionsList) to control the marker on map, which could help us when we want to do something, we can get marker easily, and we can change markers(like hide some markers, delete some markers, and also add some markers with special img)。
2. we can use pic in res/drawable(I have put some pics in drawable-hdpi, drawable-xhdpi, drawable-xxhdpi), and layout in res/layouts(I have put a value layout named res/layout/value-layout.xml), what you need to do is to initialize your layout(using LayoutInflater), then you can put your view in to the function addMarkerToMap(latLng, title, view)。
3. we can deleteMarker via removeMarkers, There are 3 options。
4. we can getMarkerList to handle our markers, then we can change title, change snippet and all the other things that we need to change.
5. cluster function is complex, I used a markerOptions and index of our marker to create item in cluster. There are some introductions to clusters.
(1) With the help of markerOptionsList, we can make our own renderer(how to draw cluster markers on our map), we can get each attribute, we can keep our icon of our markers(before clustering), title, snippet and so on.
(2) We can start cluster after we add several points on map, or begin with the cluster, it depends on what we want to do. we can use baseGoogleMap.startCluster(context) to begin the cluster.
(3) If we want to stop cluster, we can use baseGoogleMap.stopCluster(), and it will show all points.
(4) If we hide some point before cluster, these markers will be not added to our cluster when we start cluster.
(5) In google map, if so many markers overlap in a small place, our marker click event will become strange that if we click the item, it will always show the bottom marker, and we can never click the others. To solve the proble, I add a function named "setOnCirculateMarkerClick", when you face the situation, you can click one place, and we will show all markers in this small place in circle. But bad news is that the Algorithm is very inmature, I just use a LinkedList to store the datas, and if we click other place or change the zoom of camera, the process will repeat again, so if you have any promption, please contact me. If it is unneccessary, please use "cluster" but not "setOnCirculateMarkerClick".
