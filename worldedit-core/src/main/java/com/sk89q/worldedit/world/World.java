/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.world;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.TreeGenerator.TreeType;
import com.sk89q.worldedit.world.registry.WorldData;

/**
 * Represents a world (dimension).
 */
public interface World extends Extent {

    /**
     * Get the name of the world.
     *
     * @return a name for the world
     */
    String getName();

    /**
     * Get the maximum Y.
     *
     * @return the maximum Y
     */
    int getMaxY();

    /**
     * Get the minimum Y.
     *
     * @return the minimum Y
     */
    int getMinY();

    /**
     * Checks whether the given block ID is a valid block ID.
     *
     * @param id the block ID
     * @return true if the block ID is a valid one
     */
    boolean isValidBlockType(int id);

    /**
     * Checks whether the given block ID uses data values for differentiating
     * types of blocks.
     *
     * @param id the block ID
     * @return true if the block uses data values
     */
    boolean usesBlockData(int id);

    /**
     * Create a mask that matches all liquids.
     *
     * <p>Implementations should override this so that custom liquids
     * are supported.</p>
     *
     * @return a mask
     */
    Mask createLiquidMask();

    /**
     * Use the given item on the block at the given location on the given side.
     *
     * @param item The item
     * @param face The face
     * @return Whether it succeeded
     */
    boolean useItem(Vector position, BaseItem item, Direction face);

    /**
     * @deprecated Use {@link #getLazyBlock(Vector)}
     */
    @Deprecated
    int getBlockType(Vector pt);

    /**
     * @deprecated Use {@link #getLazyBlock(Vector)}
     */
    @Deprecated
    int getBlockData(Vector pt);

    /**
     * Similar to {@link Extent#setBlock(Vector, BaseBlock)} but a
     * {@code notifyAndLight} parameter indicates whether adjacent blocks
     * should be notified that changes have been made and lighting operations
     * should be executed.
     *
     * <p>If it's not possible to skip lighting, or if it's not possible to
     * avoid notifying adjacent blocks, then attempt to meet the
     * specification as best as possible.</p>
     *
     * <p>On implementations where the world is not simulated, the
     * {@code notifyAndLight} parameter has no effect either way.</p>
     *
     * @param position position of the block
     * @param block block to set
     * @param notifyAndLight true to to notify and light
     * @return true if the block was successfully set (return value may not be accurate)
     */
    boolean setBlock(Vector position, BaseBlock block, boolean notifyAndLight) throws WorldEditException;

    /**
     * @deprecated Use {@link #setBlock(Vector, BaseBlock)}
     */
    @Deprecated
    boolean setBlockType(Vector position, int type);

    /**
     * @deprecated Use {@link #setBlock(Vector, BaseBlock)}
     */
    @Deprecated
    void setBlockData(Vector position, int data);

    /**
     * @deprecated Use {@link #setBlock(Vector, BaseBlock)}
     */
    @Deprecated
    boolean setTypeIdAndData(Vector position, int type, int data);

    /**
     * Get the light level at the given block.
     *
     * @param position the position
     * @return the light level (0-15)
     */
    int getBlockLightLevel(Vector position);

    /**
     * Clear a chest's contents.
     *
     * @param position the position
     * @return true if the container was cleared
     */
    boolean clearContainerBlockContents(Vector position);

    /**
     * Drop an item at the given position.
     *
     * @param position the position
     * @param item the item to drop
     * @param count the number of individual stacks to drop (number of item entities)
     */
    void dropItem(Vector position, BaseItemStack item, int count);

    /**
     * Drop one stack of the item at the given position.
     *
     * @param position the position
     * @param item the item to drop
     * @see #dropItem(Vector, BaseItemStack, int) shortcut method to specify the number of stacks
     */
    void dropItem(Vector position, BaseItemStack item);

    /**
     * Simulate a block being mined at the given position.
     *
     * @param position the position
     */
    void simulateBlockMine(Vector position);

    /**
     * Regenerate an area.
     *
     * @param region the region
     * @param editSession the {@link EditSession}
     * @return true if re-generation was successful
     */
    boolean regenerate(Region region, EditSession editSession);

    /**
     * Generate a tree at the given position.
     *
     * @param type the tree type
     * @param editSession the {@link EditSession}
     * @param position the position
     * @return true if generation was successful
     * @throws MaxChangedBlocksException thrown if too many blocks were changed
     */
    boolean generateTree(TreeGenerator.TreeType type, EditSession editSession, Vector position) throws MaxChangedBlocksException;

    /**
     * @deprecated Use {@link #generateTree(TreeType, EditSession, Vector)}
     */
    @Deprecated
    boolean generateTree(EditSession editSession, Vector position) throws MaxChangedBlocksException;

    /**
     * @deprecated Use {@link #generateTree(TreeType, EditSession, Vector)}
     */
    @Deprecated
    boolean generateBigTree(EditSession editSession, Vector position) throws MaxChangedBlocksException;

    /**
     * @deprecated Use {@link #generateTree(TreeType, EditSession, Vector)}
     */
    @Deprecated
    boolean generateBirchTree(EditSession editSession, Vector position) throws MaxChangedBlocksException;

    /**
     * @deprecated Use {@link #generateTree(TreeType, EditSession, Vector)}
     */
    @Deprecated
    boolean generateRedwoodTree(EditSession editSession, Vector position) throws MaxChangedBlocksException;

    /**
     * @deprecated Use {@link #generateTree(TreeType, EditSession, Vector)}
     */
    @Deprecated
    boolean generateTallRedwoodTree(EditSession editSession, Vector position) throws MaxChangedBlocksException;

    /**
     * Load the chunk at the given position if it isn't loaded.
     *
     * @param position the position
     */
    void checkLoadedChunk(Vector position);

    /**
     * Fix the given chunks after fast mode was used.
     *
     * <p>Fast mode makes calls to {@link #setBlock(Vector, BaseBlock, boolean)}
     * with {@code false} for the {@code notifyAndLight} parameter, which
     * may causes lighting errors to accumulate. Use of this method, if
     * it is implemented by the underlying world, corrects those lighting
     * errors and may trigger block change notifications.</p>
     *
     * @param chunks a list of chunk coordinates to fix
     */
    void fixAfterFastMode(Iterable<BlockVector2D> chunks);

    /**
     * Relight the given chunks if possible.
     *
     * @param chunks a list of chunk coordinates to fix
     */
    void fixLighting(Iterable<BlockVector2D> chunks);

    /**
     * Play the given effect.
     *
     * @param position the position
     * @param type the effect type
     * @param data the effect data
     * @return true if the effect was played
     */
    boolean playEffect(Vector position, int type, int data);

    /**
     * Queue a block break effect.
     *
     * @param server the server
     * @param position the position
     * @param blockId the block ID
     * @param priority the priority
     * @return true if the effect was played
     */
    boolean queueBlockBreakEffect(Platform server, Vector position, int blockId, double priority);

    /**
     * Get the data for blocks and so on for this world.
     *
     * @return the world data
     */
    WorldData getWorldData();

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();

}
