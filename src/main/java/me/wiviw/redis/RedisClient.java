package me.wiviw.redis;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static me.wiviw.Main.memberList;
import static me.wiviw.Main.pubJedis;

@Log4j2
public class RedisClient {

    public static Jedis subJedis;

    public static class RunnableImpl implements Runnable {

        public void run() {
            subJedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    log.info("Channel Requests Message Received: {}", message);
                    sendMemberList();
                }
            }, "Requests");
        }
    }

    public static void sendMemberList(){
        Gson gson = new Gson();
        pubJedis.publish("MemberList", gson.toJson(memberList));
    }


    public RedisClient(String uri) {
        subJedis = new Jedis(uri);

        Thread t1 = new Thread(new RunnableImpl());
        t1.start();
    }
}
