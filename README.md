# BetterParagliders
A compat mod to implement Paraglider's stamina system with Better Combat attacks. Almost all attacks (save for special
right click abilities) will now drain stamina. Here's a short list of everything that now drains stamina:
- **Basic Attacks** - Attacks are carefully have their weapon tier and attack duration factored into the amount of stamina
  that they drain. So, a diamond sword will drain slightly more than an iron sword because of it being a higher tier,
  and an iron axe will drain slightly more than an iron sword because of the attack duration.
- **Ranged Attacks** - Ranged attacks from bows and crossbows will also drain stamina. Holding a bow will slowly drain
  stamina until the arrow is loosed, and cocking a crossbow back will also drain stamina. Encouraging the use of
  crossbows a little more since less overall stamina is used.
- **Blocking** - Blocking drains stamina based on the amount of damage absorbed by the block. The cooldown for a shield
  is now removed until the player's stamina is completely depleted. It will be available again once the player regains
  all their stamina.

## Other Features
- **Config File** - The server config file for this mod will contain various settings to increase or reduce the amount
  of stamina each action consumes, as well as what type of effects running out of stamina will give the player
  (as well as the intensity of said effects).
  > YOUR_WORLD_SAVE/serverconfig/betterparagliders-server.toml

- **Attributes** - This mod contains multiple attributes. Each with a default value set to 1.0 and a range
  from 0.0 - 2.0. Increasing the value increases the stamina cost, and decreasing the value decreases stamina cost.
  This was implemented in order for this mod to have compatibility with RPG mods such as
  [Pufferfish's Skills](https://www.curseforge.com/minecraft/mc-mods/puffish-skills) and
  [Project MMO](https://www.curseforge.com/minecraft/mc-mods/project-mmo).
    - **sprinting_stamina_reduction**
    - **swimming_stamina_reduction**
    - **idle_stamina_regen**
    - **submerged_stamina_regen**
    - **water_breathing_stamina_regen**
    - **base_melee_stamina_reduction**
    - **two_handed_stamina_reduction**
    - **one_handed_stamina_reduction**
    - **range_stamina_reduction**
    - **block_stamina_reduction**

## Mod Dependencies
- [Paragliders](https://www.curseforge.com/minecraft/mc-mods/paragliders/)
- [Better Combat](https://www.curseforge.com/minecraft/mc-mods/better-combat-by-daedelus/)

## Install Instructions
This mod should be pretty straightforward. Just follow the links for the mod dependencies listed above, install the
latest versions for the respective minecraft version along with this one, and everything should work fine!

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
