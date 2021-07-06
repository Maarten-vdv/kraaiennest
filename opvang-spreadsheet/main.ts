import {calculateTotals} from "./calculateTotals";
import {Child, Parent, Registration, Total} from "./models";
import {loadChildren, loadParents, loadRegistrations, loadTotals} from "./sheet";
import File = GoogleAppsScript.Drive.File;

function onOpen() {
	const ui = SpreadsheetApp.getUi();
	// Or DocumentApp or FormApp.
	ui.createMenu('Opvang')
		.addItem('Bereken totalen', 'calculateHours')
		.addItem('Exporteer facturen', 'createQuotes')
		.addToUi();
}

function calculateHours() {
	const ui = SpreadsheetApp.getUi(); // Same variations.
	const result = ui.prompt(
		'Berekenen voor maand',
		'Geef maand nummer:',
		ui.ButtonSet.OK_CANCEL);

	// Process the user's response.
	const button = result.getSelectedButton();
	const text: string = result.getResponseText();

	calculateTotals(Number.parseInt(text));
}

function createQuotes(): void {
	const ui = SpreadsheetApp.getUi(); // Same variations.
	const result = ui.prompt(
		'Berekenen voor maand',
		'Geef maand nummer:',
		ui.ButtonSet.OK_CANCEL);
	const month = Number(result.getResponseText());

	const totals: Total[] = loadTotals(month);
	const registrations: Record<number, Registration[]> = loadRegistrations(month);
	const children: Child[] = loadChildren();
	const parents: Record<number, Parent> = loadParents(children);

	totals.forEach(total => fillQuote(total, registrations[total.childId], parents[total.childId]))
}


export function fillQuote(total: Total, registration: Registration[], parent: Parent) {
	const quoteTemplate: File = DriveApp.getFilesByName("factuur-template.html").next();

	let quoteHtml: string = quoteTemplate.getBlob().getDataAsString();
	quoteHtml = quoteHtml.replace("{{naam}}", parent.quoteName || parent.childName);

	const blob: GoogleAppsScript.Base.Blob = Utilities.newBlob(quoteHtml, "text/html", "text.html");
	const pdf: GoogleAppsScript.Base.Blob = blob.getAs("application/pdf");

	saveFactuurToDrive(pdf, "test.pdf", "Opvang/Facturen");
}
