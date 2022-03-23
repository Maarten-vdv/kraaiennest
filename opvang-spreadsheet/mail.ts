import {Parent} from "./models";
import {dayjs, getSchoolYear} from "./util";
import File = GoogleAppsScript.Drive.File;
import MailAdvancedParameters = GoogleAppsScript.Mail.MailAdvancedParameters;
import GmailDraft = GoogleAppsScript.Gmail.GmailDraft;

export function sendMail(quoteFile: File, parent: Parent, month: number, send) {
    //month in dayjs is 0 based
    const monthString = dayjs().month(month-1).format("MMMM");


    const addresses: string = parent.quoteVia;

    if (send) {
        const email: MailAdvancedParameters = {
            to: addresses,
            subject: `Oudervereniging 't Kraaiennest - factuur Opvang maand ${monthString} (schooljaar ${getSchoolYear(month)})`,
            attachments: [quoteFile.getAs("application/pdf")],
            htmlBody: `<html><body><p>Beste,</p><p>In bijlage vindt u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}.</p>
<p>Met vriendelijke groeten,<br />Oudervereniging 't Kraaiennest</p></body></html>`,
            name: "Oudervereniging 't Kraaiennest Grembergen"
        };
        MailApp.sendEmail(email);
    } else {
        GmailApp.createDraft(addresses,
            `Oudervereniging 't kraaiennest - factuur Opvang maand ${monthString} (schooljaar ${getSchoolYear(month)})`,
            `In bijlage vindt u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}`,
            {
                attachments: [quoteFile.getAs("application/pdf")],
                name: "Oudervereniging 't Kraaiennest Grembergen",
                htmlBody: `<html><body><p>Beste,</p><p>In bijlage vindt u de factuur voor de opvang op school voor de maand ${monthString} voor ${parent.childName}.</p>
<p>Met vriendelijke groeten,<br />Oudervereniging 't Kraaiennest</p></body></html>`
            });
    }
}

export function sendDrafts(drafts: GmailDraft[]) {
    drafts.forEach(draft => draft.send());
}
