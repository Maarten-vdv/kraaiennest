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
}

export interface Child {
	childId: number;
	firstName: string;
	lastName: string;
	group: string;
}

export interface Total {
	childId: number;
	morning: number;
	evening: number;
}
