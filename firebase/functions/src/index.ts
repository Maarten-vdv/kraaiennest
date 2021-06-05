import * as functions from "firebase-functions";
import {exportRegistration} from "./exportRegistrations";
import {Registration} from "./registration";

const region = "europe-west3";

// Triggered when a group of bids is added to the bids collection.
// This will update a spreadsheet with history data in Google Drive.
exports.exportOrderData = functions.region(region).firestore
	.document("registrations/{id}")
	.onCreate(async (snapshot) => {
		const id = snapshot.id;
		const registration: Registration = snapshot.data() as Registration;
		await exportRegistration(id, registration);
	});
