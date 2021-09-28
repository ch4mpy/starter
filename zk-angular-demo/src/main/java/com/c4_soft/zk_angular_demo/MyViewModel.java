package com.c4_soft.zk_angular_demo;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

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
		this.matricule = matricule + count;
		if (matricule != null && !matricule.isEmpty()) {
			System.out.println("Setting matricule with: " + matricule + count);
		} else {
			System.out.println("got empty matricule: " + matricule);
		}
	}
}
