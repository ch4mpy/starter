import { Injectable } from '@angular/core';

declare var zkbind: any

export class ZkService {
  private zk: {
    command: (name: string) => void,
    after: (name: string, callback: (resp: any) => void) => void
  };

  constructor(zkId: String) { 
    try {
      this.zk = zkbind.$("$" + zkId);
    } catch(error) {
      console.warn('Non ZK env', zkId, error);
      this.zk = {
        command: (name: string) => {},
        after: (name: string, callback: (resp: any) => void) => {}
      }
    }
  }

  command(name: string) {
    this.zk.command(name);
  }

  after(name: string, callback: (resp: any) => void) {
    this.zk.after("updateCount", callback);
  }
  
}
