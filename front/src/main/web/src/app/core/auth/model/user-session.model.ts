import {User} from "./user.model";

export class UserSession {

  private id: string;
  private user: User;
  private simpSessionId: string;
  private loginTime: Date;
  private logoutTime: Date;

  constructor(userSession: UserSession = <UserSession>{}) {
    this.id = userSession.id;
    this.user = new User(userSession.user);
    this.simpSessionId = userSession.simpSessionId;
    this.loginTime = userSession.loginTime;
    this.logoutTime= userSession.logoutTime;
  }

  getId(): string {
    return this.id;
  }

  getUser(): User {
    return this.user;
  }

  getSimpSessionId(): string {
    return this.simpSessionId;
  }

  getLoginTime(): Date {
    return this.loginTime;
  }

  getLogoutTime(): Date {
    return this.logoutTime;
  }
}
