import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import {
  FaultResponseDto
} from "@c4-soft/faults-api";
import {
  HouseholdsControllerRestClient,
  HouseholdTypeDto
} from "@c4-soft/households-api";

@Component({
  selector: "app-root",
  template: `
    <!--The content below is only a placeholder and can be replaced.-->
    <div style="text-align:center;" class="content">
      <h1>Application Angular 12</h1>
    </div>
    <mat-grid-list cols="2">
      <mat-grid-tile>
        <mat-card>
          <mat-card-header>
            <mat-card-title>ZK</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <p>Compteur ZK: {{ cnt }}</p>
            <button
              (click)="incrementZk()"
              mat-raised-button
              style="margin: 1em;"
              color="accent"
            >
              ZK ++
            </button>
            <button
              (click)="resetZk()"
              mat-raised-button
              style="margin: 1em;"
              color="accent"
            >
              Reset
            </button>
          </mat-card-content>
        </mat-card>
      </mat-grid-tile>
      <mat-grid-tile>
        <mat-card>
          <mat-card-header>
            <mat-card-title>micro-service REST</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <p *ngFor="let t of householdTypes">
              {{ t.label }}
            </p>
            <button
              (click)="fetchHousehodTypes()"
              mat-raised-button
              style="margin: 1em;"
              color="accent"
            >
              get
            </button>
            <button
              (click)="clearHousehodTypes()"
              mat-raised-button
              style="margin: 1em;"
              color="accent"
            >
              clear
            </button>
          </mat-card-content>
        </mat-card>
      </mat-grid-tile>
    </mat-grid-list>
    <router-outlet></router-outlet>
  `,
  styles: [],
})
export class AppComponent implements OnInit {
  cnt = 0;

  householdTypes: Array<HouseholdTypeDto> = [];
  faults: FaultResponseDto[] = [];

  constructor(
    private householdsApi: HouseholdsControllerRestClient
  ) {}

  ngOnInit(): void {
    this.resetZk();
  }

  incrementZk() {
    var intputElt = document.querySelector('.matriculeTextbox') as HTMLInputElement;
    intputElt.value = 'Hacked!';
    intputElt.dispatchEvent(new FocusEvent('blur'));
    
    var incrementBtn = document.querySelector('.incrementButton') as HTMLButtonElement;
    incrementBtn.click();
  }

  resetZk() {
  }

  clearHousehodTypes() {
    this.householdTypes = [];
  }

  fetchHousehodTypes() {
    this.householdsApi
      .getAllTypes()
      .toPromise()
      .then((types) => (this.householdTypes = types._embedded.householdTypes));
  }
}
