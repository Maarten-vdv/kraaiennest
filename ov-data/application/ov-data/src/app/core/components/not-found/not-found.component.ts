import {Component} from '@angular/core';

@Component({
    selector: 'app-not-found',
    template: `
        <b>404</b>
        <p>Not found</p>
    `,
    styles: [`
      :host {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
      }
    `]
})
export class NotFoundComponent {

}
