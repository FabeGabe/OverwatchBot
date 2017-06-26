package com.fabegabe.command;

import com.fabegabe.Bot;
import com.fabegabe.command.commands.HelpCommand;
import com.fabegabe.command.commands.SRCommand;
import com.fabegabe.command.commands.SyncCommand;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by User on 6/25/2017.
 */

public class CommandRegistry {

    private Set<Command> commands;

    public CommandRegistry(Bot bot) {
        this.commands = new HashSet<>();
        this.commands.add(new HelpCommand(this));
        this.commands.add(new SyncCommand(bot));
        this.commands.add(new SRCommand(bot));
    }

    public Command getCommand(String command) {
        for(Command c : commands)
            if(c.getName().equalsIgnoreCase(command))
                return c;
        for(Command c : commands)
            if(Arrays.asList(c.getAliases()).contains(command))
                return c;
        return null;
    }

    public Set<Command> getCommands() {
        return commands;
    }

}