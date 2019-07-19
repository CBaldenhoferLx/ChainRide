var lastUpdateTBT = 0;
var directionsService = new google.maps.DirectionsService();

function updateTBT() {
	if (Date.now()-lastUpdateTBT>3000) {			// limit to 3 sec
		
		var myPos = new google.maps.LatLng(currentCoords.lat, currentCoords.lng);
		var leaderPos = new google.maps.LatLng(leaderLocation.lat, leaderLocation.lng);
		
		var request = {
			origin: myPos,
			destination: leaderPos,
			travelMode: 'DRIVING'
		};
		
		directionsService.route(request, function(result, status) {
			if (status == 'OK') {
				console.log(result);
				
				if (result.routes.length>0) {
					var route = result.routes[0];
					
					if (route.legs.length>0) {
						var leg = route.legs[0];
						
						$("#tbtList").empty();
						
						leg.steps.forEach(function(step){
							
							var listItem = "<li>" + step.maneuver + " in " + step.distance.text + "</li>";
							$("#tbtList").append(listItem);
							
						});
					}
				}
			}
		});
		
		
		lastUpdateTBT = Date.now();
	}
}