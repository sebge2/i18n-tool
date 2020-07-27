import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from "../../../../../core/auth/model/user.model";
import {Subject} from "rxjs";
import {AuthenticationService} from "../../../../../core/auth/service/authentication.service";
import {DroppedFile} from "../../../../../core/shared/directive/drag-drop.directive";

@Component({
    selector: 'app-edit-profile-avatar',
    templateUrl: './edit-profile-avatar.component.html',
    styleUrls: ['./edit-profile-avatar.component.css']
})
export class EditProfileAvatarComponent implements OnInit, OnDestroy {

    public currentUser: User;

    public _currentUserAvatar: string;
    public _uploadUserAvatar: any;

    private readonly _destroyed$ = new Subject();

    constructor(private authenticationService: AuthenticationService) {
    }

    public ngOnInit() {
        this.authenticationService.currentUser()
            .subscribe(currentUser => {
                this.currentUser = currentUser;

                this._currentUserAvatar = (this.currentUser != null)
                    ? `url('/api/user/${this.currentUser.id}/avatar')`
                    : null;
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public getAvatarUrl(): any {
        return this._uploadUserAvatar ? this._uploadUserAvatar : this._currentUserAvatar;
    }

    public onFileDropped(file: DroppedFile) {
        const reader = new FileReader();
        reader.readAsDataURL(file.file);
        reader.onload = (_event) => {
            this._uploadUserAvatar = `url(${reader.result})`;
        };
    }
}
