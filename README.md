# Starter project for spring-boot & Ionic-Angular
Mono repo for:
- spring-boot REST micro-services contained in a maven multi-module project (see `api` below)
- Angular front-end containing libraries and a Ionic application (see `angular-ui` below)

The aim here is to go further than most "getting-started" guides with following stuff ready:
- mono-repo but hight modularity from start
- all traffic over https, even on dev machine or inside Docker (and K8s)
- security with OpenID on both resource-server (REST APIs) and client (Angular app). Default authorisation server is Auth0, but it's just a matter of configuration to switch to Keycloak, MS Identity Server or whatever
- spring-security annotations
- no session (use OAuth2 token instead)
- API documented with OpenAPI (Swagger)
- client-side lib generated from OpenAPI spec => UI code consumes strongly typed payloads
- persitent data access with spring-data
- REST API services packaging as runnable jars, but also "java" Docker container and GraalVM "native" Docker container
- UI packaging as web app as well as Android and iOS apps
- three different app options:
  * households-api is servlet / JPA / HATOEAS
  * orders-API is servlet / JPA / hibernate-envers 
  * faults-api is fully reactive (webflux with R2DBC)


## Requirements
- bash prompt with git on the classpath (on Windows, Git bash is perfect)
- JDK 16 or above
- Node LTS. You might use nvm to manage node version if you need other versions vor other projects
- Docker and Kubernetes (Docker Desktop is just fine)
- SSL certificate for your dev machine (see below to generate a self-signed one)
- access to a relational DB
  * a local Postgresql instance with a `starter` db and `starter` user is fine. Do not forget to check in `pg_hba.conf` that it's be possible to connect to DB from docker containers / k8s nodes.
  * a free instance from [elephantsql.com](https://elephantsql.com) works too. Just activate `elephant` profile (after editing `application.properties` files with the username you got from elephantsql.com)
  * any other relational DB ... given you edit configuration
- Following environment variables:
``` bash
# ~/.bashrc
export SPRING_DATASOURCE_PASSWORD=change-me
export SPRING_R2DBC_PASSWORD=change-me
export SERVER_SSL_KEY_PASSWORD=change-me
export SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_PASSWORD

# replace wlp4s0 with your network adapter ID or hardcode IP or wahtever
export HOST_IP=`ip -f inet -o addr show wlp4s0 | cut -d\  -f 7 | cut -d/ -f 1`
```
- openssl if you need to generate self-signed SSL certificate (Git bash includes it)

I defaulted OpenID authorisation server to an [Auth0](https://auth0.com) free plan.
I also provide with a `keycloak` profile to switch from Auth0 in the cloud to a local Keycloak instance. This requires quite some configuration both on Keycloak instance (to add roles to tokens) and `application.properties`.

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
- install certificate file as "trusted root authority" on your system 
  - on Windows: run `certmgr.msc` as admin and import .crt file as root authority certificate
  - on Fedora 34 `sudo cp ~/.ssh/${HOSTNAME}_self_signed.pem /etc/pki/ca-trust/source/anchors/ && sudo update-ca-trust && certutil -d sql:$HOME/.pki/nssdb -n ${HOSTNAME} -A -t "TCu,TCu,TCu" -i ~/.ssh/${HOSTNAME}_self_signed.pem`
- define `SERVER_SSL_KEY_STORE` environement variable to something like `file:///C:/Users/ch4mp/.ssh/bravo-ch4mp_self_signed.jks` or `file:///home/ch4mp/.ssh/bravo-ch4mp_self_signed.jks`

You might refer to this [answer I posted on stackoverflow](https://stackoverflow.com/questions/63874373/how-to-properly-setup-my-ionic-angular-dev-machine-with-self-signed-certificat/63874376#63874376) for details about configuring Angular / Ionic app with SSL certificates.

## Building and running REST API
APIs are served at https://localhost:4201/households (servlet), https://localhost:4202/orders (servlet) and https://localhost:4203/faults (reactive)
All requests must be issued with authentication.
Use Postman or another REST client to get OAuth2 access-token from your OpenID authorisation-server and issue test requests.
You might get OAuth2 token endpoints from Keycloak from an URI like https://${HOSTNAME}:9443/auth/realms/master/.well-known/openid-configuration

### Clone and configure project
``` bash
git clone https://github.com/ch4mpy/starter.git
cd starter/api
cp ~/.ssh/${HOSTNAME}_self_signed.jks ./webmvc/households-api/src/main/resources/self_signed.jks
cp ~/.ssh/${HOSTNAME}_self_signed.jks ./webmvc/orders-api/src/main/resources/self_signed.jks
cp ~/.ssh/${HOSTNAME}_self_signed.jks ./webflux/faults-api/src/main/resources/self_signed.jks
cp ~/.ssh/${HOSTNAME}_self_signed.pem ./bindings/ca-certificates/
```

Edit `webmvc/households-api/src/main/resources/application.properties`, `webmvc/orders-api/src/main/resources/application.properties` and `webflux/faults-api/src/main/resources/application.properties` to match your environment (copy `bravo-ch4mp` profile section and create one matching your `$HOSTNAME`).

Edit `webmvc/households-api/src/k8s/deployment.yaml`, `webmvc/orders-api/src/k8s/deployment.yaml` and `webflux/faults-api/src/k8s/deployment.yaml`with your IP.

### Generate OpenAPI spec files
``` bash
./mvnw clean verify -Popenapi
```

### Packaging profiles

#### no profile => genrate "fat" jars
``` bash
./mvnw clean package
```

#### `build-image` => Docker containers with regular java app
``` bash
mvn clean package -Pbuild-image
```

#### `build-native-image` => Docker containers with Graalvm native image
Native image is build inside docker containers. Network eavy as GraalVM is downloaded at each build.
Generate
``` bash
mvn clean package -Pbuild-native-image -DskipTests
```

### Run
APIs are served at:
- https://bravo-ch4mp:4201/households
- https://bravo-ch4mp:4202/orders
- https://bravo-ch4mp:4203/faults
- https://bravo-ch4mp:4204/grants & https://bravo-ch4mp:4204/users

#### fat-jar
``` bash
java -jar webmvc/households-api/target/households-api-0.0.1-SNAPSHOT.jar &
java -jar webmvc/orders-api/target/orders-api-0.0.1-SNAPSHOT.jar &
java -jar webmvc/proxies-api/target/proxies-api-0.0.1-SNAPSHOT.jar &
java -jar webflux/faults-api/target/faults-api-0.0.1-SNAPSHOT.jar &
```

#### docker
Depending which DB setup you chose, you might use `elephant` instead of `$HOSTNAME-db` profile
``` bash
# stop and remove potentially already running containers
docker stop households-api
docker stop orders-api
docker stop faults-api
docker stop proxies-api
docker rm households-api
docker rm orders-api
docker rm faults-api
docker rm proxies-api

# start
docker run --rm \
  --add-host $HOSTNAME:$HOST_IP \
  -e SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD \
  -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD \
  -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://$HOST_IP/starter \
  -p 4201:4201 \
  --name households-api \
  -t households-api:0.0.1-SNAPSHOT &

docker run --rm \
  --add-host $HOSTNAME:$HOST_IP \
  -e SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD \
  -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD \
  -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://$HOST_IP/starter \
  -p 4202:4202 \
  --name orders-api \
  -t orders-api:0.0.1-SNAPSHOT &

docker run --rm\
  --add-host $HOSTNAME:$HOST_IP \
  -e SPRING_R2DBC_PASSWORD=$SPRING_R2DBC_PASSWORD \
  -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD \
  -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://$HOST_IP/starter \
  -p 4203:4203 \
  --name faults-api \
  -t faults-api:0.0.1-SNAPSHOT &

docker run --rm\
  --add-host $HOSTNAME:$HOST_IP \
  -e SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD \
  -e SERVER_SSL_KEY_PASSWORD=$SERVER_SSL_KEY_PASSWORD \
  -e SERVER_SSL_KEY_STORE_PASSWORD=$SERVER_SSL_KEY_STORE_PASSWORD \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://$HOST_IP/starter \
  -p 4204:4204 \
  --name proxies-api \
  -t proxies-api:0.0.1-SNAPSHOT &
```

#### K8s
Depending which DB setup you chose, you might use `elephant` instead of `${HOSTNAME}-db` profile
``` bash
kubectl create configmap starter-api \
  --from-literal spring.profiles.active="kubernetes,${HOSTNAME}-db"
kubectl create secret generic starter-api \
  --from-literal server.ssl.key.password=${SERVER_SSL_KEY_PASSWORD} \
  --from-literal server.ssl.key-store.password=${SERVER_SSL_KEY_STORE_PASSWORD} \
  --from-literal spring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
  --from-literal spring.r2dbc.password=${SPRING_R2DBC_PASSWORD}
docker tag faults-api:0.0.1-SNAPSHOT faults-api:0.0.1-SNAPSHOT
docker tag households-api:0.0.1-SNAPSHOT households-api:0.0.1-SNAPSHOT
docker tag orders-api:0.0.1-SNAPSHOT orders-api:0.0.1-SNAPSHOT
docker tag proxies-api:0.0.1-SNAPSHOT proxies-api:0.0.1-SNAPSHOT

# current dir should be starter/api
kubectl proxy &
kubectl apply -f webflux/faults-api/src/k8s/service.yaml
kubectl apply -f webflux/faults-api/src/k8s/deployment.yaml
kubectl apply -f webmvc/households-api/src/k8s/service.yaml
kubectl apply -f webmvc/households-api/src/k8s/deployment.yaml
kubectl apply -f webmvc/orders-api/src/k8s/service.yaml
kubectl apply -f webmvc/orders-api/src/k8s/deployment.yaml
kubectl apply -f webmvc/proxies-api/src/k8s/service.yaml
kubectl apply -f webmvc/proxies-api/src/k8s/deployment.yaml
```

## Angular UI

### Building and running

Edit `angular-ui/starter-ui/package.json` to set your $HOSTNAME instead of "bravo-ch4mp"
``` bash
# initial dir should be starter/api
cd ../angular-ui
npm i
npm run starter-ui:serve
```

### Creating from scratch a new Angular workspace with Ionic app and OpenAPI client lib

This commands should be run from `angular-workspace-templates` parent directory. It will:
- create an Angular workspace
- add an Angular application to this workspace
- turn this regular Angular app into an Ionic one (install dependencies, replace or edit angular config files)
- replace Angular minimal app with an Ionic app composed of a menu in a split-pane and two pages
- add an Angular lib to Angular workspace. This lib is generated by `openapitools` from the OpenAPI spec file produced by Spring REST API (with `mvn clean verify -Popenapi`)
``` bash
# Edit this exports according to your needs
export WORKSPACE_NAME=angular-workspace
export APP_NAME=bao-loc
export API_LIB_NAME=bao-loc-api
export API_SPEC_FILE=bao-loc.openapi.json
export CAPACITOR_ID=com.c4_soft.bao_loc

# Prepare a script copy
cp angular-workspace-templates/init.sh $WORKSPACE_NAME-init.sh
sed -i 's/WORKSPACE_NAME/'$WORKSPACE_NAME'/g' $WORKSPACE_NAME-init.sh
sed -i 's/APP_NAME/'$APP_NAME'/g' $WORKSPACE_NAME-init.sh
sed -i 's/API_LIB_NAME/'$API_LIB_NAME'/g' $WORKSPACE_NAME-init.sh
sed -i 's/API_SPEC_FILE/'$API_SPEC_FILE'/g' $WORKSPACE_NAME-init.sh
sed -i 's/CAPACITOR_ID/'$CAPACITOR_ID'/g' $WORKSPACE_NAME-init.sh
sed -i 's/HOSTNAME/'$HOSTNAME'/g' $WORKSPACE_NAME-init.sh
sed -i 's/USERNAME/'$USERNAME'/g' $WORKSPACE_NAME-init.sh

# Install required tools
npm i -g @angular/cli
npm i -g @ionic/cli

# Go!
bash $WORKSPACE_NAME-init.sh

# cleanup
rm $WORKSPACE_NAME-init.sh
```
