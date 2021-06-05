// Google Sheet
import {google} from "googleapis";
import * as moment from "moment";
import * as serviceAccount from "../sheets_updater_service_account.json";
import {Registration} from "./registration";

const sheets = google.sheets("v4");
const jwtClient = new google.auth.JWT({
	email: serviceAccount.client_email,
	key: serviceAccount.private_key,
	scopes: ["https://www.googleapis.com/auth/spreadsheets"],
});

const jwtAuthPromise = jwtClient.authorize();

export async function exportRegistration(id: string, registration: Registration): Promise<void> {
	console.info(`Exporting registration ${id}`);

	const date: string = moment(registration.registrationTime.toDate()).format("dd-mm-yyyy");
	const finalData = [id, registration.childId, registration.partOfDay, date, registration.halfHours, registration.realHalfHours];

	await jwtAuthPromise;

	await sheets.spreadsheets.values.append({
		auth: jwtClient,
		spreadsheetId: "1YpO8oe2I8cEImtKjnBscOLbiNnnTaDP1e82YAaymu20",
		range: "Sheet1!A1:E1",
		valueInputOption: "RAW",
		requestBody: {values: [finalData], majorDimension: "ROWS"},
	}, {});
}
