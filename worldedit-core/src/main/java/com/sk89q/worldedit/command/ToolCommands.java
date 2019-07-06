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

package com.sk89q.worldedit.command;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.command.tool.BlockDataCyler;
import com.sk89q.worldedit.command.tool.BlockReplacer;
import com.sk89q.worldedit.command.tool.DistanceWand;
import com.sk89q.worldedit.command.tool.FloatingTreeRemover;
import com.sk89q.worldedit.command.tool.FloodFillTool;
import com.sk89q.worldedit.command.tool.LongRangeBuildTool;
import com.sk89q.worldedit.command.tool.NavigationWand;
import com.sk89q.worldedit.command.tool.QueryTool;
import com.sk89q.worldedit.command.tool.SelectionWand;
import com.sk89q.worldedit.command.tool.TreePlanter;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.item.ItemType;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;

@CommandContainer(superTypes = CommandPermissionsConditionGenerator.Registration.class)
public class ToolCommands {
    private final WorldEdit we;

    public ToolCommands(WorldEdit we) {
        this.we = we;
    }

    @Command(
        name = "none",
        desc = "Unbind a bound tool from your current item"
    )
    public void none(Player player, LocalSession session) throws WorldEditException {

        session.setTool(player.getItemInHand(HandSide.MAIN_HAND).getType(), null);
        player.printInfo(TranslatableComponent.of("worldedit.tool.none.equip"));
    }

    @Command(
        name = "/selwand",
        aliases = "selwand",
        desc = "Selection wand tool"
    )
    @CommandPermissions("worldedit.setwand")
    public void selwand(Player player, LocalSession session) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new SelectionWand());
        player.printInfo(TranslatableComponent.of("worldedit.tool.selwand.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "/navwand",
        aliases = "navwand",
        desc = "Navigation wand tool"
    )
    @CommandPermissions("worldedit.setwand")
    public void navwand(Player player, LocalSession session) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new NavigationWand());
        player.printInfo(TranslatableComponent.of("worldedit.tool.navWand.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "info",
        desc = "Block information tool"
    )
    @CommandPermissions("worldedit.tool.info")
    public void info(Player player, LocalSession session) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new QueryTool());
        player.printInfo(TranslatableComponent.of("worldedit.tool.info.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "tree",
        desc = "Tree generator tool"
    )
    @CommandPermissions("worldedit.tool.tree")
    public void tree(Player player, LocalSession session,
                     @Arg(desc = "Type of tree to generate", def = "tree")
                     TreeGenerator.TreeType type) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new TreePlanter(type));
        player.printInfo(TranslatableComponent.of("worldedit.tool.tree.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "repl",
        desc = "Block replacer tool"
    )
    @CommandPermissions("worldedit.tool.replacer")
    public void repl(Player player, LocalSession session,
                     @Arg(desc = "The pattern of blocks to place")
                         Pattern pattern) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new BlockReplacer(pattern));
        player.printInfo(TranslatableComponent.of("worldedit.tool.repl.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "cycler",
        desc = "Block data cycler tool"
    )
    @CommandPermissions("worldedit.tool.data-cycler")
    public void cycler(Player player, LocalSession session) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new BlockDataCyler());
        player.printInfo(TranslatableComponent.of("worldedit.tool.data-cycler.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "floodfill",
        aliases = { "flood" },
        desc = "Flood fill tool"
    )
    @CommandPermissions("worldedit.tool.flood-fill")
    public void floodFill(Player player, LocalSession session,
                          @Arg(desc = "The pattern to flood fill")
                              Pattern pattern,
                          @Arg(desc = "The range to perform the fill")
                              int range) throws WorldEditException {

        LocalConfiguration config = we.getConfiguration();

        if (range > config.maxSuperPickaxeSize) {
            player.printError("Maximum range: " + config.maxSuperPickaxeSize);
            return;
        }

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new FloodFillTool(range, pattern));
        player.printInfo(TranslatableComponent.of("worldedit.tool.floodfill.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "deltree",
        desc = "Floating tree remover tool"
    )
    @CommandPermissions("worldedit.tool.deltree")
    public void deltree(Player player, LocalSession session) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new FloatingTreeRemover());
        player.printInfo(TranslatableComponent.of("worldedit.tool.deltree.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "farwand",
        desc = "Wand at a distance tool"
    )
    @CommandPermissions("worldedit.tool.farwand")
    public void farwand(Player player, LocalSession session) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new DistanceWand());
        player.printInfo(TranslatableComponent.of("worldedit.tool.farwand.equip", TextComponent.of(itemType.getName())));
    }

    @Command(
        name = "lrbuild",
        aliases = { "/lrbuild" },
        desc = "Long-range building tool"
    )
    @CommandPermissions("worldedit.tool.lrbuild")
    public void longrangebuildtool(Player player, LocalSession session,
                                   @Arg(desc = "Pattern to set on left-click")
                                       Pattern primary,
                                   @Arg(desc = "Pattern to set on right-click")
                                       Pattern secondary) throws WorldEditException {

        final ItemType itemType = player.getItemInHand(HandSide.MAIN_HAND).getType();
        session.setTool(itemType, new LongRangeBuildTool(primary, secondary));
        player.printInfo(TranslatableComponent.of("worldedit.tool.lrbuild.equip", TextComponent.of(itemType.getName())));
        String primaryName = "pattern";
        String secondaryName = "pattern";
        if (primary instanceof BlockPattern) {
            primaryName = ((BlockPattern) primary).getBlock().getBlockType().getName();
        }
        if (secondary instanceof BlockPattern) {
            secondaryName = ((BlockPattern) secondary).getBlock().getBlockType().getName();
        }
        player.printInfo(TranslatableComponent.of("worldedit.tool.lrbuild.set", TextComponent.of(primaryName), TextComponent.of(secondaryName)));
    }
}
