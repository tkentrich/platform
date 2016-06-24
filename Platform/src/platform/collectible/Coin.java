/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.collectible;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import platform.Platform;
import platform.Dimension;

public class Coin extends Collectible {

    private static ArrayList<CollectResult> collect;
    private static Dimension size;
    private static HashMap<String, BufferedImage> images;
    
    private Dimension speed;
    
    public Coin(Dimension position) {
        super(position);
        init();
    }
    public Coin(Dimension position, Dimension speed) {
        super(position);
        push(speed.times(weight()));
        System.out.println("Coin speed: " + speed);
        init();
    }
    
    public void init() {
        if (collect == null) {
            collect = new ArrayList();
            collect.add(new ScoreChange(100));
        }
        if (size == null) {
            size = new Dimension(16, 16);
        }
        if (images == null) {
            images = new HashMap();
                images = new HashMap();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                images.put("", ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Component/Coin.png")));
            } catch (IOException ex) {
            }
        
        }
    }
    @Override
    public ArrayList<CollectResult> collect() {
        return collect;
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public boolean passable() {
        return true;
    }

    @Override
    public Dimension size() {
        return size.copy();
    }

    @Override
    public BufferedImage image() {
        return images.get("");
    }

    @Override
    public int weight() {
        return 1;
    }
    
    @Override
    public int maxFallSpeed() {
        return Platform.blockSize.y() * 8;
    }
    
    @Override
    public int friction() {
        return 0;
    }

    @Override
    public double frictionFactor() {
        return 1;
    }
}
