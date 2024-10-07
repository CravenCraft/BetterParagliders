## V0.6.0 - Major Bug Fixes
- 

## V0.5.3 - Better Combat Version Update
- Bumped the Better Combat version up to the latest 1.8.5 version.

## V0.5.2 - Attribute Fixes & Better Combat Version Bump
- Added support for 1.8.4 version of Better Combat
- Added a lang file for Attribute name support
- Fixed a bug that didn't register attribute value changes

## V0.5.1 - Bug Fixes
- Fixed bug causing the game to crash if a datapack isn't configured properly.
- Fixed bug causing shields to be usable when stamina is drained.

## V0.5.0 - The Datapack Update!
- Datapacks should now be fully supported & use my own standalone system removing the need for mixins.
- Support for bows via datapacks.
- Support for shields via datapacks.
- Items can now all be overriden via datapacks to make an entirely custom experience for the user.
- Restructure of a LOT more code. Things should be even cleaner now (hopefully).
- Bows & Shields now longer have finicky stamina systems. They should work just as well as the melee one now. 
- A LOT of logic has been reworked to happen server side to support datapacks
- Bows & Crossbows now have working support
- Datapacks are now integrated. Still need to polish up the implementation up a bit, but working pretty well as of now.
- Some minor performance boosts as certain calls won't be made nearly as much after the code rework.

## V0.4.0 - The Configuration & Parameter Update!
- Added a server and client config (though the client config is purely a placeholder for now).
- Updated Shield stamina costs
- Added a proper mod version check so now the mod will ensure the proper version of every mod is installed.
- Made the difficult choice to remove Combat Roll support. Makes supporting multiple versions more difficult, and just increases the scope of this mod to more than it truly needs to be.
- Refactored & Reorganized a LOT of code. Things are much nicer now, and updates/ports will be a lot easier in the future.

## V0.3.0
- Optimized network code a bit more. Should be effected less by lag spikes on servers now.
- Added **effects** and **effects_strength** to the server config to allow users to add _any_ effect they want
  based on the effect's ID, and to edit the strength of the respective effects. If the effect does not exist,
  the player will receive a message stating which one doesn't exist when their stamina is depleted, and if no
  effect strength is set, then it will default to 1.
- Should be the last updated before I work on porting to 1.20.1!

## V0.2.4
- Fixed a bug causing stamina to only drain running energy when sprinting and attacking.
- Fixed a bug causing dual wielded weapons to only drain stamina from the main hand item.
- Added more attribute support (**one_handed_factor** and **two_handed_factor**) and renamed **strength_factor** to **melee_factor**.
- Added more config suppot (**oneHandedStaminaConsumption** and **twoHandedStaminaConsumption**).

## V0.2.3
- Fixed major bug that caused the game to crash when on a server and blocking an attack.

## V0.2.2
- Fixed small bug that caused stamina not to drain unless **both** paragliding and running stamina was set to true.

## V0.2.1
- Added attribute support! Can now be configured to work with RPG mods like Pufferfish's Skills or Project MMO
- Updated the config. Paragliding and Running no longer need to be set here. Can set how much certain actions will drain
  stamina (melee, ranged, blocking, rolling).
- Combat Roll enchantments have been reworked to work with the new stamina system.

## V0.2.0
- MASSIVE rework of the entire mod structure using mixins
- Eliminated a LOT of unnecessary code
- Fixed a MASSIVE bug in multiplayer where players would sometimes share the same stamina

## V0.1.0
- First official version of this mod. Has some pretty basic support of all basic attacks, blocking, and rolling.