import {Parent} from "./models";
import {dayjs, getSchoolYear} from "./util";
import File = GoogleAppsScript.Drive.File;
import MailAdvancedParameters = GoogleAppsScript.Mail.MailAdvancedParameters;
import GmailDraft = GoogleAppsScript.Gmail.GmailDraft;

export function sendMail(quoteFile: File, parent: Parent, month: number, send) {
	const monthString = dayjs().month(month).format("MMMM");

	let mails = [];
	if(parent.email1 && parent.email1 !== "") {
		mails.push(parent.email1);
	}
	if(parent.email2  && parent.email2 !== "") {
		mails.push(parent.email2);
	}
	const addresses: string = mails.join(", ")

	if (send) {
		const email: MailAdvancedParameters = {
			to: addresses,
			subject: `Oudervereniging 't Kraaiennest - factuur Opvang maand ${monthString} (schooljaar ${getSchoolYear(month)})`,
			attachments: [quoteFile.getAs("application/pdf")],
			htmlBody: `<html><body><p>In bijlage vindt u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}</p></body></html>`,
			name: "Oudervereniging 't Kraaiennest Grembergen"
		};
		//MailApp.sendEmail(email);
	} else {
		GmailApp.createDraft(addresses,
			`Oudervereniging 't kraaiennest - factuur Opvang maand ${monthString} (schooljaar ${getSchoolYear(month)})`,
			`In bijlage vindt u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}`,
			{
				attachments: [quoteFile.getAs("application/pdf")],
				name: "Oudervereniging 't Kraaiennest Grembergen",
				htmlBody: `<html><body><p>In bijlage vindt u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}</p></body></html>`
			});
	}
}

export function sendDrafts(drafts: GmailDraft[] ) {
	drafts.forEach(draft => draft.send());
}
