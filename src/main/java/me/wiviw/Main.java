package me.wiviw;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;

import me.wiviw.redis.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import static me.wiviw.redis.RedisClient.sendMemberList;

@Log4j2
public class Main {

    public static Jedis pubJedis;
    public static RedisClient redisClient;
    public static JsonObject memberList;

    public static void main(String[] args) throws FileNotFoundException {



        if (System.getProperty("db.redis.uri") == null) {
            log.error("db.redis.uri is null or not set, please set this so the whitelist will work.");
            return;
        }
        try {
            pubJedis = new Jedis(System.getProperty("db.redis.uri"));
            redisClient = new RedisClient(System.getProperty("db.redis.uri"));
        } catch (JedisConnectionException exception) {
            log.error("Failed to Connect: {}", exception.toString());
            return;
        } catch (Exception exception) {
            log.error("db.redis.uri is either not set or is invalid, please set this so the whitelist will work. {}", exception.toString());
            return;
        }
        try {
            memberList = (JsonObject) JsonParser.parseReader(new FileReader("users.json"));
        } catch (FileNotFoundException exception) {
            log.error("users.json Not found.");
            System.exit(-1);
        }

        log.info("Starting JSON: {}", memberList);
        Scanner myObj = new Scanner(System.in);
        while (true) {
            log.info("Press enter to update from file the list of users and also send to server.");
            myObj.nextLine();
            memberList = (JsonObject) JsonParser.parseReader(new FileReader("users.json"));
            log.info("Member list is now: {}", memberList);
            sendMemberList();
            log.info("Success!");
        }

    }
}