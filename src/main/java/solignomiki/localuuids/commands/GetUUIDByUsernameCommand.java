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
import solignomiki.localuuids.LocalUUIDs;

public class GetUUIDByUsernameCommand implements CommandManager.CommandRegistry {
	private static final SimpleCommandExceptionType UNKNOWN_ERROR = new SimpleCommandExceptionType(() -> {
		return I18n.getInstance().translateKey("localuuids.unknown_error");
	});
	private static final SimpleCommandExceptionType UNKNOWN_USER = new SimpleCommandExceptionType(() -> {
		return I18n.getInstance().translateKey("localuuids.unknown_user");
	});


	public GetUUIDByUsernameCommand() {

	}

	public void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register((LiteralArgumentBuilder<CommandSource>) (Object) LiteralArgumentBuilder.literal("getuuidbyusername")
			.requires((c) -> {return ((ServerCommandSource) c).hasAdmin();})
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
		);
	}
}
