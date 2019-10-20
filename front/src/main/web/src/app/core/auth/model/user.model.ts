import {UserRole} from "./user-role.model";

export class User {

    readonly id: string;
    readonly username: string;
    readonly email: string;
    readonly avatarUrl: string;
    readonly roles: UserRole[];

    constructor(user: User = <User>{}) {
        Object.assign(this, user);

        this.roles =
            (user.roles != null)
                ? user.roles
                : [];
    }

    hasRole(role: UserRole): boolean {
        return this.roles.includes(role);
    }
}
