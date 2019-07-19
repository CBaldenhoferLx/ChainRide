
var baseUrl = window.location.protocol + "//" + window.location.host + "/ChainRide/ws/";

var userId = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 3).toUpperCase();
var isLeader = false;
var isFollower = false;

$(document).on("locationChanged", function() {
    console.log("Location changed");
    
    if (isLeader) {
        leader.sendLocation();
    }
    
    if (isFollower) {
        follower.followLeader();
    }
});

var leaders = {

    pollingEnabled: false,
    pollingHandle: null,
    pollingIntervalMs: 3000,
    listElementName: null,

    setPollingEnabled: function(enable) {
        this.pollingEnabled = enable;

        if (this.pollingEnabled) {
            this.pollingHandle = setInterval(function() { leaders.updateLeaders() }, this.pollingIntervalMs);
        } else {
            clearInterval(this.pollingHandle);
        }
    },

    updateLeaders: function() {
        console.log("updateLeaders");
        
        $.get(baseUrl + 'sessions/leaders', function(data) {
            console.log(data);
            
            $(leaders.listElementName).empty();

            data.forEach(function(e) {
                var listItem = "<li><a href='javascript:selectLeader(\"" + e.name + "\");'>" + e.name + "</a></li>";
                $(leaders.listElementName).append(listItem);
            });
        });
    },
    
    setElement: function(e) {
        this.listElementName = e;
    }
};

var leader = {
    registerEnabled: false,
    registerHandle: null,
    registerIntervalMs: 8000,
    
    sendLocationEnabled: false,
    sendLocationHandle: null,
    //sendLocationIntervalMs: 5000,
    sendLocationLastTs: 0,
    
    setRegisterEnabled: function(enable) {
        this.registerEnabled = enable;
        
        if (this.registerEnabled) {
            this.registerHandle = setInterval(this.registerLeader, this.registerIntervalMs);
        } else {
            clearInterval(this.registerHandle);
        }
    },
    
    registerLeader: function() {
        console.log("registerLeader");
        
        $.get(baseUrl + 'sessions/registerLeader?id=' + userId, function(data) {
        });
    },
    
    setSendLocationEnabled: function(enable) {
        this.sendLocationEnabled = enable;
        
        /*
        if (this.sendLocationEnabled) {
            this.sendLocationHandle = setInterval(this.sendLocation, this.sendLocationIntervalMs);
        } else {
            clearInterval(this.sendLocationHandle);
        }*/
    },
    
    sendLocation: function(forceSend) {
        if (!forceSend) {
            if (!this.sendLocationEnabled) return;
            if (Date.now() - this.sendLocationLastTs < 3000) return;
        }
        
        this.sendLocationLastTs = Date.now();
        
        $.get(baseUrl + 'loc/' + userId + '/update?lat=' + currentCoords.lat + '&lng=' + currentCoords.lng, function(data) {
        });
    }

};

var follower = {
    
    leaderToFollow: "",
    followEnabled: false,
    followHandle: null,
    followLastTs: 0,
    //followIntervalMs: leader.sendLocationIntervalMs,        // use the same
    leaderDistanceElementName: null,
    tbtElementName: null,
    
    setFollowEnabled: function(enable) {
        this.followEnabled = enable;
        
        /*
        if (this.followEnabled) {
            this.followHandle = setInterval(this.followLeader, this.followIntervalMs);
        } else {
            clearInterval(this.followHandle);
        }*/
    },
    
    followLeader: function(forceSend) {
        if (!forceSend) {
            if (!this.followEnabled) return;
            if (Date.now() - this.followLastTs < 3000) return;
        }
        
        this.followLastTs = Date.now();
        
        $.get(baseUrl + 'loc/guidance?lid=' + follower.leaderToFollow + '&lat=' + currentCoords.lat + '&lng=' + currentCoords.lng, function(data) {
            console.log(data);
            
            $(follower.leaderDistanceElementName).html(data.ld + " m");
            
            $(follower.tbtElementName).empty();

            data.maneuvers.forEach(function(e) {
                var listItem = "<li>" + e.m + " in " + e.d + " m</li>";
                $(follower.tbtElementName).append(listItem);
            });
        });
    }
};