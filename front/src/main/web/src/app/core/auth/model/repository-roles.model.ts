import { RepositoryRolesDto } from '../../../api';
import { UserRole } from './user-role.model';

export class RepositoryRoles {
  static fromDto(roles: RepositoryRolesDto) {
    return new RepositoryRoles(
      roles.repository,
      roles.sessionRoles ? roles.sessionRoles.map((sessionRole) => UserRole[sessionRole]) : []
    );
  }

  constructor(public repository: string, public sessionRoles: UserRole[]) {}
}
