package ca.dsevvv.sevsuperblaster.menu;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.blockentity.BlasterBenchEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BlasterBenchMenu extends AbstractContainerMenu {
    public final BlasterBenchEntity blockEntity;
    private final Level level;

    public BlasterBenchMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BlasterBenchMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(SevSuperBlaster.BLASTER_MENU.get(), containerId);
        this.blockEntity = ((BlasterBenchEntity) blockEntity);
        this.level = playerInventory.player.level();

        addPlayerHotbar(playerInventory);

        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 98, 8));
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 0;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 0;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 1;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, SevSuperBlaster.BLASTER_BENCH.get());
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 4, 26 + i * 18));
        }
    }

    public boolean isSuperBlasterInside(){
        return blockEntity.inventory.getStackInSlot(0).getItem() == SevSuperBlaster.SUPER_BLASTER.get();
    }

    public int getBlasterLevel(){
        if(isSuperBlasterInside()){
            return blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_LVL);
        }
        return 0;
    }

    public int getBlasterDamage(){
        if(isSuperBlasterInside()){
            return blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_DMG);
        }
        return 0;
    }

    public int getBlasterExplosion(){
        if(isSuperBlasterInside()){
            return blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_EXPLOSION_SIZE);
        }
        return 0;
    }

    public int getBlasterHeal(){
        if(isSuperBlasterInside()){
            return blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_HEAL_ON_KILL);
        }
        return 0;
    }

    public float getBlasterSpeed(){
        if(isSuperBlasterInside()){
            return blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_HOMING_SPEED);
        }
        return 0;
    }

    public int getBlasterCurrentPower(){
        if(isSuperBlasterInside()){
            int dmg = blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_DMG);
            int size = blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_EXPLOSION_SIZE);
            int heal = blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_HEAL_ON_KILL);
            float spd = blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_HOMING_SPEED);
            int spdInt = Math.round(spd * 10);

            return dmg + size + heal + spdInt;
        }
        return 0;
    }

    public int getBlasterMaxPower(){
        if(isSuperBlasterInside()){
            int lvl = blockEntity.inventory.getStackInSlot(0).get(SevSuperBlaster.BLASTER_LVL);
            return 10 + ((lvl - 1) * 5);
        }
        return 10;
    }
}
