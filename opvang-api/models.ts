export interface Registration {
	childId: number;
	time: string;
	partOfDay: "O" | "A";
	halfHours: number;
	realHalfHours: number;
}

export interface CheckIn {
	childId: number;
	time: string;
	partOfDay: "O" | "A";
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
}

export interface Child {
	childId: number;
	firstName: string;
	lastName: string;
	group: string;
	pin: string;
	qrId: string;
}

export interface Total {
	childId: number;
	morning: number;
	evening: number;
}

export interface Settings {
	OGM: Record<number, string>;
	bic: string;
	accountNr: string;
	rate: number;
}
