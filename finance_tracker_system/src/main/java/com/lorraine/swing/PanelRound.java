/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lorraine.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author seany
 */
public class PanelRound extends JPanel {

    // -------------------------------------------------------------------------
    // Round corner fields
    // -------------------------------------------------------------------------
    private boolean roundEnabled    = true;
    private int     roundTopLeft    = 20;
    private int     roundTopRight   = 20;
    private int     roundBottomLeft = 20;
    private int     roundBottomRight= 20;

    // -------------------------------------------------------------------------
    // Shadow fields
    // -------------------------------------------------------------------------
    private boolean    shadowEnabled  = true;
    private ShadowType shadowType     = ShadowType.CENTER;
    private int        shadowSize     = 10;
    private float      shadowOpacity  = 0.4f;
    private Color      shadowColor    = Color.BLACK;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public PanelRound() {
        setOpaque(false);
        applyBorder();
    }

    // -------------------------------------------------------------------------
    // Border: reserves space for the shadow so it is never clipped
    // -------------------------------------------------------------------------
    private void applyBorder() {
        if (shadowEnabled) {
            int s = shadowSize;
            // Which sides need padding depends on shadow direction
            int top    = needsPad(shadowType, Side.TOP)    ? s : 0;
            int left   = needsPad(shadowType, Side.LEFT)   ? s : 0;
            int bottom = needsPad(shadowType, Side.BOTTOM) ? s : 0;
            int right  = needsPad(shadowType, Side.RIGHT)  ? s : 0;
            setBorder(new EmptyBorder(top, left, bottom, right));
        } else {
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }
    }

    private enum Side { TOP, LEFT, BOTTOM, RIGHT }

    private boolean needsPad(ShadowType type, Side side) {
        return switch (type) {
            case TOP -> side == Side.TOP;
            case BOT -> side == Side.BOTTOM;
            case TOP_LEFT -> side == Side.TOP  || side == Side.LEFT;
            case TOP_RIGHT -> side == Side.TOP  || side == Side.RIGHT;
            case BOT_LEFT -> side == Side.BOTTOM || side == Side.LEFT;
            case BOT_RIGHT -> side == Side.BOTTOM || side == Side.RIGHT;
            default -> true;
        }; // CENTER — all sides
    }

    // -------------------------------------------------------------------------
    // Paint
    // -------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics grphcs) {
        if (shadowEnabled) {
            paintWithShadow(grphcs);
        } else {
            paintNoShadow(grphcs);
        }
        super.paintComponent(grphcs);
    }

    /**
     * Replicates the proven approach from PanelShadow:
     *  1. Paint the panel shape into a BufferedImage.
     *  2. Pass that image to ShadowRenderer to get a blurred shadow image.
     *  3. Draw shadow image first, then the panel image on top.
     */
    private void paintWithShadow(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;

        int size   = shadowSize * 2;
        int imgW   = getWidth()  - size;
        int imgH   = getHeight() - size;

        if (imgW <= 0 || imgH <= 0) return;

        // Panel image offset (where the actual panel face sits)
        int px, py;
        switch (shadowType) {
            case TOP -> {
                px = shadowSize; py = size;
            }
            case BOT -> {
                px = shadowSize; py = 0;
            }
            case TOP_LEFT -> {
                px = size;       py = size;
            }
            case TOP_RIGHT -> {
                px = 0;          py = size;
            }
            case BOT_LEFT -> {
                px = size;       py = 0;
            }
            case BOT_RIGHT -> {
                px = 0;          py = 0;
            }
            default -> {
                px = shadowSize;
                py = shadowSize;
                // CENTER
            }
        }

        // 1. Draw panel shape into a BufferedImage
        BufferedImage panelImg = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gi = panelImg.createGraphics();
        gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gi.setColor(getBackground());
        if (roundEnabled) {
            gi.fill(buildRoundShape(0, 0, imgW, imgH));
        } else {
            gi.fillRect(0, 0, imgW, imgH);
        }
        gi.dispose();

        // 2. Generate blurred shadow from that image
        ShadowRenderer renderer = new ShadowRenderer(shadowSize, shadowOpacity, shadowColor);
        BufferedImage shadowImg = renderer.createShadow(panelImg);

        // 3. Draw shadow then panel face
        g2.drawImage(shadowImg, 0, 0, null);
        g2.drawImage(panelImg, px, py, null);
    }

    /** Paint with rounded corners only, no shadow. */
    private void paintNoShadow(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        if (roundEnabled) {
            g2.fill(buildRoundShape(0, 0, getWidth(), getHeight()));
        } else {
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }

    // -------------------------------------------------------------------------
    // Rounded shape — each corner radius is independent
    // -------------------------------------------------------------------------
    private Shape buildRoundShape(int x, int y, int width, int height) {
        Area area;
        if (roundTopLeft > 0) {
            area = new Area(makeCorner(x, y, width, height, roundTopLeft, true, true));
        } else {
            area = new Area(new Rectangle2D.Double(x, y, width, height));
        }
        if (roundTopRight > 0) {
            area.intersect(new Area(makeCorner(x, y, width, height, roundTopRight, false, true)));
        }
        if (roundBottomLeft > 0) {
            area.intersect(new Area(makeCorner(x, y, width, height, roundBottomLeft, true, false)));
        }
        if (roundBottomRight > 0) {
            area.intersect(new Area(makeCorner(x, y, width, height, roundBottomRight, false, false)));
        }
        return area;
    }

    private Shape makeCorner(int x, int y, int w, int h,
                             int radius, boolean isLeft, boolean isTop) {
        int rx = Math.min(w, radius);
        int ry = Math.min(h, radius);
        Area area = new Area(new RoundRectangle2D.Double(x, y, w, h, rx, ry));
        // Fill back the sharp sides we don't want rounded
        if (isLeft) {
            area.add(new Area(new Rectangle2D.Double(x + rx / 2.0, y, w - rx / 2.0, h)));
        } else {
            area.add(new Area(new Rectangle2D.Double(x, y, w - rx / 2.0, h)));
        }
        if (isTop) {
            area.add(new Area(new Rectangle2D.Double(x, y + ry / 2.0, w, h - ry / 2.0)));
        } else {
            area.add(new Area(new Rectangle2D.Double(x, y, w, h - ry / 2.0)));
        }
        return area;
    }

    // =========================================================================
    // Bean properties — getters & setters (NetBeans reads these)
    // =========================================================================

    // --- Round ---------------------------
    public boolean isRoundEnabled() { return roundEnabled; }
    public void setRoundEnabled(boolean roundEnabled) {
        this.roundEnabled = roundEnabled;
        repaint();
    }

    public int getRoundTopLeft() { return roundTopLeft; }
    public void setRoundTopLeft(int roundTopLeft) {
        this.roundTopLeft = roundTopLeft;
        repaint();
    }

    public int getRoundTopRight() { return roundTopRight; }
    public void setRoundTopRight(int roundTopRight) {
        this.roundTopRight = roundTopRight;
        repaint();
    }

    public int getRoundBottomLeft() { return roundBottomLeft; }
    public void setRoundBottomLeft(int roundBottomLeft) {
        this.roundBottomLeft = roundBottomLeft;
        repaint();
    }

    public int getRoundBottomRight() { return roundBottomRight; }
    public void setRoundBottomRight(int roundBottomRight) {
        this.roundBottomRight = roundBottomRight;
        repaint();
    }

    /** Convenience: set all four corners to the same radius.
     * @param radius */
    public void setRoundAll(int radius) {
        this.roundTopLeft     = radius;
        this.roundTopRight    = radius;
        this.roundBottomLeft  = radius;
        this.roundBottomRight = radius;
        repaint();
    }

    // --- Shadow --------------------------
    public boolean isShadowEnabled() { return shadowEnabled; }
    public void setShadowEnabled(boolean shadowEnabled) {
        this.shadowEnabled = shadowEnabled;
        applyBorder();
        repaint();
    }

    public ShadowType getShadowType() { return shadowType; }
    public void setShadowType(ShadowType shadowType) {
        this.shadowType = shadowType;
        applyBorder();
        repaint();
    }

    public int getShadowSize() { return shadowSize; }
    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        applyBorder();
        repaint();
    }

    public float getShadowOpacity() { return shadowOpacity; }
    public void setShadowOpacity(float shadowOpacity) {
        this.shadowOpacity = shadowOpacity;
        repaint();
    }

    public Color getShadowColor() { return shadowColor; }
    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        repaint();
    }
}
