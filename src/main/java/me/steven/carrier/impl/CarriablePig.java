package me.steven.carrier.impl;

import me.steven.carrier.ClientUtils;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.EntityCarriable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

public class CarriablePig extends EntityCarriable<PigEntity>  {

    public static final Identifier TYPE = new Identifier("carrier", "minecraft_pig");
    @Environment(EnvType.CLIENT)
    private static PigEntity dummyPig;
    @Environment(EnvType.CLIENT)
    private static PigEntityRenderer renderer;

    @NotNull
    @Override
    public EntityType<PigEntity> getParent() {
        return EntityType.PIG;
    }

    public CarriablePig() {
        super(TYPE, EntityType.PIG);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public PigEntity getEntity() {
        if (dummyPig == null)
            dummyPig = new PigEntity(EntityType.PIG, MinecraftClient.getInstance().world);
        return dummyPig;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public EntityRenderer<PigEntity> getEntityRenderer() {
        if (renderer == null)
            renderer = new PigEntityRenderer(ClientUtils.defaultEntityCtx());
        return renderer;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        updateEntity(carrier.getCarryingData());
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw + 90));
        matrices.translate(-0.6, 0.8, -0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }

}
