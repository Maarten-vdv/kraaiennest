import {Registration} from "./models";
import {getTotalsSheet, loadRegistrations} from "./sheet";

export function calculateTotals(month: number) {
	const registrations: { [key: number]: Registration[] } = loadRegistrations(month);
	const sheet = getTotalsSheet(month);
	sheet.clear();
	sheet.appendRow(["Kind", "O", "A"])

	Object.keys(registrations)
		.sort((one, two) => (one > two ? -1 : 1))
		.forEach(childId => {
			let morning = registrations[childId].reduce((acc, v) => acc + getHalfHours(v, "O"), 0);
			let evening = registrations[childId].reduce((acc, v) => acc + getHalfHours(v, "A"), 0);
			sheet.appendRow([childId, morning, evening])
		});
}

function getHalfHours(reg: Registration, partOfDay: string) {
	if(partOfDay !== reg.partOfDay) {
		return 0;
	}

	// if there are manual halfHours, use those, otherwise use teh calculated ones.
	return reg.halfHours === 0 ? reg.calcHalfHours : reg.halfHours;
}
