package net.cravencraft.betterparagliders;
import me.shedaniel.autoconfig.AutoConfig;
import net.combatroll.CombatRoll;
import net.combatroll.config.ServerConfigWrapper;
import net.cravencraft.betterparagliders.capabilities.UpdatedPlayerMovement;
import net.cravencraft.betterparagliders.client.overlay.NewStaminaWheelOverlay;
import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import net.cravencraft.betterparagliders.network.ModNet;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
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
        UpdatedModCfg.init();
        ModNet.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(UpdatedPlayerMovement.class);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
        event.registerAboveAll("stamina_wheel", new NewStaminaWheelOverlay());
    }
}
