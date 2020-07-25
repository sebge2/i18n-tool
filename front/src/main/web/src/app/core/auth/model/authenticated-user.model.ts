import {UserRole} from "./user-role.model";
import {User} from "./user.model";
import {AuthenticatedUserDto} from "../../../api";

export class AuthenticatedUser {

    public static from(user: AuthenticatedUser) { // TODO
        return new AuthenticatedUser({});
    }

    readonly user: User;

    constructor(private dto: AuthenticatedUserDto) {
        this.user = User.fromDto(dto.user);
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
