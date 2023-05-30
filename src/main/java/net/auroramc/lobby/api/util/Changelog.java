/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
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
