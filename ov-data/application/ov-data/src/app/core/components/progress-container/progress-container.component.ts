import { Component } from '@angular/core';

@Component({
  selector: 'app-progress-container',
  template: `
    <mat-spinner diameter="50"></mat-spinner>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
  `]
})
export class ProgressContainerComponent {

}
