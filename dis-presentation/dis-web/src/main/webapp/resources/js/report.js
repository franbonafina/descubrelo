var reportWindow;

function OpenReport(url) {
	// console.log("OpenReport con url: " + url);
	reportWindow = window.open(url, '_blank');
	
	// Security Problem
	// Uncaught SecurityError: Blocked a frame with origin "thispage:XXXX" 
	// from accessing a frame with origin "theotherpage.com:XYXY". Protocols, domains, 
	// and ports must match.(anonymous function) @ VMXXXXXX:X
	/*
	 setTimeout(function(){
		console.log("Value: " + (reportWindow.document.readyState !== 'complete'));
		if(reportWindow.document.readyState !== 'complete'){
			alert('no cargo');
		}else{
			alert('cargo');
		}
	} , 3000);
	*/
}
