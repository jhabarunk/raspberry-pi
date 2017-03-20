function json2xls(_json,sheetname) {
	if(!sheetname)sheetname = "data";
		var uri = 'data:application/vnd.ms-excel;base64,',
				template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">' +
						'<head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>' +
						'<x:Name>Ark1</x:Name>' +
						'<x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->' +
						'<style>td{border:none;font-family: Calibri, sans-serif;} .number{mso-number-format:"0.00";}</style>' +
						'<meta name=ProgId content=Excel.Sheet>' +
						'</head><body>' +
						getTable(_json) +
						'</body></html>',
				base64 = function (s) { 
						return window.btoa(unescape(encodeURIComponent(s))); // #50
				};
		getContent(
				sheetname,
				uri + base64(template),
				'xls',
				template,
				'application/vnd.ms-excel'
		);
		
		function getTable(_json) {
			var html = '<table>',
					rows = getDataRows(_json);
			// Transform the rows to HTML
			jQuery.each(rows, function (i, row) {
					var tag = i ? 'td' : 'th',
							val,
							j,
							n = true ? (1.1).toLocaleString()[1] : '.';

					html += '<tr>';
					for (j = 0; j < row.length; j = j + 1) {
							val = row[j];
							// Add the cell
							if (typeof val === 'number') {
									if (n === ',') {
											html += '<' + tag + (typeof val === 'number' ? ' class="number"' : '') + '>' + val.toString().replace(".", ",") + '</' + tag + '>';
									} else {
											html += '<' + tag + (typeof val === 'number' ? ' class="number"' : '') + '>' + val.toString() + '</' + tag + '>';
									}
							} else {
									html += '<' + tag + '>' + val + '</' + tag + '>';
							}
					}

					html += '</tr>';
			});
			html += '</table>';
			return html;
	};
	function getDataRows(_json){
		var dataRows = [[]];
		for(var i in _json){
			for(var j in _json[i]){
				if(dataRows[0].indexOf(j) == -1){
					dataRows[0].push(j);
				}
			}
		}
		for(var i in _json){
			var _arr = [];
			for(var j in dataRows[0]){
				var _v = _json[i][dataRows[0][j]] ? _json[i][dataRows[0][j]] : "";
				_arr.push(_v);
			}
			dataRows.push(_arr);
		}
		return dataRows;
	}
	function getContent(name, href, extension, content, MIME) {
		var a,
				blobObject,
				downloadAttrSupported = document.createElement('a').download !== undefined;

		// Download attribute supported
		if (downloadAttrSupported) {
				blobObject = new Blob([content]);
				a = document.createElement('a');
				a.href = window.URL.createObjectURL(blobObject);
				a.target      = '_blank';
				a.download    = name + '.' + extension;
				document.body.appendChild(a);
				a.click();
				a.remove();
		} else if (window.Blob && window.navigator.msSaveOrOpenBlob) {
				// Falls to msSaveOrOpenBlob if download attribute is not supported
				blobObject = new Blob([content]);
				window.navigator.msSaveOrOpenBlob(blobObject, name + '.' + extension);

		} else {
				console.log("download not supported !!");
		}
	}
};