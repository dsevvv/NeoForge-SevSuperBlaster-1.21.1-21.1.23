package ca.dsevvv.sevsuperblaster;

import ca.dsevvv.sevsuperblaster.block.BlasterBenchBlock;
import ca.dsevvv.sevsuperblaster.entity.client.GunshotRenderer;
import ca.dsevvv.sevsuperblaster.entity.projectile.Gunshot;
import ca.dsevvv.sevsuperblaster.item.SuperBlasterItem;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SevSuperBlaster.MODID)
public class SevSuperBlaster
{
    public static final String MODID = "sevsuperblaster";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> BLASTER_BENCH = registerBlock("blaster_bench", () -> new BlasterBenchBlock(BlockBehaviour.Properties.of().noOcclusion()));
    public static final DeferredItem<Item> SUPER_BLASTER = ITEMS.register("super_blaster", () -> new SuperBlasterItem(new Item.Properties()));
    public static final DeferredItem<Item> GUNSHOT_ITEM = ITEMS.register("gunshot", () -> new Item(new Item.Properties()));
    public static final Supplier<EntityType<Gunshot>> GUNSHOT = ENTITIES.register("gunshot", () -> EntityType.Builder.of(Gunshot::new, MobCategory.CREATURE)
            .sized(0.25f, 0.25f).build("gunshot"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLASTER_TAB = CREATIVE_MODE_TABS.register("sevsuperblaster", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sevsuperblaster"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> SUPER_BLASTER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(SUPER_BLASTER.get());
                output.accept(BLASTER_BENCH.get());
                output.accept(GUNSHOT_ITEM.get());
            }).build());

    public SevSuperBlaster(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITIES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Initializing NeoForge-SevSuperBlaster-1.21.1-21.1.23");
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(GUNSHOT.get(), GunshotRenderer::new);
        }
    }
}
