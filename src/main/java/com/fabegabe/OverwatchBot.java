package com.fabegabe;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.logging.Logger;

/**
 * Created by User on 6/25/2017.
 */

public class OverwatchBot {

    private File usersFile;
    private JSONObject properties, users;
    public static final Logger LOGGER = Logger.getLogger(OverwatchBot.class.getName());

    public static void main(String[] args) {
        new OverwatchBot().init();
    }

    public void init() {
        File file = new File("./bot.json");
        this.usersFile = new File("./users.json");
        try {
            if(!file.exists() && !usersFile.exists()) {
                file.createNewFile();
                FileUtils.copyInputStreamToFile(getFile("bot.json"), file);
                this.usersFile.createNewFile();
                FileUtils.copyInputStreamToFile(getFile("users.json"), this.usersFile);
                throw new FileNotFoundException("Files bot.json & users.json not found!");
            } else if (!file.exists()) {
                file.createNewFile();
                FileUtils.copyInputStreamToFile(getFile("bot.json"), file);
                throw new FileNotFoundException("File bot.json not found!");
            } else if (!usersFile.exists()) {
                this.usersFile.createNewFile();
                FileUtils.copyInputStreamToFile(getFile("users.json"), this.usersFile);
                throw new FileNotFoundException("File users.json not found!");
            }
            {
                BufferedReader reader = new BufferedReader(new FileReader(this.usersFile));
                String line = reader.readLine();
                if(line == null || line.isEmpty()) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(this.usersFile));
                    writer.write("{}");
                    writer.flush();
                    writer.close();
                }
                reader.close();
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            JSONParser parser = new JSONParser();
            this.properties = (JSONObject) parser.parse(new BufferedReader(new FileReader(file)));
            this.users = (JSONObject) parser.parse(new BufferedReader(new FileReader(usersFile)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        if(((String)properties.get("token")).isEmpty()) {
            LOGGER.warning("Token is not set!");
            return;
        }
        try {
            new Bot(this);
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }

    protected JSONObject getProperties() {
        return properties;
    }

    public File getUsersFile() {
        return usersFile;
    }

    public JSONObject getUsers() {
        return users;
    }

    public void saveUserData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getUsersFile()));
            writer.write(getUsers().toJSONString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InputStream getFile(String file) {
        return OverwatchBot.class.getClassLoader().getResourceAsStream(file);
    }

}