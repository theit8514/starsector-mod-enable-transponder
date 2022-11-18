package com.theit8514.enabletransponder.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.listeners.CurrentLocationChangedListener;
import com.theit8514.enabletransponder.EnableTransponderConstants;
import com.theit8514.enabletransponder.dialog.EnableTransponderDialogPlugin;
import com.theit8514.enabletransponder.utils.TransponderNotificationManager;

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

        // Tags that we want to monitor:
        // theme_core_populated (core worlds that are populated at game start)
        // Collection<String> tags = curr.getTags();
        // for (String tag : tags) {
        // log.info("Tag: " + tag);
        // }

        if (curr.hasTag(EnableTransponderConstants.SYSTEM_IGNORE_TAG)) {
            return;
        }

        if (curr.hasTag(EnableTransponderConstants.SYSTEM_COREWORLD_TAG)) {
            if (TransponderNotificationManager.alreadyNotified(locationId)) {
                log.info(String.format(
                        "Changed location to %s (a core world), but we've already notified the user for this location.",
                        curr.getName()));
                return;
            }

            Global.getSector().getCampaignUI().showInteractionDialog(
                    new EnableTransponderDialogPlugin(curr, true),
                    Global.getSector().getPlayerFleet());
        }
    }
}
