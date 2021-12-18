export enum UserRole {
  MEMBER_OF_ORGANIZATION = 'MEMBER_OF_ORGANIZATION',

  MEMBER_OF_REPOSITORY = 'MEMBER_OF_REPOSITORY',

  ADMIN = 'ADMIN',
}

export const ALL_USER_ROLES: UserRole[] = Object.keys(UserRole).map((key) => UserRole[key]);
