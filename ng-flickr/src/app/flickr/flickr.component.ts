import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {GalleryItem, ImageItem} from '@ngx-gallery/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {FlickrService} from '../flickr.service';

@Component({
    selector: 'app-flickr',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `
        <select [formControl]="albumCtrl">
            <option *ngFor="let album of albums$ | async" [value]="album.id">
                <span>{{album.title._content}}</span>
            </option>
        </select>
        <div>
            <gallery [items]="photos$ | async" class="bg-transparent"></gallery>

            <!--            <a *ngFor="let photo of photos$ | async" (click)="select(photo)">-->
            <!--                <img [src]="url(photo)" class="thumbnail"/>-->
            <!--            </a>-->
        </div>
    `,
    styleUrls: ['./flickr.component.scss']
})
export class FlickrComponent implements OnInit {

    @Input() userId: string;

    albums$: Observable<any[]>;
    photos$: Observable<GalleryItem[]>;
    selectedPhoto$: Observable<any>;
    albumCtrl = new FormControl();

    private selected$ = new BehaviorSubject(null);

    constructor(private flickr: FlickrService) {}

    ngOnInit(): void {
        this.albums$ = this.flickr.getAlbumsForUser(this.userId);
        this.photos$ = this.albumCtrl.valueChanges.pipe(
            filter(v => !!v),
            switchMap(albumId =>
                this.flickr.getPhotosForAlbum(this.userId, albumId).pipe(
                    map(photos => photos.map(p => new ImageItem({src: this.url(p), thumb: this.thumbUrl(p)})))
                )
            )
        );
        // this.selectedPhoto$ = this.selected$.pipe(
        //     filter(v => !!v),
        //     switchMap(photoId => this.flickr.getPhoto(this.userId, photoId))
        // );
    }

    select(photo: any) {

    }

    url(photo: any) {
        return this.flickr.photoUrl(photo, 'b');
    }

    thumbUrl(photo: any) {
        return this.flickr.photoUrl(photo, 't');
    }

}
