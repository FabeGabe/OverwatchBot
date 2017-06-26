package com.fabegabe.command.commands;

import com.fabegabe.Bot;
import com.fabegabe.command.Command;
import com.fabegabe.api.OverwatchAPI;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;

/**
 * Created by User on 6/26/2017.
 */

public class SRCommand implements Command {

    private Bot bot;

    public SRCommand(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void exec(Message m, String[] args) {
        String id = m.getAuthor().getId();
        if(args.length == 0) {
            if(!bot.getMain().getUsers().containsKey(id)) {
                m.getTextChannel()
                        .sendMessage(
                                new MessageBuilder().append(m.getAuthor())
                                        .append(" \u26A0 You must first synchronize an Overwatch account with Discord!" +
                                                " Use **+sync** to link your account now!")
                                        .build()).queue(message -> m.delete().queue());
                return;
            }
            JSONArray array = (JSONArray) bot.getMain().getUsers().get(id);
            MessageBuilder builder = new MessageBuilder();
            builder.append(m.getAuthor());
            builder.append("\n```\n");
            int i = 0;
            for(Object object : array) {
                String s = (String) object;
                String[] split = s.split(":");
                builder.append((++i) + " - " + split[1] + " - Platform: " + split[0] + "\n");
            }
            builder.append("\n```\nUse \"+sr <#>\" to check the SR for that specific account.");
            m.getTextChannel().sendMessage(builder.build()).queue(message -> m.delete().queue());
            return;
        }
        if(args.length == 1) {
            if(!bot.getMain().getUsers().containsKey(id)) {
                m.getTextChannel()
                        .sendMessage(
                                new MessageBuilder().append(m.getAuthor())
                                        .append(" \u26A0 You must first synchronize an Overwatch account with Discord!" +
                                                " Use **+sync** to link your account now!")
                                        .build()).queue(message -> m.delete().queue());
                return;
            }
            if(!isInt(args[0])) {
                m.getTextChannel()
                        .sendMessage(
                                new MessageBuilder().append(m.getAuthor())
                                        .append(" \u26A0 Argument must be a number.")
                                        .build()).queue(message -> m.delete().queue());
                return;
            }
            int val = Integer.valueOf(args[0]);
            JSONArray array = (JSONArray) bot.getMain().getUsers().get(id);
            if(val > array.size() || val <= 0) {
                m.getTextChannel()
                        .sendMessage(
                                new MessageBuilder().append(m.getAuthor())
                                        .append(" \u26A0 Argument must be a number between 1 and " + array.size()
                                                + ". Do you really want to cause an ArrayOutOfBoundsException?")
                                        .build()).queue(message -> m.delete().queue());
                return;
            }
            String s = (String) array.get(val - 1);
            String[] split = s.split(":");
            if(split[0].startsWith("pc")) {
                OverwatchAPI.Region region = OverwatchAPI.Region.getByName(split[0].split("(?=-)")[1]);
                String battleTag = split[1];
                switch(OverwatchAPI.getUserStatus(OverwatchAPI.Platform.PC, region, battleTag)) {
                    case NOT_FOUND:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 The profile requested could not be found!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    case OK:
                        JSONObject object = OverwatchAPI.getPlayerInfo(OverwatchAPI.Platform.PC,
                                region, battleTag);
                        if(!(Boolean)object.get("success")) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                            " Please report this to the developer: ")
                                                    .append(m.getJDA().getUserById(137277356791431168L))
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        JSONObject result = (JSONObject) object.get("result");
                        if(((String)result.get("rating")).isEmpty()) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" The requested user doesn't have a competitive profile!")
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.ORANGE);
                        builder.setAuthor((String) result.get("name"), "https://www.overbuff.com/players/pc/"
                                + battleTag.replaceAll("#", "-"), "http://bit.ly/2tbBuCT");
                        builder.setThumbnail((String) result.get("icon"));
                        // builder.setImage((String) result.get("ratingIcon"));
                        builder.addField("Platform", "PC-" + region.toString().toUpperCase(), false);
                        builder.addField("SR", (String) result.get("rating"), false);
                        builder.addField("Rank", (String) result.get("ratingName"), false);
                        JSONObject compStats = ((JSONObject) result.get("competitiveStats"));
                        JSONObject gamesPlayed = (JSONObject) compStats.get("games");
                        builder.addField("Games Played", gamesPlayed.get("won")
                                + "/" + gamesPlayed.get("played") + " Games Won", false);
                        // WIP
//                        double ratio = (double) (Integer.valueOf(gamesPlayed.get("won").toString())
//                                / Integer.valueOf(gamesPlayed.get("played").toString()) * 100);
//                        builder.addField("Win Ratio", Double.toString(ratio) + "%", false);
                        builder.setTitle(result.get("name") + "'s Competitive Overwatch Stats");
                        MessageEmbed embed = builder.build();
                        m.getTextChannel().sendMessage(embed).queue(message -> m.delete().queue());
                        return;
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
            }
            OverwatchAPI.Platform platform = OverwatchAPI.Platform.getByName(split[0]);
            OverwatchAPI.Region region = OverwatchAPI.Region.CONSOLE;
            switch(OverwatchAPI.getUserStatus(OverwatchAPI.Platform.PC, region, split[1])) {
                case NOT_FOUND:
                    m.getTextChannel()
                            .sendMessage(
                                    new MessageBuilder().append(m.getAuthor())
                                            .append(" \u26A0 The profile requested could not be found!")
                                            .build()).queue(message -> m.delete().queue());
                    return;
                case OK:
                    JSONObject object = OverwatchAPI.getPlayerInfo(OverwatchAPI.Platform.PC,
                            region, split[1]);
                    if(!(Boolean)object.get("success")) {
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                        " Please report this to the developer: ")
                                                .append(m.getJDA().getUserById(137277356791431168L))
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    }
                    JSONObject result = (JSONObject) object.get("result");
                    if(((String)result.get("rating")).isEmpty()) {
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" The requested user doesn't have a competitive profile!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    }
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.ORANGE);
                    builder.setAuthor((String) result.get("name"), "https://www.overbuff.com/players/" + platform + "/"
                            + split[1], "http://bit.ly/2tbBuCT");
                    builder.setThumbnail((String) result.get("icon"));
                    // builder.setImage((String) result.get("ratingIcon"));
                    builder.addField("Platform", platform.toString().toUpperCase(), false);
                    builder.addField("SR", (String) result.get("rating"), false);
                    builder.addField("Rank", (String) result.get("ratingName"), false);
                    JSONObject compStats = ((JSONObject) result.get("competitiveStats"));
                    JSONObject gamesPlayed = (JSONObject) compStats.get("games");
                    builder.addField("Games Played", gamesPlayed.get("won")
                            + "/" + gamesPlayed.get("played") + " Games Won", false);
//                    double ratio = (double) Integer.valueOf((String) gamesPlayed.get("won"))
//                            / Integer.valueOf((String)gamesPlayed.get("played")) * 100;
//                    builder.addField("Win Ratio", ratio + "%", false);
                    builder.setTitle(result.get("name") + "'s Competitive Overwatch Stats");
                    MessageEmbed embed = builder.build();
                    m.getTextChannel().sendMessage(embed).queue(message -> m.delete().queue());
                    return;
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
        }
        OverwatchAPI.Region region = OverwatchAPI.Region.CONSOLE;
        if(args.length > 2) {
            region = OverwatchAPI.Region.getByName(args[1]);
        }
        switch(args[0]) {
            case "xbla":
            case "xb1":
            case "xbone":
            case "xboxlive":
            case "xboxuno":
            case "xboxone":
            case "xbox":
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = (OverwatchAPI.Region.isValidRegion(args[1]) ? 1 : 2); i < args.length; i++) {
                    stringBuilder.append(args[i]).append(" ");
                }
                String xboxGamerTag = stringBuilder.toString().trim();
                switch(OverwatchAPI.getUserStatus(OverwatchAPI.Platform.XBOX, region, xboxGamerTag)) {
                    case NOT_FOUND:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 The profile requested could not be found!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    case OK:
                        JSONObject object = OverwatchAPI.getPlayerInfo(OverwatchAPI.Platform.XBOX,
                                OverwatchAPI.Region.CONSOLE, xboxGamerTag);
                        if(!(Boolean)object.get("success")) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                            " Please report this to the developer: ")
                                                    .append(m.getJDA().getUserById(137277356791431168L))
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        JSONObject result = (JSONObject) object.get("result");
                        if(((String)result.get("rating")).isEmpty()) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" The requested user doesn't have a competitive profile!")
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.ORANGE);
                        builder.setAuthor((String) result.get("name"), "https://www.overbuff.com/players/xbl/"
                                + xboxGamerTag, "http://bit.ly/2tbBuCT");
                        builder.setThumbnail((String) result.get("icon"));
                        // builder.setImage((String) result.get("ratingIcon"));
                        builder.addField("Platform", "Xbox One", false);
                        builder.addField("SR", (String) result.get("rating"), false);
                        builder.addField("Rank", (String) result.get("ratingName"), false);
                        JSONObject compStats = ((JSONObject) result.get("competitiveStats"));
                        JSONObject gamesPlayed = (JSONObject) compStats.get("games");
                        builder.addField("Games Played", gamesPlayed.get("won")
                                + "/" + gamesPlayed.get("played") + " Games Won", false);
//                        double ratio = (double) Integer.valueOf((String) gamesPlayed.get("won"))
//                                / Integer.valueOf((String)gamesPlayed.get("played")) * 100;
//                        builder.addField("Win Ratio", ratio + "%", false);
                        builder.setTitle(result.get("name") + "'s Competitive Overwatch Stats");
                        MessageEmbed embed = builder.build();
                        m.getTextChannel().sendMessage(embed).queue(message -> m.delete().queue());
                        return;
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
            case "ps4":
            case "psn":
            case "playstation":
            case "playstationnetwork":
            case "playstation4":
            case "play4":
            case "psf":
            case "playstationfour":
            case "psfour":
            case "playcuatro":
            case "playstationcuatro":
                if(args.length > 3) {
                    m.getTextChannel()
                            .sendMessage(
                                    new MessageBuilder().append(m.getAuthor())
                                            .append(" Usage: **" + getUsage() + "**")
                                            .build()).queue(message -> m.delete().queue());
                    return;
                }
                String psnId = args[2];
                switch(OverwatchAPI.getUserStatus(OverwatchAPI.Platform.PLAYSTATION, OverwatchAPI.Region.CONSOLE, psnId)) {
                    case NOT_FOUND:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 The profile requested could not be found!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    case OK:
                        JSONObject object = OverwatchAPI.getPlayerInfo(OverwatchAPI.Platform.PLAYSTATION,
                                OverwatchAPI.Region.AMERICAS, psnId);
                        if(!(Boolean)object.get("success")) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                            " Please report this to the developer: ")
                                                    .append(m.getJDA().getUserById(137277356791431168L))
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        JSONObject result = (JSONObject) object.get("result");
                        if(((String)result.get("rating")).isEmpty()) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" The requested user doesn't have a competitive profile!")
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.ORANGE);
                        builder.setAuthor((String) result.get("name"), "https://www.overbuff.com/players/psn/"
                                + psnId, "http://bit.ly/2tbBuCT");
                        builder.setThumbnail((String) result.get("icon"));
                        // builder.setImage((String) result.get("ratingIcon"));
                        builder.addField("Platform", "PlayStation 4", false);
                        builder.addField("SR", (String) result.get("rating"), false);
                        builder.addField("Rank", (String) result.get("ratingName"), false);
                        JSONObject compStats = ((JSONObject) result.get("competitiveStats"));
                        JSONObject gamesPlayed = (JSONObject) compStats.get("games");
                        builder.addField("Games Played", gamesPlayed.get("won")
                                + "/" + gamesPlayed.get("played") + " Games Won", false);
//                        double ratio = (double) Integer.valueOf((String) gamesPlayed.get("won"))
//                                / Integer.valueOf((String)gamesPlayed.get("played")) * 100;
//                        builder.addField("Win Ratio", ratio + "%", false);
                        builder.setTitle(result.get("name") + "'s Competitive Overwatch Stats");
                        MessageEmbed embed = builder.build();
                        m.getTextChannel().sendMessage(embed).queue(message -> m.delete().queue());
                        return;
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
            case "pc":
            case "battlenet":
            case "computer":
            case "pcmasterrace":
            case "windows":
            case "battle.net":
            case "blizzardclient":
            case "blizzardapp":
            case "bnet":
                if(args.length > 3) {
                    m.getTextChannel()
                            .sendMessage(
                                    new MessageBuilder().append(m.getAuthor())
                                            .append(" Usage: **" + getUsage() + "**")
                                            .build()).queue(message -> m.delete().queue());
                    return;
                }
                String battleTag = args[2];
                switch(OverwatchAPI.getUserStatus(OverwatchAPI.Platform.PC, region, battleTag)) {
                    case NOT_FOUND:
                        m.getTextChannel()
                                .sendMessage(
                                        new MessageBuilder().append(m.getAuthor())
                                                .append(" \u26A0 The profile requested could not be found!")
                                                .build()).queue(message -> m.delete().queue());
                        return;
                    case OK:
                        JSONObject object = OverwatchAPI.getPlayerInfo(OverwatchAPI.Platform.PC,
                                region, battleTag);
                        if(!(Boolean)object.get("success")) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" \u26A0 Hmmm. Something wrong seems to have happened." +
                                                            " Please report this to the developer: ")
                                                    .append(m.getJDA().getUserById(137277356791431168L))
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        JSONObject result = (JSONObject) object.get("result");
                        if(((String)result.get("rating")).isEmpty()) {
                            m.getTextChannel()
                                    .sendMessage(
                                            new MessageBuilder().append(m.getAuthor())
                                                    .append(" The requested user doesn't have a competitive profile!")
                                                    .build()).queue(message -> m.delete().queue());
                            return;
                        }
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.ORANGE);
                        builder.setAuthor((String) result.get("name"), "https://www.overbuff.com/players/pc/"
                                + battleTag.replaceAll("#", "-"), "http://bit.ly/2tbBuCT");
                        builder.setThumbnail((String) result.get("icon"));
                        // builder.setImage((String) result.get("ratingIcon"));
                        builder.addField("Platform", "PC-" + region.toString().toUpperCase(), false);
                        builder.addField("SR", (String) result.get("rating"), false);
                        builder.addField("Rank", (String) result.get("ratingName"), false);
                        JSONObject compStats = ((JSONObject) result.get("competitiveStats"));
                        JSONObject gamesPlayed = (JSONObject) compStats.get("games");
                        builder.addField("Games Played", gamesPlayed.get("won")
                                + "/" + gamesPlayed.get("played") + " Games Won", false);
//                        double ratio = (double) Integer.valueOf((String) gamesPlayed.get("won"))
//                                / Integer.valueOf((String)gamesPlayed.get("played")) * 100;
//                        builder.addField("Win Ratio", ratio + "%", false);
                        builder.setTitle(result.get("name") + "'s Competitive Overwatch Stats");
                        MessageEmbed embed = builder.build();
                        m.getTextChannel().sendMessage(embed).queue(message -> m.delete().queue());
                        return;
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
        return "sr";
    }

    @Override
    public String getDescription() {
        return "Reads your skill rating in all your registered profiles, or the one selected.";
    }

    @Override
    public String getUsage() {
        return "+sr [platform] [region] [username]";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"skillrating", "seasonrank", "skill"};
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

}