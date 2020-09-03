function doGet(e) {
	const sheetId = "16_SNwRVKHoWVevxmAzpJs-EmxUaEVavcaKaTn_vS_Qw";
	const template = HtmlService.createTemplateFromFile('index');
	const props = PropertiesService.getUserProperties();
	const now = new Date();

	// no userId found
	if (!e.parameter || !e.parameter.userId) {
		const errorTemplate = HtmlService.createTemplateFromFile('error');
		return errorTemplate.evaluate()
			.setTitle('Opvang');
	}

	// only available on weekdays between 6h and 19h
	if (now.getDay() === 0 || now.getDay() === 6 || now.getHours() < 6 || now.getHours() > 19) {
		const errorTemplate = HtmlService.createTemplateFromFile('unavailable');
		return errorTemplate.evaluate()
			.setTitle('Opvang');
	}

	const userId = e.parameter.userId;
	const spreadsheet = SpreadsheetApp.openById(sheetId);

	props.setProperty("userId", userId);
	props.setProperty("sheetId", sheetId);

	template.name = getName(spreadsheet, userId);
	return template.evaluate()
		.setTitle('Opvang');
}

function getName(spreadsheet, userId) {
	const sheet = spreadsheet.getSheetByName("Namen");
	const values = sheet.getRange(2, 1, sheet.getLastRow(), sheet.getLastColumn()).getValues();
	const row = values.findIndex(r => r[0] == userId);

	if (row != -1) {
		return values[row][1];
	}
	return userId;
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
	// const row = getCurrentRowForUser(timeSheet, userId, timeOfDay);
	// always append, no overwrite
	const row = timeSheet.getLastRow() + 1;

	console.log("userId: " + userId + ", row: " + row);
	updateRow(timeSheet, row, userId, now, halfHours, timeOfDay);
	return {success: true, message: 'Sucessfully submitted!'};
}

function updateRow(sheet, row: number, userId: number, now: Date, halfHours: number, timeOfDay: string) {
	const target = sheet.getRange(row, 1, 1, 5);
	target.setValues([[userId, now, timeOfDay, now, halfHours]]);
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
