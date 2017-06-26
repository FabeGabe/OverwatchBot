package com.fabegabe;

import com.fabegabe.command.CommandRegistry;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

/**
 * Created by User on 6/25/2017.
 */
public class Bot {

    private JDA jda;
    private CommandRegistry commandRegistry;
    private OverwatchBot owBot;

    public Bot(OverwatchBot bot) throws LoginException, RateLimitedException {
        this.commandRegistry = new CommandRegistry(this);
        this.owBot = bot;
        this.jda = new JDABuilder(AccountType.BOT).setToken((String) bot.getProperties()
                .get("token")).setStatus(OnlineStatus.ONLINE).setAutoReconnect(true)
                .setGame(Game.of("+help | Overwatch Bot rev.15.4"))
                .addEventListener(new OWListener(this)).buildAsync();
    }

    public JDA getJDA() {
        return jda;
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public OverwatchBot getMain() {
        return owBot;
    }

}