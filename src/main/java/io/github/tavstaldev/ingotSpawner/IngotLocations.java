package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.minecorelib.config.ConfigurationBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the storage and retrieval of ingot spawn locations.
 * <p>
 * This class extends the `ConfigurationBase` to handle the configuration file
 * `locations.yml`, which stores the list of spawn locations for ingots.
 * It provides methods to load, add, remove, and save locations.
 * </p>
 */
public class IngotLocations extends ConfigurationBase {

    /**
     * List of ingot spawn locations.
     */
    public List<Location> data = new ArrayList<>();

    /**
     * Constructor for the `IngotLocations` class.
     * Initializes the configuration file `locations.yml`.
     */
    public IngotLocations() {
        super(IngotSpawner.Instance, "locations.yml", null);
    }

    /**
     * Loads default values for the configuration if none exist.
     * <p>
     * If the `locations` key is not present in the configuration, it initializes
     * it with an empty list. Then, it deserializes the stored locations and adds
     * them to the `data` list.
     * </p>
     */
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

    /**
     * Adds a new location to the list of spawn locations and saves the updated list.
     *
     * @param location The `Location` object to be added.
     */
    public void addLocation(Location location) {
        data.add(location);
        saveLocations();
    }

    /**
     * Removes a location from the list of spawn locations if it matches the given location.
     * <p>
     * A location is considered a match if it is in the same world and within a
     * 2-block radius (distance squared less than 4).
     * </p>
     *
     * @param location The `Location` object to be removed.
     * @return `true` if a location was removed, `false` otherwise.
     */
    public boolean removeLocation(Location location) {
        var result = data.removeIf(loc -> loc.getWorld().equals(location.getWorld())
                && loc.distanceSquared(location) < 4);
        saveLocations();
        return result;
    }

    /**
     * Saves the current list of spawn locations to the configuration file.
     * <p>
     * The locations are serialized into strings and stored under the `locations` key.
     * </p>
     */
    private void saveLocations() {
        List<String> serializedData = new ArrayList<>();
        for (var loc : data) {
            serializedData.add(serializeLocation(loc));
        }
        set("locations", serializedData);
        save();
    }

    /**
     * Serializes a `Location` object into a string representation.
     * <p>
     * The format is: `worldName,x,y,z`.
     * </p>
     *
     * @param location The `Location` object to serialize.
     * @return The serialized string representation of the location.
     */
    private String serializeLocation(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ();
    }

    /**
     * Deserializes a string representation of a location into a `Location` object.
     * <p>
     * The string format should be: `worldName,x,y,z`.
     * </p>
     *
     * @param s The serialized string representation of the location.
     * @return The deserialized `Location` object.
     */
    private Location deserializeLocation(String s) {
        String[] parts = s.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}
