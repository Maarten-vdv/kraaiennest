import {Parent, Registration, Settings, Total} from "./models";
import {dayjs, getSchoolYear} from "./util";
import File = GoogleAppsScript.Drive.File;

export function generateQuote(total: Total, registrations: Registration[], parent: Parent, settings: Settings, month: number): string {
	if (!parent) {
		return;
	}
	const quoteTemplate: File = DriveApp.getFilesByName("factuur-template.html").next();
	const fileName = `${getSchoolYear()}_${month}_${parent.childName.replace(/\s/g, "_")}.pdf`;

	let quoteHtml: string = quoteTemplate.getBlob().getDataAsString();

	quoteHtml = quoteHtml
		.replace("{{naam}}", parent.quoteName || parent.childName)
		.replace("{{adres}}", parent.streetAndNr)
		.replace("{{postcode}}", parent.postalCode)
		.replace("{{gemeente}}", parent.commune)
		.replace("{{factuurNaam}}", fileName.replace(".pdf", ""))
		.replace("{{factuurDatum}}", dayjs().format("DD-MM-YYYY"))
		.replace("{{betreft}}", parent.childName)
		.replace("{{betalenVoor}}", dayjs().add(dayjs.duration({'days': settings.dueDateRelative})).format("DD-MM-YYYY"))
		.replace("{{betalenVoor2}}", dayjs().add(dayjs.duration({'days': settings.dueDateRelative})).format("DD-MM-YYYY"));

	const eveningCost = total.evening * settings.rate;
	const morningCost = total.morning * settings.rate;

    const text =
        'BCD'
        + '\n002'
        + '\n1'
        + '\nSCT'
        + '\nKREDBEBB'
        + '\nOV KRAAIENNEST'
        + '\nBE21744058424103'
        + `\nEUR${eveningCost + morningCost}`
        + '\nOPVG'
        + `\n${settings.OGM[total.childId]}`

	const qrCodeBlob = UrlFetchApp.fetch('https://chart.googleapis.com/chart', {
		'method' : 'post',
		'payload' : {
			'cht': 'qr',
			'chl': text,
			'chs': '200x200'
		}}).getBlob();
    const bytes = qrCodeBlob.getBytes();
    const qrBase64String = Utilities.base64Encode(bytes);

	// month in dayjs in 0 based
	quoteHtml = quoteHtml
		.replace("{{maand}}", dayjs().month(month-1).format("MMMM"))
		.replace("{{halveUrenOchtend}}", total.morning + "")
		.replace("{{halveUrenAvond}}", total.evening + "")
		.replace(/{{tarief}}/g, (settings.rate).toLocaleString(undefined, {minimumFractionDigits: 2}))
		.replace("{{totaalOchtend}}", morningCost.toLocaleString(undefined))
		.replace("{{totaalAvond}}", eveningCost.toLocaleString(undefined))
		.replace(/{{totaal}}/g, (eveningCost + morningCost).toLocaleString(undefined));

	quoteHtml = quoteHtml
		.replace("{{rekening}}", settings.accountNr)
		.replace("{{bic}}", settings.bic)
		.replace("{{OGM}}", settings.OGM[total.childId]);

	quoteHtml = quoteHtml
		.replace("{{QR}}", qrBase64String);

	quoteHtml = quoteHtml.replace("{{detail}}", getHtmlDetails(registrations));
	const blob: GoogleAppsScript.Base.Blob = Utilities.newBlob(quoteHtml, "text/html", "text.html");
	const pdf: GoogleAppsScript.Base.Blob = blob.getAs("application/pdf");

	const parentFolder = getDriveFolderFromPath("Opvang/" + getSchoolYear() + "/Facturen/");
	if (!parentFolder.getFoldersByName(month + "").hasNext()) {
		const monthFolder: Folder = parentFolder.createFolder(month + "");
		monthFolder.createFolder("printen");
	}

	let path = "Opvang/" + getSchoolYear() + "/facturen/" + month + (total.toPrint ? "/printen" : "");
	saveToDrive(pdf, fileName, path);
	return fileName;
}

function getHtmlDetails(registrations: Registration[]): string {
	let result: string = "";
	registrations
		.filter(reg => reg.halfHours > 0 || reg.calcHalfHours > 0)
		.forEach(reg => {
			if (reg.partOfDay === "O") {
				result += `<tr><td>${dayjs(reg.date).format("DD-MM-YYYY")}</td><td>${dayjs(reg.time).format("HH:mm")}</td><td>${dayjs().hour(8).minute(0).format("HH:mm")}</td><td>${reg.halfHours === 0 ? reg.calcHalfHours : reg.halfHours}</td></tr>`
			} else {
				result += `<tr><td>${dayjs(reg.date).format("DD-MM-YYYY")}</td><td>${dayjs().hour(15).minute(45).format("HH:mm")}</td><td>${dayjs(reg.time).format("HH:mm")}</td><td>${reg.halfHours === 0 ? reg.calcHalfHours : reg.halfHours}</td></tr>`
			}
		})
	return result;
}
