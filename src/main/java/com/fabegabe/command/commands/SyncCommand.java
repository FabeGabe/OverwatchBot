package com.fabegabe.command.commands;

import com.fabegabe.Bot;
import com.fabegabe.api.OverwatchAPI;
import com.fabegabe.command.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.json.simple.JSONArray;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by User on 6/25/2017.
 */

public class SyncCommand implements Command {

    private Bot bot;

    public SyncCommand(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void exec(Message m, String[] args) {
        if(args.length < 2) {
            m.getTextChannel()
                    .sendMessage(
                            new MessageBuilder().append(m.getAuthor())
                                    .append(" Usage: **" + getUsage() + "**")
                                    .build()).queue(message -> m.delete().queue());
            return;
        }
        switch(args[0].toLowerCase()) {
            case "xbla":
            case "xb1":
            case "xbone":
            case "xboxlive":
            case "xboxuno":
            case "xboxone":
            case "xbox":
                LinkedList<String> list = new LinkedList<>(Arrays.asList(args));
                list.removeFirst();
                StringBuilder builder = new StringBuilder();
                for (String s : list) {
                    builder.append(s).append(" ");
                }
                String xboxGamerTag = builder.toString().trim();
                switch (OverwatchAPI.getUserStatus(OverwatchAPI.Platform.XBOX, OverwatchAPI.Region.CONSOLE, xboxGamerTag)) {
                    case NOT_FOUND:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 The profile requested could not be found!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    case OK:
                        JSONArray array = new JSONArray();
                        if (bot.getMain().getUsers().containsKey(m.getAuthor().getId())) {
                            array = (JSONArray) bot.getMain().getUsers().get(m.getAuthor().getId());
                            if (array.contains("xbl:" + xboxGamerTag)) {
                                m.getTextChannel()
                                        .sendMessage(
                                                new MessageBuilder().append(m.getAuthor())
                                                        .append(" \u26A0 You have already " +
                                                                "synchronized this profile to your Discord account!")
                                                        .build()).queue(message -> m.delete().queue());
                                return;
                            }
                        }
                        array.add("xbl:" + xboxGamerTag);
                        bot.getMain().getUsers().put(m.getAuthor().getId(), array);
                        bot.getMain().saveUserData();
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u2705 You have successfully synchronized your " +
                                                        "**Xbox Live** profile with Discord!")
                                                .build()).queue(message -> m.delete().queue());
                        break;
                    case BAD_REQUEST:
                    case NOT_ACCEPTABLE:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                        " Please report this to the developer: ")
                                                .append(m.getJDA().getUserById(137277356791431168L))
                                                .build()).queue(message -> m.delete().queue());
                        break;
                    default:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \uD83D Hmmm. It seems the servers are currently down." +
                                                        " Please try again some other time.")
                                                .build()).queue(message -> m.delete().queue());
                        break;
                }
                break;
            case "ps4":
            case "psn":
            case "playstation":
            case "playstationnetwork":
            case "playstation4":
            case "play4":
            case "psf":
            case "psfour":
            case "playstationfour":
            case "playcuatro":
            case "playstationcuatro":
                if (args.length > 2) {
                    m.getTextChannel()
                            .sendMessage(
                                    new MessageBuilder().append(m.getAuthor())
                                            .append(" Usage: **" + getUsage() + "**")
                                            .build()).queue(message -> m.delete().queue());
                    return;
                }
                String psnId = args[1];
                switch (OverwatchAPI.getUserStatus(OverwatchAPI.Platform.PLAYSTATION, OverwatchAPI.Region.CONSOLE, psnId)) {
                    case NOT_FOUND:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 The profile requested could not be found!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    case OK:
                        JSONArray array = new JSONArray();
                        if (bot.getMain().getUsers().containsKey(m.getAuthor().getId())) {
                            array = (JSONArray) bot.getMain().getUsers().get(m.getAuthor().getId());
                            if (array.contains("psn:" + psnId)) {
                                m.getTextChannel()
                                        .sendMessage(
                                                new MessageBuilder().append(m.getAuthor())
                                                        .append(" \u26A0 You have already" +
                                                                " synchronized this profile to your Discord account!")
                                                        .build()).queue(message -> m.delete().queue());
                                return;
                            }
                        }
                        array.add("psn:" + psnId);
                        bot.getMain().getUsers().put(m.getAuthor().getId(), array);
                        bot.getMain().saveUserData();
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u2705 You have successfully synchronized your " +
                                                        "PlayStation Network profile with Discord!")
                                                .build()).queue(message -> m.delete().queue());
                        break;
                    case BAD_REQUEST:
                    case NOT_ACCEPTABLE:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                        " Please report this to the developer: ")
                                                .append(m.getJDA().getUserById(137277356791431168L))
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    default:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \uD83D Hmmm. It seems the servers are currently down." +
                                                        " Please try again some other time.")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                }
                break;
            case "pc":
            case "battlenet":
            case "computer":
            case "pcmasterrace":
            case "windows":
            case "battle.net":
            case "blizzardclient":
            case "blizzardapp":
            case "bnet":
                if (args.length > 3) {
                    m.getTextChannel()
                            .sendMessage(
                                    new MessageBuilder().append(m.getAuthor())
                                            .append(" Usage: **" + getUsage() + "**")
                                            .build()).queue(message -> m.delete().queue());
                    return;
                }
                OverwatchAPI.Region reg = OverwatchAPI.Region.AMERICAS;
                if (OverwatchAPI.Region.isValidRegion(args[1])) {
                    String region = args[1];
                    reg = OverwatchAPI.Region.getByName(region);
                }
                String battleTag = args[OverwatchAPI.Region.isValidRegion(args[1]) ? 2 : 1];
                if(!battleTag.contains("#")) {
                    m.getTextChannel()
                            .sendMessage(
                                    new MessageBuilder().append(m.getAuthor())
                                            .append(" Wrong Battle Tag format! You must provide a #. Ex: `TestDummy#1234`")
                                            .build()).queue(message -> m.delete().queue());
                    return;
                }
                switch(OverwatchAPI.getUserStatus(OverwatchAPI.Platform.PC, reg, battleTag)) {
                    case NOT_FOUND:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 The profile requested could not be found!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    case OK:
                        JSONArray array = new JSONArray();
                        if(bot.getMain().getUsers().containsKey(m.getAuthor().getId())) {
                            array = (JSONArray) bot.getMain().getUsers().get(m.getAuthor().getId());
                            if (array.contains("pc-" + reg + ":" + battleTag)) {
                                m.getTextChannel()
                                        .sendMessage(
                                                new MessageBuilder().append(m.getAuthor())
                                                        .append(" \u26A0 You have already synchronized" +
                                                                " this profile to your Discord account!")
                                                        .build()).queue(message -> m.delete().queue());
                                return;
                            }
                        }
                        array.add("pc-" + reg + ":" + battleTag);
                        bot.getMain().getUsers().put(m.getAuthor().getId(), array);
                        bot.getMain().saveUserData();
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u2705 You have successfully synchronized your " +
                                                        "Overwatch profile with Discord!")
                                                .build()).queue(message -> m.delete().queue());
                        break;
                    case BAD_REQUEST:
                    case NOT_ACCEPTABLE:
                        m.getTextChannel()
                            .sendMessage(
                                    new MessageBuilder().append(m.getAuthor())
                                            .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                    " Please report this to the developer: ")
                                            .append(m.getJDA().getUserById(137277356791431168L))
                                            .build()).queue(message -> m.delete().queue());
                        return;
                    default:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \uD83D Hmmm. It seems the servers are currently down." +
                                                        " Please try again some other time.")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                }
                break;
            default:
                m.getTextChannel()
                        .sendMessage(
                                new MessageBuilder().append(m.getAuthor())
                                        .append(" \u26A0 Invalid platform! (pc, ps4, xbox)")
                                        .build()).queue(message -> m.delete().queue());
                return;
        }
    }

    @Override
    public String getName() {
        return "sync";
    }

    @Override
    public String getDescription() {
        return "Synchronize your Discord account with your Overwatch account!";
    }

    @Override
    public String getUsage() {
        return "+sync <pc|xbox|ps4> [region (pc only); default = us] <username|battletag>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"synchronize", "owsync", "owsynchronize", "syncro", "synch", "owsyncro"
                , "link", "owlink", "overwatchlink"};
    }

}