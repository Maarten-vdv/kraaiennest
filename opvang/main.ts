function doGet(e) {
	// return error template if no id found
	const sheetId = "16_SNwRVKHoWVevxmAzpJs-EmxUaEVavcaKaTn_vS_Qw";
	const template = HtmlService.createTemplateFromFile('index');
	const props = PropertiesService.getUserProperties();
	if (!e.parameter || !e.parameter.userId) {
		const errorTemplate = HtmlService.createTemplateFromFile('error');
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
	const values = sheet.getRange(1, 1, sheet.getLastRow(), sheet.getLastColumn()).getValues();
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
	const row = getCurrentRowForUser(spreadsheet);
	console.log("userId: " + userId + ", row" + row);

	updateRow(timeSheet, userId, row, now, halfHours);
	return {success: true, message: 'Sucessfully submitted!'};
}

function updateRow(sheet, cell, userId, now, halfHours) {
	const target = sheet.getRange(cell.row, 1, 1, 5);
	target.setNumberFormat("HH:MM");
	target.setValues([userId, now, now.getHours() >= 12 ? "A" : "O", now, halfHours]);

	sheet.get(cell.row, 2).setNumberFormat("DD/MM");
	sheet.get(cell.row, 4).setNumberFormat("HH:MM");
}

function getCurrentRowForUser(sheet) {
	const lines = sheet.getRange(1, 1, sheet.getLastRow(), 3).getValues();
	const today = new Date();
	today.setHours(0, 0, 0, 0);
	const row = lines.findIndex(value => {
		const date = new Date(value);
		return date === today;
	});

	return row == -1 ? sheet.getLastRow() + 1 : row;
}
