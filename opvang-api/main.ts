import {CheckIn, Registration} from "./models";
import {createCheckIn, createRegistration, loadCheckInsForDay, loadChildren, loadRegistrations, loadRegistrationsForDay} from "./sheet";
import {dayjs, getSchoolYear, loadDayjs} from "./util";

function getRequest(e: GoogleAppsScript.Events.DoGet) {
	const endpoint = e.parameter["endpoint"];
	loadDayjs();

	switch (endpoint) {
		case "children": {
			return returnJSON(loadChildren());
		}

		case "checkIn": {
			const month: number = Number.parseInt(e.parameter["month"]) || dayjs().month() + 1;
			const day: number = Number.parseInt(e.parameter["day"]) || dayjs().day();
			const folder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));
			return returnJSON(loadCheckInsForDay(folder, month, day));
		}

		case "registration": {
			const month: number = Number.parseInt(e.parameter["month"]) || dayjs().month() + 1;
			const day: number = Number.parseInt(e.parameter["day"]);
			const folder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));
			return returnJSON(!!day ?
				loadRegistrationsForDay(folder, month, day) :
				loadRegistrations(folder, month));
		}

		default: {
			return returnJSON({error: "invalid endpoint"});
		}
	}
}

function test() {
	postRequest({
		parameter: {endpoint: "registration"}, postData: {
			contents:
				'{"childId":"1","halfHours":1,"partOfDay":"A","realHalfHours":1,"time": "2021-07-24T13:27:00.000Z"}'
		}
	})
}

function postRequest(e) {
	const endpoint = e.parameter["endpoint"];
	const content = e.postData.contents;
	loadDayjs();

	switch (endpoint) {
		case "checkIn": {
			const checkIn: CheckIn = JSON.parse(content);

			const month: number = Number.parseInt(e.parameter["month"]) || dayjs().month() + 1;
			const folder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));
			createCheckIn(folder, month, checkIn);
			return returnJSON(checkIn);
		}

		case "registration": {
			const registration: Registration = JSON.parse(content);

			const month: number = Number.parseInt(e.parameter["month"]) || dayjs().month() + 1;
			const folder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(month));
			createRegistration(folder, month, registration);
			return returnJSON(registration);
		}

		default: {
			return returnJSON({error: "invalid endpoint"});
		}
	}
}

function returnJSON(value: any): GoogleAppsScript.Content.TextOutput {
	return ContentService.createTextOutput(JSON.stringify(value)).setMimeType(ContentService.MimeType.JSON);
}
