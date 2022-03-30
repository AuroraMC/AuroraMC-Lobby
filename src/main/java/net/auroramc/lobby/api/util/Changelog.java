/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

public class Changelog {

    private final int id;
    private final String game;
    private final String version;
    private final long timestamp;
    private final String updateTitle;
    private final String url;

    public Changelog(int id, String game, String version, long timestamp, String updateTitle, String url) {
        this.id = id;
        this.game = game;
        this.version = version;
        this.timestamp = timestamp;
        this.updateTitle = updateTitle;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getGame() {
        return game;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public String getUrl() {
        return url;
    }
}
