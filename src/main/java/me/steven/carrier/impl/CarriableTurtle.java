package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TurtleEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CarriableTurtle extends EntityCarriable<TurtleEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "turtle");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableTurtle());
    private static TurtleEntity dummyTurtle;
    private static TurtleEntityRenderer turtleRenderer;

    private CarriableTurtle() {
        super(TYPE, EntityType.TURTLE);
    }

    @Override
    public TurtleEntity getEntity() {
        if (dummyTurtle == null)
            dummyTurtle = new TurtleEntity(EntityType.TURTLE, MinecraftClient.getInstance().world);
        return dummyTurtle;
    }

    @Override
    public EntityRenderer<TurtleEntity> getEntityRenderer() {
        if (turtleRenderer == null)
            turtleRenderer = new TurtleEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return turtleRenderer;
    }


    @Override
    public void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        PlayerEntity player = (PlayerEntity) holder;
        updateEntity(holder.getHolding());
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-1.0, 1.2, 0.2);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}