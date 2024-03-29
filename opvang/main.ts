function doGet(e) {
	const sheetId = "16_SNwRVKHoWVevxmAzpJs-EmxUaEVavcaKaTn_vS_Qw";
	const template = HtmlService.createTemplateFromFile('index');
	const props = PropertiesService.getUserProperties();
	const now = new Date("2020-10-05T15:34:00");

	const isPresence = e.parameter.mode === "presence";
	const spreadsheet = SpreadsheetApp.openById(sheetId);

	if(isPresence) {
		return HtmlService.createTemplateFromFile('presence').evaluate();
	}

	// no userId found
	if (!e.parameter || !e.parameter.userId) {
		const errorTemplate = HtmlService.createTemplateFromFile('error');
		return errorTemplate.evaluate()
			.setTitle('Opvang');
	}

	const isOv = e.parameter.testId === "OV";

	// only available on weekdays between 6h and 19h
	if (!isOv && (now.getDay() === 0 || now.getDay() === 6 || now.getHours() < 6 || now.getHours() >= 19)) {
		const errorTemplate = HtmlService.createTemplateFromFile('unavailable');
		return errorTemplate.evaluate()
			.setTitle('Opvang');
	}

	const qrId = e.parameter.userId;
	const user =  getUser(spreadsheet, qrId);

	template.name = user.name;
	props.setProperty("userId", user.id);
	props.setProperty("sheetId", sheetId);

	return template.evaluate()
		.setTitle('Opvang');
}

function getPresentNames() {
	const sheetId = "16_SNwRVKHoWVevxmAzpJs-EmxUaEVavcaKaTn_vS_Qw";
	const spreadsheet = SpreadsheetApp.openById(sheetId);
	const now = new Date();

	const timeOfDay = now.getHours() >= 12 ? 'A' : 'O'

	const nameSheet = spreadsheet.getSheetByName("Namen");
	const names = nameSheet.getRange(2, 1, nameSheet.getLastRow(), 2).getValues();

	const mainSheet = spreadsheet.getSheetByName("Registraties");
	const registrations = mainSheet.getRange(2, 1, nameSheet.getLastRow(), 3).getValues().filter(row => {
		const date = new Date(row[1]);
		return date.getDate() === now.getDate() && date.getMonth() === now.getMonth() && row[2] === timeOfDay
	} );

	return registrations.map(r => {
		const name = names.find(n => n[0] === r[0]);
		return name ? name[1] : null
	}).filter(n => !!n);
}

function getUser(spreadsheet, qrId) {
	const sheet = spreadsheet.getSheetByName("Namen");
	const values = sheet.getRange(2, 1, sheet.getLastRow(), sheet.getLastColumn()).getValues();
	// QRID is the 5th column in the names sheet
	const row = values.findIndex(r => r[5] == qrId);

	if (row != -1) {
		return {name: values[row][1], id: parseInt(values[row][0]), qrId: qrId};
	}
	return {name: qrId, id: qrId, qrId: qrId };
}

//Called from the client with form data, basic validation for blank values
function formSubmit(formData) {
	const halfHours = formData.halfHours;
	const now: Date = new Date(formData.now);
	const props = PropertiesService.getUserProperties();
	const userId = parseInt(props.getProperty("userId"));

	let property = props.getProperty("sheetId");
	const spreadsheet = SpreadsheetApp.openById(property);
	const timeSheet = spreadsheet.getSheetByName("Registraties");
	const timeOfDay = now.getHours() >= 12 ? "A" : "O";
	// always append, no overwrite
	const row = timeSheet.getLastRow() + 1;

	console.log("userId: " + userId + ", row: " + row);
	updateRow(timeSheet, row, userId, now, halfHours, timeOfDay);
	return {success: true, message: 'Sucessfully submitted!'};
}

function updateRow(sheet, row: number, userId: number, now: Date, halfHours: number, timeOfDay: string) {
	const target = sheet.getRange(row, 1, 1, 6);
	let calcHalfHours = 0;

	if(now.getHours() >= 12) {
		let then  = new Date();
		then.setHours(15);
		then.setMinutes(45);
		calcHalfHours = Math.ceil((now.getTime() - then.getTime()) / 1800000);
	}

	target.setValues([[userId, now, timeOfDay, now, halfHours, calcHalfHours]]);
}

function getCurrentRowForUser(sheet, userId, timeOfDay): number {
	const lines = sheet.getRange(1, 1, sheet.getLastRow(), 3).getValues();
	const today = new Date();
	const row = lines.findIndex(value => {
		const date = new Date(value[1]);
		return value[0] == userId
			&& date.getDay() === today.getDay() && date.getMonth() == today.getMonth() && date.getFullYear() == today.getFullYear()
			&& value[2] == timeOfDay;
	});

	return row === -1 ? sheet.getLastRow() + 1 : row + 1;
}
