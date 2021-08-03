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
}
