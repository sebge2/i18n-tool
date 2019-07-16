import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user.model";

@Injectable({
    providedIn: 'root'
})
export class AuthenticationService {

    constructor(private httpClient: HttpClient) {
    }

    get currentUser(): Observable<User> {
        return this.httpClient.get<User>('/api/authentication/user');
    }
}
