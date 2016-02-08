package de.mineformers.investiture.allomancy.impl.misting;

import de.mineformers.investiture.allomancy.api.misting.Inject;
import de.mineformers.investiture.allomancy.api.misting.Oracle;
import de.mineformers.investiture.allomancy.client.particle.FootStep;
import de.mineformers.investiture.allomancy.impl.AllomancyAPIImpl;
import de.mineformers.investiture.client.util.Rendering;
import de.mineformers.investiture.serialisation.Serialise;
import de.mineformers.investiture.util.PathFinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayDeque;
import java.util.Queue;

import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * ${JDOC}
 */
public class OracleImpl extends AbstractMisting implements Oracle, ITickable
{
    @Inject
    private Entity entity;
    @Serialise
    private int spawnDimension;
    @Serialise
    private BlockPos spawnPoint;
    private int timer;
    private Queue<BlockPos> path = new ArrayDeque<>();

    @Override
    public void startBurning()
    {
        spawnPoint = null;
        if (entity instanceof EntityPlayer)
            spawnPoint = ((EntityPlayer) entity).getBedLocation();
        if (spawnPoint == null)
            spawnPoint = entity.worldObj.getSpawnPoint();
        spawnDimension = entity.dimension;
        timer = 0;
        path.clear();
        path.addAll(PathFinding.bresenham(entity, spawnPoint));
    }

    @Override
    public void update()
    {
        if (!entity.worldObj.isRemote || path.isEmpty() || entity.dimension != spawnDimension)
            return;
        timer++;
        if (timer > 5)
            timer = 0;
        else
            return;
        BlockPos step = path.poll();
        FootStep particle1 = new FootStep(entity.worldObj, new Vec3(step).addVector(0.3, 0.0001, 0.3), 1, 0f, 0f);
        FootStep particle2 = new FootStep(entity.worldObj, new Vec3(step).addVector(0.6, 0.0001, 0.6), 1, 0f, 0f);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle1);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle2);
    }

    @Override
    public BlockPos spawnPoint()
    {
        return spawnPoint;
    }
}