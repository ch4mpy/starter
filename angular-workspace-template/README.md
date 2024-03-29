# Create an Angular workspace with Ionic app and OpenAPI client lib

## How to use
- make sure you `cd` to your project home dir
- first run [com.c4-soft.springaddons:spring-webmvc-archetype](https://github.com/ch4mpy/spring-addons/tree/master/spring-webmvc-archetype) and `mvn clean install -Popenapi` it
- copy `angular-workspace-template` to your project home dir
- run the following (edit variables and npm init values according to your needs):
``` bash
# Edit this exports according to your needs
export WORKSPACE_NAME=angular-workspace
export APP_NAME=poc
export API_LIB_NAME=poc-api
export API_SPEC_FILE=poc-api.openapi.json
export CAPACITOR_ID=com.c4_soft.poc

npm set init.author.email "ch4mp@c4-soft.com"
npm set init.author.name "ch4mpy"
npm set init.license "Apache-2.0"

# Prepare a script copy
cp angular-workspace-template/init.sh $WORKSPACE_NAME.sh
sed -i 's/WORKSPACE_NAME/'$WORKSPACE_NAME'/g' $WORKSPACE_NAME.sh
sed -i 's/APP_NAME/'$APP_NAME'/g' $WORKSPACE_NAME.sh
sed -i 's/API_LIB_NAME/'$API_LIB_NAME'/g' $WORKSPACE_NAME.sh
sed -i 's/API_SPEC_FILE/'$API_SPEC_FILE'/g' $WORKSPACE_NAME.sh
sed -i 's/CAPACITOR_ID/'$CAPACITOR_ID'/g' $WORKSPACE_NAME.sh
sed -i 's/HOSTNAME/'$HOSTNAME'/g' $WORKSPACE_NAME.sh
sed -i 's/USERNAME/'$USERNAME'/g' $WORKSPACE_NAME.sh

cp angular-workspace-template/_package.json angular-workspace-template/package.json
sed -i 's/API_LIB_NAME/'$API_LIB_NAME'/g' angular-workspace-template/package.json

cp angular-workspace-template/_index.html angular-workspace-template/index.html
sed -i 's/APP_NAME/'$APP_NAME'/g' angular-workspace-template/index.html

# Install required tools
npm i -g @angular/cli
npm i -g @ionic/cli

# Go!
bash $WORKSPACE_NAME.sh

# cleanup
rm $WORKSPACE_NAME.sh
rm angular-workspace-template/package.json
rm angular-workspace-template/index.html
```

## What is done
- create an Angular workspace
- add an Angular application to this workspace
- turn this regular Angular app into an Ionic one (install dependencies, replace or edit angular config files)
- replace Angular minimal app with an Ionic app composed of a menu in a split-pane and two pages
- add an Angular lib to Angular workspace. This lib is generated by `openapitools` from the OpenAPI spec file produced by Spring REST API (with `mvn clean verify -Popenapi`)

## What is **not** done
- Replace Angular schematics with Ionic ones (I personally prefer Angular ones). To do so:
  ```bash
  sed -i 's/"@schematics\/angular:component": {/"@ionic\/angular-toolkit:page": {\
      "style": "scss"\
  },\
  "@ionic\/angular-toolkit:component": {/' $WORKSPACE_NAME/angular.json;
  sed -i 's/"projectType": "application"/"projectType": "application",\
  "cli": {\
      "analytics": false,\
      "defaultCollection": "@ionic\/angular-toolkit"\
  }/' $WORKSPACE_NAME/angular.json;
  ```
- Configure Angular apps to be served over `https`:
  - run:
    ```bash
    sed -i 's/"serve": "ionic serve"/"serve": "ionic serve --ssl --external --public-host='$HOSTNAME' -c='$HOSTNAME'"/' projects/$APP_NAME/package.json
    sed -i 's/"android": "ionic capacitor run android -l/"android": "ionic capacitor run android -l --ssl --external --public-host='$HOSTNAME' -c='$HOSTNAME'/' projects/$APP_NAME/package.json
    ```
  - In angular.json, for each app, under architect -> serve -> configurations, add (after editing HOSTNAME, USERNAME and APP_NAME):
    ```json
    "HOSTNAME": {
      "browserTarget": "APP_NAME:build:development",
      "host": "HOSTNAME",
      "ssl": true,
      "sslCert": "C:/Users/USERNAME/.ssh/HOSTNAME_self_signed.crt",
      "sslKey": "C:/Users/USERNAME/.ssh/HOSTNAME_req_key.pem"
    },
    ```
- Configure Android or iOS app deep links.
  To do it on Android:
  - Add intent filters in AndroidManifest.xml such as: 
    ``` xml
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="https" android:host="bravo-ch4mp" android:port="8100" />
        </intent-filter>

        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="https" android:host="bao-loc.c4-soft.com" />
        </intent-filter>
    ```
- Configure Android or iOS app to use https.
  To do it on Android:
  - Add this to `CapacitorConfig` in `projects/$APP_NAME/capacitor.config.ts`: 
    ```typescript
    server: {
        hostname: 'localhost',
        androidScheme: 'https'
    }
    ```
  - create file `res/xml/network_security_config` such as (android resources names can not contain '-'):
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <network-security-config>
        <base-config cleartextTrafficPermitted="false">
            <trust-anchors>
            <certificates src="@raw/bravo_ch4mp_self_signed"/>
            <certificates src="system"/>
            </trust-anchors>
        </base-config>
    </network-security-config>
    ```
    Here:
    - `cleartextTrafficPermitted="false"` forces the use of https for all trafic
    - `<certificates src="@raw/bravo_ch4mp_self_signed"/>` is required only if certificate used by remote servers are self-signed (`res/raw/bravo_ch4mp_self_signed.crt` is the certificate with which my local API instance is served)
  - Add `networkSecurityConfig` property to `application` tag in `AndroidManifest.xml`: 
    ```xml
    <application
        ...
        android:networkSecurityConfig="@xml/network_security_config">
    ```