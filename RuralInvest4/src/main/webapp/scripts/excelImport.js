var uploader;
$(document).ready(function () {
	$("#importExcel").click(function() { 
		if (uploader==null) {
			setupFileUploader();
		}
		uploader.setEndpoint(submitUrlBase);
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
			 } 
		 },
		close: function() {
			 $('#uploader-error').hide();
		},
		open: function() { 
			 uploader.cancelAll();
			 uploader.clearStoredFiles();
		}
	 });
});

function uploadConfig() {
	submitUrlBase='admin/import';
	$("#qq-form").attr("action",submitUrlBase);
	$('#uploader-error').hide();
	$("#upload-dialog").dialog('option', 'title', titleConfig);
	$('#upload-description').text(uploadDescConfig);
	$('#upload-fail').text(uploadFailConfig);
	if (uploader==null) { 
		setupFileUploader(true);
	}
	uploader.setEndpoint(submitUrlBase);
	$("#upload-dialog").dialog("open");
}

function uploadRestore() {
	submitUrlBase='admin/restore';
	$("#qq-form").attr("action",submitUrlBase);
	$('#uploader-error').hide();
	$("#upload-dialog").dialog('option', 'title', titleRestore);
	$('#upload-description').text(uploadDescRestore);
	$('#upload-fail').text(uploadFailRestore);
	if (uploader==null) {
		setupFileUploader(true);
	}
	uploader.setEndpoint(submitUrlBase);
	$("#upload-dialog").dialog("open");
}

function uploadBlock(blockId) {
	uploadBlockId=blockId;
	$("#qq-form").attr("action",$("#qq-form").attr("action")+"/"+blockId);
	if (uploader==null) {
		setupFileUploader();
	}
	uploader.setEndpoint(submitUrlBase+uploadBlockId);
	$("#upload-dialog").dialog("open");
}

function setupFileUploader(isRestore) {
//		var endpoint = submitUrlBase+uploadBlockId;
		uploader = new qq.FineUploader({
            element: document.getElementById("uploader"),
            multiple: false,
            request: {
//                endpoint: endpoint,
                customHeaders: { "X-CSRF-Token": csrfToken }
            },
            form: {
            	autoUpload: true
            },
            validation: {
           	 allowedExtensions: isRestore ? ['riv'] : ['xlsx'],
           		  sizeLimit: 2048000 // 2Mb
           		  /*isRestore 
           	  		? 1024000
           	  		: 51200 // 500 kB = 500 * 1024 bytes */
            },
            disableCancelForFormUploads: true,
           text: { fileInputTitle: '' },
           retry: { showButton: false },
           failedUploadTextDisplay: { mode:'none' },
           callbacks: {
                onError: function(id, name, errorReason, xhr) {
                	$('#uploader-error-message').text(errorReason);
        	    	$('#uploader-error').show();
                },
                onSubmit: function(id, name) {
                	$('#uploader-error').hide();
                },
                onComplete: isRestore 
        		? function(id, name, responseJSON) { if (responseJSON.success) { window.location.href='../home'; } } 
        		: function(id, name, responseJSON) {
                	if (responseJSON.success) {
    		    		if (uploadBlockId!='') {
    		    			window.location.href=window.location.href.split('#')[0]+'#b'+uploadBlockId;
    		    		}
    		    		location.reload(true);
    				}
                } 
            }
        });
}