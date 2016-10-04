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

function uploadConfig() {
	$("#upload-dialog").dialog('option', 'title', titleConfig);
	$('#upload-description').text(uploadDescConfig);
	submitUrlBase='admin/import';
	setupFileUploader(true);
	$("#upload-dialog").dialog("open");
}

function uploadRestore() {
	$("#upload-dialog").dialog('option', 'title', titleRestore);
	$('#upload-description').text(uploadDescRestore);
	submitUrlBase='admin/restore';
	setupFileUploader(true);
	$("#upload-dialog").dialog("open");
}

function uploadBlock(blockId) {
	uploadBlockId=blockId;
	setupFileUploader();
	$("#upload-dialog").dialog("open");
}

function setupFileUploader(isRestore) {
	var endpoint = submitUrlBase+uploadBlockId;
	$('#jquery-wrapped-fine-uploader').fineUploader({
      request: {
        endpoint: endpoint
      },
      multiple: false,
      button: $("#uploader-button"),
      validation: {
    	 allowedExtensions: isRestore ? ['riv'] : ['xlsx'],
    	  sizeLimit: isRestore 
    	  		? 1024000
    	  		: 51200 // 500 kB = 500 * 1024 bytes 
    			
		}
      ,
      failedUploadTextDisplay: {
    	  mode:'none'
      }
    }).on('error', function (event, id, name, reason) {
    	$('#uploader-error-message').text(reason);
    	$('#uploader-error').show();
    	$('#uploader-button').show();
    }).on('complete', isRestore 
    		? function(event, id, name, responseJSON) { if (responseJSON.success) { window.location.href='../home'; } } 
    		: function (event, id, name, responseJSON) {
		    	if (responseJSON.success) {
		    		if (uploadBlockId!='') {
		    			window.location.href=window.location.href.split('#')[0]+'#b'+uploadBlockId;
		    		}
		    		location.reload(true);
				}
    }).on('showMessage', function (event, id, name, message) {
    	$('#uploader-error-message').text(message);
    	$('#uploader-error').show();
    	$('#uploader-button').show();
    }).on('submit', function(event, id, name) {
    	$('#uploader-error').hide();
    	$('#uploader-button').hide();
    });
}