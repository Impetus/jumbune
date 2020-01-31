
import {
	HttpEvent,
	HttpInterceptor,
	HttpHandler,
	HttpRequest,
	HttpErrorResponse
 } from '@angular/common/http';
 import { Observable, throwError } from 'rxjs';
 import { retry, catchError } from 'rxjs/operators';
 import { Router } from "@angular/router";
 
 export class HttpErrorInterceptor implements HttpInterceptor {

	constructor(private router: Router) {

	
		
	}
	intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		return next.handle(request)
			.pipe(
				retry(1),
				catchError((error: HttpErrorResponse) => {
					let errorMessage = '';
					if (error.error instanceof ErrorEvent) {
						// client-side error
						errorMessage = `Error: ${error.error.message}`;
					}	
					else if (error.status == 401) {						
						this.router.navigate(["/login"]);
					} 			
					else {
						// server-side error					
						errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
					}
					//window.alert(errorMessage);
					return throwError(errorMessage);
				})
			)
	}
 }



//  if (error instanceof HttpErrorResponse) {
// 	if (error.error instanceof ErrorEvent) {
// 			console.error("Error Event");
// 	} else {
// 			console.log(`error status : ${error.status} ${error.statusText}`);
// 			switch (error.status) {
// 					case 401:      //login
// 							this.router.navigateByUrl("/login");
// 							break;
// 					case 403:     //forbidden
// 							this.router.navigateByUrl("/unauthorized");
// 							break;
// 			}
// 	} 
// } else {
// 	console.error("some thing else happened");
// }
// return throwError(error);