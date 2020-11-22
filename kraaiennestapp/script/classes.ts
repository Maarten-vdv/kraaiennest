class Presence {
	child: Child;
	timestamps: Date[];

	constructor(child, timestamps) {
		this.child = child;
		this.timestamps = timestamps;
	}
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
	partOfDay;
	registrationTime: Date;
}
