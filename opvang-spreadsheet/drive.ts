import Sheet = GoogleAppsScript.Spreadsheet.Sheet;
import HTTPResponse = GoogleAppsScript.URL_Fetch.HTTPResponse;

function saveFactuurToDrive(blob: GoogleAppsScript.Base.Blob, name: string, path: string): void {
	DriveApp.createFile(blob).setName(name).moveTo(getDriveFolderFromPath(path));
}

function saveSheetToDrive(url: string, sheet: Sheet, fileName: string): void {
	const blob: GoogleAppsScript.Base.Blob = getAsBlob(url, sheet, null).setName(fileName);
	DriveApp.createFile(blob).moveTo(DriveApp.getFoldersByName("facturen").next());
}

function getDriveFolderFromPath(path) {
	return (path || "/").split("/").reduce(function (prev, current) {
		if (prev && current) {
			const folders = prev.getFoldersByName(current);
			return folders.hasNext() ? folders.next() : null;
		} else {
			return current ? null : prev;
		}
	}, DriveApp.getRootFolder());
}

function getAsBlob(url, sheet, range): GoogleAppsScript.Base.Blob {
	let rangeParam = '';
	let sheetParam = '';
	if (range) {
		rangeParam =
			'&r1=' + (range.getRow() - 1)
			+ '&r2=' + range.getLastRow()
			+ '&c1=' + (range.getColumn() - 1)
			+ '&c2=' + range.getLastColumn()
	}
	if (sheet) {
		sheetParam = '&gid=' + sheet.getSheetId()
	}
	// A credit to https://gist.github.com/Spencer-Easton/78f9867a691e549c9c70
	// these parameters are reverse-engineered (not officially documented by Google)
	// they may break overtime.
	const exportUrl = url.replace(/\/edit.*$/, '')
		+ '/export?exportFormat=pdf&format=pdf'
		+ '&size=A4'
		+ '&portrait=true'
		+ '&fitw=true'
		+ '&top_margin=0.75'
		+ '&bottom_margin=0.75'
		+ '&left_margin=0.7'
		+ '&right_margin=0.7'
		+ '&sheetnames=false&printtitle=false'
		+ '&pagenum=UNDEFINED' // change it to CENTER to print page numbers
		+ '&gridlines=false'
		+ '&fzr=FALSE'
		+ sheetParam
		+ rangeParam;

	Logger.log('exportUrl=' + exportUrl)
	let response: HTTPResponse;
	let i = 0;
	for (; i < 5; i += 1) {
		response = UrlFetchApp.fetch(exportUrl, {
			muteHttpExceptions: true,
			headers: {
				Authorization: 'Bearer ' + ScriptApp.getOAuthToken(),
			},
		})
		if (response.getResponseCode() === 429) {
			// printing too fast, retrying
			Utilities.sleep(2000);
		} else {
			break;
		}
	}

	if (i === 5) {
		throw new Error('Printing failed. Too many sheets to print.');
	}

	return response.getBlob();
}
