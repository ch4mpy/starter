# Starter project for spring-boot & Ionic-Angular
Mono repo for both:
- spring-boot REST micro-services contained in a maven multi-module project (see `api` below)
- Angular front-end containing libraries and a Ionic application (see `angular-ui` below)

The aim here is to go further than most "getting-started" guides with following stuff ready:
- mono-repo but hight modularity from start
- all traffic over https, even on dev machine
- security with OpenID on both resource (REST APIs) and client (Angular app). I use Keycloak as authorisation-server, but switch to another should be easy
- spring-security annotations
- no session (use OAuth2 token instead)
- API documented with Swagger
- client-side lib generated from OpenAPI spec => UI code consumes strongly typed payloads
- persitent data access with spring-data
- REST API services packaging as runnable jars, but also java Docker container and (soon) native Docker container
- UI packaging as web app as well as Android and iOS apps

## Requirements
- JDK 11 or above
- Node LTS. You might use nvm to manage node version if you need other versions vor other projects
- Docker and Kubernetes (Docker Desktop is just fine)
- SSL certificate for your dev machine (see below to generate a self-signed one)
- Keycloak with a `starter` client. See [Keycloak doc](https://www.keycloak.org/docs/latest/server_installation/#_setting_up_ssl) for configuring your SLL certificate
- PostrgeSQL or any other relational DB with a `starter` db and `starter` user. It should be possible to connect to DB from docker containers.
- Following environment variables:
  - `SPRING_DATASOURCE_PASSWORD`: DB password for `starter` user
  - `SERVER_SSL_KEY_PASSWORD`
  - `SERVER_SSL_KEY_STORE_PASSWORD`
- openssl if you need to generate self-signed SSL certificate (Git bash includes it)

## Content

### `api` folder containing a maven mono-repo for spring-boot projects
- `common-web` with default exception handler
- `common-security` with WebSecurityConfig for OpenID (and CORS)
- `starter-domain` with a few JPA entities and repositories
- `starter-api-webmvc` with a @Controller, DTOs and OpenAPI spec generation. Also demos unit-tests covering security rules


### `angular-ui` folder containing an Angular mono-repo
- `@c4-soft/starter-api-webmvc` lib generated from OpenAPI spec
- `starter-ui` Ionic app with OpenID login (using Keycloak)

## Generating self-signed certificate
- copy both `self_signed_template.config` and `self_signed.sh` to `~/.ssh`
- `cd ~/.ssh`
- edit `self_signed_template.config` to set your IP address, e-mail, company etc.. Do not touch `[hostname]` wich is a placeholder updated by `self_signed.sh`.
- check `SERVER_SSL_KEY_PASSWORD` and `SERVER_SSL_KEY_STORE_PASSWORD` environnement variables are set (`self_signed.sh` need it).
- run `. ~/.ssh/self_signed.sh`.
- review the printed commands, copy and execute it. At first run you should need all of it.
If you have several JDKs installed, you should run `self_signed.sh` for each and execute last command only to keep the same certificate files.
- install `.crt` file as "trusted root authority" on your system (on Windows, this is done with `certmgr.msc`)
- define `SERVER_SSL_KEY_STORE` environement variable to something like `file:///C:/Users/ch4mp/.ssh/bravo-ch4mp_self_signed.jks`

## Building and running REST API
All requests must be issued with authentication.
Use Postman or another REST client to get OAuth2 access-token from your OpenID authorisation-server and issue test requests.
You might get OAuth2 token endpoints from Keycloak from an URI like https://bravo-ch4mp:9443/auth/realms/master/.well-known/openid-configuration

### Runnable jar
- `git clone https://github.com/ch4mpy/starter.git`
- `cd starter/api`
- Create a property file for a profile matching your HOSTNAME environment variable (file must be named `application-${HOSTNAME}.properties` see `application-bravo-ch4mp.properties` for a sample)
- `./mvnw spring-boot:run -pl starter-api-webmvc -Dspring.profiles.active=$HOSTNAME`
API is served at https://${HOSTNAME}:4210

### Docker
- `./mvnw clean package`
- `cp $(echo $SERVER_SSL_KEY_STORE | sed "s/file:\/\/\///") ./starter-api-webmvc/self_signed.jks`
- `cp $(echo $SERVER_SSL_KEY_STORE | sed "s/\.jks/\.pfx/" | sed "s/file:\/\/\///") ./starter-api-webmvc/self_signed.pfx`
- `docker build --build-arg SERVER_SSL_KEY_STORE_PASSWORD --build-arg HOSTNAME -t starter-api-webmvc:0.0.1-SNAPSHOT ./starter-api-webmvc/`
- edit following command with your IP: `docker run --add-host $HOSTNAME:192.168.1.59 -e SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD -e SERVER_SSL_KEY_STORE=self_signed.jks -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD -e SPRING_PROFILES_ACTIVE=$HOSTNAME -p 443:4210 --name starter-api-webmvc -t starter-api-webmvc:0.0.1-SNAPSHOT`
API is served at https://localhost

### K8s
- `kubectl create configmap starter-api-webmvc --from-literal spring.profiles.active="k8s,${HOSTNAME}" --from-literal server.ssl.key-store=self_signed.jks`
- `kubectl create secret generic starter-api-webmvc --from-literal server.ssl.key.password=${SERVER_SSL_KEY_PASSWORD} --from-literal server.ssl.key-store.password=${SERVER_SSL_KEY_STORE_PASSWORD} --from-literal spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}`
- `docker tag starter-api-webmvc:0.0.1-SNAPSHOT starter-api-webmvc:0.0.1-SNAPSHOT`
- `kubectl apply -f starter-api-webmvc/src/k8s/starter-api-webmvc-service.yaml`
- edit starter-api-webmvc/src/k8s/starter-api-webmvc-deployment.yaml with your IP and run `kubectl apply -f starter-api-webmvc/src/k8s/starter-api-webmvc-deployment.yaml`
API is served at https://localhost

## Building and running UI
- ensure you ran at least once `verify` maven goal with `openapi` profile activated (`mvn verify -Popenapi`, but `package` and `install` also trigger verify)
- cd `angular-ui` and run `npm i`
- edit `angular-ui/starter-ui/package.json` to set your $HOSTNAME instead of "bravo-ch4mp" and then either run `npm run starter-ui:serve` from `angular-ui` folder or `npm run serve` from `angular-ui/starter-ui` folder