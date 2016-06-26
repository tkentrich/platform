package platform;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import platform.Dimension;
import platform.area.Area;
import platform.area.Space;
import platform.component.Component;
import platform.component.Player;
import platform.component.Pose;

/**
 *
 * @author richkent
 */
public class CanvasViewer extends Canvas {
    
    private BufferStrategy strategy;
    
    public CanvasViewer() {
        setIgnoreRepaint(true);
    }
    
    public CanvasViewer(Dimension size) {
        setBounds(0, 0, size.width, size.height);
        setIgnoreRepaint(true);
    }
    
    public void paintPose(PoseScene p) {
        
        if (strategy == null) {
            createBufferStrategy(2);
            strategy = getBufferStrategy();
            System.out.println(strategy.toString());
        }
        
        if (strategy == null) {
            return;
        }
        Dimension totalSize = new Dimension(getWidth(), getHeight());
        
        Dimension coord;
        // coord = new Dimension(start);
        if (strategy == null) {
            try {
                createBufferStrategy(2);
                strategy = getBufferStrategy();
            } catch (IllegalStateException ise) {
                System.out.println("Still unable to set canvas/strategy");
                return;
            }
        }
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        
        if (g == null) {
            System.out.println("Graphics g is null....");
        } else {
            try {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, totalSize.x(), totalSize.y());
                
                p.editing.display(g, new Dimension(0));
                p.display.display(g, new Dimension(0));
                
                g.setColor(Color.WHITE);
                int y = 350;
                
                g.dispose();
                strategy.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void paintArea(Area a) {
        if (a == null || !a.initialized()) {
            return;
        }
        if (strategy == null) {
            createBufferStrategy(2);
            strategy = getBufferStrategy();
            System.out.println(strategy.toString());
        }
        
        if (strategy == null) {
            return;
        }
        Dimension totalSize = new Dimension(getWidth(), getHeight());
        
        Dimension coord;
        // coord = new Dimension(start);
        if (strategy == null) {
            try {
                createBufferStrategy(2);
                strategy = getBufferStrategy();
            } catch (IllegalStateException ise) {
                System.out.println("Still unable to set canvas/strategy");
                return;
            }
        }
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        
        if (g == null) {
            System.out.println("Graphics g is null....");
        } else {
            try {
                g.fillRect(0, 0, totalSize.x(), totalSize.y());
                //TODO: Background pictures
                
                Dimension start = a.player().position().plus(a.player().size().dividedBy(2)).minus(totalSize.dividedBy(2));
                Dimension spaceStart = start.dividedBy(Platform.spaceSize);
                Dimension spaceEnd = start.plus(totalSize).dividedBy(Platform.spaceSize);
                
                ArrayList<Component> comps = new ArrayList();
                for (int x = spaceStart.x(); x <= spaceEnd.x(); x++) {
                    for (int y = spaceStart.y(); y <= spaceEnd.y(); y++) {
                        Space space = a.space(new Dimension(x, y));
                        if (space != null) {
                            for (Component c : space.components()) {
                                if (!comps.contains(c)) {
                                    comps.add(c);
                                }
                            }
                        }
                    }
                }
                
                for (Component c : comps) {
                    c.display(g, start);
                }
                
                String strScore = String.format("SCORE %06d", a.score());
                g.setColor(Color.BLACK);
                g.drawString(strScore, 22, 22);
                
                g.setColor(Color.WHITE);
                g.drawString(strScore, 20, 20);
                
                g.dispose();
                strategy.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
