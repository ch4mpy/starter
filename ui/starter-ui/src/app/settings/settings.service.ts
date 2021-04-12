import { Injectable } from '@angular/core';
import { Storage } from '@ionic/storage';
import { HouseholdControllerRestClient } from '@c4-soft/starter-api';

@Injectable({ providedIn: 'root' })
export class SettingsService {
  constructor(
    private storage: Storage,
    private starterApi: HouseholdControllerRestClient,
  ) {
    this.init();
  }

  async init() {
    return this.storage.create();
  }

  getStarterApiUrl(): Promise<string> {
    return this.storage.get(SettingsService.CITIZEN_API_URL_BASE_PATH_KEY).then(url => url || this.starterApi.configuration.basePath);
  }

  setStarterApiUrl(url: string) {
    return this.storage.set(SettingsService.CITIZEN_API_URL_BASE_PATH_KEY, url);
  }

  private static readonly CITIZEN_API_URL_BASE_PATH_KEY = "starterApiUrl";
}
