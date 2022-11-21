package com.theit8514.enabletransponder.dialog;

import java.util.Map;
import java.util.Set;

import com.fs.starfarer.api.*;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Highlights;
import com.theit8514.enabletransponder.EnableTransponderConstants;

import org.apache.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class EnableTransponderDialogPlugin implements InteractionDialogPlugin {
    public static final Logger log = Global.getLogger(EnableTransponderDialogPlugin.class);

    private enum Option {
        MAIN,
        ENABLE_TRANSPONDER,
        NEVER_FOR_SYSTEM,
        NEVER_FOR_FACTION,
        DO_NOTHING,
    }

    private InteractionDialogAPI dialog;
    private OptionPanelAPI options;
    private TextPanelAPI text;
    private boolean coreWorld;
    private LocationAPI location;
    private FactionAPI faction;
    private VisualPanelAPI visual;
    private Set<FactionAPI> factions;

    public EnableTransponderDialogPlugin(Set<FactionAPI> factions, LocationAPI location, boolean coreWorld) {
        this.factions = factions;
        this.location = location;
        this.coreWorld = coreWorld;
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        this.options = dialog.getOptionPanel();
        this.text = dialog.getTextPanel();
        this.visual = dialog.getVisualPanel();
        this.visual.setVisualFade(0.25f, 0.25f);

        Global.getSector().getMemoryWithoutUpdate().set(EnableTransponderConstants.DIALOG_PLUGIN_KEY, this, 0f);

        showBaseOptions();
    }

    public InteractionDialogAPI getDialog() {
        return dialog;
    }

    public void showBaseOptions() {
        optionSelected(null, Option.MAIN);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        options.clearOptions();
        Option opt = (Option) optionData;

        switch (opt) {
            case MAIN:
                displayMainDialog();
                break;
            case ENABLE_TRANSPONDER:
                Global.getSector().getPlayerFleet().getAbility("transponder").activate();
                dialog.dismiss();
                break;
            case NEVER_FOR_SYSTEM:
                if (location == null) {
                    log.error("Dialog reached NEVER_FOR_SYSTEM, but location is null");
                    dialog.dismiss();
                    return;
                }
                location.addTag(EnableTransponderConstants.SYSTEM_IGNORE_TAG);
                dialog.dismiss();
                break;
            case NEVER_FOR_FACTION:
                if (faction == null) {
                    log.error("Dialog reached NEVER_FOR_FACTION, but faction is null");
                    dialog.dismiss();
                    return;
                }
                setIgnoredFaction(faction.getId());
                dialog.dismiss();
                break;
            case DO_NOTHING:
                dialog.dismiss();
                break;
        }
    }

    private void displayMainDialog() {
        String locationName = location.getNameWithNoType();
        // Setup visual panel.
        dialog.showVisualPanel();
        try {
            SectorEntityToken locationToken = location.getEntitiesWithTag(Tags.STAR).get(0);
            visual.showMapMarker(locationToken, locationName, locationToken.getIndicatorColor(), false,
                    null, null, null);
        } catch (Exception e) {
            try {
                SectorEntityToken planetToken = location.getEntitiesWithTag(Tags.PLANET).get(0);
                visual.showMapMarker(planetToken, locationName, planetToken.getIndicatorColor(), false,
                        null, null, null);
            } catch (Exception e2) {
                // ignored
            }
        }

        // TODO: If faction is set, show faction relation on visual panel. Right now not
        // sure if this is possible with current API.

        // Setup text panel
        text.clear();
        text.addPara(String.format(
                "Upon entering %s%s, your communicaitons officer detects faint radio signals on standard broadcast channels.",
                locationName,
                coreWorld ? ", a core world" : ""));
        text.highlightInLastPara(locationName);
        if (!factions.isEmpty()) {
            text.addPara(String.format(
                    "They run the signals through an analyzer which determines they are from %s.",
                    getFactionNames()));
            Highlights highlights = new Highlights();
            for (FactionAPI faction : factions) {
                highlights.append(faction.getDisplayNameWithArticleWithoutArticle(), faction.getBaseUIColor());
                // text.highlightInLastPara(faction.getBaseUIColor(),
                // faction.getDisplayNameWithArticleWithoutArticle());
            }
            text.setHighlightsInLastPara(highlights);
        }

        text.addPara(
                "Your conn officer notes that your transponder is off, and patrols in the system are likely to give you trouble over the fact, if you're spotted.");

        options.addOption("Turn the transponder on", Option.ENABLE_TRANSPONDER);
        options.setTooltip(Option.ENABLE_TRANSPONDER,
                "Using \"Enable Transponder\" may cause unwanted attention or cause you to be identified.");
        options.setTooltipHighlightColors(Option.ENABLE_TRANSPONDER, EnableTransponderConstants.HIGHLIGHT_COLOR);
        options.setTooltipHighlights(Option.ENABLE_TRANSPONDER, "Enable Transponder");
        options.addOption("Keep the transponder off", Option.DO_NOTHING);
        options.setShortcut(Option.DO_NOTHING, Keyboard.KEY_ESCAPE, false, false, false, false);
        options.setTooltip(Option.DO_NOTHING,
                "Will not enable the transponder, but may cause patrol fleets to intercept you.");

        if (location != null) {
            options.addOption("Never for this system", Option.NEVER_FOR_SYSTEM);
            options.setTooltip(Option.NEVER_FOR_SYSTEM,
                    "This dialog won't appear when you enter this system anymore, however it may still show if other conditions are met.");
        }
        if (faction != null) {
            options.addOption("Never for this faction", Option.NEVER_FOR_FACTION);
            options.setTooltip(Option.NEVER_FOR_FACTION,
                    "This dialog won't appear when this faction has a presence in the current system, however it may still show if other conditions are met.");
        }
    }

    private String getFactionNames() {
        int numFactions = factions.size();
        if (numFactions == 1) {
            return factions.iterator().next().getDisplayNameWithArticle();
        }

        StringBuilder sb = new StringBuilder();
        FactionAPI[] array = factions.toArray(new FactionAPI[numFactions]);
        for (int i = 0; i < array.length; i++) {
            FactionAPI faction = array[i];
            sb.append(faction.getDisplayNameWithArticle() + ", ");
            if (i == array.length - 2) {
                sb.setLength(sb.length() - 2);
                sb.append(" and ");
            }
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    private void setIgnoredFaction(String id) {
        Global.getSector().getMemory().set(EnableTransponderConstants.FACTION_IGNORE_KEY_PREFIX + id, true);
    }
}
