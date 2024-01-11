package net.cravencraft.betterparagliders.mixins.bettercombat.attributes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.bettercombat.logic.WeaponRegistry;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(WeaponRegistry.class)
public class WeaponRegistryMixin {



    @Inject(method = "loadContainers", at = @At("HEAD"), remap = false)
    private static void getResourceNames(ResourceManager resourceManager, CallbackInfo ci) throws IOException {
        Iterator var3 = resourceManager.listResourceStacks("weapon_attributes", (fileName) -> fileName.getPath().endsWith(".json")).entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry<ResourceLocation, List<Resource>> entry = (Map.Entry)var3.next();
            ResourceLocation identifier = entry.getKey();
            for (Resource resource : entry.getValue()) {
                JsonReader staminaReader = new JsonReader(new InputStreamReader(resource.open()));
                JsonObject weaponAttributes = JsonParser.parseReader(staminaReader).getAsJsonObject();
//                weaponAttributes.has("attributes");
                if (weaponAttributes.has("attributes")) {
                    weaponAttributes = weaponAttributes.getAsJsonObject("attributes");
//                    JsonElement attackRange1 = weaponAttributes.get("stamina_cost");
                    if (weaponAttributes.has("stamina_cost")) {
                        double staminaCost = weaponAttributes.get("stamina_cost").getAsDouble();
                        String itemName = identifier.getPath().replace("weapon_attributes/", "").replace(".json", "");
                        BetterParaglidersMod.LOGGER.info("ADDING TO STAMINA OVERRIDE: " + itemName + " + " + staminaCost);
                        CalculateStaminaUtils.addDatapackStaminaOverride(itemName, staminaCost);
                    }
                }
                staminaReader.close();
            }
        }
    }
}