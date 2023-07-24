import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from "@angular/router";
import {MatToolbarModule} from "@angular/material/toolbar";
import {LetDirective, PushPipe} from "@ngrx/component";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {MatInputModule} from "@angular/material/input";
import {ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {OverlayModule} from "@angular/cdk/overlay";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        LetDirective,
        PushPipe
    ],
    exports: [
        CommonModule,
        RouterModule,
        ReactiveFormsModule,
        LetDirective,
        PushPipe,

        MatButtonModule,
        MatToolbarModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        OverlayModule,
        MatProgressSpinnerModule
    ]
})
export class SharedModule {
}
