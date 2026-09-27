package com.github.tacowasa059.sphericalplayermod.common.command;

import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = SphericalPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChangePlayerData {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("setSphere")
                .requires(source -> source.hasPermission(3))
                .then(Commands.literal("size")
                        .then(Commands.literal("set")
                            .then(Commands.argument("targets", EntityArgument.players())
                                    .then(Commands.argument("size", FloatArgumentType.floatArg())
                                            .executes(context -> {
                                                float size = FloatArgumentType.getFloat(context, "size");
                                                if (size > 20f || size<=0) {
                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.RED + "Player size must be within 0 to 20."), false);
                                                    return 0;
                                                }
                                                Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                for (ServerPlayer player : players) {
                                                    ((ICustomPlayerData) player).sphericalPlayerMod$setSize(size);
                                                }
                                                // フィードバックは一度だけ、対象のプレイヤーの人数を表示
                                                context.getSource().sendSuccess(()->Component.literal(ChatFormatting.GOLD+"[SPM] "+ChatFormatting.GREEN +
                                                        "Player size set to " + ChatFormatting.AQUA + size + ChatFormatting.GREEN + " for " + players.size() + " player(s)."), false);
                                                return 1;
                                            })
                                    )
                            )
                        )
                        .then(Commands.literal("get")
                            .then(Commands.argument("target", EntityArgument.player())
                                    .executes(context -> {
                                        ServerPlayer players = EntityArgument.getPlayer(context, "target");
                                        ICustomPlayerData playerData=(ICustomPlayerData)players;
                                        context.getSource().sendSuccess(()->Component.literal(ChatFormatting.GOLD+"[SPM] "+ChatFormatting.GREEN + "Player size is " + ChatFormatting.AQUA + playerData.sphericalPlayerMod$getSize()), false);
                                        return 1;
                                    })
                            )
                        )
                )
                .then(Commands.literal("RestitutionCoefficient")
                        .then(Commands.literal("set")
                            .then(Commands.argument("targets", EntityArgument.players())
                                    .then(Commands.argument("value", FloatArgumentType.floatArg())
                                            .executes(context -> {
                                                float size = FloatArgumentType.getFloat(context, "value");
                                                if (size > 1.2f || size<=0) {
                                                    context.getSource().sendSuccess(()->Component.literal(ChatFormatting.RED + "Player restitution coefficient must be within 0 to 1.2."), false);
                                                    return 0;
                                                }
                                                Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                for (ServerPlayer player : players) {
                                                    ((ICustomPlayerData) player).sphericalPlayerMod$setRestitutionCoefficient(size);
                                                }
                                                // フィードバックは一度だけ、対象のプレイヤーの人数を表示
                                                context.getSource().sendSuccess(()->Component.literal(ChatFormatting.GOLD+"[SPM] "+ChatFormatting.GREEN + "Player restitution coefficient set to " + ChatFormatting.AQUA + size + ChatFormatting.GREEN + " for " + players.size() + " player(s)."), false);
                                                return 1;
                                            })
                                    )
                            )
                        )
                        .then(Commands.literal("get")
                            .then(Commands.argument("target", EntityArgument.player())
                                    .executes(context -> {
                                        ServerPlayer players = EntityArgument.getPlayer(context, "target");
                                        ICustomPlayerData playerData=(ICustomPlayerData)players;
                                        context.getSource().sendSuccess(()->Component.literal(ChatFormatting.GOLD+"[SPM] "+ChatFormatting.GREEN + "Player restitution coefficient is " + ChatFormatting.AQUA + playerData.sphericalPlayerMod$getRestitutionCoefficient()), false);
                                        return 1;
                                    })
                            )
                        )
                )
                .then(Commands.literal("isBall")
                        .then(Commands.literal("set")
                            .then(Commands.argument("targets", EntityArgument.players())
                                    .then(Commands.argument("flag", BoolArgumentType.bool())
                                            .executes(context -> {
                                                boolean flag = BoolArgumentType.getBool(context, "flag");
                                                Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                                for (ServerPlayer player : players) {
                                                    ((ICustomPlayerData) player).sphericalPlayerMod$setFlag(flag);
                                                }
                                                ChatFormatting flagColor = flag ? ChatFormatting.AQUA : ChatFormatting.RED;
                                                context.getSource().sendSuccess(()->Component.literal(ChatFormatting.GOLD+"[SPM] "+ChatFormatting.GREEN+"Player isBall flag set to " + flagColor + flag + ChatFormatting.GREEN + " for " + players.size() + " player(s)."), true);
                                                return 1;
                                            })
                                    )
                            )
                        )
                ));

        dispatcher.register(Commands.literal("sphericalplayer")
                .requires(source -> source.hasPermission(2))
                .then(sphericalModeCommand("on", true))
                .then(sphericalModeCommand("off", false)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> sphericalModeCommand(String name, boolean enabled) {
        return Commands.literal(name)
                .executes(context -> setSphereMode(context.getSource(), context.getSource().getPlayerOrException(), enabled))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> setSphereMode(context.getSource(),
                                EntityArgument.getPlayer(context, "target"), enabled)));
    }

    private static int setSphereMode(CommandSourceStack source, ServerPlayer player, boolean enabled) {
        ((ICustomPlayerData) player).sphericalPlayerMod$setFlag(enabled);
        String state = enabled ? "enabled" : "disabled";
        source.sendSuccess(() -> Component.literal("[SPM] Spherical mode " + state + " for "
                + player.getGameProfile().getName() + "."), true);
        return 1;
    }

}
