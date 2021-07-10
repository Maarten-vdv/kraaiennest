import {Dayjs} from "dayjs";
import {calculateTotals} from "./calculateTotals";
import {Child, Parent, Registration, Settings, Total} from "./models";
import {loadChildren, loadParents, loadRegistrations, loadSettings, loadTotals} from "./sheet";
import File = GoogleAppsScript.Drive.File;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

function onOpen() {
	const ui = SpreadsheetApp.getUi();
	// Or DocumentApp or FormApp.
	ui.createMenu('Opvang')
		.addItem('Bereken totalen', 'calculateHours')
		.addItem('Exporteer facturen', 'createQuotes')
		.addToUi();
}

function calculateHours() {
	let name = SpreadsheetApp.getActiveSpreadsheet().getName();
	if(!name.startsWith("registraties-"))
	{
		return
	}
	const month = Number.parseInt(name.split("-")[1]);
	const ui = SpreadsheetApp.getUi();
	ui.alert(getSchoolYear(month))
	const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));
	const data: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
	const children: Record<string, Child> = loadChildren(data).reduce((acc: Record<string, Child>, child: Child) => {
		acc[child.childId] = child;
		return acc;
	}, {});
	calculateTotals(children);
}

function createQuotes(): void {
	const ui = SpreadsheetApp.getUi(); // Same variations.
	const result = ui.prompt(
		'Berekenen voor maand',
		'Geef maand nummer:',
		ui.ButtonSet.OK_CANCEL);
	const month = Number(result.getResponseText());
	const opvangFolder: Folder = getDriveFolderFromPath("Opvang");
	const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));

	const data: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
	const OGM: Spreadsheet = loadSpreadSheetByName(opvangFolder, "OGM");

	const totals: Total[] = loadTotals(month);
	const registrations: Record<number, Registration[]> = loadRegistrations(month);
	const children: Child[] = loadChildren(data);
	const parents: Record<number, Parent> = loadParents(data, children);
	const settings = loadSettings(data, OGM, month);

	//totals.forEach(total => fillQuote(total, registrations[total.childId], parents[total.childId]))
	fillQuote(totals[0], registrations[totals[0].childId], parents[totals[0].childId], settings, month);
}

export function fillQuote(total: Total, registrations: Registration[], parent: Parent, settings: Settings, month: number) {
	const quoteTemplate: File = DriveApp.getFilesByName("factuur-template.html").next();
	const dayjs = Dayjs.load();

	let quoteHtml: string = quoteTemplate.getBlob().getDataAsString();

	quoteHtml = quoteHtml
		.replace("{{naam}}", parent.quoteName || parent.childName)
		.replace("{{adres}}", parent.streetAndNr)
		.replace("{{postcode}}", parent.postalCode)
		.replace("{{gemeente}}", parent.commune)
		.replace("{{factuurNaam}}", "de factuur naam")
		.replace("{{factuurDatum}}", dayjs().format("DD-MM-YYYY"))
		.replace("{{betreft}}", parent.quoteName || parent.childName)
		.replace("{{betalenVoor}}", dayjs().add(dayjs.duration({'days': settings.dueDateRelative})).format("DD-MM-YYYY"));

	const eveningCost = total.evening * settings.rate;
	const morningCost = total.morning * settings.rate;

	quoteHtml = quoteHtml
		.replace("{{maand}}", dayjs().month(month).format("MMMM"))
		.replace("{{halveUrenOchtend}}", total.morning + "")
		.replace("{{halveUrenAvond}}", total.evening + "")
		.replace(/{{tarief}}/g, (settings.rate).toLocaleString(undefined, {minimumFractionDigits: 2}))
		.replace("{{totaalOchtend}}", morningCost.toLocaleString(undefined))
		.replace("{{totaalAvond}}", eveningCost.toLocaleString(undefined))
		.replace(/{{totaal}}/g, (eveningCost + morningCost).toLocaleString(undefined));

	quoteHtml = quoteHtml
		.replace("{{rekening}}", settings.accountNr)
		.replace("{{bic}}", settings.bic)
		.replace("{{OGM}}", settings.OGM[total.childId]);

	quoteHtml = quoteHtml.replace("{{detail}}", getHtmlDetails(registrations));
	const blob: GoogleAppsScript.Base.Blob = Utilities.newBlob(quoteHtml, "text/html", "text.html");
	const pdf: GoogleAppsScript.Base.Blob = blob.getAs("application/pdf");

	saveFactuurToDrive(pdf, "test.pdf", "Opvang/Facturen");
}

function getHtmlDetails(registrations: Registration[]): string {
	let result: string = "";
	const dayjs = Dayjs.load();

	registrations
		.filter(reg => reg.halfHours > 0 || reg.calcHalfHours > 0)
		.forEach(reg => {
			if (reg.partOfDay === "O") {
				result += `<tr><td>${dayjs(reg.date).format("DD-MM-YYYY")}</td><td>${dayjs(reg.time).format("HH:mm")}</td><td>${dayjs().hour(8).minute(0).format("HH:mm")}</td><td>${reg.halfHours === 0 ? reg.calcHalfHours : reg.halfHours}</td></tr>`
			} else {
				result += `<tr><td>${dayjs(reg.date).format("DD-MM-YYYY")}</td><td>${dayjs().hour(15).minute(45).format("HH:mm")}</td><td>${dayjs(reg.time).format("HH:mm")}</td><td>${reg.halfHours === 0 ? reg.calcHalfHours : reg.halfHours}</td></tr>`
			}
		})
	return result;
}

function getSchoolYear(month: number): string {
	const dayjs = Dayjs.load();
	const year = dayjs().year();
	if (month < 6) {
		return (year - 1) + "-" + year;
	} else {
		return year + "-" + (year + 1);
	}
}

