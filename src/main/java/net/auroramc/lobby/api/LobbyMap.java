/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.api;

import org.json.JSONObject;

import java.io.File;

public class LobbyMap {

    private final int id;
    private final String name;
    private final String author;
    private final JSONObject mapData;

    public LobbyMap(int id, String name, String author, JSONObject mapData) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.mapData = mapData;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public JSONObject getMapData() {
        return mapData;
    }

}
