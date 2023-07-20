/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.api.parkour.plates;

import net.auroramc.lobby.api.parkour.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public abstract class PressurePlate {

    protected final Location location;
    protected Parkour parkour;
    protected Material material;

    @SuppressWarnings("unused")
    public PressurePlate(Location location) {
        this.location =  location;
        this.material = getMaterial();
    }

    /**
     * Set the parkour the pressure plate belongs to.
     *
     * @param parkour the parkour it belongs to.
     */
    public void setParkour(Parkour parkour) {
        this.parkour = parkour;
    }

    /**
     * The location the pressure plate it located.
     * @return the location of the pressure plate.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * The type of pressure plate. This is just an arbitrary value.
     *
     * Default ID's:
     * 0 - Start Point
     * 1 - End Point
     * 2 - Restart Point
     * 3 - Checkpoint
     * @return the pressure plate type
     */
    public abstract int getType();

    /**
     * Get the type of material the pressure plate has.
     * @return Material type.
     */
    @SuppressWarnings("unused")
    public abstract Material getMaterial();

    /**
     * Places the pressure plate material on the block the pressure plate is located at.
     */
    public void placeMaterial() {
        location.getBlock().setType(material);
    }

    /**
     * Remove the material by setting it to air.
     */
    public void removeMaterial() {
        location.getBlock().setType(Material.AIR);
    }

    /**
     * Get the parkour the pressure plate belongs to.
     * @return the parkour it belongs to.
     */
    public Parkour getParkour() {
        return parkour;
    }

}
