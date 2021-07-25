import {Dayjs} from "dayjs";
export let dayjs;

export function loadDayjs() {
	dayjs = Dayjs.load();
}

export function getSchoolYear(month: number): string {
	const year = dayjs().year();
	if (month < 7) {
		return (year - 1) + "-" + year;
	} else {
		return year + "-" + (year + 1);
	}

}

