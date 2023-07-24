import {Component} from '@angular/core';
import {Overlay, OverlayRef} from "@angular/cdk/overlay";
import {NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router} from "@angular/router";
import {filter} from "rxjs";
import {ComponentPortal} from "@angular/cdk/portal";
import {ProgressContainerComponent} from "../progress-container/progress-container.component";

@Component({
    selector: 'app-shell',
    templateUrl: './shell.component.html',
    styleUrls: ['./shell.component.scss']
})
export class ShellComponent {

    private overlayRef: OverlayRef | null = null;

    constructor(private overlay: Overlay, private router: Router) {
        this.router.events.pipe(
            filter((e) =>
                e instanceof NavigationStart ||
                e instanceof NavigationEnd ||
                e instanceof NavigationCancel ||
                e instanceof NavigationError)
        ).subscribe(e => {
            if (e instanceof NavigationStart) {
                this.overlayRef = this.overlay.create({
                    positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
                    hasBackdrop: true
                });
                this.overlayRef.attach(new ComponentPortal(ProgressContainerComponent))
            } else {
                this.overlayRef?.detach();
            }
        });
    }
}
