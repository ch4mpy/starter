import { APP_INITIALIZER, NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';
import { environment } from 'starter-ui/src/environments/environment';

@NgModule({
    imports: [AuthModule.forRoot({config: environment.openIdConfiguration} )],
})
export class AuthConfigModule {}
