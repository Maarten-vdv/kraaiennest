import {NgModule} from '@angular/core';
import {ShellComponent} from './components/shell/shell.component';
import {SharedModule} from "../shared/shared.module";
import {HttpClientModule} from "@angular/common/http";
import { NotFoundComponent } from './components/not-found/not-found.component';
import { ProgressContainerComponent } from './components/progress-container/progress-container.component';

@NgModule({
    declarations: [
        ShellComponent,
        NotFoundComponent,
        ProgressContainerComponent
    ],
    exports: [
        ShellComponent
    ],
    imports: [
        SharedModule,
        HttpClientModule
    ]
})
export class CoreModule {
}
