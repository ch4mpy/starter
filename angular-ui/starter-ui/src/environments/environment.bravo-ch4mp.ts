import { LogLevel, OpenIdConfiguration } from "angular-auth-oidc-client";

const openIdConfiguration: OpenIdConfiguration = {
  // https://github.com/damienbod/angular-auth-oidc-client/blob/master/docs/configuration.md
  clientId: 'PLJO8P148QjTwkbNUy8BhCVcpFmLqXtG',
  forbiddenRoute: '/settings',
  eagerLoadAuthWellKnownEndpoints: false,
  ignoreNonceAfterRefresh: true, // Keycloak sends refresh_token with nonce
  logLevel: LogLevel.Warn,
  postLogoutRedirectUri: 'https://bravo-ch4mp:8100',
  redirectUrl: 'https://bravo-ch4mp:8100',
  renewTimeBeforeTokenExpiresInSeconds: 60,
  responseType: 'code',
  scope: 'email openid offline_access roles',
  silentRenew: true,
  useRefreshToken: true,
  authority: 'https://dev-ch4mpy.eu.auth0.com',
  unauthorizedRoute: '/settings',
};

export const environment = {
  production: false,
  openIdConfiguration,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
