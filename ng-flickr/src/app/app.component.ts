import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
  <app-flickr [userId]="userId"></app-flickr>
  `,
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'ng-flickr';
  userId = '73493557@N02';
}
