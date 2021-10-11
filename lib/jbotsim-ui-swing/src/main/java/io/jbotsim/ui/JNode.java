/*
 * Copyright 2008 - 2020, Arnaud Casteigts and the JBotSim contributors <contact@jbotsim.io>
 *
 *
 * This file is part of JBotSim.
 *
 * JBotSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JBotSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JBotSim.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package io.jbotsim.ui;

import io.jbotsim.core.Link;
import io.jbotsim.core.Node;
import io.jbotsim.core.Topology;
import io.jbotsim.ui.painting.NodePainter;
import io.jbotsim.ui.painting.UIComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.nio.file.Paths;

/**
 * <p>The {@link JNode} element draws a {@link Node}, using a Swing {@link JButton}.</p>
 */
@SuppressWarnings("serial")
public class JNode extends JButton implements MouseListener, MouseMotionListener, MouseWheelListener {
    protected Image icon;
    protected Image scaledIcon;
    protected Integer drawSize;
    protected double zcoord = -1;
    protected Node node;
    public static double camheight = 200;
    protected static Node destination = null;
    protected static Integer currentButton = 1;


    public JNode(Node node) {
        this.node = node;
        this.setToolTipText(Integer.toString(node.getID()));
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setContentAreaFilled(false);
        setBorderPainted(false);
        updateIcon();
        update();
    }

    public Node getNode(){
        return node;
    }

    public void updateIcon() {
        String path = (String) node.getProperty("icon");
        try {
            URL iconUrl = getClass().getResource(path);
            if (iconUrl != null)
                icon = Toolkit.getDefaultToolkit().getImage(iconUrl);
            else
                icon = ImageIO.read(Paths.get(path).toUri().toURL());
        } catch (Exception e) {
            if (node.hasProperty("icon")) {
                System.err.println("Unable to set icon: " + path);
                System.err.println(e.getMessage());
            }
            setDefaultIcon();
            return;
        }
        updateIconSize();
    }

    private void setDefaultIcon() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        icon = tk.getImage(getClass().getResource("/io/jbotsim/ui/icons/default-node-icon.png"));
        updateIconSize();
    }

    public void updateIconSize() {
        drawSize = (int) (node.getIconSize() * camheight / (camheight - node.getZ()));
        scaledIcon = icon.getScaledInstance(drawSize * 2, drawSize * 2, Image.SCALE_DEFAULT);
        setIcon(new ImageIcon(scaledIcon));
        setBounds((int) node.getX() - drawSize, (int) node.getY() - drawSize, drawSize * 2, drawSize * 2);
    }

    public void update() {
        if (node.getZ() != zcoord) {
            zcoord = node.getZ();
            updateIconSize();
        }
        setBounds((int) node.getX() - drawSize, (int) node.getY() - drawSize, drawSize * 2, drawSize * 2);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        double direction = this.node.getDirection();
        if(direction != Node.DEFAULT_DIRECTION) {
            AffineTransform newXform = g2d.getTransform();
            newXform.rotate(direction - Node.DEFAULT_DIRECTION, drawSize, drawSize);
            g2d.setTransform(newXform);
        }
        g2d.drawImage(scaledIcon, 0, 0, null);
        JTopology jTopology = (JTopology) this.getParent();
        UIComponent uic = new UIComponent(g2d);
        for (NodePainter painter : jTopology.nodePainters)
            painter.paintNode(uic, node);
    }

    // EVENTS
    public void mousePressed(MouseEvent e) {
        Topology tp = node.getTopology();
        if (((JTopology) getParent()).handler.ctrlPressed) {
            if (e.getButton() == 1)
                tp.selectNode(node);
        } else {
            currentButton = e.getButton();
            tp.setProperty("refreshMode", tp.getRefreshMode());
            tp.setRefreshMode(Topology.RefreshMode.EVENTBASED);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (currentButton == 1)
            node.translate(e.getX() - drawSize, (int) e.getY() - drawSize);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        if (currentButton == 3) {
            destination = node;
        }
    }

    public void mouseExited(MouseEvent e) {
        destination = null;
    }

    public void mouseReleased(MouseEvent e) {
        Topology tp = node.getTopology();
        if (tp.hasProperty("refreshMode"))
            tp.setRefreshMode((Topology.RefreshMode) tp.getProperty("refreshMode"));
        if (destination != null) {
            tp.addLink(new Link(node, destination, tp.getOrientation()));
            destination = null;
        } else {
            if (e.getButton() == MouseEvent.BUTTON3)
                tp.removeNode(node);
            else if (e.getButton() == MouseEvent.BUTTON2)
                tp.selectNode(node);
        }
        currentButton = 1;
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        double z = node.getZ() - 2 * notches;
        if (z > .8 * camheight)
            z = .8 * camheight;
        if (z < 0)
            z = 0;
        node.setLocation(node.getX(), node.getY(), z);
    }

    @Override
    public JToolTip createToolTip() {
        setToolTipText(node.toString());
        return super.createToolTip();
    }
}
