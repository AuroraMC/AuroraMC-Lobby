/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.api;

import org.json.JSONObject;

import java.io.File;

public class LobbyMap {

    private final int id;
    private final String name;
    private final String author;
    private final String game;
    private final JSONObject mapData;

    public LobbyMap(int id, String name, String author, String game, JSONObject mapData) {
        this.id = id;
        this.name = name;
        this.game = game;
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

    public String getGame() {
        return game;
    }
}
