const API = (() => {
	const sheetId = "1jj4LA0nXCOudtgCFNNveF3Wnmp8wQ_yEzkizpTL3EBk";
	const spreadsheet = SpreadsheetApp.openById(sheetId);
	const now = new Date();
	const timeOfDay = now.getHours() >= 12 ? 'A' : 'O';
	const nameSheet = spreadsheet.getSheetByName("Namen");
	const mainSheet = spreadsheet.getSheetByName("Registraties");

	const _getPresentNames = () => {
		const names = nameSheet.getRange(2, 1, nameSheet.getLastRow(), 5).getValues();
		const values = mainSheet.getRange(2, 1, mainSheet.getLastRow(), 4).getValues();
		const registrations = values.filter(row => {
			const date = new Date(row[1]);
			return date.getDate() === now.getDate() && date.getMonth() === now.getMonth() && row[2] === timeOfDay;
		});

		const children = names.reduce((acc, child) => {
			acc[child[0]] = {child: {firstName: child[3], lastName: child[2], group: child[4]}, timestamps: []};
			return acc;
		}, {});

		Logger.log(children);

		registrations.forEach(registration => {
			const regObj = {
				date: registration[1],
				partOfDay: registration[2],
				registeredAt: registration[3]
			}
			children[registration[0]].timestamps.push(regObj);
		});
		// filter out children with no registrations
		return Object.keys(children).reduce((acc, id) => {
			if(children[id].timestamps.length > 0) {
				acc.push(children[id]);
			}
			return acc;
		}, []);
	}

	//Called from the client with form data, basic validation for blank values
	function formSubmit(formData) {
		const halfHours = formData.halfHours;
		const now = new Date(formData.now);
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

	function updateRow(sheet, row, userId, now, halfHours, timeOfDay) {
		const target = sheet.getRange(row, 1, 1, 6);
		let calcHalfHours = 0;
		if (now.getHours() >= 12) {
			let then = new Date();
			then.setHours(15);
			then.setMinutes(45);
			calcHalfHours = Math.ceil((now.getTime() - then.getTime()) / 1800000);
		}
		target.setValues([[userId, now, timeOfDay, now, halfHours, calcHalfHours]]);
	}

	function getCurrentRowForUser(sheet, userId, timeOfDay) {
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

	return {
		getPresentNames: _getPresentNames
	}
})();
