package com.fabegabe.command.commands;

import com.fabegabe.command.Command;
import com.fabegabe.command.CommandRegistry;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;

/**
 * Created by User on 6/25/2017.
 */

public class HelpCommand implements Command {

    private CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void exec(Message m, String[] args) {
        if (args.length > 1) {
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append("\nUsage: **+help [command]**")
                                    .build()).queue(message -> m.delete().queue());
            return;
        }
        if (args.length == 0) {
            MessageBuilder builder = new MessageBuilder();
            builder.append("```\n");
            for (Command command : registry.getCommands()) {
                builder.append(command.getName() + " - "
                        + command.getDescription());
                builder.append("\nUsage: " + command.getUsage());
                builder.append("\nAliases: "
                        + Arrays.toString(command.getAliases()) + "\n");
            }
            builder.append("```");
            m.getAuthor().openPrivateChannel().queue(privateChannel ->
                    {
                        privateChannel.sendMessage(builder.build())
                                .queue();
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder()
                                                .append(m.getAuthor())
                                                .append(" You have been sent the help guide! ")
                                                .append("\uD83D\uDCD1").build()).queue();
                        m.delete().queue();
                    }
            );
            return;
        }
        if (registry.getCommand(args[0]) == null) {
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append(" That command does not exist! ")
                                    .append("\u26D4").build()).queue(message -> m.delete().queue());
            return;
        }
        MessageBuilder builder = new MessageBuilder();
        Command command = registry.getCommand(args[0]);
        builder.append(m.getAuthor());
        builder.append("\n```\n");
        builder.append(command.getName() + " - " + command.getDescription());
        builder.append("\n" + "Usage: " + command.getUsage() + "\n");
        builder.append("\n" + "Aliases: "
                + Arrays.toString(command.getAliases()));
        builder.append("```");
        m.getTextChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows the description and usages of commands!";
    }

    @Override
    public String getUsage() {
        return "+help [command]";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

}