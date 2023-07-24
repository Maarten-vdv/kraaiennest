import {CheckIn, Child, Info, Registration} from "./models";
import {dayjs, getSchoolYear} from "./util";
import Sheet = GoogleAppsScript.Spreadsheet.Sheet;
import Spreadsheet = GoogleAppsScript.Spreadsheet.Spreadsheet;

const ISOformat = "YYYY-MM-DDTHH:mm:ssZ";

export function createCheckIn(folder: Folder, month: number, checkIn: CheckIn): void {
    const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
    const sheet = spreadsheet.getSheetByName("check-ins");

    const time = dayjs(checkIn.time);
    const newValue: any[] = [checkIn.childId, time.format("DD-MM-YYYY"), time.format("HH:mm")];

    const rows: any[][] = sheet.getDataRange().getValues();
    let index = -1;
    let i = 0;
    while (index < 0 && i < rows.length) {
        const row: any[] = rows[i];
        if (row[0] === checkIn.childId && time.date() === dayjs(row[1]).date()) {
            index = i;
        }
        i++;
    }
    // ignore check in if there already is one
    if (index === -1) {
        sheet.appendRow(newValue);
        sheet.autoResizeColumn(1);
    }
}

export function createRegistration(folder: Folder, month: number, registration: Registration): void {
    const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month);
    const sheet = spreadsheet.getSheetByName("registraties");

    const time = dayjs(registration.time);
    const newValue: any[] = [registration.childId, time.format("DD-MM-YYYY"), registration.partOfDay, time.format("HH:mm"), registration.halfHours, registration.realHalfHours];

    const rows: any[][] = sheet.getDataRange().getValues();
    let index = -1;
    let i = 0;
    while (index < 0 && i < rows.length) {
        const row: any[] = rows[i];
        if (row[0] === registration.childId && row[2] === registration.partOfDay && time.date() === dayjs(row[1]).date()) {
            index = i;
        }
        i++;
    }
    // ignore registration if there already is one
    if (index === -1) {
        sheet.appendRow(newValue);
        sheet.autoResizeColumn(1);
    }
}

export function loadCheckInsForDay(folder: Folder, month: number, day: number): CheckIn[] {
    const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
    const sheet = spreadsheet.getSheetByName("check-ins");

    const rows: any[] = sheet.getDataRange().getValues();
    const checkIns: CheckIn[] = rows.map(row => {
        const time = dayjs(row[2], "HH:mm");
        return {
            childId: row[0],
            time: dayjs(row[1], "DD-MM-YYYY").hour(time.hour()).minute(time.minute()).format(ISOformat),
            partOfDay: 'A'
        }
    });
    return checkIns.filter(reg => dayjs(reg.time).date() === day);
}

export function loadRegistrations(folder: Folder, month: number): Registration[] {
    const spreadsheet = loadSpreadSheetByName(folder, "registraties-" + month)
    const sheet = spreadsheet.getSheetByName("registraties");

    const rows: any[] = sheet.getDataRange().getValues();
    return rows.filter(row => !!row[0]).map(row => {
        const time = dayjs(row[3], "HH:mm");
        return {
            childId: row[0],
            partOfDay: row[2],
            time: dayjs(row[1], "DD-MM-YYYY").hour(time.hour()).minute(time.minute()).format(ISOformat),
            halfHours: row[4],
            realHalfHours: row[5],
        } as Registration
    });
}


export function loadRegistrationsForDay(folder: Folder, month: number, day: number): Registration[] {
    return loadRegistrations(folder, month).filter(reg => {
        const time = dayjs(reg.time);
        const now = dayjs();
        return time.date() === day && (now.hour() > 12 ? time.hour() > 12 : time.hour() <= 12);
    });
}

export function loadChildren(): Child[] {
    const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(dayjs().month() + 1));
    const spreadsheet: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
    const sheet: Sheet = spreadsheet.getSheetByName("kinderen");

    const rows: any[] = sheet.getDataRange().getValues();
    rows.shift(); // remove header row

    return rows.map(row => ({
        childId: row[0],
        lastName: row[2],
        firstName: row[3],
        group: row[4],
        qrId: row[5],
        pin: row[6]
    }));
}

export function loadChildByQrId(qrId: string): Child {
    const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(dayjs().month() + 1));
    const spreadsheet: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
    const sheet: Sheet = spreadsheet.getSheetByName("kinderen");

    const rows: any[] = sheet.getDataRange().getValues();
    rows.shift(); // remove header row

    const row = rows.find(row => row[5] === qrId);

    return {
        childId: row[0],
        lastName: row[2],
        firstName: row[3],
        group: row[4],
        qrId: row[5],
        pin: row[6]
    };
}


export function loadParents(child: Child): Info {
    const yearFolder: Folder = getDriveFolderFromPath("Opvang/" + getSchoolYear(dayjs().month() + 1));
    const spreadsheet: Spreadsheet = loadSpreadSheetByName(yearFolder, "Gegevens")
    const sheet: Sheet = spreadsheet.getSheetByName("ouders");

    const rows: any[] = sheet.getDataRange().getValues();
    rows.shift(); // remove header row

    const row = rows.find(row => row[0] === child.childId);
    const street = row[12].split(" ");
    return {
        parent1: {
            firstName: row[7],
            lastName: row[6],
            email: row[8],
            phone: row[11],
            registerNr: ""
        },
        parent2: {
            email: row[9],
            firstName: row[4],
            lastName: row[5],
            phone: row[10],
            registerNr: ""
        },
        address: {
            houseNr: street.pop(),
            street: street.join(" "),
            postalCode: row[13],
            commune: row[14]
        },
        child: child
    };
}

export function updateInfo(folder: Folder, info: Info) {
    const spreadsheet = loadSpreadSheetByName(folder, "Gegevens");
    const sheet = spreadsheet.getSheetByName("ouders");

    const time = dayjs();
    const newValue: any[] = [];

    const rows: any[][] = sheet.getDataRange().getValues();
    const rowIndex = rows.findIndex(row => row[0] === info.child.childId);
    sheet.getRange(rowIndex, 0, rowIndex, rows[0].length - 1).setValues(newValue);
    sheet.autoResizeColumn(1);
}
