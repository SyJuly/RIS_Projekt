package gameLWJGL.objects;

import gameLWJGL.collision.Collision;
import gameLWJGL.collision.CollisionDirection;
import gameLWJGL.input.IMoveable;
import gameLWJGL.physics.PhysicsObject;
import gameLWJGL.world.Camera;

import static org.lwjgl.opengl.GL11.*;

public class Player extends PhysicsObject implements IMoveable {


    public static final double JUMP_STRENGTH = 0.3f;
    public static final double SPEED = 0.03f;
    public static final float INITIAL_SIZE = 0.05f;
    public static final float WEIGHT_DIFF = 0.005f;
    public static final float[] DEFAULT_COLOR = new float[]{1,1,1};

    private  boolean isJumping = false;
    private float xDelta;

    private float[] color;

    public Player(float x, float y, String id){
        super(x,y, INITIAL_SIZE, INITIAL_SIZE, id, ObjectType.PLAYER);
        this.color = DEFAULT_COLOR;
    }

    public Player(float x, float y, String id, float[] color){
        super(x,y, INITIAL_SIZE, INITIAL_SIZE, id, ObjectType.PLAYER);
        this.color = color;
    }

    @Override
    public boolean update() {
        boolean physicsHasBeenUpdated = super.update();
        x += xDelta;
        if(isJumping && speedY < 0){
            isJumping = false;
        }
        return true; //TODO: could be improved
    }

    @Override
    public void render(Camera camera){

        float xOffset = camera.getXOffset(x);
        float yOffset = camera.getYOffset(y);
        glBegin(GL_QUADS);

        glColor4f(color[0],color[1],color[2],0);
        glVertex2f(-width + xOffset, height + yOffset);
        glVertex2f(width + xOffset, height + yOffset);
        glVertex2f(width + xOffset, -height + yOffset);
        glVertex2f(-width + xOffset, -height + yOffset);
        glEnd();

        /*glBegin(GL_QUADS);
                glTexCoord2f(0,0);
                glVertex2f(-squareSize + x, squareSize + y);
                glTexCoord2f(1,0);
                glVertex2f(squareSize + x, squareSize + y);
                glTexCoord2f(1,1);
                glVertex2f(squareSize + x, -squareSize + y);
                glTexCoord2f(0,1);
                glVertex2f(-squareSize + x, -squareSize + y);
            glEnd();*/
    }

    public void gainWeight(){
        weight += WEIGHT_DIFF;
        width += weight;
        height += weight;
    }

    public void looseWeight(){
        weight = Math.max(weight - WEIGHT_DIFF, 0);
        width += weight;
        height += weight;
    }

    @Override
    public void move(int xDirection, int yDirection) {
        if(yDirection > 0){
            jump();
        }
        xDelta = (float) SPEED * xDirection;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public float[] getSpecifics() {
        float[] physicsSpecifics = super.getSpecifics();
        float[] specifics = new float[physicsSpecifics.length + color.length];
        for(int i = 0; i < physicsSpecifics.length; i++){
            specifics[i] = physicsSpecifics[i];
        }
        specifics[physicsSpecifics.length] = color[0];
        specifics[physicsSpecifics.length + 1] = color[1];
        specifics[physicsSpecifics.length + 2] = color[2];
        return specifics;
    }

    @Override
    public void setSpecifics(float[] specifics) {
        color = new float[]{specifics[specifics.length - 3], specifics[specifics.length - 2], specifics[specifics.length - 1]};
        float[] physicsSpecifics = new float[specifics.length - color.length];
        for(int i = 0; i < physicsSpecifics.length; i++){
            physicsSpecifics[i] = specifics[i];
        }
        super.setSpecifics(physicsSpecifics);
    }

    public void jump() {
        if(!isJumping){
            isJumping = true;
            accelerate(0, JUMP_STRENGTH);
        }
    }

    @Override
    public void handleCollision(Collision collisionData) {
        super.handleCollision(collisionData);
        GameObject collidingGameObject = collisionData.gameObjects[0].id == id ? collisionData.gameObjects[1] : collisionData.gameObjects[0];
        if(collidingGameObject.objectType != ObjectType.PLAYER) return;
        if(collisionData.aMetBs != CollisionDirection.UPSIDE){
            looseWeight();
            if(weight > 0){
                for(int i = 0; i < 5; i++){
                    System.out.println();
                }
                System.out.println("LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOSER");
            }
        }
    }
}
