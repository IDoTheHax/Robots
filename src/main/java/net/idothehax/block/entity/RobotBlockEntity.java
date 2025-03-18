package net.idothehax.block.entity;

import net.idothehax.RobotRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RobotBlockEntity extends BlockEntity {
    // Robot state variables
    private int energy = 0;
    private int maxEnergy = 1000;
    private boolean isActive = false;

    public RobotBlockEntity(BlockPos pos, BlockState state) {
        super(RobotRegistries.ROBOT_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RobotBlockEntity entity) {
        if (world.isClient()) return;

        if (entity.isActive && entity.energy > 0) {
            // Robot logic here
            entity.energy--;

            // Example: Make the robot do something every 20 ticks (1 second)
            if (world.getTime() % 20 == 0) {
                // Do robot actions
                entity.performRobotAction(world, pos);
            }

            // Deactivate if energy is depleted
            if (entity.energy <= 0) {
                entity.isActive = false;
            }

            // Mark entity for update
            entity.markDirty();
        }
    }

    // This is the method that will be called from the ticker
    public void tick(World world, BlockPos pos, BlockState state) {
        tick(world, pos, state, this);
    }

    private void performRobotAction(World world, BlockPos pos) {
        // Implement robot behaviors here
        // For example, scan surrounding blocks, move, etc.
    }

    // Getters and setters
    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, maxEnergy);
        this.markDirty();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
        this.markDirty();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.energy = nbt.getInt("Energy");
        this.maxEnergy = nbt.getInt("MaxEnergy");
        this.isActive = nbt.getBoolean("IsActive");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Energy", this.energy);
        nbt.putInt("MaxEnergy", this.maxEnergy);
        nbt.putBoolean("IsActive", this.isActive);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return super.toInitialChunkDataNbt(registryLookup);
    }
}