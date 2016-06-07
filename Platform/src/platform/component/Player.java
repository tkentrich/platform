package platform.component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import platform.Dimension;
import platform.Platform;
import static java.lang.Math.PI;
import platform.PlayerCommand;

/**
 *
 * @author richkent
 */
public class Player extends Component {

    enum PlayerStatus { STAND, WALK, JUMP };
    enum PlayerFacing { LEFT, RIGHT };
    private PlayerStatus status;
    private PlayerFacing facing;
    private int jumpsRemaining;
    private int jumpForceRemaining;
    
    private boolean kb_left, kb_right, kb_up, kb_down, kb_jump, kb_fire, kb_run;
        
    private static HashMap<String, BufferedImage> images;

    public class Pose {
        public double neckTheta;       // {-PI/2..PI/2} 0 = straight up
        public double frontShldrTheta; // {-PI  ..PI  } 0 = straight ahead
        public double frontElbowTheta; // {-PI  ..PI  } 0 = straight with Shoulder
        public double backShldrTheta;  // {-PI  ..PI  } 0 = straight ahead
        public double backElbowTheta;  // {-PI  ..PI  } 0 = straight with Shoulder
        public double frontHipTheta;   // {-PI/3..PI/3} 0 = straight down
        public double frontKneeTheta;  // {0    ..PI/2} 0 = straight with Hip
        public double backHipTheta;    // {-PI/3..PI/3} 0 = straight down
        public double backKneeTheta;   // {0    ..PI/2} 0 = straight with Hip
        public int millis;     // milliseconds until target should be reached
        public Pose(double neck, double frShldr, double frElbow, double bkShldr, double bkElbow, double frHip, double frKnee, double bkHip, double bkKnee, int ms) {
            neckTheta = neck;
            frontShldrTheta = frShldr;
            frontElbowTheta = frElbow;
            backShldrTheta = bkShldr;
            backElbowTheta = bkElbow;
            frontHipTheta = frHip;
            frontKneeTheta = frKnee;
            backHipTheta = bkHip;
            backKneeTheta = bkKnee;
            millis = ms;
        }
    }
    public Pose atRest() {
        return new Pose(0, 0, 0, PI, 0, PI/6, -PI/6, -PI/6, PI/6, 0);
    }
    public Pose test() {
        return new Pose(-PI/8, 0, 1, PI, -1, 1, -1, -0.5, 0, 500);
    }

    private Pose pose;
    private Pose targetPose;
    
    public Player(Dimension position) {
        super(position);
        pose = atRest();
        targetPose = atRest();
        kb_left = kb_right = kb_up = kb_down = kb_jump = kb_fire = kb_run = false;
        status = PlayerStatus.STAND;
        facing = PlayerFacing.RIGHT;
    }

    @Override
    public int maxFallSpeed() {
        return Platform.blockSize.y() * 10;
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
        return Platform.blockSize.times(3).dividedBy(3,2);
    }

    @Override
    public int weight() {
        return 100;
    }

    @Override
    public ArrayList<CollisionResult> collide(Component c, Collision.CollisionType type) {
        return null;
    }
    
    @Override
    public int friction() {
        return 0;
    }
    
    @Override
    public void standing(int actingFriction) {
        super.standing(actingFriction);
        playerStanding();
    }
    @Override
    public void standing(boolean standing) {
        super.standing(standing);
        playerStanding();
    }
    public void playerStanding() {
        jumpsRemaining = numberOfJumps();
        jumpForceRemaining = jumpForce();
    }
    
    @Override
    public void move(int ms) {
        super.move(ms);
    }
    
    public void ui(PlayerCommand comm) {
        // System.out.printf("User %s key %d%n", (comm.typed() ? "typed" : "released"), comm.event().getKeyCode());
        switch (comm.event().getKeyCode()) {
            case KeyEvent.VK_UP:
                kb_up = comm.typed();
                break;
            case KeyEvent.VK_DOWN:
                kb_down = comm.typed();
                break;
            case KeyEvent.VK_LEFT:
                kb_left = comm.typed();
                if (kb_left) {
                    kb_right = false;
                    facing = PlayerFacing.LEFT;
                }
                break;
            case KeyEvent.VK_RIGHT:
                kb_right = comm.typed();
                if (kb_right) {
                    kb_left = false;
                    facing = PlayerFacing.RIGHT;
                }
                break;
            case KeyEvent.VK_SHIFT:
                kb_run = comm.typed();
                break;
            case KeyEvent.VK_CONTROL:
                kb_fire = comm.typed();
                break;
            case KeyEvent.VK_SPACE:
                kb_jump = comm.typed();
                if (kb_jump && speed().y() == 0) {
                    switch (status) {
                        case WALK:
                        case STAND:
                            status = PlayerStatus.JUMP;
                            break;
                    }
                }
                break;
            case KeyEvent.VK_ALT:
                // ???
        }
    }
    
    public void control() {
        switch (status) {
            case JUMP:
                break;
        }
        
        int orig_speed = speed().x();
        if (kb_left) {
            push(new Dimension(-walkForce(), 0));
            if (speed().x() < -walkSpeed()) {
                speed().setX((orig_speed < -walkSpeed()) ? orig_speed : -walkSpeed());
            }
        } else if (kb_right) {
            push(new Dimension(walkForce(), 0));
            if (speed().x() > walkSpeed()) {
                speed().setX((orig_speed < walkSpeed()) ? orig_speed : walkSpeed());
            }
        }
    }
    
    @Override
    public void display(Graphics2D g_start, Dimension start) {
        Dimension middle = position().plus(size().dividedBy(2));
        Dimension chestSize = size().dividedBy(3, 4);
        Dimension neckSize = size().dividedBy(6, 8);
        
        int big = chestSize.x();
        int med = big / 2;
        int small = big / 4;
        int tiny = big / 6;
        
        Dimension midpoint = middle;//.plus(0, med);
        Dimension neckStart = midpoint.plus(chestSize.plus(neckSize).times(0, -1));
        Dimension shoulder = middle.minus(0, med);
        Dimension elbow = shoulder.plus(-med * 2, 0);
        Dimension knee = middle.plus(0, big + med + small);
        
        Graphics2D g_orig;
        g_orig = (Graphics2D) g_start.create();
        g_orig.transform(AffineTransform.getTranslateInstance(-start.x(), -start.y()));
        Graphics2D g;
                
        // Back arm
        g = (Graphics2D) g_orig.create();
        g.setColor(armColor());
        g.transform(AffineTransform.getRotateInstance(pose.backShldrTheta, shoulder.x(), shoulder.y()));
        g.fillPolygon(polygon(shoulder.plus(-med, -small), -med, small, med, small));
        g.transform(AffineTransform.getRotateInstance(pose.backElbowTheta, elbow.x(), elbow.y()));
        g.fillPolygon(polygon(elbow.plus(0, -small), -med, small, med, small));
        g.dispose();
                
        // Back leg
        g = (Graphics2D) g_orig.create();
        g.setColor(legColor());
        g.transform(AffineTransform.getRotateInstance(pose.backHipTheta, midpoint.x(), midpoint.y()));
        g.fillPolygon(polygon(midpoint.plus(-small, big + small), med, 0, -small, med));
        g.transform(AffineTransform.getRotateInstance(pose.backKneeTheta, knee.x(), knee.y()));
        g.fillPolygon(polygon(knee.plus(-small, 0), med, 0, -small, med));
        g.dispose();
        
        // Torso
        g = (Graphics2D) g_orig.create();
        g.setColor(torsoColor());
        g.fillPolygon(polygon(midpoint, chestSize, chestSize.times(-1, 1)));
        g.fillPolygon(polygon(midpoint, chestSize.times(-1), chestSize.times(1, -1)));

        // Neck
        g.fillPolygon(polygon(neckStart, neckSize, neckSize.times(-1, 1)));

        // Head
        g.setColor(headColor());
        g.transform(AffineTransform.getRotateInstance(pose.neckTheta, neckStart.x(), neckStart.y()));
        g.fillPolygon(polygon(neckStart.plus(-med, 0), big, 0, -big, tiny)); // mask
        g.fillPolygon(polygon(neckStart.plus(-med, 0), 0, -big, small, 0)); // hood front
        g.fillPolygon(polygon(neckStart.plus(med, 0), tiny, -big, -small, 0)); // hood back
        g.fillPolygon(polygon(neckStart.plus(-big), big, -big, big, big)); // cap
        g.setColor(featherColor(1));
        g.fillPolygon(polygon(neckStart.plus(0, -big*2), big * 2, -med, 0, med)); // feather 1
        g.setColor(featherColor(2));
        g.fillPolygon(polygon(neckStart.plus(big / 2, -big * 3 / 2), big * 2, -med, 0, med)); // feather 2
        g.setColor(featherColor(3));
        g.fillPolygon(polygon(neckStart.plus(big, -big), big * 2, -med, 0, med)); // feather 3
        g.setColor(eyeColor());
        g.fillPolygon(polygon(neckStart.plus(0, -med), small, -tiny, -small, 0));
        g.dispose();

        // Front arm
        g = (Graphics2D) g_orig.create();
        g.setColor(armColor());
        g.transform(AffineTransform.getRotateInstance(pose.frontShldrTheta, shoulder.x(), shoulder.y()));
        g.fillPolygon(polygon(shoulder.plus(-med, -small), -med, small, med, small));
        g.transform(AffineTransform.getRotateInstance(pose.frontElbowTheta, elbow.x(), elbow.y()));
        g.fillPolygon(polygon(elbow.plus(0, -small), -med, small, med, small));
        g.dispose();
                
        // Front leg
        g = (Graphics2D) g_orig.create();
        g.setColor(legColor());
        g.transform(AffineTransform.getRotateInstance(pose.frontHipTheta, midpoint.x(), midpoint.y()));
        g.fillPolygon(polygon(midpoint.plus(-small, big + small), med, 0, -small, med));
        g.transform(AffineTransform.getRotateInstance(pose.frontKneeTheta, knee.x(), knee.y()));
        g.fillPolygon(polygon(midpoint.plus(-small, big + med + small), med, 0, -small, med));
        g.dispose();
        
        // Bounding rectangle
        g = (Graphics2D) g_orig.create();
        g.setColor(Color.WHITE);
        g.drawRect(position().x(), position().y(), size().x(), size().y());
        g.dispose();
        
    }
    
    private int numberOfJumps() {
        return 1;
    }
    private int jumpForce() {
        return Platform.blockSize.y() * 3 * weight();
    }
    private int walkForce() {
        return Platform.blockSize.x() * 2 * weight();
    }
    private int walkSpeed() {
        return Platform.blockSize.x() * (kb_run ? 8 : 4);
    }
    
    private Color armColor() {
        return Color.BLUE;
    }
    private Color legColor() {
        return Color.BLUE;
    }
    private Color torsoColor() {
        return Color.LIGHT_GRAY;
    }
    private Color headColor() {
        return Color.DARK_GRAY;
    }
    private Color featherColor(int i) {
        switch (i) {
            case 1:
                return Color.WHITE;
            case 2:
                return Color.WHITE;
            case 3:
                return Color.WHITE;
        }
        return Color.WHITE;
    }
    private Color eyeColor() {
        return Color.RED;
    }

    @Override
    public BufferedImage image() {
        if (images == null) {
            initImages();
        }
        return images.get("Default");
    }    
    public static void initImages() {
        if (images == null) {
            images = new HashMap();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try {
                images.put("Default", ImageIO.read(classLoader.getResourceAsStream("Resources/Graphics/Component/Player/Default.png")));
            } catch (IOException ex) {
            }
        }
    }
}
