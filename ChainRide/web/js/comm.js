/* global currentCoords */

var isDebug = getURLParam("isDebug");
var baseUrl = window.location.protocol + "//" + window.location.host + "/ChainRide/ws/";

function initComm() {
    $(document).on("locationChanged", function() {
        console.log("Location changed");
    });
    
    user.init();
}

function getURLParam(name){
    var results = new RegExp('[\?&]' + name + '=([^]*)').exec(window.location.href);
    if (results==null){
       return null;
    }
    else{
       return results[1] || 0;
    }
}

function configureLeader() {
    configureSession(true, null);
}

function configureFollower(newLeader) {
    configureSession(false, newLeader);
}

function configureSession(isLeader, leaderId) {
    user.isLeader = isLeader;
    user.isFollower = !isLeader;
    follower.leaderToFollow = leaderId;
    
    leader.setUpdateEnabled(isLeader);
    if (isLeader) leader.update();

    follower.setFollowEnabled(!isLeader);
    if (!isLeader) follower.followLeader();
}

var user = {
    id: null,
    isLeader: false,
    isFollower: false,
    
    init: function() {
        if (!isDebug) this.id = document.cookie;
        if (!this.id || this.id.length<3) {
            this.setId(Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 3).toUpperCase());
        }
    },
    
    setId: function(id) {
        this.id = id;
        if (!isDebug) document.cookie = id;
    }
};

var leaders = {

    pollingEnabled: false,
    pollingHandle: null,
    pollingIntervalMs: 3000,
    listElementName: "#leaderList",

    setPollingEnabled: function(enable) {
        this.pollingEnabled = enable;

        if (this.pollingEnabled) {
            this.pollingHandle = setInterval(function() { leaders.updateLeaders(); }, this.pollingIntervalMs);
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
    }
    
};

var leader = {
    updateEnabled: false,
    updateHandle: null,
    updateIntervalMs: 5000,
    followersListElementName: "#followerList",
    
    setUpdateEnabled: function(enable) {
        this.updateEnabled = enable;
        
        if (this.updateEnabled) {
            this.updateHandle = setInterval(this.update, this.updateIntervalMs);
        } else {
            clearInterval(this.updateHandle);
        }
    },
    
    update: function() {
        if (!currentCoords) return;
        
        $.get(baseUrl + 'sessions/' + user.id + '/leader?lat=' + currentCoords.lat + '&lng=' + currentCoords.lng, function(data) {
            
            console.log(data);
            
            $(leader.followersListElementName).empty();

            data.forEach(function(e) {
                var distance = getDistanceFromLatLonInKmPos(currentCoords, e.loc);
                
                var listItem = "<li>Follower&nbsp;" + e.id + ", Distance: " + distance + " m</li>";
                $(leader.followersListElementName).append(listItem);
            });
            
        });
    }

};

var follower = {
    
    leaderToFollow: "",
    followEnabled: false,
    followHandle: null,
    followIntervalMs: leader.updateIntervalMs,        // use the same
    leaderDistanceElementName: "#distanceToLeader",
    tbtElementName: "#tbtList",
    
    setFollowEnabled: function(enable) {
        this.followEnabled = enable;
        
        if (this.followEnabled) {
            this.followHandle = setInterval(this.followLeader, this.followIntervalMs);
        } else {
            clearInterval(this.followHandle);
        }
    },
    
    followLeader: function() {
        if (!currentCoords) return;
        
        $.get(baseUrl + 'sessions/' + user.id + '/follow?lid=' + follower.leaderToFollow + '&lat=' + currentCoords.lat + '&lng=' + currentCoords.lng, function(data) {
            console.log(data);
            
            if (data.leader) {
                var dist = (getDistanceFromLatLonInKmPos(leader.loc, currentCoords) / 1000).toFixed(0);
                $(follower.leaderDistanceElementName).html(dist + " m");
            }
            
            $(follower.tbtElementName).empty();

            data.maneuvers.forEach(function(e) {
                var listItem = "<li>" + e.m + " in " + e.d + " m</li>";
                $(follower.tbtElementName).append(listItem);
            });
        });
    }
};