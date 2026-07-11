package com.jamiedev.bygone.core.mixin.blockPhasing;

import com.jamiedev.bygone.common.entity.BlockPhasingEntity;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class BlockPhasingEntityMixin {

	@Redirect(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;noPhysics:Z", opcode = Opcodes.GETFIELD))
	private boolean injected(Entity entity) {
		if (entity instanceof BlockPhasingEntity phasing && phasing.isPhasing()) return false;
		return entity.noPhysics;
	}

}
