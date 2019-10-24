import {HttpClientModule} from '@angular/common/http';
import {Injector, NgModule} from '@angular/core';
import {createCustomElement} from '@angular/elements';
import {ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {GalleryModule} from '@ngx-gallery/core';
import {AppComponent} from './app.component';
import {FlickrComponent} from './flickr/flickr.component';

@NgModule({
    declarations: [
        AppComponent,
        FlickrComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        GalleryModule.withConfig({loadingMode: 'indeterminate', loadingStrategy: 'lazy'})
    ],
    entryComponents: [FlickrComponent],
    providers: [],
    bootstrap: []
})
export class AppModule {
    constructor(private injector: Injector) {
        const customElement = createCustomElement(FlickrComponent, {injector});
        customElements.define('app-flickr', customElement);
    }

    ngDoBootstrap() {

    }
}
