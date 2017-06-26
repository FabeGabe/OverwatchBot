package com.fabegabe;

import com.fabegabe.command.Command;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by User on 6/25/2017.
 */

public class OWListener extends ListenerAdapter {

    private JDA jda;
    private Bot bot;

    public OWListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(ReadyEvent e) {
        this.jda = e.getJDA();
        for(Guild guild : jda.getGuilds()) {
            OverwatchBot.LOGGER.info("Connected to Guild " + guild.getId()
                    + " (" + guild.getName() + ")");
        }
        jda.getSelfUser().getManager().setName("OverwatchBot").queue();
        OverwatchBot.LOGGER.info("Connection success.");
    }

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        Guild guild = e.getGuild();
        if (guild.getSelfMember().getJoinDate()
                .isBefore(OffsetDateTime.now().minusMinutes(10))) {
            return;
        }
        OverwatchBot.LOGGER.info("Connected to Guild " + guild.getId()
                + " (" + guild.getName() + ")");
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        Guild guild = e.getGuild();
        OverwatchBot.LOGGER.info("Disconnected from Guild " + guild.getId()
                + " (" + guild.getName() + ")");
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if(e.getAuthor() == e.getJDA().getSelfUser()) {
            return;
        }
        e.getChannel().sendMessage(new MessageBuilder().append("Please send your commands via a guild.")
                .build()).queue();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if(e.getAuthor() == e.getJDA().getSelfUser()) {
            return;
        }
        Message m = e.getMessage();
        if (!m.getStrippedContent().startsWith("+"))
            return;
        String msg = m.getRawContent().substring(1);
        String cmd = msg.split("\\s+")[0];
        String[] args = msg.split("\\s+");
        LinkedList<String> strings = new LinkedList<>(Arrays.asList(args));
        strings.removeFirst();
        String[] ar = strings.toArray(new String[strings.size()]);
        if (bot.getCommandRegistry().getCommand(cmd) == null) {
            m.delete().queue();
            return;
        }
        Command command = bot.getCommandRegistry().getCommand(cmd);
        command.exec(m, ar);
    }

}