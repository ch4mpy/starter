import { Injectable } from '@angular/core';
import { Storage } from '@ionic/storage';
import { HouseholdControllerRestClient } from '@c4-soft/households-api';

@Injectable({ providedIn: 'root' })
export class SettingsService {
  constructor(
    private storage: Storage,
    private householdsApi: HouseholdControllerRestClient,
  ) {
    this.init();
  }

  async init() {
    return this.storage.create();
  }

  async getHouseholdsApiUrl(): Promise<string> {
    const url = await this.storage.get(SettingsService.HOUSEHOLDS_API_URL_BASE_PATH_KEY);
    return url || this.householdsApi.configuration.basePath;
  }

  setHouseholdsApiUrl(url: string) {
    return this.storage.set(SettingsService.HOUSEHOLDS_API_URL_BASE_PATH_KEY, url);
  }

  private static readonly HOUSEHOLDS_API_URL_BASE_PATH_KEY = "householdsApiBasePath";
}
