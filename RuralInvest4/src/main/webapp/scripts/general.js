function showSummary(summary) {
	$('#'+summary).dialog({
		resizable: true, height:250, width:800
	});
};

function randomPhoto(src, begin, end) {
	var d = new Date();
	var seconds = d.getSeconds(); 
	var ofThree = (seconds % (end-begin+1)) +begin;
	$('#'+src).attr('src',"img/faoinaction"+ofThree+"x400.jpg");
}

function formSubmit() {document.form.submit;}

function openDownloadDialog(type, pid) {
	$("#dd_local").attr("href","javascript:download('../"+type+"/export/"+pid+"');");
	$("#dd_generic").attr("href","javascript:download('../"+type+"/export/"+pid+"?generic=true');");	
	$('#download-dialog').dialog('open');
}

function download(downloadUrl) {
	$('#download-dialog').dialog('close');
	location.href=downloadUrl;
}

function confirmDelete(href) {
	$('#deleteUrl').val(href);
	$('#confirmDelete').dialog('open');
}

function copyContrib(href) {
	$('#copyUrl').val(href);
	$('#copyYear').dialog('open');
}

function search(unfinished, objType, freeText) {
	document.quickSearch.freeText.value=freeText;
	document.quickSearch.objType.value=objType;
	document.quickSearch.unfinished.value=unfinished;
	document.quickSearch.submit();
}

function formatToNum(text) {
	return text.split(curSepThou).join('').replace(curSepDec,'.')
}
function numToFormat(num) {
	num=(num+'').replace('.',curSepDec);
	return addCommas(num);
}

//TODO: include variables thousand and decimal separator
function replaceNonNum(txtbox) {
	var foo = "[^\d|"+checkCrazySeparator(curSepThou)+"|"+checkCrazySeparator(curSepDec)+"]";
	if (/foo/.test(txtbox.value)) { // matches any character except number, comma or dot
		txtbox.value = txtbox.value.replace(/[^\d|\.|,]/g,""); // replaces bad character with null
		return true;
	}
	else return false;
}

function checkCrazySeparator(separator) {
	if (separator=='.') return '\.';
	if (separator=='\'') return '\\\\';
	else return separator;
}

function commasKeyup(txtbox) {
//debugger;
	var pos = getCaretPosition(txtbox);
	if (replaceNonNum(txtbox)) {
		setCaretPosition(txtbox, pos-1);
	} else {
		var text=txtbox.value;
	  	
	  	txtbox.value = addCommas(text);
	  	if (txtbox.value.length>text.length)
	  		setCaretPosition(txtbox, pos+1);
	  	else if (text.length==txtbox.value.length)
	  		setCaretPosition(txtbox, pos);
	  	else setCaretPosition(txtbox, pos-1);
	  	txtbox.focus();
  	}
}

function setCaretPosition(ctrl, pos){
	if(ctrl.setSelectionRange) {
		ctrl.focus();
		ctrl.setSelectionRange(pos,pos);
	} else if (ctrl.createTextRange) {
		var range = ctrl.createTextRange();
		range.collapse(true);
		range.moveEnd('character', pos);
		range.moveStart('character', pos);
		range.select();
	}
}

function getCaretPosition (ctrl) {
	var CaretPos = 0;
	// IE Support
	if (document.selection) {	
		ctrl.focus ();
		var Sel = document.selection.createRange();
		var SelLength = document.selection.createRange().text.length;
		Sel.moveStart ('character', -ctrl.value.length);
		CaretPos = Sel.text.length - SelLength;
	}
	// Firefox support
	else if (ctrl.selectionStart || ctrl.selectionStart == '0')
	CaretPos = ctrl.selectionStart;	
	return (CaretPos);
}

function addCommas(nStr) {
	nStr += '';
	nStr = nStr.split(curSepThou).join('');
	x = nStr.split(curSepDec);
	var x1 = x[0];
	var x2 = x.length > 1 ? curSepDec + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) { x1 = x1.replace(rgx, '$1' + curSepThou + '$2');	}
	return x1 + x2;
}

function selectAllChron(a,b) {
	for (var i=0; i<12; i++) {
		for (var x=0; x<2; x++) {
			var id=a+'-'+i+'-'+x;
			var square = document.getElementById(b+'-'+id);
			square.style.backgroundColor="#e7ae0f";
			document.getElementById("ch"+id).value="true";
		}	
	}
}

function selDeselChron(blockOrder, id) {
	var square = document.getElementById(blockOrder+"-"+id);
	if (square.style.backgroundColor!='') { 
		// option is already selected, must deselect the option
		square.style.backgroundColor="";
		document.getElementById("ch"+id).value="false";
	} else { // select the option
		square.style.backgroundColor="#e7ae0f";
		document.getElementById("ch"+id).value="true";	
	}
}

function menuItemOver(menuItem) {
	for (i=1;i<=8;i++) {
		var row = document.getElementById("menu"+i);
		if (i==menuItem) {
			row.style.display = 'block';
		} else {
			row.style.display = 'none';
		}
	}
}

function filterType_onchange(picker) {
	$(".toggle").toggleClass('hidden',$("#type").prop("selectedIndex")>=2);
}

function deselect(select) {
	for (var i=0; i < select.length; i++) {
		select.options[i].selected = false;
	}
}

function toggle(itemId){
	var el = document.getElementById(itemId);
	el.style.display = el.style.display=='block' ? 'none' : 'block';
}

function round(original, decimals, noComma) {
	var plain = original.toFixed(decimals);
	return plain;
	/*if (noComma==true) {
		return plain;
	} else {
		return addCommas(plain);
	}*/
}