# Starter project for spring-boot & Ionic-Angular
Mono repo for both:
- spring-boot REST micro-services contained in a maven multi-module project (see `api` below)
- Angular front-end containing libraries and a Ionic application (see `angular-ui` below)

The aim here is to go further than most "getting-started" guides with following stuff ready:
- mono-repo but hight modularity from start
- all traffic over https, even on dev machine or inside Docker (and K8s)
- security with OpenID on both resource-server (REST APIs) and client (Angular app). I use Keycloak as authorisation-server, but switch to another should be easy
- spring-security annotations
- no session (use OAuth2 token instead)
- API documented with Swagger
- client-side lib generated from OpenAPI spec => UI code consumes strongly typed payloads
- persitent data access with spring-data
- REST API services packaging as runnable jars, but also "java" Docker container and (soon) "native" Docker container
- UI packaging as web app as well as Android and iOS apps
- both servlet (with JPA) and fully reactive (webflux with R2DBC) API samples


## Requirements
- bash prompt with git on the classpath (on Windows, Git bash is perfect)
- JDK 11 or above
- Node LTS. You might use nvm to manage node version if you need other versions vor other projects
- Docker and Kubernetes (Docker Desktop is just fine)
- SSL certificate for your dev machine (see below to generate a self-signed one)
- Keycloak with a `starter` client. See [Keycloak doc](https://www.keycloak.org/docs/latest/server_installation/#_setting_up_ssl) for configuring your SLL certificate
- PostrgeSQL or any other relational DB with a `starter` db and `starter` user. It should be possible to connect to DB from docker containers.
- Following environment variables:
  - `SPRING_DATASOURCE_PASSWORD`: DB password for `starter` user
  - `SPRING_R2DBC_PASSWORD`: DB password for `starter` user
  - `SERVER_SSL_KEY_PASSWORD`: a password of your choice if you don't already SSL certificate
  - `SERVER_SSL_KEY_STORE_PASSWORD`: a password of your choice if you don't already SSL certificate
- openssl if you need to generate self-signed SSL certificate (Git bash includes it)


## Content

### `api` folder containing a maven mono-repo for spring-boot projects
Two sub-modules: first for servlet (webmvc + JPA) and second for reactive (weflux + R2DBC) apps, each with:
- `common-web` with default exception handler
- `common-security` with WebSecurityConfig for OpenID (and CORS)
- `starter-domain` with a few JPA entities and repositories
- `starter-api` with a @Controller, DTOs and OpenAPI spec generation. Also demos unit-tests covering security rules

### `angular-ui` folder containing an Angular mono-repo
- `@c4-soft/starter-api-webmvc` lib generated from OpenAPI spec
- `@c4-soft/starter-api-webflux` lib generated from OpenAPI spec
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
APIs are served at https://localhost:4210/households (servlet) and https://localhost:4310/faults (reactive)
All requests must be issued with authentication.
Use Postman or another REST client to get OAuth2 access-token from your OpenID authorisation-server and issue test requests.
You might get OAuth2 token endpoints from Keycloak from an URI like https://${HOSTNAME}:9443/auth/realms/master/.well-known/openid-configuration

### Runnable jar
``` bash
git clone https://github.com/ch4mpy/starter.git
cd starter/api
```
Edit `webmvc/starter-api-webmvc/src/main/resources/application.properties` and `webflux/starter-api-webflux/src/main/resources/application.properties` to match your environment (copy `bravo-ch4mp` profile section and create one matching your `$HOSTNAME`).
``` bash
# generate OpenAPI spec files
./mvnw clean verify -Popenapi
# generate runnable jars
./mvnw clean package
java -jar webmvc/starter-api-webmvc/target/starter-api-webmvc-0.0.1-SNAPSHOT.jar &
java -jar webflux/starter-api-webflux/target/starter-api-webflux-0.0.1-SNAPSHOT.jar &
```

### Docker
Edit `export HOST_IP=192.168.8.100` with your IP, then run:
``` bash
# current dir should be starter/api
cp $(echo $SERVER_SSL_KEY_STORE | sed "s/file:\/\/\///") ./webmvc/starter-api-webmvc/self_signed.jks
cp $(echo $SERVER_SSL_KEY_STORE | sed "s/file:\/\/\///") ./webflux/starter-api-webflux/self_signed.jks
cp $(echo $SERVER_SSL_KEY_STORE | sed "s/\.jks/\.pfx/" | sed "s/file:\/\/\///") ./webmvc/starter-api-webmvc/self_signed.pfx
cp $(echo $SERVER_SSL_KEY_STORE | sed "s/\.jks/\.pfx/" | sed "s/file:\/\/\///") ./webflux/starter-api-webflux/self_signed.pfx
docker build --build-arg SERVER_SSL_KEY_STORE_PASSWORD --build-arg HOSTNAME -t starter-api-webmvc:0.0.1-SNAPSHOT ./webmvc/starter-api-webmvc/
docker build --build-arg SERVER_SSL_KEY_STORE_PASSWORD --build-arg HOSTNAME -t starter-api-webflux:0.0.1-SNAPSHOT ./webflux/starter-api-webflux/
docker run \
  --add-host $HOSTNAME:$HOST_IP \
  -e SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD \
  -e SERVER_SSL_KEY_STORE=self_signed.jks \
  -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD \
  -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD \
  -e SPRING_PROFILES_ACTIVE=$HOSTNAME \
  -p 4210:4210 \
  --name starter-api-webmvc \
  -t starter-api-webmvc:0.0.1-SNAPSHOT &
docker run \
  --add-host $HOSTNAME:$HOST_IP \
  -e SPRING_R2DBC_PASSWORD=$SPRING_R2DBC_PASSWORD \
  -e SERVER_SSL_KEY_STORE=self_signed.jks \
  -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD \
  -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD \
  -e SPRING_PROFILES_ACTIVE=$HOSTNAME \
  -p 4310:4310 \
  --name starter-api-webflux \
  -t starter-api-webflux:0.0.1-SNAPSHOT &
```

### K8s
Edit `webmvc/starter-api-webmvc/src/k8s/starter-api-webmvc-deployment.yaml` and `webflux/starter-api-webflux/src/k8s/starter-api-webflux-deployment.yaml`with your IP
``` bash
# current dir should be starter/api
kubectl create configmap starter-api \
  --from-literal spring.profiles.active="kubernetes,${HOSTNAME}" \
  --from-literal server.ssl.key-store=self_signed.jks
kubectl create secret generic starter-api \
  --from-literal server.ssl.key.password=${SERVER_SSL_KEY_PASSWORD} \
  --from-literal server.ssl.key-store.password=${SERVER_SSL_KEY_STORE_PASSWORD} \
  --from-literal spring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
  --from-literal spring.r2dbc.password=${SPRING_R2DBC_PASSWORD}
docker tag starter-api-webmvc:0.0.1-SNAPSHOT starter-api-webmvc:0.0.1-SNAPSHOT
docker tag starter-api-webflux:0.0.1-SNAPSHOT starter-api-webflux:0.0.1-SNAPSHOT
kubectl apply -f webmvc/starter-api-webmvc/src/k8s/starter-api-webmvc-service.yaml
kubectl apply -f webmvc/starter-api-webmvc/src/k8s/starter-api-webmvc-deployment.yaml
kubectl apply -f webflux/starter-api-webflux/src/k8s/starter-api-webflux-service.yaml
kubectl apply -f webflux/starter-api-webflux/src/k8s/starter-api-webflux-deployment.yaml
```
API is served at https://localhost:4210 and https://localhost:4310


## Building and running Angular UI
Edit `angular-ui/starter-ui/package.json` to set your $HOSTNAME instead of "bravo-ch4mp"
``` bash
# initial dir should be starter/api
cd ../angular-ui
npm i
npm run starter-ui:serve
```