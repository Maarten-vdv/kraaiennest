// Compiled using ts2gas 3.6.3 (TypeScript 3.9.7)
function doGet(e) {
	const sheetId = "16_SNwRVKHoWVevxmAzpJs-EmxUaEVavcaKaTn_vS_Qw";
	const props = PropertiesService.getUserProperties();
	const mode = e.parameter.mode;

	if (mode === "presence") {
		const content = API.getPresentNames();
		return ContentService.createTextOutput(JSON.stringify(content)).setMimeType(ContentService.MimeType.JSON);
	} else if (mode === "children") {
		const content = API.getChildren();
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

function doPost(e) {
	const mode = e.parameter.mode;
	if (mode === "checkIn") {
		const child: Child = JSON.parse(e.postData.contents);
		API.checkIn(child.id);
		return ContentService.createTextOutput(JSON.stringify({success: true})).setMimeType(ContentService.MimeType.JSON);
	} else if (mode === "register") {
		const registration: Registration = JSON.parse(e.postData.contents);
		Logger.log(registration);
		try {
			API.register(registration);
		} catch(e) {
			return ContentService.createTextOutput(JSON.stringify({success: false, error: e})).setMimeType(ContentService.MimeType.JSON);
		}
		return ContentService.createTextOutput(JSON.stringify({success: true})).setMimeType(ContentService.MimeType.JSON);
	} else {
		return ContentService.createTextOutput(JSON.stringify({success: true})).setMimeType(ContentService.MimeType.JSON);
	}
}

//# sourceMappingURL=module.js.map
