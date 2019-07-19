export class User {

  id: string;
  username: string;
  email: string;
  avatarUrl: string;
  roles: string[];

  constructor(user: User = <User>{}) {
    this.id = user.id;
    this.username = user.username;
    this.email = user.email;
    this.avatarUrl = user.avatarUrl;

    this.roles =
      (user.roles != null)
        ? user.roles
        : [];
  }

}
