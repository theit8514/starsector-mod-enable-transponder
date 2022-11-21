package com.theit8514.enabletransponder.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.CurrentLocationChangedListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.theit8514.enabletransponder.EnableTransponderConstants;
import com.theit8514.enabletransponder.dialog.EnableTransponderDialogPlugin;
import com.theit8514.enabletransponder.utils.TransponderNotificationManager;

import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

public class LocationListener implements CurrentLocationChangedListener {
    public static final Logger log = Global.getLogger(LocationListener.class);

    @Override
    public void reportCurrentLocationChanged(LocationAPI prev, LocationAPI curr) {
        // When the player changes locations, clear the notification list.
        TransponderNotificationManager.clear();
        if (prev == null || curr == null || prev.isHyperspace() || curr.isHyperspace()) {
            // QoL or Transpoffder will do this part
            return;
        }

        String locationId = curr.getId();
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null) {
            return;
        }

        boolean transponderOn = playerFleet.isTransponderOn();
        if (transponderOn) {
            // It's already on, no problems.
            return;
        }

        if (curr.hasTag(EnableTransponderConstants.SYSTEM_IGNORE_TAG)) {
            return;
        }

        boolean coreWorld = curr.hasTag(EnableTransponderConstants.SYSTEM_COREWORLD_TAG);
        FactionAPI playerFaction = playerFleet.getFaction();
        HashSet<FactionAPI> factionsWhoCare = new HashSet<FactionAPI>();
        // Find fleets in the current system (this includes a "station" fleet).
        scanFleets(curr.getFleets(), playerFaction, factionsWhoCare);
        // Find planets with markets
        scanPlanets(curr.getPlanets(), playerFaction, factionsWhoCare);

        if (!factionsWhoCare.isEmpty()) {
            // Only notify once per location.
            if (TransponderNotificationManager.alreadyNotified(locationId)) {
                log.info(String.format(
                        "Changed location to %s (a system owned by %d factions), but we've already notified the user for this location.",
                        curr.getName(),
                        factionsWhoCare.size()));
                return;
            }

            Global.getSector().getCampaignUI().showInteractionDialog(
                    new EnableTransponderDialogPlugin(factionsWhoCare, curr, coreWorld),
                    Global.getSector().getPlayerFleet());
            return;
        }
    }

    private void scanFleets(List<CampaignFleetAPI> fleets, FactionAPI playerFaction,
            HashSet<FactionAPI> factionsWhoCare) {
        for (CampaignFleetAPI fleet : fleets) {
            // Only report on stations.
            if (fleet.isPlayerFleet() || !fleet.isStationMode()) {
                continue;
            }

            FactionAPI faction = fleet.getFaction();
            log.info(String.format("Detected station %s owned by faction %s", fleet.getName(), faction.getDisplayName()));
            if (factionsWhoCare.contains(faction)) {
                continue;
            }

            // Don't notify if the station belongs to a player or an ignored faction.
            if (isIgnoredFaction(faction)) {
                continue;
            }

            boolean hostileTo = faction.isHostileTo(playerFaction);
            // Only notify if the fleet is friendly or neutral, and if they care about the
            // transponder status.
            if (!hostileTo || !faction.getCustomBoolean("allowsTransponderOffTrade")) {
                factionsWhoCare.add(faction);
            }
        }
    }

    private void scanPlanets(List<PlanetAPI> planets, FactionAPI playerFaction, HashSet<FactionAPI> factionsWhoCare) {
        for (PlanetAPI planet : planets) {
            MarketAPI market = planet.getMarket();
            if (market == null) {
                continue;
            }

            FactionAPI faction = market.getFaction();
            log.info(String.format("Detected market %s owned by faction %s", market.getName(), faction.getDisplayName()));
            if (factionsWhoCare.contains(faction)) {
                continue;
            }

            // Don't notify if the station belongs to a player or an ignored faction.
            if (isIgnoredFaction(faction)) {
                continue;
            }

            boolean hostileTo = faction.isHostileTo(playerFaction);
            // Only notify if the fleet is friendly or neutral, and if they care about the
            // transponder status.
            if (!hostileTo || !faction.getCustomBoolean("allowsTransponderOffTrade")) {
                factionsWhoCare.add(faction);
            }
        }
    }

    private boolean isIgnoredFaction(FactionAPI faction) {
        if (faction.isPlayerFaction() || faction.isNeutralFaction()) {
            return true;
        }

        String id = faction.getId();
        MemoryAPI memory = Global.getSector().getMemory();
        return memory.getBoolean(EnableTransponderConstants.FACTION_IGNORE_KEY_PREFIX + id) ||
                memory.getBoolean(EnableTransponderConstants.FACTION_NEVER_NOTIFY_KEY_PREFIX + id);
    }
}
