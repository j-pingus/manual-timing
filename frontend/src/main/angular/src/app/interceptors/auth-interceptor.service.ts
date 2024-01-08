import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Constants} from "../Constants";
import {catchError, Observable, throwError} from "rxjs";
import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private router: Router, private _snackBar: MatSnackBar) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authToken = 'Bearer: ' + sessionStorage.getItem(Constants.USER_ID);
    const newReq = req.clone({
      setHeaders: {Authorization: authToken}
    });
    return next.handle(newReq).pipe(
      catchError(error => {
        if (error.status == 401) {
          console.error('please login');
          this._snackBar.open('You are not authorized to perform this operation, please login as referee');
          this.router.navigate(['/']);
        } else {
          console.error("error is intercepted", error);
        }
        return throwError(error.message);
      }));
  }

}
