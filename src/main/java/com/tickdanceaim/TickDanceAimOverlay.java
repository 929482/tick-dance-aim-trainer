package com.tickdanceaim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.lang.*;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;


@Singleton
class TickDanceAimOverlay extends Overlay
{
    private final TickDanceAimConfig config;
    private final Client client;
    private final TickDanceAimPlugin plugin;

    private static final int MAX_DRAW_DISTANCE = 32;
    private static final int LOCAL_TILE_SIZE = Perspective.LOCAL_TILE_SIZE;

    @Inject
    private TickDanceAimOverlay(Client client, TickDanceAimConfig config, TickDanceAimPlugin plugin)
    {
        this.config = config;
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.gameArea == null)
            return null;
        if (plugin.gameArea.distanceTo(client.getLocalPlayer().getWorldLocation()) > MAX_DRAW_DISTANCE)
            return null;

        if (config.tile1Color().getAlpha() != 0)
            worldPointRender(graphics, plugin.tile1, config.tile1Color());
        if (config.tile2Color().getAlpha() != 0)
            worldPointRender(graphics, plugin.tile2, config.tile2Color());
        if (!plugin.itemSwitches.get(plugin.activeSwitch).isEmpty())
            itemSwitchRender(graphics, plugin.tile1, plugin.itemSwitches.get(plugin.activeSwitch));
        drawAreaBorder(graphics, plugin.gameArea);
        return null;
    }

    private void worldPointRender(Graphics2D graphics, WorldPoint p, Color c)
    {
        if (p == null)
            return;
        LocalPoint lp = LocalPoint.fromWorld(client, p);
        if (lp == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        Stroke stroke = new BasicStroke(3.0f);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, c, stroke);
        }
    }

    private void drawAreaBorder(Graphics2D graphics, WorldArea a)
    {
        LocalPoint corners[] = areaToLocalPoints(a);
        if (corners == null)
            return;

        int pathX[] = new int[5];
        int pathY[] = new int[5];
        for (int i = 0; i < 4; ++i) {
            int plane = a.getPlane();
            Point p = Perspective.localToCanvas(client, corners[i], plane);
            if (p == null)
                return;
            pathX[i] = p.getX();
            pathY[i] = p.getY();
        }
        pathX[4] = pathX[0];
        pathY[4] = pathY[0];
        graphics.setColor(config.borderColor());
        Stroke stroke = new BasicStroke(1.0f);
        graphics.setStroke(stroke);
        graphics.drawPolyline(pathX, pathY, 5);
    }

    void itemSwitchRender(Graphics2D graphics, WorldPoint wp, ItemSwitch itemSwitch)
    {
        LocalPoint lp = LocalPoint.fromWorld(client, wp);
        if (lp == null || itemSwitch.icon == null) {
            return;
        }
        Point sp = Perspective.localToCanvas(client, lp, plugin.gameArea.getPlane(), 0);
        if (sp == null)
            return;

        OverlayUtil.renderImageLocation(client, graphics, lp, itemSwitch.icon, 0);
        int ticksRemaining = plugin.switchTick + config.switchRate() - plugin.tickCounter;
        if (!plugin.switchPattern.isEmpty())
            ticksRemaining = plugin.patternTicksRemaining();
        if (ticksRemaining >= 0)
            OverlayUtil.renderTextLocation(graphics, sp, String.valueOf(ticksRemaining), Color.WHITE);
    }

    LocalPoint[] areaToLocalPoints(WorldArea a)
    {
        LocalPoint corners[] = new LocalPoint[4];
        int lts = LOCAL_TILE_SIZE / 2;
        corners[0] = LocalPoint.fromWorld(client, a.getX(), a.getY());
        corners[1] = LocalPoint.fromWorld(client, a.getX() + a.getWidth() - 1, a.getY());
        corners[2] = LocalPoint.fromWorld(client, a.getX() + a.getWidth() - 1, a.getY() + a.getHeight() - 1);
        corners[3] = LocalPoint.fromWorld(client, a.getX(), a.getY() + a.getHeight() - 1);
        for (int i = 0; i < 4; ++i)
            if (corners[i] == null)
                return null;
        //fromWorlds returns the center of the tile, so we need to move them half a tile to the edge, using moveLocalPoint
        corners[0] = moveLocalPoint(corners[0], -lts, -lts);
        corners[1] = moveLocalPoint(corners[1],  lts, -lts);
        corners[2] = moveLocalPoint(corners[2],  lts,  lts);
        corners[3] = moveLocalPoint(corners[3], -lts,  lts);
        return corners;
    }

    LocalPoint moveLocalPoint(LocalPoint p, int dx, int dy)
    {
        return new LocalPoint(p.getX() + dx, p.getY() + dy);
    }
}
