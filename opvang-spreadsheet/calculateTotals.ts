import {Child, Parent, Registration, Supervisor} from "./models";
import {loadRegistrations} from "./sheet";
import Sheet = GoogleAppsScript.Spreadsheet.Sheet;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

export function calculateTotals(activeSheet: Spreadsheet, children: Record<number, Child>, parents: Record<number, Parent>,
								supervisors: Record<number, Supervisor>) {
	const registrations: { [key: number]: Registration[] } = loadRegistrations(activeSheet);
	const sheet: Sheet = activeSheet.getSheetByName("berekende uren");
	const sheetNonBillable: Sheet = activeSheet.getSheetByName("berekende uren (niet factureren)");
	sheet.clear();
	sheetNonBillable.clear();
	sheet.appendRow(["ID", "Naam", "Ochtend", "Avond", "factuur naam", "email verzonden", "afprinten"])

	const supervisorRows = [];
	const teacherRows = [];
	Object.keys(registrations)
		.sort((one, two) => (one > two ? -1 : 1))
		.forEach(childId => {
			const morning = registrations[childId].reduce((acc, v) => acc + getHalfHours(v, "O"), 0);
			const evening = registrations[childId].reduce((acc, v) => acc + getHalfHours(v, "A"), 0);
			const child: Child = children[childId];

			const parent: Parent = parents[childId];
			if (parent && parent.isTeacher) {
				teacherRows.push([childId, child.lastName + " " + child.firstName, morning, evening, "", "", ""])
			} else if (supervisors[childId]) {
				supervisorRows.push([childId, child.lastName + " " + child.firstName, morning, evening, "", "", ""])
			} else if (parent) {
				const hasMail = parent.email1 || parent.email2;
				sheet.appendRow([childId, child.lastName + " " + child.firstName, morning, evening, "", hasMail ? false : "", !hasMail ? true : ""])
			}
		});

	sheetNonBillable.appendRow(["Toezicht", "", "O", "A"]);
	supervisorRows.forEach(row => sheetNonBillable.appendRow(row));

	sheetNonBillable.appendRow(["Kinderen van leerkrachten", "", "O", "A"]);
	teacherRows.forEach(row => sheetNonBillable.appendRow(row));

	sheet.autoResizeColumns(1, 2);
	sheetNonBillable.autoResizeColumns(1, 2);
}

function getHalfHours(reg: Registration, partOfDay: string) {
	if (partOfDay !== reg.partOfDay) {
		return 0;
	}

	// if there are manual halfHours, use those, otherwise use teh calculated ones.
	return reg.halfHours === 0 ? reg.calcHalfHours : reg.halfHours;
}
