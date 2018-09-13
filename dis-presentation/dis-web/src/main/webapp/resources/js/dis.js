$(window).load(function() {	
	// Deshabilita eventos de un checkbox disabled 
	// $(".dis-checkbox-fix .ui-selectlistbox-item.ui-state-disabled .ui-chkbox-box").off();
});

$(document).ready(function() {
	resize();
});

$(window).resize(function(){
	resize();
});

$(window).scroll(function () {
	resize();
});

function resize() {
	if ($('#layout-portlets-cover').height() > 500 || $(window).height() > 500 	) {
		
		if($('#layout-portlets-cover').height() > $(window).height()){
			$('#layout-menubar').css({'height':($('#layout-portlets-cover').height())+'px'});
		}else{
			$('#layout-menubar').css({'height':($(window).height() - 55)+'px'});
		}
		
	}else{
		$('#layout-menubar').css({'height': '500px'});
	}
}

var spinnerOptions = {
		  lines: 13, // The number of lines to draw
		  length: 20, // The length of each line
		  width: 10, // The line thickness
		  radius: 30, // The radius of the inner circle
		  scale: 0.5, // Scales overall size of the spinner
		  corners: 1, // Corner roundness (0..1)
		  rotate: 0, // The rotation offset
		  direction: 1, // 1: clockwise, -1: counterclockwise
		  color: '#22508A', // #rgb or #rrggbb or array of colors
		  speed: 1, // Rounds per second
		  trail: 60, // Afterglow percentage
		  shadow: false, // Whether to render a shadow
		  hwaccel: false, // Whether to use hardware acceleration
		  className: 'spinner', // The CSS class to assign to the spinner
		  zIndex: 2e9, // The z-index (defaults to 2000000000)
		  top: '50%', // Top position relative to parent
		  left: '50%' // Left position relative to parent
		};

var spinnerTarget = document.getElementById('spinner-target');
var globalSpinner = new Spinner(spinnerOptions );	

function ajaxStatusError() {
	globalSpinner.stop()
	$('#layout-header').hide();
	$('#layout-menubar').hide();
	$('#layout-portlets-cover').hide();
	$('#ajaxErrorPage').show()
}

// It scroll to top of the page if it finds that there is at least one faces message.
function scrollOnMessageToTop() {
	var res = $('.ui-messages :first-child').eq(0)
	
	if(res.length > 0) {
		$("body").animate({scrollTop: 0}, "slow")
	}	
}

// Pagination and column toggler fix.
function updateToggles(widget) {
    $( widget.jqId + ' .ui-chkbox .ui-chkbox-box').each(function() {
        var chkbox = $(this);
        if(chkbox.hasClass('ui-state-active')) {
            widget.check(chkbox);
        }
        else {
            widget.uncheck(chkbox);
        }
    });
}
 
// Table Filters KeyUp Function
var delayFilterKeyUpTimeOut = 0;
var delayFilterKeyUp = function(widgetTableName) {
	 clearTimeout (delayFilterKeyUpTimeOut);
	 delayFilterKeyUpTimeOut = setTimeout(function(){
		 PF(widgetTableName).filter();
	 }, 2000);
}

var removeDuplicatedToggler = function(dir){
	var toggler = $(dir);
	toggler.slice(0,toggler.length -1).each(function(a,d){ 
		$(d).remove();
	});
	$(dir).css('display','none');
}

var HTTP_STATUS_CODES = {
    'CODE_200' : 'OK',
    'CODE_201' : 'Created',
    'CODE_202' : 'Accepted',
    'CODE_203' : 'Non-Authoritative Information',
    'CODE_204' : 'No Content',
    'CODE_205' : 'Reset Content',
    'CODE_206' : 'Partial Content',
    'CODE_300' : 'Multiple Choices',
    'CODE_301' : 'Moved Permanently',
    'CODE_302' : 'Found',
    'CODE_303' : 'See Other',
    'CODE_304' : 'Not Modified',
    'CODE_305' : 'Use Proxy',
    'CODE_307' : 'Temporary Redirect',
    'CODE_400' : 'Bad Request',
    'CODE_401' : 'Unauthorized',
    'CODE_402' : 'Payment Required',
    'CODE_403' : 'Forbidden',
    'CODE_404' : 'Not Found',
    'CODE_405' : 'Method Not Allowed',
    'CODE_406' : 'Not Acceptable',
    'CODE_407' : 'Proxy Authentication Required',
    'CODE_408' : 'Request Timeout',
    'CODE_409' : 'Conflict',
    'CODE_410' : 'Gone',
    'CODE_411' : 'Length Required',
    'CODE_412' : 'Precondition Failed',
    'CODE_413' : 'Request Entity Too Large',
    'CODE_414' : 'Request-URI Too Long',
    'CODE_415' : 'Unsupported Media Type',
    'CODE_416' : 'Requested Range Not Satisfiable',
    'CODE_417' : 'Expectation Failed',
    'CODE_500' : 'Internal Server Error',
    'CODE_501' : 'Not Implemented',
    'CODE_502' : 'Bad Gateway',
    'CODE_503' : 'Service Unavailable',
    'CODE_504' : 'Gateway Timeout',
    'CODE_505' : 'HTTP Version Not Supported'
};
