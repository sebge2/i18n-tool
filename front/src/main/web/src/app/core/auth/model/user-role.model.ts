export enum UserRole {

    MEMBER_OF_ORGANIZATION = "MEMBER_OF_ORGANIZATION",

    ADMIN = "ADMIN"

}

export const ALL_USER_ROLES = Object.keys(UserRole).map(key => UserRole[key]);
