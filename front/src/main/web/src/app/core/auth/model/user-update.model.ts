import {UserRole} from "./user-role.model";

export class UserUpdate {

    username: string;
    email: string;
    password: string;
    avatarUrl: string;
    roles: UserRole[];

    constructor(user: UserUpdate = <UserUpdate>{}) {
        Object.assign(this, user);

        this.roles = (user.roles != null) ? user.roles : [];
    }
}