# Starter project for spring-boot & Ionic-Angular

## Requirements
- JDK 11 or above
- Node LTS. You might use nvm to manage node version if you need other versions vor other projects
- SSL certificate for your dev machine (see below to generate a self-signed one)
- Keycloak with a `starter` realm and `starter-ui` client. See [Keycloak doc](https://www.keycloak.org/docs/latest/server_installation/#_setting_up_ssl) for configuring your SLL certificate
- PostrgeSQL or any other relational DB with a `starter` db and `starter` user
- Following environment variables:
  - `SPRING_DATASOURCE_PASSWORD`: DB password for `starter` user
  - `SERVER_SSL_KEY_PASSWORD`
  - `SERVER_SSL_KEY_STORE_PASSWORD`
- openssl if you need to generate self-signed SSL certificate (Git bash includes it)

## Content
- maven mono-repo for spring-boot projects containing
  * `common-web` with default exception handler
  * `common-security` with WebSecurityConfig for OpenID (and CORS)
  * `starter-domain` with a few JPA entities and repositories
  * `starter-api` with a @Controller, DTOs and OpenAPI spec generation
  * unit tests
- Angular mono-repo with
  * `@c4-soft/starter-api` lib generated from OpenAPI spec
  * `starter-ui` Ionic app with OpenID login (using Keycloak)

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

## Building and running

### On your dev machine
- `git clone https://github.com/ch4mpy/starter.git`
- `cd starter/api`
- Create a property file for a profile matching your hostname (see `application-bravo-ch4mp.properties` for a sample)
- `./mvnw -pl starter-api spring-boot:run -Dspring.profiles.active=bravo-ch4mp` (use your own profile instead of `bravo-ch4mp`)

### Docker
- `./mvnw clean package`
- `cp $(echo $SERVER_SSL_KEY_STORE | sed "s/file:\/\/\///") ./starter-api-webmvc/self_signed.jks`
- `cp $(echo $SERVER_SSL_KEY_STORE | sed "s/\.jks/\.pfx/" | sed "s/file:\/\/\///") ./starter-api-webmvc/self_signed.pfx`
- `docker build --build-arg SERVER_SSL_KEY_STORE_PASSWORD --build-arg HOSTNAME -t starter-api-webmvc:0.0.1-SNAPSHOT ./starter-api-webmvc/`
- edit following command with your IP: `docker run --add-host $HOSTNAME:192.168.1.59 -e SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD -e SERVER_SSL_KEY_STORE=self_signed.jks -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD -e SPRING_PROFILES_ACTIVE=$HOSTNAME -p 443:4210 --name starter-api-webmvc -t starter-api-webmvc:0.0.1-SNAPSHOT`

### K8s
- `kubectl create configmap starter-api --from-literal spring.profiles.active="k8s,${HOSTNAME}" --from-literal server.ssl.key-store=self_signed.jks`
- `kubectl create secret generic starter-api --from-literal server.ssl.key.password=${SERVER_SSL_KEY_PASSWORD} --from-literal server.ssl.key-store.password=${SERVER_SSL_KEY_STORE_PASSWORD} --from-literal spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}`
- `docker tag starter/api starter/api:snapshot`
- `kubectl apply -f starter-api/src/k8s/starter-api-service.yaml`
- `kubectl apply -f starter-api/src/k8s/starter-api-deployment.yaml`