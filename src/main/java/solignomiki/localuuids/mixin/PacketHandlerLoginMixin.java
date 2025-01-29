package solignomiki.localuuids.mixin;

import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.command.commands.CommandWhitelist;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import solignomiki.localuuids.LocalUUIDs;

import java.util.UUID;

@Mixin(value = PacketHandlerLogin.class, remap = false)
abstract class PacketHandlerLoginMixin extends PacketHandler {
	@Final
	@Mutable
	@Shadow
	private MinecraftServer mcServer;

	@Inject(
		method = "doLogin(Lnet/minecraft/core/net/packet/PacketLogin;)V",
		at = @At("HEAD")
	)
	private void onHandleLoginPacket(PacketLogin loginPacket, CallbackInfo ci) {
		String uuid = LocalUUIDs.DB_MANAGER.findPlayerUUID(loginPacket.username);

		if (uuid == null) {
			if (!mcServer.propertyManager.getBooleanProperty("white-list", false)) {
				uuid = UUID.randomUUID().toString();
				LocalUUIDs.DB_MANAGER.addPlayer(loginPacket.username, uuid);
				LocalUUIDs.DB_MANAGER.saveDb();
				LocalUUIDs.DB_MANAGER.reloadDb();
				loginPacket.uuid = UUID.fromString(uuid);
			}
		} else {
			loginPacket.uuid = UUID.fromString(uuid);
		}
	}
}
