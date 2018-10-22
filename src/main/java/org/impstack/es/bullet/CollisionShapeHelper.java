package org.impstack.es.bullet;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * A helper factory to create {@link com.jme3.bullet.collision.shapes.CollisionShape} objects.
 */
public class CollisionShapeHelper {

    public static CollisionShape createBoxShape(Vector3f extent) {
        return new BoxCollisionShape(extent);
    }

    public static CollisionShape createBoxShape(Spatial spatial) {
        return new BoxCollisionShape(((BoundingBox) spatial.getWorldBound()).getExtent(new Vector3f()));
    }

    public static CollisionShape createSphereShape(float radius) {
        return new SphereCollisionShape(radius);
    }

    public static CollisionShape createMeshShape(Spatial spatial) {
        return CollisionShapeFactory.createMeshShape(spatial);
    }

    public static CollisionShape createDynamicMeshShape(Spatial spatial) {
        return CollisionShapeFactory.createDynamicMeshShape(spatial);
    }

    public static CollisionShape createCapsuleShape(Spatial spatial) {
        Vector3f extent = ((BoundingBox) spatial.getWorldBound()).getExtent(new Vector3f());
        return new CapsuleCollisionShape(extent.z, (2 * extent.y) - (2 * extent.z));
    }

    /**
     * The created collisionshape is a capsule collision shape that is attached to a compound collision shape with an
     * offset to set the object center at the bottom of the capsule.
     * @param radius radius of the capsule
     * @param height height of the capsule
     * @param centerAtBottom true if the center of the capsule should be at the bottom of the object
     * @return a capsule physical shape
     */
    public static CollisionShape createCapsuleShape(float radius, float height, boolean centerAtBottom) {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(radius, height - (2 * radius));
        if (centerAtBottom) {
            CompoundCollisionShape compoundShape = new CompoundCollisionShape();
            Vector3f offset = new Vector3f(0, height * 0.5f, 0);
            compoundShape.addChildShape(capsule, offset);
            return compoundShape;
        } else {
            return capsule;
        }
    }

}
