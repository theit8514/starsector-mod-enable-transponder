package com.theit8514.enabletransponder.listeners;

import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.theit8514.enabletransponder.EnableTransponderConstants;
import com.theit8514.enabletransponder.dialog.EnableTransponderDialogPlugin;
import com.theit8514.enabletransponder.utils.TransponderHelper;
import com.theit8514.enabletransponder.utils.TransponderNotificationManager;

public class CampaignListener extends BaseCampaignEventListener {
	public static final Logger log = Global.getLogger(CampaignListener.class);

	public CampaignListener() {
		super(false);
	}

	@Override
	public void reportFleetSpawned(CampaignFleetAPI fleet) {
		if (fleet == null) {
			return;
		}

		String locationId = fleet.getContainingLocation().getId();
		// Shortcut exit if we've already notified for this system.
		if (TransponderNotificationManager.hasNotified(locationId)) {
			return;
		}

		CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
		if (playerFleet == null) {
			return;
		}

		if (playerFleet.getContainingLocation().isHyperspace()) {
			// Probably don't need to notify in hyperspace, which is lawless.
			return;
		}

		boolean transponderOn = playerFleet.isTransponderOn();
		if (transponderOn) {
			// Our transponder is already on.
			return;
		}

		FactionAPI faction = fleet.getFaction();
		if (faction.isPlayerFaction() || isIgnoredFaction(faction.getId())) {
			// We don't need to turn our transponder on in our own system, unless it's also
			// owned by someone else.
			return;
		}

		String name = fleet.getName();
		log.info(name);
		Set<Matcher> fleetPatrolMatchers = TransponderHelper.getFleetPatrolMatchers();
		for (Matcher matcher : fleetPatrolMatchers) {
			if (matcher.reset(name).matches()) {
				if (TransponderNotificationManager.alreadyNotified(locationId)) {
					log.info(String.format("Detected a %s fleet, but we've already notified the user.", name));
					return;
				}

				// Check to see if the target faction is hostile to us, in which case we
				// probably don't care about their transponder requests.
				FactionAPI playerFaction = playerFleet.getFaction();
				boolean hostileTo = faction.isHostileTo(playerFaction);
				// Also, if the faction doesn't care about transponder status, then don't bother
				// with pestering the user.
				if (!hostileTo || !faction.getCustomBoolean("allowsTransponderOffTrade")) {
					// We've entered neutral or friendly faction territory.
					String displayName = faction.getDisplayNameWithArticle();
					log.info(String.format(
							"Showing enable transponder dialog due to detecting a %s fleet from %s faction.",
							fleet.getName(), displayName));
					Global.getSector().getCampaignUI().showInteractionDialog(
							new EnableTransponderDialogPlugin(faction),
							Global.getSector().getPlayerFleet());
				}
			}
		}
	}

	private boolean isIgnoredFaction(String id) {
		MemoryAPI memory = Global.getSector().getMemory();
		return memory.getBoolean(EnableTransponderConstants.FACTION_IGNORE_KEY_PREFIX + id) ||
				memory.getBoolean(EnableTransponderConstants.FACTION_NEVER_NOTIFY_KEY_PREFIX + id);
	}
}
