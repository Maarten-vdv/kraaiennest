import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NotFoundComponent} from "./core/components/not-found/not-found.component";

const routes: Routes = [
    {
        path: "user",
        loadChildren: () => import('./user/user.module')
    },
    {
        path: "not-found",
        component: NotFoundComponent
    },
    {
        path: "**",
        redirectTo: "/not-found"
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
