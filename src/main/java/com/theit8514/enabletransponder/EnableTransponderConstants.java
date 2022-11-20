package com.theit8514.enabletransponder;

import java.awt.Color;

import com.fs.starfarer.api.Global;

public class EnableTransponderConstants {
    /** The mod id for loading configuration */
    public static final String MOD_ID = "EnableTranspONder";

    // #region Settings
    /** Color for highlights */
    public static final Color HIGHLIGHT_COLOR = Global.getSettings().getColor("buttonShortcut");
    /** Disable flag to completely remove mod */
    public static final boolean DISABLED = Global.getSettings().getBoolean("EnableTranspONder_disabled");
    // #endregion

    // #region Memory Keys
    /**
     * The memory key prefix used by mods to mark their fleets as 'patrol' fleets
     * The suffix does not matter, and the value should contain a fleet name
     * (case-insensitive)
     * or a Regex pattern.
     */
    public static final String FLEET_PATROL_LIST_KEY_PREFIX = "$EnableTranspONder_fleet_patrol_";
    /**
     * The memory key prefix used by mods to mark their faction as never notified
     * This prefix should be suffixed with the faction name and store a 'true' in
     * the memory key.
     */
    public static final String FACTION_NEVER_NOTIFY_KEY_PREFIX = "$EnableTranspONder_Faction_NeverNotify_";
    /**
     * The memory key prefix to store the player's do-not-notify list
     * Should not be modified by external processes
     */
    public static final String FACTION_IGNORE_KEY_PREFIX = "$EnableTranspONder_Faction_Ignore_";
    /**
     * The memory key to store the reference to the current EnableTranspONder Dialog
     */
    public static final String DIALOG_PLUGIN_KEY = "$EnableTranspONder_EnableTransponderDialogPlugin";
    /** The cache key used for storing the locations that have been notified */
    public static final String NOTIFICATION_CACHE_KEY = "$EnableTranspONder_location_cache";
    // #endregion

    // #region System Tags
    /** The tag to apply to systems to mark them as ignored */
    public static final String SYSTEM_IGNORE_TAG = "enabletransponder_ignore";
    /** The tag to check for systems in the core worlds */
    public static final String SYSTEM_COREWORLD_TAG = "theme_core_populated";
    // #endregion
}
