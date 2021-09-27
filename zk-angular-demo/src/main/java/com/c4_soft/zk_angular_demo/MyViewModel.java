package com.c4_soft.zk_angular_demo;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;

@NotifyCommand(value = "updateCount", onChange = "_vm_.count")
@ToClientCommand({ "updateCount" })
@ToServerCommand({ "init", "increment" })
public class MyViewModel {

	private int count;

	private String matricule;

	@Init
	@Command
	@NotifyChange("count")
	public void init() {
		count = 100;
	}

	@Command
	@NotifyChange("count")
	public void increment() {
		++count;
	}

	public int getCount() {
		return count;
	}

	public String getMatricule() {
		return matricule;
	}

	public void setMatricule(String matricule) {
		System.out.println("Setting matricule with: " + matricule);
		this.matricule = matricule;
	}
}
