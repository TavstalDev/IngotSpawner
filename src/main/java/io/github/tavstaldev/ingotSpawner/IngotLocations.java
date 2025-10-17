package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.minecorelib.config.ConfigurationBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class IngotLocations extends ConfigurationBase {
    public IngotLocations () {
        super(IngotSpawner.Instance, "locations.yml", null);
    }

    public List<Location> data = new ArrayList<>();

    @Override
    protected void loadDefaults() {
        if (get("locations") == null) {
            resolve("locations", new ArrayList<>());
        }

        var rawData = getStringList("locations");
        for (var entry : rawData) {
            data.add(deserializeLocation(entry));
        }
    }

    public void addLocation(Location location) {
        data.add(location);
        saveLocations();
    }

    public boolean removeLocation(Location location) {
        var result = data.removeIf(loc -> loc.getWorld().equals(location.getWorld())
                && loc.distanceSquared(location) < 4);
        saveLocations();
        return result;
    }

    private void saveLocations() {
        List<String> serializedData = new ArrayList<>();
        for (var loc : data) {
            serializedData.add(serializeLocation(loc));
        }
        set("locations", serializedData);
        save();
    }

    private String serializeLocation(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ();
    }

    private Location deserializeLocation(String s) {
        String[] parts = s.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}
