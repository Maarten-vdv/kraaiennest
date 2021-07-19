import {calculateTotals} from "./calculateTotals";
import {sendMail} from "./mail";
import {Child, Parent, Registration, Supervisor, Total} from "./models";
import {generateQuote} from "./quote";
import {loadChildren, loadParents, loadRegistrations, loadSettings, loadSupervisors, loadTotals, markQuoteGenerated} from "./sheet";
import {getSchoolYear, loadDayjs} from "./util";
import FileIterator = GoogleAppsScript.Drive.FileIterator;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

function onOpen() {
	const ui = SpreadsheetApp.getUi();
	// Or DocumentApp or FormApp.
	ui.createMenu('Opvang')
		.addItem('Bereken totalen', 'calculateHours')
		.addItem('Maak facturen', 'createQuotes')
		.addItem('Mail facturen', 'sendMails')
		.addToUi();
}

function calculateHours(activeSpreadsheet: Spreadsheet) {
	loadDayjs();

	let name = activeSpreadsheet.getName();
	if (!name.startsWith("registraties-")) {
		return;
	}
	const month = Number.parseInt(name.split("-")[1]);
	const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));
	const data: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
	let childArray: Child[] = loadChildren(data);
	const children: Record<string, Child> = childArray.reduce((acc: Record<string, Child>, child: Child) => {
		acc[child.childId] = child;
		return acc;
	}, {});
	const parents: Record<number, Parent> = loadParents(data, childArray);
	const supervisors: Record<number, Supervisor> = loadSupervisors(data);
	calculateTotals(activeSpreadsheet, children, parents, supervisors);
}

function createQuotes(activeSpreadsheet: Spreadsheet): void {
	loadDayjs();

	let name = activeSpreadsheet.getName();
	if (!name.startsWith("registraties-")) {
		return;
	}
	const month = Number.parseInt(name.split("-")[1]);
	const opvangFolder: Folder = getDriveFolderFromPath("Opvang");
	const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));

	const data: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
	const OGM: Spreadsheet = loadSpreadSheetByName(opvangFolder, "OGM");

	const totals: Total[] = loadTotals(activeSpreadsheet);
	const registrations: Record<number, Registration[]> = loadRegistrations(SpreadsheetApp.getActiveSpreadsheet());
	const children: Child[] = loadChildren(data);
	const parents: Record<number, Parent> = loadParents(data, children);
	const settings = loadSettings(data, OGM, month);

	const ui = SpreadsheetApp.getUi();
	ui.alert(`Het script gaat nu een batch van ${settings.batchSize || 50} facturen proberen aan te maken in Goolge drive map ${"Opvang/" + getSchoolYear(month) + "/Facturen/" + month}`);

	const totalsToProcess = totals.filter(total => !total.quoteName).slice(0, settings.batchSize || 50);
	totalsToProcess.forEach(total => {
		const fileName: string = generateQuote(total, registrations[total.childId], parents[total.childId], settings, month);
		markQuoteGenerated(SpreadsheetApp.getActiveSpreadsheet(), total, fileName);
	});
}

function sendMails(activeSpreadsheet: Spreadsheet) {
	loadDayjs();

	let name = activeSpreadsheet.getName();
	if (!name.startsWith("registraties-")) {
		return
	}
	const month = Number.parseInt(name.split("-")[1]);
	const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));

	const data: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
	const settings = loadSettings(data, null, month);
	const quoteFolder: Folder = getDriveFolderFromPath(`Opvang/${getSchoolYear(month)}/facturen/${month}`);

	const ui = SpreadsheetApp.getUi();
	ui.alert(`Het script gaat nu een batch van ${settings.batchSize || 50} facturen proberen te versturen`);

	const children: Child[] = loadChildren(data);
	const parents: Record<number, Parent> = loadParents(data, children);
	const totals: Total[] = loadTotals(activeSpreadsheet);
	const totalsToProcess = totals.filter(total => !!total.quoteName).slice(0, settings.batchSize || 50);

	totalsToProcess.forEach(total => {
		let fileIterator: FileIterator = quoteFolder.getFilesByName(total.quoteName);
		if (!!parents[total.childId] && fileIterator.hasNext()) {
			sendMail(fileIterator.next(), parents[total.childId], month);
		}

	});
}
