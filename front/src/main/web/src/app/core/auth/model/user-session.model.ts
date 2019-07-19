import {User} from "./user.model";

export class UserSession {

  private _id: string;
  private _user: User;
  private _simpSessionId: string;
  private _loginTime: Date;
  private _logoutTime: Date;

  constructor(userSession: UserSession = <UserSession>{}) {
    this._id = userSession.id;
    this._user = new User(userSession.user);
    this._simpSessionId = userSession.simpSessionId;
    this._loginTime = userSession.loginTime;
    this._logoutTime= userSession.logoutTime;
  }

  get id(): string {
    return this._id;
  }

  get user(): User {
    return this._user;
  }

  get simpSessionId(): string {
    return this._simpSessionId;
  }

  get loginTime(): Date {
    return this._loginTime;
  }

  get logoutTime(): Date {
    return this._logoutTime;
  }
}
