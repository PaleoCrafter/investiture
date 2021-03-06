package de.mineformers.investiture.client.util;

import com.google.common.base.Throwables;
import de.mineformers.investiture.util.Reflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import java.lang.invoke.MethodHandle;

import static java.lang.Math.*;
import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Provides utility methods for rendering.
 */
public class Rendering
{
    private static final MethodHandle VANILLA_CAMERA_TRANSFORMS;

    static
    {
        VANILLA_CAMERA_TRANSFORMS = Reflection.methodHandle(EntityRenderer.class, void.class)
                                              .srgName("func_78479_a")
                                              .parameterType(float.class)
                                              .parameterType(int.class)
                                              .build();
    }

    public static void setupCameraTransforms(float partialTicks, int pass)
    {
        try
        {
            VANILLA_CAMERA_TRANSFORMS.bindTo(Minecraft.getMinecraft().entityRenderer).invokeExact(partialTicks, pass);
        }
        catch (Throwable throwable)
        {
            Throwables.propagate(throwable);
        }
    }

    /**
     * Draws a rectangle to the screen.
     *
     * @param x      the rectangle's upper left-hand corner's x coordinate
     * @param y      the rectangle's upper left-hand corner's y coordinate
     * @param uMin   the u (horizontal) coordinate of the rectangle's upper left-hand corner in the texture
     * @param vMin   the v (vertical) coordinate of the rectangle's lower right-hand corner in the texture
     * @param uMax   the u (horizontal) coordinate of the rectangle's upper left-hand corner in the texture
     * @param vMax   the v (vertical) coordinate of the rectangle's lower right-hand corner in the texture
     * @param width  the rectangle's width on screen
     * @param height the rectangle's height on screen
     */
    public static void drawRectangle(int x, int y, float uMin, float vMin, float uMax, float vMax, int width, int height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder wr = tessellator.getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.pos(x, y + height, 0)
          .tex(uMin, vMax)
          .endVertex();
        wr.pos(x + width, y + height, 0)
          .tex(uMax, vMax)
          .endVertex();
        wr.pos(x + width, y, 0)
          .tex(uMax, vMin)
          .endVertex();
        wr.pos(x, y, 0)
          .tex(uMin, vMin)
          .endVertex();
        tessellator.draw();
    }

    public static void drawRing(int centreX, int centreY, int innerRadius, int width, int accuracy, double startAngle, Colour colour)
    {
        drawRing(centreX, centreY, innerRadius, width, accuracy, startAngle, colour, colour);
    }

    public static void drawRing(int centreX, int centreY, int innerRadius, int width, int accuracy, double startAngle,
                                Colour innerColour, Colour outerColour)
    {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder wr = tess.getBuffer();
        glPointSize(5);
        double step = Math.PI / accuracy * 2;
        double halfStep = Math.PI / accuracy;
        startAngle = toRadians(startAngle);
        wr.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(centreX + cos(-halfStep + startAngle) * innerRadius, centreY + sin(-halfStep + startAngle) * innerRadius, 0)
          .color(innerColour.r(), innerColour.g(), innerColour.b(), innerColour.a())
          .endVertex();
        for (int i = 0; i < accuracy; i++)
        {
            double angle = i * step + halfStep + startAngle;
            wr.pos(centreX + cos(angle) * innerRadius, centreY + sin(angle) * innerRadius, 0)
              .color(innerColour.r(), innerColour.g(), innerColour.b(), innerColour.a())
              .endVertex();
            wr.pos(centreX + cos(angle) * (innerRadius + width), centreY + sin(angle) * (innerRadius + width), 0)
              .color(outerColour.r(), outerColour.g(), outerColour.b(), outerColour.a())
              .endVertex();
        }
        wr.pos(centreX + cos(halfStep + startAngle) * (innerRadius + width), centreY + sin(halfStep + startAngle) * (innerRadius + width), 0)
          .color(outerColour.r(), outerColour.g(), outerColour.b(), outerColour.a())
          .endVertex();
        tess.draw();
    }

    public static void drawRingQuarter(int centreX, int centreY, int innerRadius, int width, int accuracy, int quarter,
                                       Colour innerColour, Colour outerColour)
    {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder wr = tess.getBuffer();
        glPointSize(5);
        double step = Math.PI / accuracy * 2;
        double halfStep = Math.PI / accuracy;
        double startAngle = Math.PI / 2 * quarter;
        double endAngle = startAngle + Math.PI / 2;
        wr.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(centreX + cos(startAngle - (quarter % 2 == 0 ? halfStep : 0)) * (innerRadius + width),
               centreY + sin(startAngle - (quarter % 2 == 1 ? halfStep : 0)) * (innerRadius + width), 0)
          .color(outerColour.r(), outerColour.g(), outerColour.b(), outerColour.a())
          .endVertex();
        wr.pos(centreX + cos(startAngle - (quarter % 2 == 0 ? halfStep : 0)) * innerRadius,
               centreY + sin(startAngle - (quarter % 2 == 1 ? halfStep : 0)) * innerRadius, 0)
          .color(innerColour.r(), innerColour.g(), innerColour.b(), innerColour.a())
          .endVertex();
        for (int i = 0; i < accuracy / 4; i++)
        {
            double angle = i * step + halfStep + startAngle;
            wr.pos(centreX + cos(angle) * (innerRadius + width), centreY + sin(angle) * (innerRadius + width), 0)
              .color(outerColour.r(), outerColour.g(), outerColour.b(), outerColour.a())
              .endVertex();
            wr.pos(centreX + cos(angle) * innerRadius, centreY + sin(angle) * innerRadius, 0)
              .color(innerColour.r(), innerColour.g(), innerColour.b(), innerColour.a())
              .endVertex();
        }
        wr.pos(centreX + cos(endAngle - (quarter % 2 == 1 ? halfStep : 0)) * (innerRadius + width),
               centreY + sin(endAngle - (quarter % 2 == 0 ? halfStep : 0)) * (innerRadius + width), 0)
          .color(outerColour.r(), outerColour.g(), outerColour.b(), outerColour.a())
          .endVertex();
        wr.pos(centreX + cos(endAngle - (quarter % 2 == 1 ? halfStep : 0)) * innerRadius,
               centreY + sin(endAngle - (quarter % 2 == 0 ? halfStep : 0)) * innerRadius, 0)
          .color(innerColour.r(), innerColour.g(), innerColour.b(), innerColour.a())
          .endVertex();
        tess.draw();
    }

    /**
     * Renders a baked model into the world. Transformations should be applied before calling this method.
     *
     * @param model the model to draw
     */
    public static void drawModel(IBakedModel model)
    {
        drawModel(model, 0xFFFFFFFF);
    }

    /**
     * Renders a tinted baked model into the world. Transformations should be applied before calling this method.
     *
     * @param model  the model to draw
     * @param colour the tint that should be applied to the model, format is 0xAABBGGRR
     */
    public static void drawModel(IBakedModel model, int colour)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();

        pushAttrib();
        RenderHelper.disableStandardItemLighting();
        if (Minecraft.isAmbientOcclusionEnabled())
        {
            shadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            shadeModel(GL11.GL_FLAT);
        }
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        for (BakedQuad bakedquad : model.getQuads(null, null, 0))
        {
            LightUtil.renderQuadColor(worldrenderer, bakedquad, colour);
        }
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        popAttrib();
    }

    public static Vec3d interpolatedPosition(Entity entity, float partialTicks)
    {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        return new Vec3d(x, y, z);
    }

    public static void drawFacingQuad(float scale)
    {
        pushMatrix();
        rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tessellator.getBuffer().pos(-scale, -scale, 0).tex(0, 0).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.getBuffer().pos(-scale, scale, 0).tex(0, 1).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.getBuffer().pos(scale, scale, 0).tex(1, 1).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.getBuffer().pos(scale, -scale, 0).tex(1, 0).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        popMatrix();
    }
}
