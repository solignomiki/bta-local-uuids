package solignomiki.localuuids.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.server.net.command.ServerCommandSource;
import net.minecraft.server.net.command.commands.CommandWhitelist;
import solignomiki.localuuids.LocalUUIDs;
import solignomiki.localuuids.dbmanagers.JsonDatabaseManager;

import java.util.List;

public class UUIDCommand implements CommandManager.CommandRegistry {
	private static final SimpleCommandExceptionType UNKNOWN_ERROR = new SimpleCommandExceptionType(() -> {
		return I18n.getInstance().translateKey("localuuids.errors.unknown_error");
	});
	private static final SimpleCommandExceptionType UNKNOWN_USER = new SimpleCommandExceptionType(() -> {
		return I18n.getInstance().translateKey("localuuids.errors.unknown_user");
	});
	private static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(() -> {
		return I18n.getInstance().translateKey("localuuids.errors.invalid_uuid");
	});

	public UUIDCommand() {

	}

	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder<CommandSource>) (Object) LiteralArgumentBuilder.literal("uuid")
			.requires((c) -> {return ((ServerCommandSource) c).hasAdmin();})
			.then(LiteralArgumentBuilder.literal("tie")
				.then(RequiredArgumentBuilder.argument("username", StringArgumentType.word())
					.then(RequiredArgumentBuilder.argument("uuid", StringArgumentType.word())
						.executes((c) -> {
							CommandSource source = (CommandSource)c.getSource();
							Player player = source.getSender();
							String username = c.getArgument("username", String.class);
							String uuid = c.getArgument("uuid", String.class);
							try {
								LocalUUIDs.DB_MANAGER.putPlayer(username, uuid);
							} catch (IllegalArgumentException e) {
								throw INVALID_UUID.create();
							}
							if (LocalUUIDs.DB_MANAGER instanceof JsonDatabaseManager) {
								((JsonDatabaseManager) LocalUUIDs.DB_MANAGER).saveDb();
								((JsonDatabaseManager) LocalUUIDs.DB_MANAGER).reloadDb();
							}
							player.sendMessage(I18n.getInstance().translateKeyAndFormat("localuuids.tied_username_to_uuid", username, uuid));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			)
			.then(LiteralArgumentBuilder.literal("get")
				.then(RequiredArgumentBuilder.argument("username", StringArgumentType.word())
					.executes((c) -> {
						CommandSource source = (CommandSource)c.getSource();
						Player player = source.getSender();
						String username = c.getArgument("username", String.class);
						String uuid = LocalUUIDs.DB_MANAGER.findPlayerUUID(username);
						if (uuid == null) {
							throw UNKNOWN_USER.create();
						}
						player.sendMessage(uuid);
						return Command.SINGLE_SUCCESS;
					})
				)
			)
			.then(LiteralArgumentBuilder.literal("getusernames")
				.then(RequiredArgumentBuilder.argument("uuid", StringArgumentType.word())
					.executes((c) -> {
						CommandSource source = (CommandSource)c.getSource();
						Player player = source.getSender();
						String uuid = c.getArgument("uuid", String.class);
						List<String> usernames = LocalUUIDs.DB_MANAGER.findPlayerUsernames(uuid);
						if (uuid == null) {
							throw UNKNOWN_USER.create();
						}
						String str = "";
						for (String username : usernames) {
							player.sendMessage(username);
						}
						return Command.SINGLE_SUCCESS;
					})
				)
			)
			.then(LiteralArgumentBuilder.literal("remove")
				.then(LiteralArgumentBuilder.literal("byusername")
					.then(RequiredArgumentBuilder.argument("username", StringArgumentType.word())
						.executes((c) -> {
							CommandSource source = (CommandSource)c.getSource();
							Player player = source.getSender();
							String username = c.getArgument("username", String.class);
							try {
								LocalUUIDs.DB_MANAGER.removePlayerFromDatabaseByUsername(username);
							} catch (IllegalArgumentException e) {
								throw UNKNOWN_USER.create();
							}
							player.sendMessage(I18n.getInstance().translateKeyAndFormat("localuuids.removed_player", username));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
				.then(LiteralArgumentBuilder.literal("byuuid")
					.then(RequiredArgumentBuilder.argument("uuid", StringArgumentType.word())
						.executes((c) -> {
							CommandSource source = (CommandSource)c.getSource();
							Player player = source.getSender();
							String uuid = c.getArgument("uuid", String.class);
							try {
								LocalUUIDs.DB_MANAGER.removePlayerFromDatabaseByUUID(uuid);
							} catch (IllegalArgumentException e) {
								throw UNKNOWN_USER.create();
							}
							player.sendMessage(I18n.getInstance().translateKeyAndFormat("localuuids.removed_player", uuid));
							return Command.SINGLE_SUCCESS;
						})
					)
				)
			)
		);
	}

}
