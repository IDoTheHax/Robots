package net.idothehax;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.idothehax.block.RobotBlock;
import net.idothehax.block.entity.RobotBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RobotRegistries {
    public static final Block ROBOT_BLOCK = new RobotBlock(Block.Settings.create().strength(3.0f).requiresTool());
    public static BlockEntityType<RobotBlockEntity> ROBOT_BLOCK_ENTITY;
    public static final BlockItem ROBOT_BLOCK_ITEM = new RobotBlockItem(ROBOT_BLOCK, new Item.Settings());

    public static class RobotBlockItem extends BlockItem implements PolymerItem {
        public RobotBlockItem(Block block, Settings settings) {
            super(block, settings);
        }

        @Override
        public Item getPolymerItem(ItemStack itemStack, ServerPlayerEntity player) {
            return Items.IRON_BLOCK;
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, ServerPlayerEntity player) {
            return -1;
        }
    }

    public static void registerItems() {
        Registry.register(Registries.ITEM, Identifier.of(Robots.MOD_ID, "robot_block"), ROBOT_BLOCK_ITEM);
    }

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK, Identifier.of(Robots.MOD_ID, "robot_block"), ROBOT_BLOCK);
    }

    public static void registerBlockEntities() {
        ROBOT_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Robots.MOD_ID, "robot_block_entity"),
                BlockEntityType.Builder.create(RobotBlockEntity::new, ROBOT_BLOCK).build(null)
        );

        PolymerBlockUtils.registerBlockEntity(ROBOT_BLOCK_ENTITY);
    }
}
