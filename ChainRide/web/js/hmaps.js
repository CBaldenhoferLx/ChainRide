var lastUpdateTBT = 0;

function updateTBT() {
	if (Date.now()-lastUpdateTBT>3000) {			// limit to 3 sec
		
		lastUpdateTBT = Date.now();
	}
}