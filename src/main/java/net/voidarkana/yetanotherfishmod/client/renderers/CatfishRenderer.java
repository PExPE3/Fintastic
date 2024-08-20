package net.voidarkana.yetanotherfishmod.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.voidarkana.yetanotherfishmod.YetAnotherFishMod;
import net.voidarkana.yetanotherfishmod.client.models.CatfishModel;
import net.voidarkana.yetanotherfishmod.client.models.FeatherbackModel;
import net.voidarkana.yetanotherfishmod.common.entity.custom.CatfishEntity;
import net.voidarkana.yetanotherfishmod.common.entity.custom.FeatherbackEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CatfishRenderer extends GeoEntityRenderer<CatfishEntity> {
    public CatfishRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CatfishModel());
    }

    @Override
    public ResourceLocation getTextureLocation(CatfishEntity catfish) {
        return switch (catfish.getVariant()){
            case 1 -> new ResourceLocation(YetAnotherFishMod.MOD_ID, "textures/entity/catfish/catfish_blue.png");
            case 2-> new ResourceLocation(YetAnotherFishMod.MOD_ID, "textures/entity/catfish/catfish_big.png");
            case 3 -> new ResourceLocation(YetAnotherFishMod.MOD_ID, "textures/entity/catfish/catfish_pangasius.png");
            default -> new ResourceLocation(YetAnotherFishMod.MOD_ID, "textures/entity/catfish/catfish_piraiba.png");
        };
    }

    @Override
    public void render(CatfishEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLightIn) {
        if(entity.isBaby()) {
            poseStack.scale(0.6F, 0.6F, 0.6F);
        }
        else {
            poseStack.scale(1.0F, 1.0F, 1.0F);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
    }

    @Override
    protected void applyRotations(CatfishEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (animatable.isInWater()){
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, -animatable.prevTilt, -animatable.tilt)));
        }    }
}
