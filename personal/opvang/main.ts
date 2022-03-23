// Compiled using ts2gas 3.6.3 (TypeScript 3.9.7)
//import preventExtensions = Reflect.preventExtensions;
function doGet(e) {
	// return error template if no id found
	const sheetId = "1z5FOk_fnrDw_3x6i8jMXs07QUIj3hTt8u0P1XMFVO1s";
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
	return "";
}

//Called from the client with form data, basic validation for blank values
function formSubmit(formData) {
	const halfHours = formData.halfHours;
	const props = PropertiesService.getUserProperties();
	const userId = parseInt(props.getProperty("userId"));
	let property = props.getProperty("sheetId");

	const spreadsheet = SpreadsheetApp.openById(property);
	const timeSheet = spreadsheet.getSheetByName("Registraties");
	const hourSheet = spreadsheet.getSheetByName("Halve uren");
	const currentCell = getCurrentCellForUser(spreadsheet, timeSheet, userId);
	console.log(currentCell);
	const now = new Date();

	const isEvening = now.getHours() >= 12;
	const timeCell = checkCellExists(currentCell, timeSheet, userId, isEvening);
	const hourCell = checkCellExists(currentCell, hourSheet, userId, isEvening);
	console.log(timeCell);
	console.log(hourCell);

	updateTime(timeSheet, timeCell, now);
	updateHalfHours(hourSheet, hourCell, halfHours);

	return {success: true, message: 'Sucessfully submitted!'};
}

function updateTime(sheet, cell, now) {
	console.log(cell);
	const target = sheet.getRange(cell.row, cell.column);
	target.setNumberFormat("HH:MM");
	target.setValue(now);
}

function updateHalfHours(sheet, cell, halfHours) {
	const target = sheet.getRange(cell.row, cell.column);
	target.setValue(halfHours);
}

function getCurrentCellForUser(spreadsheet, sheet, userId) {
	const users = sheet.getRange(1, 1, sheet.getLastRow(), 1).getValues();
	const dates = sheet.getRange(1, 1, 1, sheet.getLastColumn()).getValues()[0];
	const today = new Date();
	const todayColumn = dates.filter(v => v != "").findIndex(value => {
		const date = new Date(value);
		return date.getDate() == today.getDate() && date.getMonth() == today.getMonth();
	});
	const userRow = users.findIndex(value => value[0] == userId);

	return {
		row: userRow == -1 ? -1 : userRow + 1,
		column: todayColumn == -1 ? -1 : (todayColumn + 1) * 2
	};
}

function checkCellExists(cell, sheet, userId, evening) {
	let newCell = {row: cell.row, column: cell.column};

	if (cell.row == -1) {
		// new user;
		const newRow = sheet.getLastRow() + 1;
		const userCell = sheet.getRange(newRow, 1);

		userCell.setValue(userId);
		newCell.row = newRow;
	}
	if (cell.column == -1) {
		// new date;
		const newColumn = sheet.getLastColumn() + 1;
		const dateCell = sheet.getRange(1, newColumn);
		const periodRange = sheet.getRange(2, newColumn, 1, 2);

		periodRange.setValues([["O", "A"]]);
		dateCell.setNumberFormat("d/M");
		dateCell.setValue(new Date());
		newCell.column = newColumn;
	}

	if (evening) {
		newCell.column += 1;
	}
	return newCell;
}

//# sourceMappingURL=module.js.map
