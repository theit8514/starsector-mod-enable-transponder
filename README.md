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

### Faction Ignore
If you want your faction to be ignored, you can set this memory key to true and it will never trigger a notification.

```
$EnableTranspONder_Faction_NeverNotify_factionid=true
```