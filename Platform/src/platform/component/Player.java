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
import platform.collectible.Collect;
import platform.collectible.Collectible;
import platform.component.Pose.Theta;
import platform.component.shot.StandardShot;

/**
 *
 * @author richkent
 */
public class Player extends LivingComponent {

    public enum PlayerStatus { STAND, WALK, JUMP, FALL, SPRING, CROUCH, SLIDE };
    public enum PlayerFacing { LEFT, RIGHT };
    public enum MainAction { NONE, WALK, RUN, JUMP, SLIDE };
    public enum SecondaryAction { NONE, FIRE };
    
    private PlayerStatus status;
    private PlayerFacing facing;
    private MainAction mainAction;
    private SecondaryAction secondaryAction;
    
    private int jumpsRemaining;
    private int jumpTimeRemaining;
    private int springTimeRemaining;
    
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
    
    public static Pose spring1() {
        return playerPose(0, 0, -10, -40, 10, -50, 10, -35, 20, -20, 20);
    }
    public static Pose spring2() {
        return playerPose(0, 0, 0, -70, 45, -80, 45, -35, 0, -20, 0);
    }
    
    public static Pose jump1() {
        return playerPose(0, 0, 10, 105, 30, -120, 0, 20, 0, -20, 0);
    }
    public static Pose jump2() {
        return playerPose(0, 0, 0, 120, 20, -105, -20, 10, -10, -10, 10);
    }
    
    public PoseChain jumpPose() {
        return new PoseChain(-1,
                new PoseLink(50, pose, jump1(), 50),
                new PoseLink(50, pose, jump2(), 50)
            );
        // return new PoseLink(50, pose, jump1(), 50);
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
    
    public PoseChain springPose() {
        return new PoseChain(-1,
                    new PoseLink(50, pose, spring1(), 50),
                    new PoseLink(50, pose, spring2(), springTime() - 50)
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
                    break;
                case SPRING:
                    mainActionPose = springPose();
                    springTimeRemaining = springTime();
                    break;
                case JUMP:
                    setAction(MainAction.JUMP);
                    jumpTimeRemaining = jumpTime();
                    speed().setY(-jumpSpeed());
                    jumpsRemaining--;
                    break;
                default:
                    mainActionPose = null;
                    pose = atRest();
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
                mainActionPose = jumpPose();
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
        switch (status) {
            default:
                return 100;
            case JUMP:
                return 0;
        }
    }

    @Override
    public ArrayList<CollisionResult> collide(Component c, Collision.CollisionType type) {
         ArrayList<CollisionResult> toReturn = new ArrayList();
         if (c instanceof Collectible) {
             toReturn.addAll(((Collectible)c).collect());
             toReturn.add(new Collect((Collectible) c));
         }
         return toReturn;
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
        switch(status) {
            default:
            case STAND:
            case WALK:
                break;
            case JUMP:
                break;
            case FALL:
                setStatus(kb_down ? PlayerStatus.CROUCH : (kb_right || kb_left ? PlayerStatus.WALK : PlayerStatus.STAND));
        }
    }
    
    public void checkedStanding() {
        if (!standing()) {
            switch (status) {
                default:
                    setStatus(PlayerStatus.FALL);
                    break;
                case JUMP:
            }
        }
    }
    
    public Pose pose() {
        return pose;
    }
    
    @Override
    public void move(int ms) {
        super.move(ms);
        switch (status) {
            case SPRING:
                springTimeRemaining -= ms;
                if (springTimeRemaining <= 0 && jumpsRemaining > 0) {
                    setStatus(PlayerStatus.JUMP);
                }
                break;
            case JUMP:
                speed().setY(-jumpSpeed());
                jumpTimeRemaining -= ms;
                if (jumpTimeRemaining <= 0) {
                    setStatus(PlayerStatus.FALL);
                }
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
                    setAction(SecondaryAction.NONE);
                    setChanged();
                    Dimension shPos = position().plus(size().dividedBy(2));
                    shPos.add(size().plus(StandardShot.shotSize()).dividedBy(2).x() * (facing == PlayerFacing.LEFT ? -1 : 1), 0);
                    notifyObservers(new StandardShot(shPos, speed().plus(StandardShot.fireSpeed * (facing == PlayerFacing.LEFT ? -1 : 1), 0)));
                    break;
            }
            
        }
    }
    
    public void ui(PlayerCommand comm) {
        switch (comm.event().getKeyCode()) {
            // TODO: For each case, add in the check (if already pressed and comm.typed() then exit)
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
        if (kb_jump) {
            switch (status) {
                case STAND:
                case WALK:
                    setStatus(PlayerStatus.SPRING);
                    break;
                case FALL:
                    if (jumpsRemaining > 0) {
                        setStatus(PlayerStatus.JUMP);
                    }
                    break;
            }
        } else if (status == PlayerStatus.JUMP) {
            setStatus(PlayerStatus.FALL);
        } else if (status == PlayerStatus.SPRING) {
            setStatus(kb_left || kb_right ? PlayerStatus.WALK : PlayerStatus.STAND);
        }
        if (kb_fire && secondaryAction == SecondaryAction.NONE) {
            setAction(SecondaryAction.FIRE);
        }
    }
    
    private Dimension middle, chestSize, neckSize;
    private int big, med, small, tiny;
    private Dimension midpoint, neckStart, shoulder, elbow, knee;

    @Override
    public void display(Graphics2D g_start, Dimension start) {
        middle = position().plus(size().dividedBy(2));
        chestSize = size().dividedBy(3, 4);
        neckSize = size().dividedBy(6, 8);
        
        big = chestSize.x();
        med = big / 2;
        small = big / 4;
        tiny = big / 6;
        
        midpoint = new Dimension(0); //middle;//.plus(0, med);
        neckStart = midpoint.plus(chestSize.plus(neckSize).times(0, -1));
        shoulder = midpoint.minus(0, med);
        elbow = shoulder.plus(0, med * 2);
        knee = midpoint.plus(0, big + med + small);
        
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
                arm(g_orig, pose.theta(Theta.BACK_SHOULDER), pose.theta(Theta.BACK_ELBOW));
                
                // Back leg
                leg(g_orig, pose.theta(Theta.BACK_HIP), pose.theta(Theta.BACK_KNEE));

                // Torso
                g = (Graphics2D) g_orig.create();
                g.setColor(color(1));
                polygon(g, midpoint, chestSize, chestSize.times(-1, 1));
                polygon(g, midpoint, chestSize.times(-1), chestSize.times(1, -1));

                // Neck
                polygon(g, neckStart, neckSize, neckSize.times(-1, 1));

                // Head
                g.setColor(color(3));
                g.transform(AffineTransform.getRotateInstance(pose.theta(Theta.NECK), neckStart.x(), neckStart.y()));
                polygon(g, neckStart.plus(-med, 0), big, 0, -big, tiny); // mask
                polygon(g, neckStart.plus(-med, 0), 0, -big, small, 0); // hood front
                polygon(g, neckStart.plus(med, 0), tiny, -big, -small, 0); // hood back
                polygon(g, neckStart.plus(-big), big, -big, big, big); // cap
                g.setColor(featherColor(1));
                polygon(g, neckStart.plus(big / 2, -big * 3 / 2), big, -med, 0, med); // feather 1
                g.setColor(featherColor(2));
                polygon(g, neckStart.plus(big, -big), big, -med, 0, med); // feather 2
                g.setColor(eyeColor());
                polygon(g, neckStart.plus(0, -med), small, -tiny, -small, 0);
                g.drawString(status.name() + " " + mainActionPose, neckStart.x() - big, neckStart.y() - big * 3);
                g.dispose();

                // Front arm
                arm(g_orig, pose.theta(Theta.FRONT_SHOULDER), pose.theta(Theta.FRONT_ELBOW));
                
                // Front leg
                leg(g_orig, pose.theta(Theta.FRONT_HIP), pose.theta(Theta.FRONT_KNEE));
                
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
    
    private void arm(Graphics2D g_orig, double t_s, double t_e) {
        Graphics2D g = (Graphics2D) g_orig.create();
        g.setColor(color(1));
        g.transform(AffineTransform.getRotateInstance(t_s, shoulder.x(), shoulder.y()));
        rectangle(g, shoulder.plus(-small, 0), med, 2 * med);
        g.transform(AffineTransform.getRotateInstance(t_e, elbow.x(), elbow.y()));
        rectangle(g, elbow.plus(-small, 0), med, med);
        g.setColor(color(2));
        circle(g, elbow, small);
        circle(g, elbow.plus(0, med), small);
        g.setColor(color(1));
        polygon(g, elbow.plus(0, small), small, -small, -small, -small, -small, small, small, small);
        polygon(g, elbow.plus(0, med + small), small, -small, -small, -small, -small, small, small, small);
        g.setColor(color(2));
        circle(g, elbow, tiny);
        circle(g, elbow.plus(0, med), tiny);
        g.dispose();
    }
    private void leg(Graphics2D g_orig, double t_h, double t_k) {
        Graphics2D g = (Graphics2D) g_orig.create();
        g.setColor(color(1));
        g.transform(AffineTransform.getRotateInstance(t_h, midpoint.x(), midpoint.y()));
        rectangle(g, midpoint.plus(-small, 2 * med), med, med + small);
        g.transform(AffineTransform.getRotateInstance(t_k, knee.x(), knee.y()));
        rectangle(g, knee.plus(-small, 0), med, med);
        g.setColor(color(2));
        circle(g, knee, small);
        circle(g, knee.plus(0, med), small);
        g.setColor(color(1));
        polygon(g, knee.plus(0, small), small, -small, -small, -small, -small, small, small, small);
        polygon(g, knee.plus(0, med + small), small, -small, -small, -small, -small, small, small, small);
        g.setColor(color(2));
        circle(g, knee, tiny);
        circle(g, knee.plus(0, med), tiny);
        g.dispose();
    }
    
    private int numberOfJumps() {
        return 1;
    }
    private int jumpTime() {
        return 500;
    }
    private int jumpSpeed() {
        return Platform.blockSize.y() * 6;
    }
    private int walkForce() {
        return Platform.blockSize.x() * weight();
    }
    private int walkSpeed() {
        return Platform.blockSize.x() * (kb_run ? 8 : 4);
    }
    private int springTime() {
        return 300;
    }
    
    private Color color(int number) {
        switch (number) {
            case 1:
            default:
                return Color.BLUE;
            case 2:
                return Color.RED;
            case 3:
                return Color.WHITE;
        }
    }
    /*private Color armColor() {
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
    }*/
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
    
    public PlayerStatus status() {
        return status;
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
