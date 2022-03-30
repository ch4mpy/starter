####################
# create workspace #
####################

ng new --create-application=false WORKSPACE_NAME
cd WORKSPACE_NAME


###################################################
# add an Angular application to Angular workspace #
###################################################
ng g application --inline-style --inline-template --routing --style=scss APP_NAME

# add OIDC client lib
npm i angular-oauth2-oidc

# handle http errors
ng g c --project=APP_NAME --type=dialog --flat -s -t NetworkError
cp ../angular-workspace-template/network-error.dialog.ts projects/APP_NAME/src/app/network-error.dialog.ts
cp ../angular-workspace-template/error-http-interceptor.ts projects/APP_NAME/src/app/error-http-interceptor.ts


###########################################
# Add an Angular lib to Angular workspace #
###########################################
# This lib is generated by `openapitools` from the OpenAPI spec file produced by APP_NAME REST API (with `mvn clean verify -Popenapi`)
ng g library @c4-soft/API_LIB_NAME
npm i -D @openapitools/openapi-generator-cli
sed -i 's/"ng": "ng",/"ng": "ng",\
    "postinstall": "npm run API_LIB_NAME:install",\
    "API_LIB_NAME:generate": "npx openapi-generator-cli generate -i ..\/API_SPEC_FILE -g typescript-angular --type-mappings AnyType=any --additional-properties=serviceSuffix=Api,npmName=@c4-soft\/API_LIB_NAME,npmVersion=0.0.1,stringEnums=true,enumPropertyNaming=camelCase,supportsES6=true,withInterfaces=true --remove-operation-id-prefix -o projects\/c4-soft\/API_LIB_NAME",\
    "API_LIB_NAME:build": "npm run API_LIB_NAME:generate \&\& npm run ng -- build @c4-soft\/API_LIB_NAME --configuration production",\
    "API_LIB_NAME:install": "cd projects\/c4-soft\/API_LIB_NAME \&\& npm i \&\& cd ..\/..\/.. \&\& npm run API_LIB_NAME:build",/' package.json
rm -Rf projects/c4-soft/API_LIB_NAME
npm run API_LIB_NAME:generate
sed -i 's/"version": "5.4.0"/"version": "5.1.0"/' openapitools.json # Because of a regression (http client "delete" args)
cp ../angular-workspace-template/.openapi-generator-ignore projects/c4-soft/API_LIB_NAME/.openapi-generator-ignore
cp ../angular-workspace-template/.gitignore projects/c4-soft/API_LIB_NAME/.gitignore
cp ../angular-workspace-template/package.json projects/c4-soft/API_LIB_NAME/package.json
sed -i 's/"$schema": ".\/node_modules\/ng-packagr\/ng-package.schema.json"/"$schema": ".\/node_modules\/ng-packagr\/ng-package.schema.json",\
  "dest": "..\/..\/..\/dist\/c4-soft\/API_LIB_NAME"/' projects/c4-soft/API_LIB_NAME/ng-package.json
cp ../angular-workspace-template/tsconfig.lib.json projects/c4-soft/API_LIB_NAME/tsconfig.lib.json
cp ../angular-workspace-template/tsconfig.lib.prod.json projects/c4-soft/API_LIB_NAME/tsconfig.lib.prod.json
npm i


#################################################
# Make Angular application an Ionic-Angular app #
#################################################

# dependencies
npm i -D @ionic/angular-toolkit
cd projects/APP_NAME/
npm init --yes
npm i @capacitor/core @capacitor/app @capacitor/haptics @capacitor/keyboard @capacitor/status-bar @ionic/angular ionicons @awesome-cordova-plugins/core @ionic/storage-angular ionic-plugin-deeplinks @awesome-cordova-plugins/deeplinks
npm i -D @capacitor/cli

# add ionicons to app assets
sed -i 's/"projects\/APP_NAME\/src\/assets"/"projects\/APP_NAME\/src\/assets",\
              { "glob": "**\/*.svg", "input": "projects\/APP_NAME\/node_modules\/ionicons\/dist\/ionicons\/svg", "output": ".\/svg" }/' ../../angular.json

# add Ionic theme style sheet
cp ../../../angular-workspace-template/styles.scss src/styles.scss

# replace index.html
cp ../../../angular-workspace-template/index.html src/index.html

# add Ionic imports & providers in app module
cp ../../../angular-workspace-template/network-error.dialog.ts src/app/network-error.dialog.ts
echo -e "import { IonicModule, IonicRouteStrategy } from '@ionic/angular';\n\
import { IonicStorageModule } from '@ionic/storage-angular';\n\
import { Deeplinks } from '@awesome-cordova-plugins/deeplinks/ngx';\n\
import { RouteReuseStrategy } from '@angular/router';\n\
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';\n\
import { ErrorHttpInterceptor } from './error-http-interceptor';\n\
$(cat src/app/app.module.ts)" > src/app/app.module.ts
sed -i 's/BrowserModule,/BrowserModule,\
    HttpClientModule,\
    IonicModule.forRoot(),\
    IonicStorageModule.forRoot(),/' src/app/app.module.ts
sed -i 's/providers: \[\]/providers: \[\
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },\
    Deeplinks,\
    {\
      provide: HTTP_INTERCEPTORS,\
      useClass: ErrorHttpInterceptor,\
      multi: true,\
    },\
  ]/' src/app/app.module.ts
ng build

# init Ionic and Capacitor tooling
echo "answer yes and yes to next two prompts"
ionic init --multi-app # y y
ionic init APP_NAME --type=angular
ionic cap add android
sed -i 's/io.ionic.starter/CAPACITOR_ID/' capacitor.config.ts
sed -i 's/www/..\/..\/dist\/APP_NAME/' capacitor.config.ts
ionic cap sync

# add useful npm targets
sed -i 's/"test": "echo \\"Error: no test specified\\" \&\& exit 1"/"test": "ionic test",\
    "build": "ionic build",\
    "serve": "ionic serve",\
    "android": "ionic capacitor run android -l"/' package.json
cd ../..
sed -i 's/"start": "ng serve"/"APP_NAME:serve": "cd projects\/APP_NAME \&\& npm run serve",\
    "APP_NAME:android": "cd projects\/APP_NAME \&\& npm run android"/' package.json
sed -i 's/"build": "ng build"/"APP_NAME:build": "cd projects\/APP_NAME \&\& npm run build"/' package.json
sed -i 's/"test": "ng test"/"APP_NAME:test": "cd projects\/APP_NAME \&\& npm run test"/' package.json
sed -i '/"watch":/d' package.json


##################################################################################################
# Replace Angular minimal app with an Ionic app composed of a menu in a split-pane and two pages #
##################################################################################################

# add Material for Angular
ng add @angular/material

cd projects/APP_NAME/src/app
cp ../../../../../angular-workspace-template/user.service.ts user.service.ts

# add OIDC conf to environment files
echo -e "import { AuthConfig, OAuthModuleConfig } from 'angular-oauth2-oidc';\n\
\n\
export const authConfig: AuthConfig = {\n\
  issuer: 'https://dev-ch4mpy.eu.auth0.com/',\n\
  redirectUri: window.location.href,\n\
  postLogoutRedirectUri: window.location.origin,\n\
  clientId: 'lRHwmwQr3bhkKZeezYD8UAaGna3KSnBB',\n\
  responseType: 'code',\n\
  //solutions:manage is a scope specific to specified audience (resource server at https://bravo-ch4mp:8080)\n\
  scope: 'openid profile email offline_access solutions:manage',\n\
  customQueryParams: {\n\
    //this is where OpenAPI REST resource-server is located\n\
    audience: 'https://bravo-ch4mp:8080',\n\
  },\n\
  showDebugInformation: true,\n\
};\n\
\n\
export const oAuthModuleConfig: OAuthModuleConfig = {\n\
  resourceServer: {\n\
    allowedUrls: ['/solutions'],\n\
    sendAccessToken: true,\n\
  },\n\
};\n\
\n\
export const environment = {\n\
  production: false,\n\
  authConfig,\n\
  oAuthModuleConfig,\n\
};" > ../environments/environment.ts
echo -e "import { AuthConfig, OAuthModuleConfig } from 'angular-oauth2-oidc';\n\
\n\
export const authConfig: AuthConfig = {\n\
  issuer: 'https://dev-ch4mpy.eu.auth0.com/',\n\
  redirectUri: window.location.href,\n\
  postLogoutRedirectUri: window.location.origin,\n\
  clientId: 'lRHwmwQr3bhkKZeezYD8UAaGna3KSnBB',\n\
  responseType: 'code',\n\
  scope: 'openid profile email offline_access solutions:manage',\n\
  customQueryParams: {\n\
    audience: 'https://bravo-ch4mp:8080',\n\
  },\n\
  showDebugInformation: true,\n\
};\n\
\n\
export const oAuthModuleConfig: OAuthModuleConfig = {\n\
  resourceServer: {\n\
    allowedUrls: ['/solutions'],\n\
    sendAccessToken: true,\n\
  },\n\
};\n\
\n\
export const environment = {\n\
  production: true,\n\
  authConfig,\n\
  oAuthModuleConfig,\n\
};" > ../environments/environment.prod.ts

# import OAuthModule in app module
echo -e "import { OAuthModule } from 'angular-oauth2-oidc';\n\
import { MatDialogModule } from '@angular/material/dialog';\n\
import { environment } from '../environments/environment';\n\
$(cat app.module.ts)" > app.module.ts
sed -i 's/imports: \[/imports: \[\
    OAuthModule.forRoot(environment.oAuthModuleConfig),\
    MatDialogModule,/' app.module.ts

# copy default content
ng g module --project=APP_NAME --routing settings
echo -e "import { FormsModule, ReactiveFormsModule } from '@angular/forms';\n\
import { IonicModule } from '@ionic/angular';\n\
$(cat settings/settings.module.ts)" > settings/settings.module.ts
sed -i 's/CommonModule,/CommonModule,\
    FormsModule,\
    ReactiveFormsModule,\
    IonicModule,/' settings/settings.module.ts
ng g c --project=APP_NAME --type=screen --flat -s -t -m=settings settings/Settings
cp ../../../../../angular-workspace-template/settings.screen.ts settings/settings.screen.ts
cp ../../../../../angular-workspace-template/settings-routing.module.ts settings/settings-routing.module.ts

ng g module --project=APP_NAME --routing user-account
echo -e "import { FormsModule, ReactiveFormsModule } from '@angular/forms';\n\
import { IonicModule } from '@ionic/angular';\n\
$(cat user-account/user-account.module.ts)" > user-account/user-account.module.ts
sed -i 's/CommonModule,/CommonModule,\
    FormsModule,\
    ReactiveFormsModule,\
    IonicModule,/' user-account/user-account.module.ts
ng g c --project=APP_NAME --type=screen --flat -s -t -m=user-account user-account/UserAccount
cp ../../../../../angular-workspace-template/user-account.screen.ts user-account/user-account.screen.ts
cp ../../../../../angular-workspace-template/user-account-routing.module.ts user-account/user-account-routing.module.ts

cp ../../../../../angular-workspace-template/app-routing.module.ts app-routing.module.ts
cp ../../../../../angular-workspace-template/app.component.ts app.component.ts

cd ../../../..