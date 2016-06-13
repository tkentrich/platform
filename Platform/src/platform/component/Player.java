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

    public enum PlayerStatus { STAND, WALK, JUMP, FALL };
    public enum PlayerFacing { LEFT, RIGHT };
    public enum Theta {NECK, FRONT_SHOULDER, FRONT_ELBOW, BACK_SHOULDER, BACK_ELBOW, FRONT_HIP, FRONT_KNEE, BACK_HIP, BACK_KNEE};
    
    private PlayerStatus status;
    private PlayerFacing facing;
    private int jumpsRemaining;
    private int jumpForceRemaining;
    
    private boolean kb_left, kb_right, kb_up, kb_down, kb_jump, kb_fire, kb_run;
        
    private static HashMap<String, BufferedImage> images;

    public static class Pose {
        public HashMap<Theta, Double> theta;
        public int millis;     // milliseconds until target should be reached
        public Pose target;
        public Pose(double neck, double frShldr, double frElbow, double bkShldr, double bkElbow, double frHip, double frKnee, double bkHip, double bkKnee, int ms) {
            theta = new HashMap();
            theta.put(Theta.NECK, neck); // neckTheta = neck;
            theta.put(Theta.FRONT_SHOULDER, frShldr); // frontShldrTheta = frShldr;
            theta.put(Theta.FRONT_ELBOW, frElbow); // frontElbowTheta = frElbow;
            theta.put(Theta.BACK_SHOULDER, bkShldr); // backShldrTheta = bkShldr;
            theta.put(Theta.BACK_ELBOW, bkElbow); // backElbowTheta = bkElbow;
            theta.put(Theta.FRONT_HIP, frHip); // frontHipTheta = frHip;
            theta.put(Theta.FRONT_KNEE, frKnee); // frontKneeTheta = frKnee;
            theta.put(Theta.BACK_HIP, bkHip); // backHipTheta = bkHip;
            theta.put(Theta.BACK_KNEE, bkKnee); // backKneeTheta = bkKnee;
            millis = ms;
        }
        public Pose(int neck, int frShldr, int frElbow, int bkShldr, int bkElbow, int frHip, int frKnee, int bkHip, int bkKnee, int ms) {
            theta = new HashMap();
            theta.put(Theta.NECK, Math.toRadians(neck)); // neckTheta = neck;
            theta.put(Theta.FRONT_SHOULDER, Math.toRadians(frShldr)); // frontShldrTheta = frShldr;
            theta.put(Theta.FRONT_ELBOW, Math.toRadians(frElbow)); // frontElbowTheta = frElbow;
            theta.put(Theta.BACK_SHOULDER, Math.toRadians(bkShldr)); // backShldrTheta = bkShldr;
            theta.put(Theta.BACK_ELBOW, Math.toRadians(bkElbow)); // backElbowTheta = bkElbow;
            theta.put(Theta.FRONT_HIP, Math.toRadians(frHip)); // frontHipTheta = frHip;
            theta.put(Theta.FRONT_KNEE, Math.toRadians(frKnee)); // frontKneeTheta = frKnee;
            theta.put(Theta.BACK_HIP, Math.toRadians(bkHip)); // backHipTheta = bkHip;
            theta.put(Theta.BACK_KNEE, Math.toRadians(bkKnee)); // backKneeTheta = bkKnee;
            millis = ms;
            target = this;
        }
        public Pose(Pose from, Pose to) {
            target = to;
            theta = new HashMap();
            for (Theta t : Theta.values()) {
                theta.put(t, delta(to.theta.get(t), from.theta.get(t)) / to.millis);
            }
        }
        public Pose(Pose from) {
            theta = new HashMap();
            for (Theta t : Theta.values()) {
                theta.put(t, from.theta(t));
            }
            millis = from.millis;
        }
        public void adjustTheta(Theta t, double delta) {
            theta.put(t, theta(t) + delta);
        }
        public double theta(Theta t) {
            return theta.get(t);
        }
        public double delta(double to, double from) {
            double delta = to - from;
            // TODO: Wraparound (Maybe to + 2PI is closer to from)
            return delta;
        }
        public String toString() {
            /*return String.format("N%1.2f FS%1.2f FE%1.2f BS%1.2f BE%1.2f FH%1.2f FK%1.2f BH%1.2f BK%1.2f [%d]", 
                    theta.get(Theta.NECK), 
                    theta.get(Theta.FRONT_SHOULDER), theta.get(Theta.FRONT_ELBOW), // frontShldrTheta, frontElbowTheta, 
                    theta.get(Theta.BACK_SHOULDER), theta.get(Theta.BACK_ELBOW), // backShldrTheta, backElbowTheta, 
                    theta.get(Theta.FRONT_HIP), theta.get(Theta.FRONT_KNEE), // frontHipTheta, frontKneeTheta, 
                    theta.get(Theta.BACK_HIP), theta.get(Theta.BACK_KNEE), // backHipTheta, backKneeTheta);
                    millis);*/
            return String.format("N%d FS%d FE%d BS%d BE%d FH%d FK%d BH%d BK%d [%d]", 
                    Math.toDegrees(theta.get(Theta.NECK)), 
                    Math.toDegrees(theta.get(Theta.FRONT_SHOULDER)), Math.toDegrees(theta.get(Theta.FRONT_ELBOW)), // frontShldrTheta, frontElbowTheta, 
                    Math.toDegrees(theta.get(Theta.BACK_SHOULDER)), Math.toDegrees(theta.get(Theta.BACK_ELBOW)), // backShldrTheta, backElbowTheta, 
                    Math.toDegrees(theta.get(Theta.FRONT_HIP)), Math.toDegrees(theta.get(Theta.FRONT_KNEE)), // frontHipTheta, frontKneeTheta, 
                    Math.toDegrees(theta.get(Theta.BACK_HIP)), Math.toDegrees(theta.get(Theta.BACK_KNEE)), // backHipTheta, backKneeTheta);
                    millis);
        }
    }
        /*
            N0.00 FS-0.52 FE0.35 BS0.44 GE-0.44 FH0.52 FK-0.52 BH-0.52 BK0.52 [1]
            N-0.09 FS-1.05 FE0.52 BS1.13 GE0.61 FH0.44 FK-0.44 BH-0.87 BK0.61 [500]
            N0.09 FS1.05 FE0.70 BS-1.05 GE0.52 FH-0.87 FK-0.52 BH0.52 BK-0.52 [650]
            N0.00 FS0.87 FE-0.35 BS-0.79 GE0.35 FH-0.35 FK0.26 BH0.26 BK-0.26 [200]
        */
        /*
            N0.00 FS-0.61 FE0.26 BS0.52 GE-0.26 FH0.52 FK-0.52 BH-0.52 BK0.52 [1]
            N-0.09 FS1.13 FE0.61 BS-1.13 GE0.61 FH-0.79 FK-0.61 BH0.44 BK-0.61 [1500]
            N0.00 FS-0.87 FE0.44 BS1.05 GE0.61 FH0.44 FK-0.52 BH-0.87 BK-0.52 [1500]
            N0.00 FS-0.61 FE0.26 BS0.52 GE-0.26 FH0.52 FK-0.52 BH-0.52 BK0.52 [1301]
        */
    public static Pose zero() {
        return new Pose(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    public static Pose atRest() {
        return new Pose(0, -35, 15, 30, -15, 30, -30, -30, 30, 1);
    }
    
    public static Pose step1() {
        return atRest();
    }
    public static Pose step2() {
        return new Pose(-5, 65, 35, -65, 35, -45, -35, 25, -35, 500);
    }
    public static Pose step3() {
        return new Pose(0, -50, 25, 60, 35, 25, -30, -50, 30, 500);
    }
    
    public static Pose test() {
        return new Pose(-PI/8, 0, 1, PI, -1, 1, -1, -0.5, 0, 5000);
    }
    private static ArrayList<Pose> restPose;
    private static ArrayList<Pose> walkPose;
    private Pose pose;
    private Pose targetPose;
    private int targetPoseTime;
    private ArrayList<Pose> poseChain;
    private int chainIndex;
    
    public void setPose(Pose pose) {
        this.pose = pose;
        this.pose.target = pose;
    }
    public void setChain(ArrayList<Pose> newChain) {
        poseChain = newChain;
        chainIndex = 0;
        setTargetPose(poseChain.get(chainIndex));
    }
    
    private void setTargetPose(Pose newPose) {
        targetPose = new Pose(pose, newPose);
        targetPoseTime = newPose.millis;
        if (targetPoseTime < 10) {
            pose = newPose;
            targetPose = null;
        }
    }
    
    public Player(Dimension position) {
        super(position);
        pose = atRest();
        targetPose = null;
        kb_left = kb_right = kb_up = kb_down = kb_jump = kb_fire = kb_run = false;
        status = PlayerStatus.STAND;
        facing = PlayerFacing.RIGHT;
        if (walkPose == null) {
            walkPose = new ArrayList();
            walkPose.add(step1());
            walkPose.add(step2());
            walkPose.add(step3());
            // walkPose.add(step4());
        }
        if (restPose == null) {
            restPose = new ArrayList();
            restPose.add(atRest());
        }
        setChain(restPose);
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
        if (standing) {
            playerStanding();
        }
    }
    public void playerStanding() {
        jumpsRemaining = numberOfJumps();
        jumpForceRemaining = jumpForce();
    }
    
    public Pose pose() {
        return pose;
    }
    
    @Override
    public void move(int ms) {
        super.move(ms);
        if (targetPose != null && targetPoseTime > ms) {
            for (Theta t : Theta.values()) {
                pose.theta.put(t, pose.theta.get(t) + targetPose.theta.get(t) * ms);
            }
            targetPoseTime -= ms;
        } else if (targetPose == null) {
            chainIndex = (chainIndex + 1) % poseChain.size();
            if (chainIndex == 0 && poseChain.size() > 1) {
                if (poseChain.get(0).millis < 5) {
                    chainIndex = 1;
                }
            }
            setTargetPose(poseChain.get(chainIndex));
            if (chainIndex == 0) {
                setChanged();
                notifyObservers();
            }
        } else {
            pose = pose.target;
            targetPose = null;
        }
    }
    
    public void ui(PlayerCommand comm) {
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
                    if (status == PlayerStatus.STAND) {
                        setChain(walkPose);
                    }
                    status = PlayerStatus.WALK;
                } else if (!kb_right) {
                    setPose(atRest());
                }
                break;
            case KeyEvent.VK_RIGHT:
                kb_right = comm.typed();
                if (kb_right) {
                    kb_left = false;
                    facing = PlayerFacing.RIGHT;
                    if (status == PlayerStatus.STAND) {
                        setChain(walkPose);
                    }
                    status = PlayerStatus.WALK;
                } else if (!kb_left) {
                    setPose(atRest());
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
            facing = PlayerFacing.LEFT;
            if (speed().x() < -walkSpeed()) {
                speed().setX((orig_speed < -walkSpeed()) ? orig_speed : -walkSpeed());
            }
            System.out.printf("Control: Orig speed %d New speed %d (Max speed: %d)%n", orig_speed, speed().x(), walkSpeed());
        } else if (kb_right) {
            push(new Dimension(walkForce(), 0));
            facing = PlayerFacing.RIGHT;
            if (speed().x() > walkSpeed()) {
                speed().setX((orig_speed > walkSpeed()) ? orig_speed : walkSpeed());
            }
            System.out.printf("Control: Orig speed %d New speed %d (Max speed: %d)%n", orig_speed, speed().x(), walkSpeed());
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
        
        Dimension midpoint = new Dimension(0); //middle;//.plus(0, med);
        Dimension neckStart = midpoint.plus(chestSize.plus(neckSize).times(0, -1));
        Dimension shoulder = middle.minus(0, med);
        Dimension elbow = shoulder.plus(0, med * 2);
        Dimension knee = middle.plus(0, big + med + small);
        
        Graphics2D g_orig;
        g_orig = (Graphics2D) g_start.create();
        g_orig.transform(AffineTransform.getTranslateInstance(-start.minus(middle).x(), -start.minus(middle).y()));
        if (facing == PlayerFacing.RIGHT) {
            g_orig.transform(AffineTransform.getScaleInstance(-1, 1));
        }
        
        Graphics2D g;
        switch (status) {
            default:
            case STAND:
            case WALK:
                if (pose == null) {
                    return;
                }

                // Back arm
                g = (Graphics2D) g_orig.create();
                g.setColor(armColor());
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.BACK_SHOULDER), shoulder.x(), shoulder.y()));
                g.fillPolygon(polygon(shoulder.plus(-small, med), small, med, small, -med));
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.BACK_ELBOW), elbow.x(), elbow.y()));
                g.fillPolygon(polygon(elbow.plus(-small, 0), small, med, small, -med));
                g.dispose();

                // Back leg
                g = (Graphics2D) g_orig.create();
                g.setColor(legColor());
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.BACK_HIP), midpoint.x(), midpoint.y()));
                g.fillPolygon(polygon(midpoint.plus(-small, big + small), med, 0, -small, med));
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.BACK_KNEE), knee.x(), knee.y()));
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
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.NECK), neckStart.x(), neckStart.y()));
                g.fillPolygon(polygon(neckStart.plus(-med, 0), big, 0, -big, tiny)); // mask
                g.fillPolygon(polygon(neckStart.plus(-med, 0), 0, -big, small, 0)); // hood front
                g.fillPolygon(polygon(neckStart.plus(med, 0), tiny, -big, -small, 0)); // hood back
                g.fillPolygon(polygon(neckStart.plus(-big), big, -big, big, big)); // cap
                // g.setColor(featherColor(1));
                // g.fillPolygon(polygon(neckStart.plus(0, -big*2), big, -med, 0, med)); // feather 0
                g.setColor(featherColor(1));
                g.fillPolygon(polygon(neckStart.plus(big / 2, -big * 3 / 2), big, -med, 0, med)); // feather 1
                g.setColor(featherColor(2));
                g.fillPolygon(polygon(neckStart.plus(big, -big), big, -med, 0, med)); // feather 2
                g.setColor(eyeColor());
                g.fillPolygon(polygon(neckStart.plus(0, -med), small, -tiny, -small, 0));
                g.dispose();

                // Front arm
                g = (Graphics2D) g_orig.create();
                g.setColor(armColor());
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.FRONT_SHOULDER), shoulder.x(), shoulder.y()));
                g.fillPolygon(polygon(shoulder.plus(-small, med), small, med, small, -med));
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.FRONT_ELBOW), elbow.x(), elbow.y()));
                g.fillPolygon(polygon(elbow.plus(-small, 0), small, med, small, -med));
                g.dispose();

                // Front leg
                g = (Graphics2D) g_orig.create();
                g.setColor(legColor());
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.FRONT_HIP), midpoint.x(), midpoint.y()));
                g.fillPolygon(polygon(midpoint.plus(-small, big + small), med, 0, -small, med));
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.FRONT_KNEE), knee.x(), knee.y()));
                g.fillPolygon(polygon(midpoint.plus(-small, big + med + small), med, 0, -small, med));
                g.dispose();

                // Bounding rectangle
                g = (Graphics2D) g_orig.create();
                g.setColor(Color.WHITE);
                g.drawRect(position().x(), position().y(), size().x(), size().y());
                g.dispose();
                
            case FALL:
            case JUMP:
        }
        
        // Info
        /*
        g = (Graphics2D) g_orig.create();
        g.setColor(Color.WHITE);
        g.drawString(String.format("Target Time: %d Chain Index: %d", targetPoseTime, chainIndex), position().x() - 100, position().y() - 100);
        g.drawString(pose.toString(), position().x() - 100, position().y() - 50);
        g.dispose();
        */
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
