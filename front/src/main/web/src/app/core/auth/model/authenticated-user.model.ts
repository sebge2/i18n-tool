import {UserRole} from "./user-role.model";
import {AuthenticatedUserDto} from "../../../api";

export class AuthenticatedUser {

    public static from(user: AuthenticatedUser) { // TODO
        return new AuthenticatedUser({});
    }

    constructor(private dto: AuthenticatedUserDto) {
    }

    get sessionRoles(): UserRole[] {
        return this.dto.sessionRoles.map(role => UserRole[role]);
    }

    hasRole(role: UserRole): boolean {
        return this.sessionRoles.includes(role);
    }

    hasAllRoles(roles: UserRole[]): boolean {
        return roles.every(role => this.hasRole(role));
    }
}
