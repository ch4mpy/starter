export class OidcUser {
  sub: string;
  preferredUsername: string;
  roles: string[] = [];

  constructor(init?: Partial<OidcUser>) {
    Object.assign(this, init);
  }

  isAuthenticated(): boolean {
    return !!this.sub;
  }

  static readonly ANONYMOUS = new OidcUser({});
}
