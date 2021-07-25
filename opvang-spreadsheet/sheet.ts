import {Child, Parent, Registration, Settings, Supervisor, Total} from "./models";
import {dayjs} from "./util";
import Sheet = GoogleAppsScript.Spreadsheet.Sheet;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

export function loadRegistrations(spreadsheet: Spreadsheet): Record<number, Registration[]> {
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
		// ignore registrations on same day after the first one
		if (!registrations[childId].find(reg => reg.date === registration.date)) {
			registrations[childId].push(registration);
		}

	});
	return registrations;
}


export function markQuoteGenerated(spreadsheet: Spreadsheet, total: Total, fileName: string) {
	const sheet = spreadsheet.getSheetByName("berekende uren");
	// column index 5 is the hasQuote column
	sheet.getRange(total.rowNr, 5).setValue(fileName);
}

export function loadChildren(data: Spreadsheet): Child[] {
	const sheet: Sheet = data.getSheetByName("kinderen");

	const rows: any[] = sheet.getDataRange().getValues();
	rows.shift(); // remove header row

	return rows.map(row => ({
		childId: row[0],
		lastName: row[2],
		firstName: row[3],
		group: row[4]
	}));
}

export function loadTotals(spreadsheet: Spreadsheet): Total[] {
	const sheet = spreadsheet.getSheetByName("berekende uren");
	const rows: any[] = sheet.getDataRange().getValues();
	rows.shift(); // remove header row;
	return rows.map((r, index) => ({
		childId: r[0],
		morning: r[2],
		evening: r[3],
		quoteName: r[4],
		mailed: !!r[5],
		toPrint: !!r[6],
		rowNr: index + 2
	}));
}

export function loadParents(data: Spreadsheet, children: Child[]): Record<number, Parent> {
	const childLookup: Record<string, Child> = children.reduce((acc, child) => {
		acc[child.lastName + " " + child.firstName] = child;
		return acc;
	}, {})

	const sheet: Sheet = data.getSheetByName("ouders");

	const rows: any[] = sheet.getDataRange().getValues();
	rows.shift(); // remove header row
	const parents: Record<string, Parent> = {};
	rows.forEach(row => {
		const childName = row[0].trim();
		const child = childLookup[childName];
		if (child) {
			parents[child.childId] = {
				childName: childName,
				group: child.group,
				quoteName: row[2] === "" ? childName : row[2],
				streetAndNr: row[3],
				postalCode: row[4],
				commune: row[5],
				email1: row[8],
				email2: row[9],
				isTeacher: row[20]
			};
		}
	});

	return parents;
}

export function loadSupervisors(data: Spreadsheet): Record<number, Supervisor> {
	const sheet: Sheet = data.getSheetByName("toezichters");
	let rows = sheet.getDataRange().getValues();
	rows.shift(); // remove header row
	return rows.reduce((acc, row) => {
		acc[row[0]] = {name: row[1], id: row[0]}
		return acc;
	}, {});
}

export function loadSettings(data: Spreadsheet, OGM: Spreadsheet, month: number): Settings {
	const sheet: Sheet = data.getSheetByName("vaste gegevens");
	const settings: Settings = sheet.getDataRange().getValues().reduce((acc, row) => {
		acc[row[0]] = row[1];
		return acc;
	}, {}) as Settings;

	if (!!OGM) {
		const ogmSheet = OGM.getSheets().find(s => s.getRange("B2").getValues()[0][0] === month)
		const rows = ogmSheet?.getDataRange().getValues();
		rows?.shift(); // remove header row
		settings.OGM = rows?.reduce((acc, row) => {
			acc[Number.parseInt(row[0])] = row[10];
			return acc;
		}, {}) || {};
	}
	return settings;
}

