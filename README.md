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