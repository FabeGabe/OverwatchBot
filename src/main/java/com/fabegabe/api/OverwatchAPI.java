package com.fabegabe.api;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by User on 6/26/2017.
 */
public class OverwatchAPI {

    public enum Platform {

        PC("pc"), PLAYSTATION("psn"), XBOX("xbl");

        private String name;
        Platform(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Platform getByName(String name) {
            for(Platform platform : values()) {
                if(platform.toString() == name.toLowerCase())
                    return platform;
            }
            return null;
        }

    }

    public enum Region {

        AMERICAS("us"), EUROPE("eu"), ASIA("asia"), CONSOLE("us");


        private String name;
        Region(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Region getByName(String name) {
            for(Region region : values()) {
                if(region.name == name.toLowerCase())
                    return region;
            }
            return CONSOLE;
        }

        public static boolean isValidRegion(String name) {
            for(Region region : values()) {
                if(region.name == name.toLowerCase())
                    return true;
            }
            return false;
        }

    }

    public enum Status {

        OK(200), BAD_REQUEST(400), NOT_FOUND(404), NOT_ACCEPTABLE(406), INTERNAL_SERVER_ERROR(500), SERVICE_UNAVAILABLE(503),
        UNKNOWN;

        private int code;
        Status() {
            this.code = 0;
        }
        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Status getStatus(int code) {
            for(Status status : values()) {
                if(status.getCode() == code)
                    return status;
            }
            return Status.UNKNOWN;
        }

    }

    public static JSONObject getPlayerInfo(Platform platform, Region region, String username) {
        JSONObject object = new JSONObject();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet("https://ow-api.com/v1/stats/" + platform + "/" + region + "/"
                + username.replaceAll("#", "-") + "/profile");
        get.addHeader("User-Agent", "Mozilla/5.0");
        try {
            CloseableHttpResponse response = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(reader);
            object.put("success", true);
            object.put("result", result);
        } catch (IOException e) {
            if(object.isEmpty()) {
                object.put("success", false);
            }
            e.printStackTrace();
        } catch (ParseException e) {
            if(object.isEmpty()) {
                object.put("success", false);
            }
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static Status getUserStatus(Platform platform, Region region, String username) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet("https://ow-api.com/v1/stats/" + platform + "/" + region + "/"
                + username.replaceAll("#", "-") + "/profile");
        get.addHeader("User-Agent", "Mozilla/5.0");
        Status status = Status.UNKNOWN;
        try {
            CloseableHttpResponse response = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(reader);
            status = !result.containsKey("error") ? Status.OK :
                    (((String)result.get("error")).toLowerCase().endsWith("not found")
                            ? Status.NOT_FOUND : Status.UNKNOWN);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

}