import {Component, inject} from '@angular/core';
import {BehaviorSubject, filter, map, Observable} from "rxjs";
import {Address, Child, Info, Parent} from "../../../models";
import {ApiService} from "../../../core/services/api.service";
import {ActivatedRoute} from "@angular/router";
import {UntilDestroy, untilDestroyed} from "@ngneat/until-destroy";

@UntilDestroy()
@Component({
    selector: 'app-user-info-page',
    templateUrl: './user-info-page.component.html',
    styleUrls: ['./user-info-page.component.scss']
})
export class UserInfoPageComponent {

    info$ = new BehaviorSubject<{
        parent1: Parent,
        parent2: Parent,
        address: Address,
        child: Child
    } | null>(null);

    parent1$: Observable<Parent | undefined> = this.info$.pipe(map(i => i?.parent1 ?? undefined));
    parent2$: Observable<Parent | undefined> = this.info$.pipe(map(i => i?.parent2 ?? undefined));
    address$: Observable<Address | undefined> = this.info$.pipe(map(i => i?.address ?? undefined));
    child$: Observable<Child | undefined> = this.info$.pipe(map(i => i?.child ?? undefined));

    constructor() {
        inject(ActivatedRoute).data.pipe(
            untilDestroyed(this),
            map(params => params["info"]),
            filter(isDefined<Info>)
        ).subscribe(info => this.info$.next(info));
    }
}

const isDefined = <T>(val: T | null | undefined): val is T => {
    return val !== undefined && val !== null;
}