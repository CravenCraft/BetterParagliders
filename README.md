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

## Mod Dependencies
- [Paragliders](https://www.curseforge.com/minecraft/mc-mods/paragliders/files/4478230) (1.19.2)
- [Better Combat](https://www.curseforge.com/minecraft/mc-mods/better-combat-by-daedelus/files/4534619) (1.19.2)
- [Combat Roll](https://www.curseforge.com/minecraft/mc-mods/combat-roll/files/4428898) (1.19.2)

## Install Instructions
This mod should be pretty straightforward. Just follow the links for the mod dependencies listed above, install them
into your minecraft mods folder along with this one, and everything should work fine!

## V0.2.0
- MASSIVE rework of the entire mod structure using mixins
- Eliminated a LOT of unnecessary code
- Fixed a MASSIVE bug in multiplayer where players would sometimes share the same stamina

## V0.1.0
First official version of this mod. Has some pretty basic support of all basic attacks, blocking, and rolling.