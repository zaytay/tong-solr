package com.zt.tongsolr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Random;

public class StatsUtils {
    private static final Random RNG = new Random();
    private static final Logger log = LoggerFactory.getLogger(StatsUtils.class);

    private static InetSocketAddress _address;
    private static DatagramChannel _channel;

    static {
        try {
            String host = DynamicConfig.getString("stats.send.host");
            int port = Integer.parseInt(DynamicConfig.getString("stats.send.port"));
            _address = new InetSocketAddress(InetAddress.getByName(host), port);
            _channel = DatagramChannel.open();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            log.error("InetSocketAddress init or DatagramChannel's open has error" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("InetSocketAddress init or DatagramChannel's open has error" + e.getMessage());
        }
    }

    public static boolean timing(String key, int value) {
        return timing(key, value, 1.0);
    }

    public static boolean timing(String key, int value, double sampleRate) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(":");
        sb.append(value);
        sb.append("|ms"); // key:value|ms
        return send(sampleRate, sb.toString());
    }

    private static boolean doSend(final String stat) {
        try {
            final byte[] data = stat.getBytes("utf-8");
            final ByteBuffer buff = ByteBuffer.wrap(data);
            final int nbSentBytes = _channel.send(buff, _address);

            if (data.length == nbSentBytes) {
                return true;
            } else {
                log.error("Couldn't send stat, Only sent {} bytes out of {} bytes", nbSentBytes, data.length);
                return false;
            }

        } catch (IOException e) {
            log.error("Could not send stats {}", StackTraceUtil.getStackTrace(e));
            return false;
        }
    }

    private static boolean send(double sampleRate, String... stats) {

        boolean retval = false; // didn't send anything
        if (sampleRate < 1.0) {
            for (String stat : stats) {
                if (RNG.nextDouble() <= sampleRate) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(stat);
                    sb.append("|@");
                    sb.append((float) sampleRate); // stat|@samplerate
                    if (doSend(sb.toString())) {
                        retval = true;
                    }
                }
            }
        } else {
            for (String stat : stats) {
                if (doSend(stat)) {
                    retval = true;
                }
            }
        }

        return retval;
    }

    private StatsUtils() {
    }

    public boolean decrement(int magnitude, double sampleRate, String... keys) {
        magnitude = magnitude < 0 ? magnitude : -magnitude;
        return increment(magnitude, sampleRate, keys);
    }

    public boolean decrement(int magnitude, String... keys) {
        magnitude = magnitude < 0 ? magnitude : -magnitude;
        return increment(magnitude, 1.0, keys);
    }

    public boolean decrement(String key) {
        return increment(key, -1, 1.0);
    }

    public boolean decrement(String... keys) {
        return increment(-1, 1.0, keys);
    }

    public boolean decrement(String key, int magnitude) {
        return decrement(key, magnitude, 1.0);
    }

    public boolean decrement(String key, int magnitude, double sampleRate) {
        magnitude = magnitude < 0 ? magnitude : -magnitude;
        return increment(key, magnitude, sampleRate);
    }

    public boolean gauge(String key, double magnitude) {
        return gauge(key, magnitude, 1.0);
    }

    public boolean gauge(String key, double magnitude, double sampleRate) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(":");
        sb.append(magnitude);
        sb.append("|g"); // key:value|g
        return send(sampleRate, sb.toString());
    }

    public boolean increment(int magnitude, double sampleRate, String... keys) {
        String[] stats = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(keys[i]);
            sb.append(":");
            sb.append(magnitude);
            sb.append("|c"); // key:value|c
            stats[i] = sb.toString();
        }
        return send(sampleRate, stats);
    }

    public boolean increment(String key) {
        return increment(key, 1, 1.0);
    }

    public boolean increment(String key, int magnitude) {
        return increment(key, magnitude, 1.0);
    }

    public boolean increment(String key, int magnitude, double sampleRate) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(":");
        sb.append(magnitude);
        sb.append("|c"); // key:value|c
        return send(sampleRate, sb.toString());
    }

    public boolean sets(String key, int magnitude) {
        return sets(key, magnitude, 1.0);
    }

    public boolean sets(String key, int magnitude, double sampleRate) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(":");
        sb.append(magnitude);
        sb.append("|s"); // key:value|s
        return send(sampleRate, sb.toString());
    }
}
