import {User} from "../domain/user";

export class UserUtils {
  private static USER_ID = 'user.id';
  private static USER_DATA = 'user.data';

  static saveUser(data: User) {
    localStorage.setItem(UserUtils.USER_DATA, JSON.stringify(data));
  }

  static getSavedUser(): User {
    const storageData = localStorage.getItem(UserUtils.USER_DATA);
    return JSON.parse(storageData ? storageData : '{}') as User;
  }

  static logout() {
    localStorage.removeItem(UserUtils.USER_ID);
  }

  static saveUserId(data: string) {
    localStorage.setItem(UserUtils.USER_ID, data);
  }

  static isRegistered() {
    return localStorage.getItem(UserUtils.USER_ID) != null;
  }

  static getUserId(): string {
    return localStorage.getItem(UserUtils.USER_ID) as string;
  }
}
