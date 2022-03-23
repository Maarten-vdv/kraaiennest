
class Presence {
	child: Child;
	timestamps: Timestamp[];

	constructor(child, timestamps) {
		this.child = child;
		this.timestamps = timestamps;
	}
}

class Timestamp {
	date: Date;
	partOfDay: 'O' | 'A';
	registeredAt: Date;
	isCheckIn: boolean;
}


class Child {
	id: string;
	firstName: string;
	lastName: string;
	group: string;

	constructor(id, firstName, lastName, group) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.group = group;
	}
}

class Registration {
	childId: string;
	halfHours: number;
	realHalfHours: number;
	partOfDay: "A" | "O";
	registrationTime: string;
}
