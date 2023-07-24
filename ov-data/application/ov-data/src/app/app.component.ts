import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
      <app-shell></app-shell>
  `,
  styles: [`
    :host {
      display: flex;
      flex-direction: column;
      
      height: 100%;
      width: 100%;
    }
  `]
})
export class AppComponent {
  title = 'ov-data';
}
