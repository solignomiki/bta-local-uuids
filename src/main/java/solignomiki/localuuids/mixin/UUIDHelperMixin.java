package solignomiki.localuuids.mixin;

import net.minecraft.core.util.helper.UUIDHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import solignomiki.localuuids.LocalUUIDs;
import solignomiki.localuuids.dbmanagers.JsonDatabaseManager;

import java.util.UUID;

@Mixin(value = UUIDHelper.class, remap = false)
abstract class UUIDHelperMixin {

	@Inject(
		method = "getUUIDFromName(Ljava/lang/String;)Ljava/util/UUID;",
		at = @At("HEAD"),
		cancellable = true
	)
	private static @Nullable UUID getUUIDFromName(String username, CallbackInfoReturnable cir) {

		String uuidString = LocalUUIDs.DB_MANAGER.findPlayerUUID(username);
		if (uuidString == null) {
			uuidString = UUID.randomUUID().toString().toString();
			LocalUUIDs.DB_MANAGER.putPlayer(username, uuidString);
			if (LocalUUIDs.DB_MANAGER instanceof JsonDatabaseManager) {
				((JsonDatabaseManager) LocalUUIDs.DB_MANAGER).saveDb();
				((JsonDatabaseManager) LocalUUIDs.DB_MANAGER).reloadDb();
			}
		}
		UUID uuid = UUID.fromString(uuidString);
		cir.setReturnValue(uuid);
		return uuid;
	}
}
