package io.github.tavstaldev.ingotSpawner.commands;

import io.github.tavstaldev.ingotSpawner.IngotSpawner;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.models.command.SubCommandData;
import io.github.tavstaldev.minecorelib.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandIngot implements CommandExecutor {
    private final PluginLogger _logger = IngotSpawner.logger().withModule(CommandIngot.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final String baseCommand = "ingot";
    private final List<SubCommandData> _subCommands = new ArrayList<>() {
        {
            // HELP
            add(new SubCommandData("help", "ingotspawner.commands.help", Map.of(
                    "syntax", "",
                    "description", "Commands.Help.Desc"
            )));
            // VERSION
            add(new SubCommandData("version", "ingotspawner.commands.version", Map.of(
                    "syntax", "",
                    "description", "Commands.Version.Desc"
            )));
            // RELOAD
            add(new SubCommandData("reload", "ingotspawner.commands.reload", Map.of(
                    "syntax", "",
                    "description", "Commands.Reload.Desc"
            )));
            // ADD LOCATION
            add(new SubCommandData("add", "ingotspawner.commands.add", Map.of(
                    "syntax", "",
                    "description", "Commands.AddLocation.Desc"
            )));
            // REMOVE LOCATION
            add(new SubCommandData("remove", "ingotspawner.commands.remove", Map.of(
                    "syntax", "",
                    "description", "Commands.RemoveLocation.Desc"
            )));
        }
    };

    public CommandIngot() {
        var command = IngotSpawner.Instance.getCommand(baseCommand);
        if (command == null) {
            _logger.error("Could not get command /" + baseCommand + " from plugin.yml! Disabling command...");
            return;
        }
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof ConsoleCommandSender) {
            _logger.info(ChatUtils.translateColors("Commands.ConsoleCaller", true).toString());
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("ingotspawner.commands.ingot")) {
            IngotSpawner.Instance.sendLocalizedMsg(player, "General.NoPermission");
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "help":
                case "?": {
                    // Check if the player has permission to use the help command
                    if (!player.hasPermission("ingotspawner.commands.help")) {
                        IngotSpawner.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    // Parse the page number for the help command
                    int page = 1;
                    if (args.length > 1) {
                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (Exception ex) {
                            IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.Common.InvalidPage");
                            return true;
                        }
                    }

                    help(player, page);
                    return true;
                }
                case "version": {
                    // Check if the player has permission to use the version command
                    if (!player.hasPermission("ingotspawner.commands.version")) {
                        IngotSpawner.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    // Send the current plugin version to the player
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("version", IngotSpawner.Instance.getVersion());
                    IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.Version.Current", parameters);

                    // Check if the plugin is up-to-date
                    IngotSpawner.Instance.isUpToDate().thenAccept(upToDate -> {
                        if (upToDate) {
                            IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.Version.UpToDate");
                        } else {
                            IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.Version.Outdated", Map.of("link", IngotSpawner.Instance.getDownloadUrl()));
                        }
                    }).exceptionally(e -> {
                        _logger.error("Failed to determine update status: " + e.getMessage());
                        return null;
                    });
                    return true;
                }
                case "reload": {
                    // Check if the player has permission to use the reload command
                    if (!player.hasPermission("ingotspawner.commands.reload")) {
                        IngotSpawner.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    // Reload the plugin configuration
                    IngotSpawner.Instance.reload();
                    IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.Reload.Done");
                    return true;
                }
                case "add": {
                    // Check if the player has permission to use the add command
                    if (!player.hasPermission("ingotspawner.commands.add")) {
                        IngotSpawner.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    IngotSpawner.getLocations().addLocation(player.getLocation());
                    IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.AddLocation.Done");
                    return true;
                }
                case "remove": {
                    // Check if the player has permission to use the remove command
                    if (!player.hasPermission("ingotspawner.commands.remove")) {
                        IngotSpawner.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    var locations = IngotSpawner.getLocations();
                    boolean removed = locations.removeLocation(player.getLocation());
                    if (removed) {
                        locations.set("locations", locations.data);
                        locations.save();
                        IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.RemoveLocation.Done");
                    } else {
                        IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.RemoveLocation.NotFound");
                    }
                    return true;
                }
            }

            // Send an error message if the subcommand is invalid
            IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.InvalidArguments");
            return true;
        }

        // Default to the help command if no arguments are provided
        if (!player.hasPermission("ingotspawner.commands.help")) {
            IngotSpawner.Instance.sendLocalizedMsg(player, "General.NoPermission");
            return true;
        }
        help(player, 1);
        return true;
    }

    private void help(Player player, int page) {
        int maxPage = 1 + (_subCommands.size() / 15);

        // Ensure the page number is within valid bounds
        if (page > maxPage)
            page = maxPage;
        if (page < 1)
            page = 1;
        int finalPage = page;

        // Send the help menu title and info
        IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.Help.Title", Map.of(
                        "current_page", finalPage,
                        "max_page", maxPage
                )
        );
        IngotSpawner.Instance.sendLocalizedMsg(player, "Commands.Help.Info");

        boolean reachedEnd = false;
        int itemIndex = 0;

        // Display up to 15 subcommands per page
        for (int i = 0; i < 15; i++) {
            int index = itemIndex + (page - 1) * 15;
            if (index >= _subCommands.size()) {
                reachedEnd = true;
                break;
            }
            itemIndex++;

            SubCommandData subCommand = _subCommands.get(index);
            if (!subCommand.hasPermission(player)) {
                i--;
                continue;
            }

            subCommand.send(IngotSpawner.Instance, player, baseCommand);
        }

        // Display navigation buttons for the help menu
        String previousBtn = IngotSpawner.Instance.localize(player, "Commands.Help.PrevBtn");
        String nextBtn = IngotSpawner.Instance.localize(player, "Commands.Help.NextBtn");
        String bottomMsg = IngotSpawner.Instance.localize(player, "Commands.Help.Bottom")
                .replace("%current_page%", String.valueOf(page))
                .replace("%max_page%", String.valueOf(maxPage));

        Map<String, Component> bottomParams = new HashMap<>();
        if (page > 1)
            bottomParams.put("previous_btn", ChatUtils.translateColors(previousBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", baseCommand, page - 1))));
        else
            bottomParams.put("previous_btn", ChatUtils.translateColors(previousBtn, true));

        if (!reachedEnd && maxPage >= page + 1)
            bottomParams.put("next_btn", ChatUtils.translateColors(nextBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", baseCommand, page + 1))));
        else
            bottomParams.put("next_btn", ChatUtils.translateColors(nextBtn, true));

        Component bottomComp = ChatUtils.buildWithButtons(bottomMsg, bottomParams);
        player.sendMessage(bottomComp);
    }
}
