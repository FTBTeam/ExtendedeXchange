package dev.ftb.extendedexchange.config;

public class ConfigHelper {
    public static ClientConfig client() {
        return ConfigHolder.client;
    }

    public static ServerConfig server() {
        return ConfigHolder.server;
    }
}
