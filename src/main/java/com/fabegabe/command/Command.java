package com.fabegabe.command;

import net.dv8tion.jda.core.entities.Message;

/**
 * Created by User on 6/25/2017.
 */
public interface Command {

    void exec(Message m, String[] args);

    String getName();

    String getDescription();

    String getUsage();

    String[] getAliases();

}