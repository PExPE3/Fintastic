package net.voidarkana.yetanotherfishmod.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.voidarkana.yetanotherfishmod.YetAnotherFishMod;
import net.voidarkana.yetanotherfishmod.common.entity.custom.GuppyEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GuppyTail extends GeoRenderLayer<GuppyEntity> {

    public GuppyTail(GeoRenderer<GuppyEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, GuppyEntity entity, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (!entity.isInvisible() && !entity.isBaby()) {
            RenderType cameo = RenderType.entityCutoutNoCull(new ResourceLocation(YetAnotherFishMod.MOD_ID,
                    "textures/entity/guppy/tail/"+entity.getTailName(entity.getTailModel())
                            +"/guppy_tail_"+entity.getTailName(entity.getTailModel())+"_"+entity.getTailColor()+".png"));

            ResourceLocation trilobiteModel = new ResourceLocation(YetAnotherFishMod.MOD_ID, "geo/guppy.geo.json");

            this.getRenderer().reRender(this.getGeoModel().getBakedModel(trilobiteModel), poseStack, bufferSource, entity, renderType,
                    bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
