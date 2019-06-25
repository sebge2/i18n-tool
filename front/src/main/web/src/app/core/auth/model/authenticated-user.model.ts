import {UserRole} from "./user-role.model";
import {User} from "./user.model";

export class AuthenticatedUser {

    readonly user: User;
    readonly sessionRoles: UserRole[];

    constructor(user: AuthenticatedUser = <AuthenticatedUser>{}) {
        Object.assign(this, user);

        this.sessionRoles =
            (user.sessionRoles != null)
                ? user.sessionRoles
                : [];
    }

    hasRole(role: UserRole): boolean {
        return this.sessionRoles.includes(role);
    }

    hasAllRoles(roles: UserRole[]): boolean {
        return roles.every(role => this.hasRole(role));
    }
}
