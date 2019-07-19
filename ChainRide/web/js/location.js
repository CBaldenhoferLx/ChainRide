
var currentLocation = "Detecting location...";
var currentCoords;
var oldCoords = null;
var updateDistance = 0.01;

navigator.geolocation.watchPosition(locationSuccess, locationFail, {enableHighAccuracy:true, timeout: 2000});

function checkNewCoords() {
    
}

function locationSuccess(pos) {
    // Location found, show map with these coordinates
    currentLocation = pos;
    currentCoords = { lat: currentLocation.coords.latitude, lng: currentLocation.coords.longitude};
    var locStr = currentLocation.coords.latitude + ", " + currentLocation.coords.longitude + " (Acc: " + currentLocation.coords.accuracy + ")";
    $("#currentLocation").html(locStr);
    $("#currentLocationGL").html(locStr);
    $("#currentLocationFO").html(locStr);
    console.log(pos);
    
    if (!oldCoords || getDistanceFromLatLonInKmPos(oldCoords, currentCoords) > updateDistance) {
        $.event.trigger({
            type: "locationChanged"
        });
        
        oldCoords = currentCoords;
    }
}

function locationFail(error) {
    // error
}

function getDistanceFromLatLonInKmPos(pos1, pos2) {
    return getDistanceFromLatLonInKm(pos1.lat, pos1.lng, pos2.lat, pos2.lng);
}

function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
    var R = 6371; // Radius of the earth in km
    var dLat = deg2rad(lat2-lat1);  // deg2rad below
    var dLon = deg2rad(lon2-lon1); 
    var a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ; 
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    var d = R * c; // Distance in km
    return d;
}

function deg2rad(deg) {
    return deg * (Math.PI/180)
}