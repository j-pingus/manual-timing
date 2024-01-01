import {Constants} from "../Constants";
import {User} from "../domain/user";

export class UserUtils {

    static saveUser(data: User) {
        localStorage.clear();
        localStorage.setItem(Constants.USER_DATA, JSON.stringify(data));

    }

    static saveUserId(data: string) {
        sessionStorage.clear();
        sessionStorage.setItem(Constants.USER_ID, data);
    }

    static getSavedUser(): User {
        const storageData = localStorage.getItem(Constants.USER_DATA);
        return JSON.parse(storageData ? storageData : '{}') as User;
    }

    static isRegistered() {
        return sessionStorage.getItem(Constants.USER_ID) != null;
    }
}
