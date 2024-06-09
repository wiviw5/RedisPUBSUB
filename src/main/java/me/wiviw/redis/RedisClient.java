package me.wiviw.nerdwhitelist.util.redis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;

@Log4j2
public class RedisClient {

    protected static Jedis jedis;

    @Getter
    @Setter
    private static Map<String, JsonElement> memberList;

    private static class RunnableImpl implements Runnable {

        public void run() {
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    memberList = JsonParser.parseString(message).getAsJsonObject().asMap();

                    log.debug("Updated Member list!\n{}", memberList.toString());
                }
            }, "channel");
        }
    }

    public RedisClient(String uri) {
        jedis = new Jedis(uri);

        Thread t1 = new Thread(new RunnableImpl());
        t1.start();
    }
}
