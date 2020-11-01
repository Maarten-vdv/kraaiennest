// Compiled using ts2gas 3.6.3 (TypeScript 3.9.7)
function doGet(e) {
	const sheetId = "16_SNwRVKHoWVevxmAzpJs-EmxUaEVavcaKaTn_vS_Qw";
	const props = PropertiesService.getUserProperties();
	const now = new Date();
	const isPresence = e.parameter.mode === "presence";
	const spreadsheet = SpreadsheetApp.openById(sheetId);

	if (isPresence) {
		const content = API.getPresentNames();
		return ContentService.createTextOutput(JSON.stringify(content)).setMimeType(ContentService.MimeType.JSON);
	}
	// // no userId found
	// if (!e.parameter || !e.parameter.userId) {
	// 	Logger.log("called without user id.");
	// 	const errorTemplate = HtmlService.createTemplateFromFile('error');
	// 	return errorTemplate.evaluate()
	// 		.setTitle('Opvang');
	// }
	// const template = HtmlService.createTemplateFromFile('index');
	// const isOv = e.parameter.testId === "OV";
	// // only available on weekdays between 6h and 19h
	// if (!isOv && (now.getDay() === 0 || now.getDay() === 6 || now.getHours() < 6 || now.getHours() >= 19)) {
	// 	const errorTemplate = HtmlService.createTemplateFromFile('unavailable');
	// 	return errorTemplate.evaluate()
	// 		.setTitle('Opvang');
	// }
	// const qrId = e.parameter.userId;
	// const user = getUser(spreadsheet, qrId);
	// template.name = user.name;
	// props.setProperty("userId", user.id);
	// props.setProperty("sheetId", sheetId);
	// return template.evaluate()
	// 	.setTitle('Opvang');
}



function getUser(spreadsheet, qrId) {
	const sheet = spreadsheet.getSheetByName("Namen");
	const values = sheet.getRange(2, 1, sheet.getLastRow(), sheet.getLastColumn()).getValues();
	// QRID is the 5th column in the names sheet
	const row = values.findIndex(r => r[5] == qrId);
	if (row != -1) {
		return {name: values[row][1], id: parseInt(values[row][0]), qrId: qrId};
	}
	return {name: qrId, id: qrId, qrId: qrId};
}

//# sourceMappingURL=module.js.map
