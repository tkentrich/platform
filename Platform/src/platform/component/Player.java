package platform.component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import platform.Dimension;
import platform.Platform;
import platform.PlayerCommand;
import platform.component.Pose.Theta;

/**
 *
 * @author richkent
 */
public class Player extends Component {

    public enum PlayerStatus { STAND, WALK, JUMP, FALL, SPRING, CROUCH, SLIDE };
    public enum PlayerFacing { LEFT, RIGHT };
    public enum MainAction { NONE, WALK, RUN, JUMP, SLIDE };
    public enum SecondaryAction { NONE, FIRE };
    
    private PlayerStatus status;
    private PlayerFacing facing;
    private MainAction mainAction;
    private SecondaryAction secondaryAction;
    
    private int jumpsRemaining;
    private int jumpForceRemaining;
    private int crouchTimeRemaining;
    
    private PoseChange mainActionPose;
    private PoseChange secondaryActionPose;
    
    private Pose pose;
    
    private boolean kb_left, kb_right, kb_up, kb_down, kb_jump, kb_fire, kb_run;
        
    private static HashMap<String, BufferedImage> images;

    public static Pose playerPose(int b, int w, int n, int fs, int fe, int bs, int be, int fh, int fk, int bh, int bk) {
        HashMap<Theta, Double> m = new HashMap();
        m.put(Theta.NECK, Math.toRadians(n));
        m.put(Theta.FRONT_SHOULDER, Math.toRadians(fs));
        m.put(Theta.FRONT_ELBOW, Math.toRadians(fe));
        m.put(Theta.BACK_SHOULDER, Math.toRadians(bs));
        m.put(Theta.BACK_ELBOW, Math.toRadians(be));
        m.put(Theta.FRONT_HIP, Math.toRadians(fh));
        m.put(Theta.FRONT_KNEE, Math.toRadians(fk));
        m.put(Theta.BACK_HIP, Math.toRadians(bh));
        m.put(Theta.BACK_KNEE, Math.toRadians(bk));
        return new Pose(m);
    }
    public static Pose zero() {
        return playerPose(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    public static Pose atRest() {
        return playerPose(0, 0, 0, -35, 15, 30, -15, 30, -30, -30, 30);
    }
    
    public static Pose crouchRest() {
        return playerPose(0, 0, 0, -35, 15, 30, -15, 30, -30, -30, 30);
    }
    
    public static Pose step1() {
        return atRest();
    }
    public static Pose step2() {
        return playerPose(0, 0, -5, 65, 35, -65, 35, -45, -35, 25, -35);
    }
    public static Pose step3() {
        return playerPose(0, 0, 0, -50, 25, 60, 35, 25, -30, -50, 30);
    }
    
    public static Pose test() {
        return playerPose(0, 0, -22, 0, 1, 180, -1, 1, -1, -90, 0);
    }
    
    public PoseChain walkChain() {
        return new PoseChain(1, 
                    new PoseLink(50, atRest(), atRest(), 5), 
                    new PoseLink(50, atRest(), step2(), 500),
                    new PoseLink(50, atRest(), step3(), 500)
            );
    }
    
    public Pose slide() {
        return playerPose(90, 0, -15, 180, -90, 45, 0, 20, -15, -10, 15);
    }
    
    public PoseLink firePose() {
        HashMap<Theta, Double> fire = new HashMap();
        fire.put(Theta.FRONT_SHOULDER, 0.0);
        fire.put(Theta.FRONT_ELBOW, 0.0);
        fire.put(Theta.BACK_SHOULDER, Math.toRadians(-10));
        fire.put(Theta.BACK_ELBOW, Math.toRadians(20));
        return new PoseLink(75, pose, new Pose(fire), 250);
    }
    
    private void setStatus(PlayerStatus newStatus) {
        if (status != newStatus) {
            switch(newStatus) {
                case WALK:
                    mainActionPose = walkChain();
                    mainActionPose.resetDeltas(pose);
                    break;
                case STAND:
                    pose = atRest();
                    mainActionPose = null;
                    break;
                case CROUCH:
                    pose = crouchRest();
                    mainActionPose = null;
                    break;
                case SLIDE:
                    pose = slide();
                    mainActionPose = null;
                default:
                    // setChain(restPose);
                    break;
            }
        }
        status = newStatus;
    }
    
    private void clearAction() {
        setAction(MainAction.NONE);
        setAction(SecondaryAction.NONE);
    }
    private void setAction(MainAction main) {
        mainAction = main;
        switch (main) {
            case WALK:
            case RUN:
                mainActionPose = walkChain();
                break;
            case SLIDE:
                break;
            // case CROUCH:
            //    break;
            case JUMP:
                break;
        }
    }
    private void setAction(SecondaryAction secondary) {
        secondaryAction = secondary;
        switch (secondaryAction) {
            case FIRE:
                secondaryActionPose = firePose();
                break;
            case NONE:
                secondaryActionPose = null;
        }
    }
    public void setPose(Pose pose) {
        this.pose = pose;
    }
    
    public Player(Dimension position) {
        super(position);
        kb_left = kb_right = kb_up = kb_down = kb_jump = kb_fire = kb_run = false;
        setStatus(PlayerStatus.STAND);
        setAction(MainAction.NONE);
        setAction(SecondaryAction.NONE);
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
    public double frictionFactor() {
        switch (status) {
            case WALK:
            case STAND:
            case JUMP:
            case FALL:
            case SPRING:
            default:
                return 1;
            case CROUCH:
                return 2;
            case SLIDE:
                return 0;
        }
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
        switch(status) {
            default:
            case STAND:
            case WALK:
                break;
            case JUMP:
            case FALL:
                setStatus(kb_right || kb_left ? PlayerStatus.STAND : PlayerStatus.WALK);
        }
    }
    
    public Pose pose() {
        return pose;
    }
    
    @Override
    public void move(int ms) {
        super.move(ms);
        switch (status) {
            case CROUCH:
                crouchTimeRemaining -= ms;
                if (crouchTimeRemaining <= 0) {
                    setStatus(PlayerStatus.JUMP);
                }
                break;
            case JUMP:
                break;
        }
        pose.adjust(ms, mainActionPose, secondaryActionPose);
        if (mainActionPose != null && !mainActionPose.active()) {
            switch (mainAction) {
                default:
                    setPose(atRest());
                    mainActionPose = null;
                    break;
            }
        }
        if (secondaryActionPose != null && !secondaryActionPose.active()) {
            switch (secondaryAction) {
                case FIRE:
                    
                    secondaryActionPose = null;
            }
            
        }
    }
    
    public void ui(PlayerCommand comm) {
        switch (comm.event().getKeyCode()) {
            case KeyEvent.VK_UP:
                kb_up = comm.typed();
                break;
            case KeyEvent.VK_DOWN:
                kb_down = comm.typed();
                if (kb_down) {
                    switch (status) {
                        case WALK:
                        case SPRING:
                            setStatus(PlayerStatus.CROUCH);
                            break;
                        case STAND:
                            setStatus(PlayerStatus.CROUCH);
                            break;
                    }
                } else {
                    switch (status) {
                        case SLIDE:
                            setStatus(PlayerStatus.WALK);
                            break;
                        case CROUCH:
                            setStatus(PlayerStatus.STAND);
                            break;
                    }
                }
                break;
            case KeyEvent.VK_LEFT:
                kb_left = comm.typed();
                if (kb_left) {
                    kb_right = false;
                    facing = PlayerFacing.LEFT;
                    setStatus(PlayerStatus.WALK);
                } else if (!kb_right) {
                    setStatus(PlayerStatus.STAND);
                }
                break;
            case KeyEvent.VK_RIGHT:
                kb_right = comm.typed();
                if (kb_right) {
                    kb_left = false;
                    facing = PlayerFacing.RIGHT;
                    setStatus(PlayerStatus.WALK);
                } else if (!kb_left) {
                    setStatus(PlayerStatus.STAND);
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
                            setStatus(PlayerStatus.CROUCH);
                            crouchTimeRemaining = crouchTime();
                            break;
                        case CROUCH:
                            setStatus(PlayerStatus.STAND);
                            break;
                        case JUMP:
                            setStatus(PlayerStatus.FALL);
                            break;
                    }
                }
                break;
            case KeyEvent.VK_ALT:
                // ???
        }
    }
    
    public void control() {
        int orig_speed = speed().x();
        if (kb_left) {
            push(new Dimension(-walkForce(), 0));
            facing = PlayerFacing.LEFT;
            if (speed().x() < -walkSpeed()) {
                speed().setX((orig_speed < -walkSpeed()) ? orig_speed : -walkSpeed());
            }
        } else if (kb_right) {
            push(new Dimension(walkForce(), 0));
            facing = PlayerFacing.RIGHT;
            if (speed().x() > walkSpeed()) {
                speed().setX((orig_speed > walkSpeed()) ? orig_speed : walkSpeed());
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
        
        Dimension midpoint = new Dimension(0); //middle;//.plus(0, med);
        Dimension neckStart = midpoint.plus(chestSize.plus(neckSize).times(0, -1));
        Dimension shoulder = midpoint.minus(0, med);
        Dimension elbow = shoulder.plus(0, med * 2);
        Dimension knee = midpoint.plus(0, big + med + small);
        
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
        return Platform.blockSize.x() * weight();
    }
    private int walkSpeed() {
        return Platform.blockSize.x() * (kb_run ? 8 : 4);
    }
    private int crouchTime() {
        return 300;
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
