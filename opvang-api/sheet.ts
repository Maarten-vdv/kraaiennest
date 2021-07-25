import {CheckIn, Child, Registration} from "./models";
import {dayjs} from "./util";
import Sheet = GoogleAppsScript.Spreadsheet.Sheet;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

export function createCheckIn(folder: Folder, month: number, checkIn: CheckIn): void {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
	const sheet = spreadsheet.getSheetByName("check-ins");

	sheet.appendRow([checkIn.childId, checkIn.date, checkIn.partOfDay]);
	sheet.autoResizeColumn(1);
}

export function createRegistration(folder: Folder, month: number, registration: Registration): void {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month);
	const sheet = spreadsheet.getSheetByName("registraties");

	const time = dayjs(registration.time);
	sheet.appendRow([registration.childId, time.format("DD-MM-YYYY"), registration.partOfDay, time.format("HH:mm"), registration.halfHours, registration.realHalfHours]);
	sheet.autoResizeColumn(1);
}

export function loadCheckInsForDay(folder: Folder, month: number, day: number): CheckIn[] {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
	const sheet = spreadsheet.getSheetByName("check-ins");

	const rows: any[] = sheet.getDataRange().getValues();
	const checkIns: CheckIn[] = rows.map(row => {
		const time = dayjs(row[3], "HH:MM");
		return {
			childId: row[0],
			time: dayjs(row[1], "DD-MM-YYYY").hour(time.hour()).minute(time.minute()),
			partOfDay: row[2]
		}
	});
	return checkIns.filter(reg => dayjs(reg.date).date() === day);
}

export function loadRegistrations(folder: Folder, month: number): Registration[] {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
	const sheet = spreadsheet.getSheetByName("registraties");

	const rows: any[] = sheet.getDataRange().getValues();
	return rows.filter(row => !!row[0]).map(row => {
		const time = dayjs(row[3], "HH:MM");
		return {
			childId: row[0],
			partOfDay: row[2],
			time: dayjs(row[1], "DD-MM-YYYY").hour(time.hour()).minute(time.minute()),
			halfHours: row[4],
			realHalfHours: row[5]
		} as Registration
	});
}


export function loadRegistrationsForDay(folder: Folder, month: number, day: number): Registration[] {
	return loadRegistrations(folder, month).filter(reg => dayjs(reg.time).date() === day);
}

export function loadChildren(): Child[] {
	let files = DriveApp.searchFiles('title contains "Gegevens"');
	if (!files.hasNext()) {
		return [];
	}
	const spreadsheet: Spreadsheet = SpreadsheetApp.open(files.next());
	const sheet: Sheet = spreadsheet.getSheetByName("kinderen");

	const rows: any[] = sheet.getDataRange().getValues();
	rows.shift(); // remove header row

	return rows.map(row => ({
		childId: row[0],
		lastName: row[2],
		firstName: row[3],
		group: row[4],
		qrId: row[5],
		pin: row[6]
	}));
}
