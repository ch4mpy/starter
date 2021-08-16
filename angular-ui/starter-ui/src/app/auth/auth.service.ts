import { Injectable } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'starter-ui/src/environments/environment';
import { OidcUser } from '../domain/oidc-user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private user$ = new BehaviorSubject<OidcUser>(OidcUser.ANONYMOUS);

  constructor(private oidcSecurityService: OidcSecurityService) {
    this.oidcSecurityService.checkAuth().subscribe(isAlreadyAuthenticated => {
      console.log('User already authenticated: ', isAlreadyAuthenticated);

      this.oidcSecurityService.userData$.subscribe(data => {
        this.user$.next(data === null ? OidcUser.ANONYMOUS : new OidcUser({ 
          sub: data.userData?.sub,
          preferredUsername: data.userData?.preferred_username,
          roles: data.userData?.resource_access && data.userData?.resource_access[environment.openIdConfiguration.clientId]?.roles || [] }));
        console.log('User: ', this.user$.value);
      });
    });
  }

  get currentUser$(): Observable<OidcUser> {
    return this.user$;
  }

  get currentUser(): OidcUser {
    return this.user$.value;
  }

  get isAuthenticated() {
    return this.user$.value.isAuthenticated();
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  logout() {
    this.oidcSecurityService.logoff();
  }

}
