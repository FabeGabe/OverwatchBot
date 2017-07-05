package com.fabegabe.command.commands;

import com.fabegabe.Bot;
import com.fabegabe.command.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONArray;

/**
 * Created by User on 6/25/2017.
 */

public class DeSyncCommand implements Command {

    private Bot bot;

    public DeSyncCommand(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void exec(Message m, String[] args) {
        User user = m.getAuthor();
        if (args.length > 1) {
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append(" Usage: **" + getUsage() + "**")
                                    .build()).queue(message -> m.delete().queue());
            return;
        }
        if (!bot.getMain().getUsers().containsKey(user.getId())) {
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append(" You have not synchronized an account yet.")
                                    .build()).queue(message -> m.delete().queue());
            return;
        }
        JSONArray array = (JSONArray) bot.getMain().getUsers().get(user.getId());
        if (array.size() == 1) {
            bot.getMain().getUsers().remove(user.getId());
            bot.getMain().saveUserData();
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append(" \u2705 You have successfully desynchronized your Overwatch account!")
                                    .build()).queue(message -> m.delete().queue());
            return;
        }
        if (args.length == 0) {
            MessageBuilder builder = new MessageBuilder();
            builder.append(m.getAuthor());
            builder.append("\n```\n");
            int i = 0;
            for (Object object : array) {
                String s = String.valueOf(object);
                String[] split = s.split(":");
                builder.append((++i) + " - " + split[1] + " - Platform: " + split[0] + "\n");
            }
            builder.append("\n```\nUse \"+desync <#>\" to check the SR for that specific account.");
            m.getTextChannel().sendMessage(builder.build()).queue(message -> m.delete().queue());
            return;
        }
        if (!isInt(args[0])) {
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append(" \u26A0 Argument must be a number.")
                                    .build()).queue(message -> m.delete().queue());
            return;
        }
        int val = Integer.valueOf(args[0]);
        if (val > array.size() || val <= 0) {
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append(" \u26A0 Argument must be a number between 1 and " + array.size()
                                            + ". Do you really want to cause an ArrayOutOfBoundsException?")
                                    .build()).queue(message -> m.delete().queue());
            return;
        }
        String s = String.valueOf(array.get(val - 1));
        array.remove(s);
        bot.getMain().getUsers().put(user.getId(), array);
        bot.getMain().saveUserData();
        m.getTextChannel()
                .sendMessage(
                        new MessageBuilder().append(m.getAuthor())
                                .append(" \u2705 You have successfully desynchronized your Overwatch account!")
                                .build()).queue(message -> m.delete().queue());
    }

    @Override
    public String getName() {
        return "desync";
    }

    @Override
    public String getDescription() {
        return "De-Synchronize an already active Overwatch account from Discord!";
    }

    @Override
    public String getUsage() {
        return "+desync <number>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"desync", "owdesync", "owdesynchronize", "desyncro", "desynch", "owdesyncro", "delink",
                "unlink", "unsync", "owunlink", "overwatchunlink"};
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}