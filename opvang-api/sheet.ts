import {CheckIn, Child, Registration} from "./models";
import {dayjs} from "./util";
import Sheet = GoogleAppsScript.Spreadsheet.Sheet;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

const ISOformat = "YYYY-MM-DDTHH:mm:ssZ";

export function createCheckIn(folder: Folder, month: number, checkIn: CheckIn): void {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
	const sheet = spreadsheet.getSheetByName("check-ins");

	const date = dayjs(checkIn.time).date();
	const rows: any[][] = sheet.getDataRange().getValues();
	let index = -1;
	let i = 0;
	while (index < 0 && i < rows.length) {
		const row: any[] = rows[i];
		if (row[0] === checkIn.childId && date === dayjs(row[1]).date()) {
			index = i;
		}
		i++;
	}
	// ignore check in if there already is one
	if (index === -1) {
		sheet.appendRow([checkIn.childId, checkIn.time, checkIn.partOfDay]);
		sheet.autoResizeColumn(1);
	}
}

export function createRegistration(folder: Folder, month: number, registration: Registration): void {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month);
	const sheet = spreadsheet.getSheetByName("registraties");

	const time = dayjs(registration.time);
	const newValue: any[] = [registration.childId, time.format("DD-MM-YYYY"), registration.partOfDay, time.format("HH:mm"), registration.halfHours, registration.realHalfHours];

	const rows: any[][] = sheet.getDataRange().getValues();
	let index = -1;
	let i = 0;
	while (index < 0 && i < rows.length) {
		const row: any[] = rows[i];
		if (row[0] === registration.childId && row[2] === registration.partOfDay && time.date() === dayjs(row[1]).date()) {
			index = i;
		}
		i++;
	}
	// ignore registration if there already is one
	if (index === -1) {
		sheet.appendRow(newValue);
		sheet.autoResizeColumn(1);
	}
}

export function loadCheckInsForDay(folder: Folder, month: number, day: number): CheckIn[] {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
	const sheet = spreadsheet.getSheetByName("check-ins");

	const rows: any[] = sheet.getDataRange().getValues();
	const checkIns: CheckIn[] = rows.map(row => {
		const time = dayjs(row[3], "HH:mm");
		return {
			childId: row[0],
			time: dayjs(row[1], "DD-MM-YYYY").hour(time.hour()).minute(time.minute()).format(ISOformat),
			partOfDay: row[2]
		}
	});
	return checkIns.filter(reg => dayjs(reg.time).date() === day);
}

export function loadRegistrations(folder: Folder, month: number): Registration[] {
	const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
	const sheet = spreadsheet.getSheetByName("registraties");

	const rows: any[] = sheet.getDataRange().getValues();
	return rows.filter(row => !!row[0]).map(row => {
		const time = dayjs(row[3], "HH:mm");
		return {
			childId: row[0],
			partOfDay: row[2],
			time: dayjs(row[1], "DD-MM-YYYY").hour(time.hour()).minute(time.minute()).format(ISOformat),
			halfHours: row[4],
			realHalfHours: row[5],
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