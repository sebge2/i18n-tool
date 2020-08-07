import {UserRole} from "./user-role.model";
import {AuthenticatedUserDto} from "../../../api";
import {RepositoryRoles} from "./repository-roles.model";
import * as _ from "lodash";

export class AuthenticatedUser {

    public static fromDto(authenticatedUser: AuthenticatedUserDto) {
        return new AuthenticatedUser(
            authenticatedUser.sessionRoles.map(sessionRole => UserRole[sessionRole]),
            authenticatedUser.repositoryRoles.map(repositoryRoles => RepositoryRoles.fromDto(repositoryRoles))
        );
    }

    constructor(public sessionRoles: UserRole[] = [],
                public repositoryRoles: RepositoryRoles[] = []) {
    }

    hasRole(role: UserRole): boolean {
        return this.sessionRoles.includes(role);
    }

    hasAllRoles(roles: UserRole[]): boolean {
        return roles.every(role => this.hasRole(role));
    }

    hasRepositoryRole(repository: string, role: UserRole): boolean {
        return _.some(
            this.repositoryRoles,
                repositoryRole => (repositoryRole.repository === repository) && (repositoryRole.sessionRoles.includes(role))
        );
    }

    hasRepositoryAccess(repository: string): boolean {
        return this.hasRepositoryRole(repository, UserRole.MEMBER_OF_REPOSITORY);
    }
}
