package com.theit8514.enabletransponder.dialog;

import java.util.Map;

import com.fs.starfarer.api.*;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
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

    public EnableTransponderDialogPlugin(FactionAPI faction) {
        this.faction = faction;
    }

    public EnableTransponderDialogPlugin(LocationAPI location, boolean coreWorld) {
        this.location = location;
        this.coreWorld = coreWorld;
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        this.options = dialog.getOptionPanel();
        this.text = dialog.getTextPanel();

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
        text.clear();
        if (location != null) {
            String locationName = location.getName();
            if (coreWorld) {
                text.addPara(String.format(
                        "You are entering %s (a core world of the sector), but don't have your transponder on.",
                        locationName));
            } else {
                text.addPara(String.format("You are entering %s, but don't have your transponder on.", locationName));
            }

            text.highlightInLastPara(locationName);
        } else if (faction != null) {
            text.addPara(String.format("You are entering the territory of %s, but don't have your transponder on.",
                    faction.getDisplayNameWithArticle()));
            text.highlightInLastPara(faction.getColor(), faction.getDisplayNameWithArticleWithoutArticle());
        }

        text.addPara("Would you like to enable your transponder?");

        options.addOption("Enable Transponder", Option.ENABLE_TRANSPONDER);
        options.setTooltip(Option.ENABLE_TRANSPONDER,
                "Using \"Enable Transponder\" may cause unwanted attention or cause you to be identified.");
        options.setTooltipHighlightColors(Option.ENABLE_TRANSPONDER, EnableTransponderConstants.HIGHLIGHT_COLOR);
        options.setTooltipHighlights(Option.ENABLE_TRANSPONDER, "Enable Transponder");
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

        options.addOption("Do Nothing", Option.DO_NOTHING);
        options.setShortcut(Option.DO_NOTHING, Keyboard.KEY_ESCAPE, false, false, false, false);
        options.setTooltip(Option.DO_NOTHING,
                "Will not enable the transponder, may cause patrol fleets to intercept you.");
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
        Global.getSector().getMemory().set(EnableTransponderConstants.FACTION_IGNORE_PREFIX + id, true);
    }
}
