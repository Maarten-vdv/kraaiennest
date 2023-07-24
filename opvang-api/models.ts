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
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    registerNr: string;
}

export interface Address {
    street: string;
    houseNr: string;
    commune: string;
    postalCode: number;
}

export interface Child {
    childId: number;
    firstName: string;
    lastName: string;
    group: string;
    pin: string;
    qrId: string;
}

export interface Info {
    parent1: Parent,
    parent2: Parent,
    address: Address,
    child: Child
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