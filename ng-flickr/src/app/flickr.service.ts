import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class FlickrService {

    private apiUrl = 'https://api.flickr.com/services/rest/';
    private apiKey = '7ec5595630bfe5e1ca3fc0e47815f55c';

    constructor(private http: HttpClient) { }


    public getAlbumsForUser(userId: string): Observable<any[]> {
        const params = {
            method: 'flickr.photosets.getList',
            user_id: userId,
            api_key: this.apiKey,
            format: 'json',
            nojsoncallback: '1'
        };
        return this.http.get(this.apiUrl, {params}).pipe(
            map((response: any) => response.photosets.photoset)
        );
    }

    public getPhotosForAlbum(userId: string, albumId: string): Observable<any[]> {
        const params = {
            method: 'flickr.photosets.getPhotos',
            user_id: userId,
            photoset_id: albumId,
            api_key: this.apiKey,
            format: 'json',
            nojsoncallback: '1'
        };
        return this.http.get(this.apiUrl, {params}).pipe(
            map((response: any) => response.photoset.photo)
        );
    }

    public photoUrl(photo: any, options: string) {
        return 'https://farm' + photo.farm + '.staticflickr.com/' + photo.server + '/' + photo.id + '_' + photo.secret + '_' + options + '.jpg';

    }
}
