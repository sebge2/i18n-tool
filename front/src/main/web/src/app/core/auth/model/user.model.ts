export class User {

    readonly id: string;
    readonly username: string;
    readonly email: string;
    readonly avatarUrl: string;
    readonly roles: string[];

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
