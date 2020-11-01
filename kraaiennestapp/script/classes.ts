class Presence {
	private child: Child;
	private timestamps: Date[];

	constructor(child, timestamps) {
		this.child = child;
		this.timestamps = timestamps;
	}
}

class Child {
	private id: string;
	private firstName: string;
	private lastName: string;
	private group: string;

	constructor(id, firstName, lastName, group) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.group = group;
	}
}
