package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.*;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstructionMarkerBlockEntity extends BuildCraftBlockEntity implements MenuProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public static Set<ConstructionMarkerBlockEntity> currentMarkers = new HashSet<>();

    public Direction direction = Direction.NORTH;
    public LaserData laser;
    public ItemStack itemBlueprint;

    public Box box = new Box();
    public BptBuilderBase bluePrintBuilder;
    public BptContext bptContext;

    private ArrayList<BuildingItem> buildersInAction = new ArrayList<>();
    private CompoundTag initNBT;

    public ConstructionMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypesBuilders.CONSTRUCTION_MARKER.get(), pos, state);
    }

    public ItemStack getBlueprint() {
        return items.get(0);
    }

    public void setBlueprint(ItemStack stack) {
        items.set(0, stack);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasBlueprint() {
        return !items.get(0).isEmpty();
    }

    public ItemStack removeBlueprint() {
        ItemStack stack = items.get(0);
        items.set(0, ItemStack.EMPTY);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return stack;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.bcrebornbuilders.construction_marker");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
        return null;
    }


    @Override
    public void initialize() {
        super.initialize();
        box.kind = Box.Kind.BLUE_STRIPES;

        if (!level.isClientSide) {
           BCNetworkManager.sendUploadBuildersInAction(getBlockPos());
        }
    }

    @Override
    protected void tick(Level level, BlockPos pos, BlockState state) {
        BuildingItem toRemove = null;

        for (BuildingItem i : buildersInAction) {
            i.update();

            if (i.isDone) {
                toRemove = i;
            }
        }

        if (toRemove != null) {
            buildersInAction.remove(toRemove);
        }

        if (level.isClientSide) {
            return;
        }

        if (itemBlueprint != null && ItemBlueprint.getId(itemBlueprint) != null && bluePrintBuilder == null) {
            BlueprintBase bpt = ItemBlueprint.loadBlueprint(itemBlueprint);
            if (bpt != null && bpt instanceof Blueprint) {
                bpt = bpt.adjustToWorld(level, xCoord, yCoord, zCoord, direction);
                if (bpt != null) {
                    bluePrintBuilder = new BptBuilderBlueprint((Blueprint) bpt, level, xCoord, yCoord, zCoord);
                    bptContext = bluePrintBuilder.getContext();
                    box.initialize(bluePrintBuilder);
                }
            } else {
                return;
            }
        }

        if (laser == null && direction != Direction.UP) {
            laser = new LaserData();
            laser.head = new Position(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F);
            laser.tail = new Position(xCoord + 0.5F + direction.getStepX() * 0.5F,
                yCoord + 0.5F + direction.getStepY() * 0.5F,
                zCoord + 0.5F + direction.getStepZ() * 0.5F);
            laser.isVisible = true;
        }

        if (initNBT != null) {
            if (bluePrintBuilder != null) {
                bluePrintBuilder.loadBuildStateToNBT(initNBT.getCompound("builderState"), this);
            }

            initNBT = null;
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        nbt.putInt("direction", direction.get3DDataValue());

        if (itemBlueprint != null) {
            CompoundTag bptNBT = new CompoundTag();
            itemBlueprint.save(bptNBT);
            nbt.put("itemBlueprint", bptNBT);
        }

        CompoundTag bptNBT = new CompoundTag();

        if (bluePrintBuilder != null) {
            CompoundTag builderCpt = new CompoundTag();
            bluePrintBuilder.saveBuildStateToNBT(builderCpt, this);
            bptNBT.put("builderState", builderCpt);
        }

        nbt.put("bptBuilder", bptNBT);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        direction = Direction.from3DDataValue(nbt.getInt("direction"));

        if (nbt.contains("itemBlueprint")) {
            itemBlueprint = ItemStack.of(nbt.getCompound("itemBlueprint"));
        }

        // The rest of load has to be done upon initialize.
        initNBT = nbt.getCompound("bptBuilder").copy();
    }

    @Override
    public List<BuildingItem> getBuilders() {
        return buildersInAction;
    }


    @Override
    public void validate() {
        super.validate();
        if (!level.isClientSide) {
            currentMarkers.add(this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!level.isClientSide) {
            currentMarkers.remove(this);
        }
    }

    public boolean needsToBuild() {
        return !isRemoved() && bluePrintBuilder != null && !bluePrintBuilder.isDone(this);
    }

    public BptContext getContext() {
        return bptContext;
    }

    @Override
    public void addAndLaunchBuildingItem(BuildingItem item) {
        buildersInAction.add(item);
        BCNetworkManager.sendNearLaunchItem(getBlockPos().getCenter(), getBlockPos(), item);
    }

    @Override
    public void receiveCommand(String command, Side side, Object sender, ByteBuf stream) {
        if (side.isServer() && "uploadBuildersInAction".equals(command)) {
        } else if (side.isClient() && "launchItem".equals(command)) {
            BuildingItem item = new BuildingItem();
            item.readData(stream);
            buildersInAction.add(item);
        }
    }

    public Box getBox() {
        return box;
    }

    @Override
    public AABB getRenderBoundingBox() {
        Box renderBox = new Box(this).extendToEncompass(box);

        return renderBox.expand(50).getBoundingBox();
    }

    @Override
    public void writeData(FriendlyByteBuf data) {
        box.writeData(data);
        data.writeByte((laser != null ? 1 : 0) | (itemBlueprint != null ? 2 : 0));
        if (laser != null) {
            laser.writeData(data);
        }
        if (itemBlueprint != null) {
            data.writeItem(itemBlueprint);
        }
    }

    @Override
    public void readData(FriendlyByteBuf data) {
        box.readData(data);
        int flags = data.readUnsignedByte();
        if ((flags & 1) != 0) {
            laser = new LaserData();
            laser.readData(data);
        } else {
            laser = null;
        }
        if ((flags & 2) != 0) {
            itemBlueprint = data.readItem();
        } else {
            itemBlueprint = null;
        }
    }

    public void sendBuildersInAction(ServerPlayer player, BlockPos pos) {
        for (BuildingItem i : buildersInAction) {
            BCNetworkManager.sendLaunchItem(player, pos, i);
        }
    }
}
