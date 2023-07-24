import {ResolveFn} from '@angular/router';
import {inject} from "@angular/core";
import {ApiService} from "../../core/services/api.service";
import {Info} from "../../models";
import {of} from "rxjs";

export const infoResolver: ResolveFn<Info | null> = (route, state) => {
    const code = route.queryParamMap.get("code");
    if (!code) {
        return of(null);
    }
    return inject(ApiService).info(code);
};
