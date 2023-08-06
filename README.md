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
- **Rolling** - The Combat Roll mod is also a dependency of this mod! As such, it drains stamina as well based on the
    player's armor value. Higher armor values equals higher roll stamina cost. However, to combat this the enchantments
    have now been reworked as well. Multi-Roll now decreases the amount of stamina drained per roll. The roll cooldown
    has also been reworked to be dependent on armor values instead of a base amount. Higher values equals a longer
    cooldown between rolls (think fat rolling in Dark Souls). To combat this Acrobat has been slightly tweaked to 
    decrease the cooldown time even with the armor value factored in. Higher enchantment levels equals lower stamina 
    cost and cooldown times!

## Other Features
- **Config File** - The server config file for this mod will be in the same location as the one for Paragliders.
  Here you can find various settings to increase or reduce the amount of stamina each action consumes. By default,
  everything is set to 1.0. Increasing to 2.0 will **double** the amount of stamina consumed, and decreasing to 0.0 will
  **completely remove** stamina consumption from this action entirely.
  > YOUR_WORLD_SAVE/serverconfig/betterparagliders-server.toml

- **Attributes** - This mod contains multiple attributes. Each with a default value set to 1.0 and a range 
  from 0.0 - 2.0. Increasing the value increases the stamina cost, and decreasing the value decreases stamina cost.
  This was implemented in order for this mod to have compatibility with RPG mods such as 
  [Pufferfish's Skills](https://www.curseforge.com/minecraft/mc-mods/puffish-skills) and
  [Project MMO](https://www.curseforge.com/minecraft/mc-mods/project-mmo).
  - **melee_factor**
  - **one_handed_factor**
  - **two_handed_factor**
  - **range_factor**
  - **block_factor**
  - **roll_factor**

## Mod Dependencies
- [Paragliders](https://www.curseforge.com/minecraft/mc-mods/paragliders/files/4478246) (1.18.2)
- [Better Combat](https://www.curseforge.com/minecraft/mc-mods/better-combat-by-daedelus/files/4428885) (1.18.2)
- [Combat Roll](https://www.curseforge.com/minecraft/mc-mods/combat-roll/files/4428900) (1.18.2)

## Install Instructions
This mod should be pretty straightforward. Just follow the links for the mod dependencies listed above, install them
into your minecraft mods folder along with this one, and everything should work fine!

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
First official version of this mod. Has some pretty basic support of all basic attacks, blocking, and rolling.
