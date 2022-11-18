package com.theit8514.enabletransponder.commands;

import org.apache.log4j.Logger;
import org.lazywizard.console.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.theit8514.enabletransponder.EnableTransponderConstants;

public class EnableTransponderCommand implements BaseCommand {
    public static final Logger log = Global.getLogger(EnableTransponderCommand.class);

    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        String[] tmp = args.split(" ", 2);
        String command = tmp[0].toLowerCase();
        args = tmp.length < 2 ? null : tmp[1];

        switch (command) {
            case "clear":
                return clearCommand(args, context);
            case "help":
                return showHelp(args, context);
            default:
                return CommandResult.BAD_SYNTAX;
        }
    }

    private CommandResult showHelp(String args, CommandContext context) {
        Console.showMessage("EnableTranspONder commands:");
        Console.showMessage("  clear: clear ignore flags for faction or the current system.");
        Console.showMessage("    EnableTranspONder clear faction <faction_id>");
        Console.showMessage("    EnableTranspONder clear system");
        Console.showMessage("");
        Console.showMessage("Use 'list factions' for a complete list of valid faction IDs.\n");
        return CommandResult.SUCCESS;
    }

    private CommandResult clearCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        String[] tmp = args.split(" ", 2);
        String command = tmp[0].toLowerCase();
        args = tmp.length < 2 ? null : tmp[1];
        switch (command) {
            case "faction":
                if (args == null || args.isEmpty()) {
                    Console.showMessage(
                            "Must enter a faction! Use 'list factions' for a complete list of valid IDs.\n");
                    return CommandResult.ERROR;
                }
                final FactionAPI faction = CommandUtils.findBestFactionMatch(args);
                if (faction == null) {
                    Console.showMessage(
                            "No such faction '" + args
                                    + "'! Use 'list factions' for a complete list of valid IDs.\n");
                    return CommandResult.ERROR;
                }
                String id = faction.getId();
                MemoryAPI memory = Global.getSector().getMemory();
                String key = EnableTransponderConstants.FACTION_IGNORE_KEY_PREFIX + id;
                log.info(String.format("clear faction: %s %s", id, key));
                if (memory.getBoolean(key)) {
                    memory.unset(key);
                    Console.showMessage("Cleared flag for faction " + id);
                } else {
                    Console.showMessage(
                            "Faction " + id + " is already removed from the do-not-notify list.");
                }
                break;
            case "system":
                LocationAPI location = Global.getSector().getPlayerFleet().getContainingLocation();
                if (!location.hasTag(EnableTransponderConstants.SYSTEM_IGNORE_TAG)) {
                    Console.showMessage(
                            "System " + location.getName() + " is already removed from the do-not-notify list.");
                } else {
                    location.removeTag(EnableTransponderConstants.SYSTEM_IGNORE_TAG);
                    Console.showMessage("Cleared flag for system " + location.getName());
                }
                break;
            default:
                return CommandResult.BAD_SYNTAX;
        }

        return CommandResult.SUCCESS;
    }
}
