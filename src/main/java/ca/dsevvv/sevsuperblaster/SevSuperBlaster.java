package ca.dsevvv.sevsuperblaster;

import ca.dsevvv.sevsuperblaster.block.BlasterBenchBlock;
import ca.dsevvv.sevsuperblaster.blockentity.BlasterBenchEntity;
import ca.dsevvv.sevsuperblaster.entity.client.GunshotModel;
import ca.dsevvv.sevsuperblaster.entity.client.GunshotRenderer;
import ca.dsevvv.sevsuperblaster.entity.projectile.Gunshot;
import ca.dsevvv.sevsuperblaster.event.GunshotKillEvent;
import ca.dsevvv.sevsuperblaster.item.SuperBlasterItem;
import ca.dsevvv.sevsuperblaster.menu.BlasterBenchMenu;
import ca.dsevvv.sevsuperblaster.menu.screen.BlasterBenchScreen;
import ca.dsevvv.sevsuperblaster.particle.provider.BoomParticleProvider;
import ca.dsevvv.sevsuperblaster.particle.provider.SparkParticleProvider;
import ca.dsevvv.sevsuperblaster.particle.provider.TrailParticleProvider;
import ca.dsevvv.sevsuperblaster.payload.UpdateBlasterBench;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TRAIL_PARTICLE = PARTICLE_TYPES.register("super_blaster_trail", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BOOM_PARTICLE = PARTICLE_TYPES.register("boom", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_PARTICLE = PARTICLE_TYPES.register("spark", () -> new SimpleParticleType(false));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLASTER_LVL = DATA_COMPONENT_TYPES.register("blaster_lvl", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLASTER_DMG = DATA_COMPONENT_TYPES.register("blaster_dmg", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLASTER_EXPLOSION_SIZE = DATA_COMPONENT_TYPES.register("blaster_explosion_size", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLASTER_HEAL_ON_KILL = DATA_COMPONENT_TYPES.register("blaster_heal_on_kill", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> BLASTER_HOMING_SPEED = DATA_COMPONENT_TYPES.register("blaster_homing_speed", () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).build());

    public static final DeferredBlock<Block> BLASTER_BENCH = registerBlock("blaster_bench", () -> new BlasterBenchBlock(BlockBehaviour.Properties.of().noOcclusion()));
    public static final Supplier<BlockEntityType<BlasterBenchEntity>> BLASTER_BENCH_ENTITY = BLOCK_ENTITY_TYPES.register(
            "blaster_bench_entity",
            () -> BlockEntityType.Builder.of(
                            BlasterBenchEntity::new,
                            BLASTER_BENCH.get()
                    )
                    .build(null)
    );
    public static final DeferredItem<Item> SUPER_BLASTER = ITEMS.register("super_blaster", () -> new SuperBlasterItem(new Item.Properties()));
    public static final DeferredItem<Item> GUNSHOT_ITEM = ITEMS.register("gunshot", () -> new Item(new Item.Properties()));
    public static final Supplier<EntityType<Gunshot>> GUNSHOT = ENTITIES.register("gunshot", () -> EntityType.Builder.of(Gunshot::new, MobCategory.MISC)
            .sized(0.175f, 0.175f).build("gunshot"));
    public static final Supplier<MenuType<BlasterBenchMenu>> BLASTER_MENU = MENU_TYPES.register("blaster_bench_menu", () -> IMenuTypeExtension.create(BlasterBenchMenu::new));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLASTER_TAB = CREATIVE_MODE_TABS.register("sevsuperblaster", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.sevsuperblaster"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> SUPER_BLASTER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(SUPER_BLASTER.get());
                output.accept(BLASTER_BENCH.get());
            }).build());

    public SevSuperBlaster(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerPayload);

        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        DATA_COMPONENT_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new GunshotKillEvent());

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void registerScreens(final RegisterMenuScreensEvent event){
        event.register(BLASTER_MENU.get(), BlasterBenchScreen::new);
    }

    private void registerPayload(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                UpdateBlasterBench.TYPE,
                UpdateBlasterBench.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleDataOnMain,
                        ServerPayloadHandler::handleDataOnMain
                )
        );
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

        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
            event.registerEntityRenderer(GUNSHOT.get(), GunshotRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
            event.registerLayerDefinition(GunshotModel.LAYER_LOCATION, GunshotModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerParticleProvider(RegisterParticleProvidersEvent event){
            event.registerSpriteSet(TRAIL_PARTICLE.get(), TrailParticleProvider::new);
            event.registerSpriteSet(BOOM_PARTICLE.get(), BoomParticleProvider::new);
            event.registerSpriteSet(SPARK_PARTICLE.get(), SparkParticleProvider::new);
        }
    }

    public class ClientPayloadHandler{
        public static void handleDataOnMain(final UpdateBlasterBench data, final IPayloadContext context){
        }
    }

    public class ServerPayloadHandler{
        public static void handleDataOnMain(final UpdateBlasterBench data, final IPayloadContext context){
            Level level = context.player().level();
            BlasterBenchEntity blockEntity = (BlasterBenchEntity) level.getBlockEntity(data.pos());

            ItemStack stack = blockEntity.inventory.getStackInSlot(0);

            switch (data.flag()){
                //change blaster lvl
                case 0:
                    stack.set(SevSuperBlaster.BLASTER_LVL, data.value());
                    break;
                //change blaster dmg
                case 1:
                    stack.set(SevSuperBlaster.BLASTER_DMG, data.value());
                    break;
                //change blaster explosion size
                case 2:
                    stack.set(SevSuperBlaster.BLASTER_EXPLOSION_SIZE, data.value());
                    break;
                //change blaster heal
                case 3:
                    stack.set(SevSuperBlaster.BLASTER_HEAL_ON_KILL, data.value());
                    break;
                //change blaster homing speed
                case 4:
                    float spd = Float.parseFloat(String.format("%d", data.value()));
                    stack.set(SevSuperBlaster.BLASTER_HOMING_SPEED, spd / 10f);
                    break;
                //remove diamonds
                case 5:
                    Inventory pInv = context.player().getInventory();
                    for(int i = 0; i < data.value(); i++){
                        int slot = pInv.findSlotMatchingItem(Items.DIAMOND.getDefaultInstance());
                        pInv.removeItem(slot, 1);
                    }
                default:
                    break;
            }
        }
    }
}
