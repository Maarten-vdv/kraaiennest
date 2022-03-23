import FileIterator = GoogleAppsScript.Drive.FileIterator;
import Folder = GoogleAppsScript.Drive.Folder;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;
import HTTPResponse = GoogleAppsScript.URL_Fetch.HTTPResponse;

function saveToDrive(blob: GoogleAppsScript.Base.Blob, name: string, path: string): void {
	DriveApp.createFile(blob).setName(name).moveTo(getDriveFolderFromPath(path));
}

function getDriveFolderFromPath(path): Folder {
	return (path || "/").split("/").reduce(function (prev, current) {
		if (prev && current) {
			const folders = prev.getFoldersByName(current);
			return folders.hasNext() ? folders.next() : null;
		} else {
			return current ? null : prev;
		}
	}, DriveApp.getRootFolder());
}

function loadSpreadSheetByName(folder: Folder, name: string): Spreadsheet {
	const files: FileIterator = folder.getFilesByName(name);
	if (!files.hasNext()) {
		throw new Error("Could not find spreadsheet " + name);
	}
	return SpreadsheetApp.open(files.next());
}

function sendMail(): void {

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
