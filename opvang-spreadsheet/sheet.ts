import {Child, Parent, Registration, Total} from "./models";
import Sheet = GoogleAppsScript.Spreadsheet.Sheet;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

export function loadRegistrations(month: number): Record<number, Registration[]> {
	const dayjs = Dayjs.load();

	const spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
	const sheet = spreadsheet.getSheetByName("registraties");

	const rows: any[] = sheet.getDataRange().getValues();
	const registrations = {};
	rows.forEach(row => {
		const childId = row[0];
		if (!registrations[childId]) {
			registrations[childId] = [];
		}

		const registration: Registration = {
			childId: childId,
			date: row[1],
			partOfDay: row[2],
			time: dayjs(row[3]),
			halfHours: row[4],
			calcHalfHours: row[5]
		}
		if (dayjs(registration.date).month() + 1 === month) {
			registrations[childId].push(registration);
		}

	});
	return registrations;
}

export function loadChildren(): Child[] {
	return [];
}

export function loadTotals(month: number): Total[] {
	const spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
	const sheet = spreadsheet.getSheetByName(month + "");
	const rows: any[] = sheet.getDataRange().getValues();
	return rows.map(r => ({childId: r[0], morning: r[1], evening: r[2]}));
}

export function loadParents(children: Child[]): Record<number, Parent> {
	const childLookup: Record<string, Child> = children.reduce((acc, child) => {
		acc[child.lastName + " " + child.firstName] = child;
		return acc;
	}, {})

	let files = DriveApp.searchFiles('title contains "Gegevens"');
	if (!files.hasNext()) {
		return {};
	}
	const spreadsheet: Spreadsheet = SpreadsheetApp.open(files.next());
	const sheet: Sheet = spreadsheet.getSheetByName("ouders");

	const rows: any[] = sheet.getDataRange().getValues();
	const parents: Record<string, Parent> = {};
	rows.forEach(row => {
		const childName = row[0];
		const child = childLookup[childName];

		const parent: Parent = {
			childName: childName,
			group: child.group,
			quoteName: row[2] === "" ? childName : row[2],
			streetAndNr: row[3],
			postalCode: row[4],
			commune: row[5],
			email1: row[8],
			email2: row[9]

		}
		parent[child.childId] = parent;
	});

	return parents;
}

export function getTotalsSheet(month: number): Sheet {
	return SpreadsheetApp.getActiveSpreadsheet().getSheetByName(month + "");
}
