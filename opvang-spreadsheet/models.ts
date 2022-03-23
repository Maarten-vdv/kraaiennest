export interface Registration {
	childId: string;
	date: string;
	time: string;
	partOfDay: "O" | "A";
	halfHours: number;
	calcHalfHours: number;
}

export interface Parent {
	childName: string;
	group: string;
	quoteName: string;
	streetAndNr: string;
	postalCode: string;
	commune: string;
	email1: string;
	email2: string;
	quoteVia: string;
	isTeacher: boolean;
}

export interface Child {
	childId: number;
	name: string;
	firstName: string;
	lastName: string;
	group: string;
}

export interface Total {
	childId: number;
	morning: number;
	evening: number;
	quoteName: string;
	mailed: boolean;
	toPrint: boolean;
	rowNr: number;
}

export interface Supervisor {
	id: string;
	name: string;
}

export interface Settings {
	OGM: Record<number, string>;
	bic: string;
	accountNr: string;
	rate: number;
	dueDateRelative: number;
	supervisors: Record<number, Supervisor>;
	batchSize: number;
}
