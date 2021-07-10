import {Child, Registration} from "./models";
import {loadRegistrations} from "./sheet";

export function calculateTotals(children: Record<string, Child>) {
	const registrations: { [key: number]: Registration[] } = loadRegistrations();
	const sheet: Sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("berekende uren");
	sheet.clear();
	sheet.appendRow(["Kind", "O", "A"])

	Object.keys(registrations)
		.sort((one, two) => (one > two ? -1 : 1))
		.forEach(childId => {
			const morning = registrations[childId].reduce((acc, v) => acc + getHalfHours(v, "O"), 0);
			const evening = registrations[childId].reduce((acc, v) => acc + getHalfHours(v, "A"), 0);
			const child = children[childId];
			sheet.appendRow([childId, child.lastName + " " + child.firstName, morning, evening])
		});

	sheet.autoResizeColumn(1);
}

function getHalfHours(reg: Registration, partOfDay: string) {
	if(partOfDay !== reg.partOfDay) {
		return 0;
	}

	// if there are manual halfHours, use those, otherwise use teh calculated ones.
	return reg.halfHours === 0 ? reg.calcHalfHours : reg.halfHours;
}
