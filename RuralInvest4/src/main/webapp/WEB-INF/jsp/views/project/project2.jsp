
function deleteDonor(donorId) {
	$.ajax({ url: '../donor/'+donorId+'/delete', type:"GET", dataType: 'json',
		success: function( data, textStatus, jqXHR ) {
			$('#donorTable tbody').empty();
			loadDonors();
		 },
		 error: function(jqXHR, status, error) {
			 alert('error: '+obj.message);
		 }
	});
}
function editDonor() {
	url = '../donor/'+$('#donor-id').val();
	if ($('#donor-id').val()==-1) { url = url+'?projectId='+projId; } 
	$.ajax({ url: url, type:"POST", dataType: 'json',
		data: { description: $('#donor-description').val(), contribType:$('#donor-contribType').val() },
		 success: function( data, textStatus, jqXHR ) {
			loadDonors();
			$( "#dialog-donor" ).dialog("close");
			$('#donor-description').val('');
			$('#donor-contribType').val([]);
		 },
		 error: function(jqXHR, status, error) {
			 $('#donorAlert').show();
			 $.each(JSON.parse(jqXHR.responseText), function(idx, obj) {
				label=$('label[for='+obj.field+']').text();
				$('#donor-error-field').text(label);
				$('#donor-error-message').text(obj.message);
			});
		 }
	});
}
function loadDonors() {
	var noCache = new Date().getTime();
	$.getJSON("../../home/"+projId+"/donors.json", { "noCache": noCache }, function(data) {
		tbody = $('#donorTable tbody');
		 var dtbody = document.getElementById("dtbody");
		 while (dtbody.firstChild) {
			 dtbody.removeChild(dtbody.firstChild);
		}
		 $.each(data, function(key) {
			 if (!data[key].notSpecified) {
				 tr = $('<tr/>', {
					id: 'donor'+data[key].orderBy,
					donorId: data[key].donorId,
					'class': (data[key].orderBy % 2 == 0) ? 'even' : 'odd'
				 });
				 
				 // description
				 if (data[key].contribType==4 && data[key].description=='state-public') {
					 text=statePub;
				 } else {
					 text=data[key].description;
				 }
				td1 = $('<td/>', {
					'class': 'left',
					text: text
				}).appendTo(tr);
				 
				 // contrib type
				 td2 = $('<td/>', {
						 'class':'left',
						 code:data[key].contribType,
						 text:cType(data[key].contribType)
			 	}).appendTo(tr);
				 
				 // edit link
				 td3 = $('<td/>');
				 a = addLink('javascript:showme('+data[key].orderBy+');','../../img/edit.png',viewEdit);
				 $(td3).append(a);
				 $(tr).append(td3);
				 
				 td4 = $('<td/>');
				 if (! data[key].inUse) {
					 d = addLink('javascript:deleteDonor('+data[key].donorId+')','../../img/delete.gif',deleteItem);
					 $(td4).append(d);
				 }
				 $(tr).append(td4);
				 $(tbody).append(tr);
			 }
		 });
	});
}
function showme(order) {
	if (order==-1) {
		$('#donor-id').val('-1');
		$('#donor-description').val('');
		$('#donor-contribType').val(0);
	} else {
		$('#donor-id').val($('#donor'+order).attr('donorId'));
		$('#donor-description').val($('#donor'+order+' td:first-child').text());
		$('#donor-contribType').val($('#donor'+order+' td:nth-child(2)').attr('code'));
	}
	$('#donorAlert').hide();
	$( "#dialog-donor" ).dialog({
		 height: 210,
		 width:500,
		 modal: true
	});
}

function addLink(href,img,alt) {
	a = $('<a/>');
	 $(a).attr('href',href);
	 edit = $('<img/>');
	 $(edit).attr('src',img);
	 $(edit).attr('alt',alt);
	 $(edit).attr('width','16');
	 $(edit).attr('height','16');
	 $(edit).attr('border','0');
	 $(a).append(edit);
	 return a;
}
function Calculate() {
	with (Math) {
		var dirMen = (document.form.beneDirectMen.value);
		var dirWomen = (document.form.beneDirectWomen.value);
		var dirChild = (document.form.beneDirectChild.value);
		
		var inMen = (document.form.beneIndirectMen.value);
		var inWomen = (document.form.beneIndirectWomen.value);
		var inChild = (document.form.beneIndirectChild.value);
	
		var totalDirect = round(1*dirMen + 1*dirWomen + 1*dirChild);
		var totalIndirect =  round(1*inMen+1*inWomen+1*inChild);
	}
	
	document.form.beneDirectTotal.value = totalDirect;
	document.form.beneIndirectTotal.value = totalIndirect;
}