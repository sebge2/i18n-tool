import {User} from "./user.model";

export class UserSession {

    readonly id: string;
    readonly user: User;
    readonly simpSessionId: string;
    readonly loginTime: Date;
    readonly logoutTime: Date;

    constructor(userSession: UserSession = <UserSession>{}) {
        this.id = userSession.id;
        this.user = new User(userSession.user);
        this.simpSessionId = userSession.simpSessionId;
        this.loginTime = userSession.loginTime;
        this.logoutTime = userSession.logoutTime;
    }

}
