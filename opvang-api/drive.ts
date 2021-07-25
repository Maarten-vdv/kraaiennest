import FileIterator = GoogleAppsScript.Drive.FileIterator;
import Folder = GoogleAppsScript.Drive.Folder;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

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
