import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Info} from "../../models";


@Injectable({
    providedIn: 'root'
})
export class ApiService {

 //   private readonly baseUrl: string = "https://tkraaiennest-grembergen.be/opvang";
    private readonly baseUrl: string = " http://localhost:4200/api";


    constructor(private http: HttpClient) {
    }

    info(qrId: string): Observable<Info> {
        return this.http.get<Info>(this.baseUrl + "?endpoint=parent&code=" + qrId);
    }

    updateInfo(info: Info): Observable<boolean> {
        return this.http.post<boolean>(this.baseUrl + "?endpoint=parent&code=" + info.child.qrId, info);

    }
}
