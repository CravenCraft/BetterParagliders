package net.cravencraft.betterparagliders;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.ConfigManager;
import net.cravencraft.betterparagliders.network.ModNet;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tictim.paraglider.contents.Contents;

@Mod(BetterParaglidersMod.MOD_ID)
@Mod.EventBusSubscriber(modid = BetterParaglidersMod.MOD_ID, bus = Bus.MOD)
public class BetterParaglidersMod
{
    public static final Logger LOGGER = LogManager.getLogger("BetterParaglidersMod");
    public static final String MOD_ID = "betterparagliders";

    public BetterParaglidersMod()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Contents.registerEventHandlers(eventBus);
        BetterParaglidersAttributes.registerEventHandlers(eventBus);
        ModNet.init();

        // Register ourselves for server and other game events we are interested in
        ConfigManager.registerConfigs();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event){
        event.add(EntityType.PLAYER, BetterParaglidersAttributes.BASE_MELEE_STAMINA_REDUCTION.get());
        event.add(EntityType.PLAYER, BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get());
        event.add(EntityType.PLAYER, BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get());
        event.add(EntityType.PLAYER, BetterParaglidersAttributes.RANGE_STAMINA_REDUCTION.get());
        event.add(EntityType.PLAYER, BetterParaglidersAttributes.BLOCK_STAMINA_REDUCTION.get());
        event.add(EntityType.PLAYER, BetterParaglidersAttributes.ROLL_STAMINA_REDUCTION.get());
    }
}
