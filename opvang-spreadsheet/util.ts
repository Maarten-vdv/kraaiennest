import {Dayjs} from "dayjs";
export let dayjs;

export function loadDayjs() {
	dayjs = Dayjs.load();
}

export function getSchoolYear(): string {
	const year = dayjs().year();
	const month = dayjs().month();

	if (month < 8) {
		return (year - 1) + "-" + year;
	} else {
		return year + "-" + (year + 1);
	}

}

