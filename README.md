# Enable TranspONder
A StarSector mod to always make sure you never forget your transponder

## Usage
Download the [release package](https://github.com/theit8514/starsector-mod-enable-transponder/releases/latest) and extract into your StarSector mods folder.
This is usually C:\Program Files (x86)\Fractal Softworks\Starsector\mods on Windows.

This mod should be safe to add to existing games, but as of right now is not removable from existing saved games. You
can disable this mod entirely by changing the file EnableTranspONder\data\config\settings.json and setting
EnableTranspONder_disabled to true. This will completely remove all features and you will not be notified.

## How it works
When entering a core world through a Gate or upon detecting a faction patrol fleet, the mod will pop up a warning
message if your Activate Transponder ability is not enabled. You can activate the transponder from this warning and
should not be penalized for having your transponder disabled. You can also select the 'Never for this faction' or
'Never for this system' options to not be notified of that particular faction or system in the future.

## Console Command
If you have Console Commands installed, a console command is available to clear faction and system ignore flags.
Use `help EnableTranspONder` for syntax.

## Mod Authors
If you are a mod author, and you don't use the standard fleet patrol names, you may need to add a config to your mod or
set a memory key identifying them. In addition, if your faction should not care about transponder status, and you
don't have allowsTransponderOffTrade set to true, you can set a memory key to ignore your faction.

### Faction Patrol Fleet Names
I have not found a way to check for the 'fleetType' of a spawned fleet, so I only have the name to go off. If your
fleet names do not contain the normal patrol fleet names, you can create a
data/config/enabletranspONder/patrol_fleet_names.csv and include the fleet name or Regex Patterns to match your patrol
fleet names.

The mod will also check for a memory key starting with $EnableTranspONder_fleet_patrol_ with the value of your fleet
name or Regex Pattern.

Example:
```
$EnableTranspONder_fleet_patrol_mymod_1=My Cool Fleet Name
$EnableTranspONder_fleet_patrol_mymod_2=.*My Pattern.*
```

All checks are case-insensitive.

### Faction Ignore
If you want your faction to be ignored, you can set this memory key to true and it will never trigger a notification.

```
$EnableTranspONder_Faction_NeverNotify_factionid=true
```