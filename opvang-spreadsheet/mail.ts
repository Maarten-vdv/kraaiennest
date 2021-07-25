import {Parent} from "./models";
import {dayjs, getSchoolYear} from "./util";
import File = GoogleAppsScript.Drive.File;
import MailAdvancedParameters = GoogleAppsScript.Mail.MailAdvancedParameters;

export function sendMail(quoteFile: File, parent: Parent, month: number) {
	const monthString = dayjs().month(month).format("MMMM");
	const email: MailAdvancedParameters = {
		to: "mail2maartenvdv@gmail.com", //to: parent.email1,
		subject: `Opvang 't kraaiennest - factuur ${monthString} (schooljaar ${getSchoolYear(month)})`,
		attachments: [quoteFile.getAs("application/pdf")],
		htmlBody: `<html><body><p>In bijlage vind u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}</p></body></html>`,
		name: "Ouderverenigign 'tkraaiennes Grembergen"
	};
//	MailApp.sendEmail(email);

	GmailApp.createDraft("mail2maartenvdv@gmail.com", //to: parent.email1
		`Opvang 't kraaiennest - factuur ${monthString} (schooljaar ${getSchoolYear(month)})`,
		`In bijlage vind u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}`,
		{
			attachments: [quoteFile.getAs("application/pdf")],
			name: "Ouderverenigign 'tkraaiennes Grembergen",
			htmlBody: `<html><body><p>In bijlage vind u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}</p></body></html>`
		});
}
