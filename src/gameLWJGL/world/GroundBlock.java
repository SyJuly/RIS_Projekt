package gameLWJGL.world;

import gameLWJGL.collision.Collision;
import gameLWJGL.objects.GameObject;
import gameLWJGL.objects.ObjectType;

import static org.lwjgl.opengl.GL11.*;

public class GroundBlock extends GameObject {

    public GroundBlock (float x, float y, float width, float height){
        super(x, y, width, height, "static", ObjectType.STATIC);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Camera camera) {
        float xOffset = camera.getXOffset(x);
        float yOffset = camera.getYOffset(y);
        glBegin(GL_QUADS);
        glColor4f(1,1,1,0);
        glVertex2f(-width + xOffset, height + yOffset);
        glVertex2f(width + xOffset, height + yOffset);
        glVertex2f(width + xOffset, -height + yOffset);
        glVertex2f(-width + xOffset, -height + yOffset);
        glEnd();
    }

    @Override
    public void handleCollision(Collision collisionData) {

    }
}
