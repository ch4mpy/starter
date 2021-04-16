# Starter project for spring-boot & Ionic-Angular

## Requirements

- JDK 11 or above
- Node LTS. You might use nvm to manage node version if you need other versions vor other projects
- SSL certificate for your dev machine
- Keycloak with a `starter` realm and `starter-ui` client. See [Keycloak doc](https://www.keycloak.org/docs/latest/server_installation/#_setting_up_ssl) for configuring your self-signed certificate
- openssl (Git bash is just fine)

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
- edit `self_signed_template.config` to set your e-mail, company etc.. Do not touch `[hostname]` wich is a placeholder updated by `self_signed.sh`.
- run `self_signed.sh` once with no arg to get help on expected and optional arguments.
- review the printed commands, copy and execute it. At first run you should need all of it.
If you have several JDKs installed, you should run `self_signed.sh` for each and execute last command only to keep the same certificate files.
- install `.crt` file as "trusted root authority" on your system (on Windows, this is done with `certmgr.msc`)