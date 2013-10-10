 $(document).ready(function () {
  		$('#uploader-button').button();

        $('#jquery-wrapped-fine-uploader').fineUploader({
          request: {
            endpoint: submitUrl
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
        }).on('complete', function (event, id, name, responseJSON) {
        	if (responseJSON.success) {
    			location.reload(true);
			}
        }).on('showMessage', function (event, id, name, message) {
        	$('#uploader-error').empty();
        	$('#uploader-error').append('<div class="alert alert-error">' + message + '</div>');
        });
      });

	$(function() {
		$("#importExcel").click(function() { 
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
				 ; 
			} 
		 }); 
	}); 