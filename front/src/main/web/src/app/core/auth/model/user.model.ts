import {UserRole} from "./user-role.model";
import {UserDto} from "../../../api";

export class User {
    constructor(private dto: UserDto) {
    }

    get id() : string {
        return this.dto.id;
    }

    get username() : string {
        return this.dto.username;
    }

    get displayName(): string{
        return this.username;
    }

    get email() : string {
        return this.dto.email;
    }

    get avatarUrl() : string {
        return this.dto.avatarUrl;
    }

    get roles(): UserRole[]  {
        return this.dto.roles.map(role => UserRole[role]);
    }

    hasRole(role: UserRole): boolean {
        return this.roles.includes(role);
    }

    get type(): UserType {
        return UserType[this.dto.type];
    }

    isInternal(): boolean {
        return this.type == UserType.INTERNAL;
    }

    isExternal(): boolean {
        return this.type == UserType.EXTERNAL;
    }

    hasAdminRole(): boolean {
        return this.hasRole(UserRole.ADMIN);
    }
}

export enum UserType {

    EXTERNAL = "EXTERNAL",

    INTERNAL = "INTERNAL"

}
