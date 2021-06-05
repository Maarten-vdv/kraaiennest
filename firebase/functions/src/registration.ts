import {Timestamp} from "@google-cloud/firestore";

export interface Registration {
	childId: string;
	halfHours: number;
	partOfDay: "O" | "A";
	realHalfHours: number;
	registrationTime: Timestamp;
}
