package com.peco2282.bcreborn.common.builder;

import com.peco2282.bcreborn.common.blueprint.BptContext;
import com.peco2282.bcreborn.api.blueprints.MappingNotFoundException;
import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.common.StackAtPosition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BuildingItem implements ISerializable {

    public static int ITEMS_SPACE = 2;

    public Position origin, destination;
    public LinkedList<StackAtPosition> stacksToDisplay = new LinkedList<>();

    public BptContext context;

    public boolean isDone = false;

    public BuildingSlot slotToBuild;

    private long previousUpdate;
    private float lifetimeDisplay = 0;
    private float maxLifetime = 0;
    private boolean initialized = false;
    private double vx, vy, vz;
    private double maxHeight;
    private float lifetime = 0;

    public void initialize() {
        if (!initialized) {
            double dx = destination.x - origin.x;
            double dy = destination.y - origin.y;
            double dz = destination.z - origin.z;

            double size = Math.sqrt(dx * dx + dy * dy + dz * dz);

            maxLifetime = (float) size * 4;
            maxHeight = size / 2;

            Position middle = new Position();
            middle.x = (destination.x + origin.x) / 2;
            middle.y = (destination.y + origin.y) / 2;
            middle.z = (destination.z + origin.z) / 2;

            Position top = new Position();
            top.x = middle.x;
            top.y = middle.y + maxHeight;
            top.z = middle.z;

            Position originToTop = new Position();
            originToTop.x = top.x - origin.x;
            originToTop.y = top.y - origin.y;
            originToTop.z = top.z - origin.z;

            Position destinationToTop = new Position();
            destinationToTop.x = destination.x - origin.x;
            destinationToTop.y = destination.y - origin.y;
            destinationToTop.z = destination.z - origin.z;

            double d1 = Math.sqrt(originToTop.x * originToTop.x + originToTop.y
                    * originToTop.y + originToTop.z * originToTop.z);

            double d2 = Math.sqrt(destinationToTop.x * destinationToTop.x + destinationToTop.y
                    * destinationToTop.y + destinationToTop.z * destinationToTop.z);

            d1 = d1 / size * maxLifetime;
            d2 = d2 / size * maxLifetime;

            maxLifetime = (float) d1 + (float) d2;

            vx = dx / maxLifetime;
            vy = dy / maxLifetime;
            vz = dz / maxLifetime;

            initialized = true;
        }
    }

    public Position getDisplayPosition(float time) {
        Position result = new Position();

        result.x = origin.x + vx * time;
        result.y = origin.y + vy * time + Math.sin(time / maxLifetime * Math.PI) * maxHeight;
        result.z = origin.z + vz * time;

        return result;
    }

    public void update() {
        if (isDone) {
            return;
        }

        initialize();

        lifetime++;

        if (lifetime > maxLifetime + stacksToDisplay.size() * ITEMS_SPACE - 1) {
            isDone = true;
        }

        lifetimeDisplay = lifetime;
        previousUpdate = new Date().getTime();
    }

    public void displayUpdate() {
        initialize();

        float tickDuration = 50.0F;
        long currentUpdate = new Date().getTime();
        float timeSpan = currentUpdate - previousUpdate;
        previousUpdate = currentUpdate;

        float displayPortion = timeSpan / tickDuration;

        if (lifetimeDisplay - lifetime <= 1.0) {
            lifetimeDisplay += 1.0 * displayPortion;
        }
    }

    public LinkedList<StackAtPosition> getStacks() {
        int d = 0;

        for (StackAtPosition s : stacksToDisplay) {
            float stackLife = lifetimeDisplay - d;

            if (stackLife <= maxLifetime && stackLife > 0) {
                s.pos = getDisplayPosition(stackLife);
                s.display = true;
            } else {
                s.display = false;
            }

            d += ITEMS_SPACE;
        }

        return stacksToDisplay;
    }

    public void writeToNBT(CompoundTag nbt) {
        CompoundTag originNBT = new CompoundTag();
        origin.writeToNBT(originNBT);
        nbt.put("origin", originNBT);

        CompoundTag destinationNBT = new CompoundTag();
        destination.writeToNBT(destinationNBT);
        nbt.put("destination", destinationNBT);

        nbt.putFloat("lifetime", lifetime);

        ListTag items = new ListTag();

        for (StackAtPosition s : stacksToDisplay) {
            CompoundTag cpt = new CompoundTag();
            s.stack.save(cpt);
            items.add(cpt);
        }

        nbt.put("items", items);
    }

    public void readFromNBT(CompoundTag nbt) throws MappingNotFoundException {
        origin = new Position(nbt.getCompound("origin"));
        destination = new Position(nbt.getCompound("destination"));
        lifetime = nbt.getFloat("lifetime");

        ListTag items = nbt.getList("items", ListTag.TAG_COMPOUND);

        for (int i = 0; i < items.size(); ++i) {
            StackAtPosition sPos = new StackAtPosition();
            sPos.stack = ItemStack.of(items.getCompound(i));
            stacksToDisplay.add(sPos);
        }
    }

    public void setStacksToDisplay(List<ItemStack> stacks) {
        if (stacks != null) {
            for (ItemStack s : stacks) {
                for (int i = 0; i < s.getCount(); ++i) {
                    StackAtPosition sPos = new StackAtPosition();
                    sPos.stack = s.copy();
                    sPos.stack.setCount(1);
                    stacksToDisplay.add(sPos);
                }
            }
        }
    }

    @Override
    public void writeData(FriendlyByteBuf data) {
        origin.writeData(data);
        destination.writeData(data);
        data.writeFloat(lifetime);
        data.writeShort(stacksToDisplay.size());
        for (StackAtPosition s : stacksToDisplay) {
            s.writeData(data);
        }
    }

    @Override
    public void readData(FriendlyByteBuf data) {
        origin = new Position();
        destination = new Position();
        origin.readData(data);
        destination.readData(data);
        lifetime = data.readFloat();
        stacksToDisplay.clear();
        int size = data.readUnsignedShort();
        for (int i = 0; i < size; i++) {
            StackAtPosition e = new StackAtPosition();
            e.readData(data);
            stacksToDisplay.add(e);
        }
    }
}
