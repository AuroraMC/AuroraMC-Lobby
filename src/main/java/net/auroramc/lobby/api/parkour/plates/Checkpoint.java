package net.auroramc.lobby.api.parkour.plates;

import org.bukkit.Location;
import org.bukkit.Material;

public class Checkpoint extends PressurePlate {

    private int checkpointNo;

    @SuppressWarnings("unused")
    public Checkpoint(Location location, int checkpointNo) {
        super(location);
        this.checkpointNo = checkpointNo;
    }

    public int getType() {
        return 3;
    }

    @Override
    public Material getMaterial() {
        return Material.WOOD_PLATE;
    }

    public int getCheckpointNo() {
        return checkpointNo;
    }

    public void setCheckpointNo(int checkpointNo) {
        this.checkpointNo = checkpointNo;
    }
}
