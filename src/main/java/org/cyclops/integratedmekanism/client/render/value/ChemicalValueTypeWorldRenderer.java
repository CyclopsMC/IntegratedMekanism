package org.cyclops.integratedmekanism.client.render.value;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.integrateddynamics.api.client.render.valuetype.IValueTypeWorldRenderer;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integratedmekanism.value.ValueObjectTypeChemicalStack;
import org.joml.Matrix4f;

/**
 * A value type world renderer for chemicals.
 * @author rubensworks
 */
public class ChemicalValueTypeWorldRenderer implements IValueTypeWorldRenderer {

    @Override
    public void renderValue(BlockEntityRendererProvider.Context context, IPartContainer partContainer,
                            Direction direction, IPartType partType, IValue value, float partialTicks,
                            PoseStack matrixStack, MultiBufferSource renderTypeBuffer,
                            int combinedLight, int combinedOverlay, float alpha) {
        ChemicalStack<?> chemicalStack = ((ValueObjectTypeChemicalStack.ValueChemicalStack) value).getRawValue();
        if (!chemicalStack.isEmpty()) {
            Chemical<?> chemical = chemicalStack.getType();
            int brightness = combinedLight;
            int l2 = brightness >> 0x10 & 0xFFFF;
            int i3 = brightness & 0xFFFF;

            // Chemical
            matrixStack.pushPose();
            TextureAtlasSprite icon = MekanismRenderer.getSprite(chemical.getIcon());
            Triple<Float, Float, Float> color = Helpers.intToRGB(chemical.getTint());

            VertexConsumer vb = renderTypeBuffer.getBuffer(RenderType.text(icon.atlasLocation()));
            Matrix4f matrix = matrixStack.last().pose();

            float min = 0F;
            float max = 12.5F;
            float u1 = icon.getU0();
            float u2 = icon.getU1();
            float v1 = icon.getV0();
            float v2 = icon.getV1();
            vb.vertex(matrix, max, max, 0).color(color.getLeft(), color.getMiddle(), color.getRight(), alpha).uv(u2, v2).uv2(l2, i3).endVertex();
            vb.vertex(matrix, max, min, 0).color(color.getLeft(), color.getMiddle(), color.getRight(), alpha).uv(u2, v1).uv2(l2, i3).endVertex();
            vb.vertex(matrix, min, min, 0).color(color.getLeft(), color.getMiddle(), color.getRight(), alpha).uv(u1, v1).uv2(l2, i3).endVertex();
            vb.vertex(matrix, min, max, 0).color(color.getLeft(), color.getMiddle(), color.getRight(), alpha).uv(u1, v2).uv2(l2, i3).endVertex();

            // Stack size
            matrixStack.translate(7F, 8.5F, 0.1F);
            String string = String.valueOf(chemicalStack.getAmount());
            float scale = ((float) 5) / (float) context.getFont().width(string);
            matrixStack.scale(scale, scale, 1F);
            context.getFont().drawInBatch(string,
                    0, 0, Helpers.RGBAToInt(200, 200, 200, (int) (alpha * 255F)),
                    false, matrixStack.last().pose(), renderTypeBuffer, Font.DisplayMode.NORMAL, 0, combinedLight);
            matrixStack.popPose();
        }
    }

}
