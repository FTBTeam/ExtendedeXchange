package dev.ftb.extendedexchange.config;

public class ConfigHelper {
    public static ClientConfig client() {
        return ConfigHolder.client;
    }

    public static ServerConfig server() {
        return ConfigHolder.server;
    }

    public static void setSearchType(SearchType searchType) {
        client().general.searchType.set(searchType);
    }
}
