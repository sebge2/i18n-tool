export class User {

  id: string;
  userName: string;
  email: string;
  avatarUrl: string;
  roles: string[];

  constructor(user: User = <User>{}) {
    this.id = user.id;
    this.userName = user.userName;
    this.email = user.email;
    this.avatarUrl = user.avatarUrl;

    this.roles =
      (user.roles != null)
        ? user.roles
        : [];
  }

}
