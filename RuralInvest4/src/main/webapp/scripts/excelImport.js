$(document).ready(function () {
	$('#uploader-button').button();
	
	$("#importExcel").click(function() { 
		setupFileUploader();
		$("#upload-dialog").dialog("open");
	}); 
	
	
	 $("#upload-dialog").dialog({ 
		 autoOpen: false, 
		 height: 400, 
		 width: 550, 
		 modal: true, 
		 buttons: { 
			 Cancel: function() { 
				 $( this ).dialog( "close" );
				 $('#uploader-button').show();
			 } 
		 }, 
		 close: function() { 
			 $('#uploader-button').show(); 
		} 
	 }); 
});

function uploadBlock(blockId) {
	uploadBlockId=blockId;
	setupFileUploader();
	$("#upload-dialog").dialog("open");
}

function setupFileUploader() {
	var endpoint = submitUrlBase+uploadBlockId;
	$('#jquery-wrapped-fine-uploader').fineUploader({
      request: {
        endpoint: endpoint
      },
      multiple: false,
      button: $("#uploader-button"),
      validation: {
    	 allowedExtensions: ['xlsx'],
    	  sizeLimit: 51200 // 500 kB = 500 * 1024 bytes
		}
      ,
      failedUploadTextDisplay: {
    	  mode:'none'
      }
    }).on('error', function (event, id, name, reason) {
    	$('#uploader-error').empty();
    	$('#uploader-error').append('<div class="alert alert-error"><h3><spring:message code="import.excel.fail"/></h3>' + reason + '</div>');
    	$('#uploader-button').show();
    }).on('complete', function (event, id, name, responseJSON) {
    	if (responseJSON.success) {
			location.reload(true);
		}
    }).on('showMessage', function (event, id, name, message) {
    	$('#uploader-error').append('<div class="alert alert-error">' + message + '</div>');
    	$('#uploader-button').show();
    }).on('submit', function(event, id, name) {
    	$('#uploader-error').empty();
    	$('#uploader-button').hide();
    });
}