import {NgModule} from '@angular/core';
import {UserInfoComponent} from './components/user-info/user-info.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedModule} from "../shared/shared.module";
import {UserInfoPageComponent} from './components/user-info-page/user-info-page.component';
import {infoGuard} from "./guards/info.guard";
import {infoResolver} from "./resolvers/info.resolver";


const routes: Routes = [{
    path: "",
    component: UserInfoPageComponent,
    canActivate: [infoGuard],
    resolve: {
        info: infoResolver
    }

}];

@NgModule({
    declarations: [
        UserInfoComponent,
        UserInfoPageComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild(routes),
    ]
})
export default class UserModule {
}
