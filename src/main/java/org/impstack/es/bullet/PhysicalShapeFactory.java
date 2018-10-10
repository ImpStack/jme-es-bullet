package org.impstack.es.bullet;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * @author remy
 * @since 10/10/18
 */
public class PhysicalShapeFactory {

    public static PhysicalShape createBoxShape(Vector3f extent) {
        return new PhysicalShape(new BoxCollisionShape(extent));
    }

    public static PhysicalShape createBoxShape(Spatial spatial) {
        return new PhysicalShape(new BoxCollisionShape(((BoundingBox) spatial.getWorldBound()).getExtent(new Vector3f())));
    }

    public static PhysicalShape createSphereShape(float radius) {
        return new PhysicalShape(new SphereCollisionShape(radius));
    }

    public static PhysicalShape createMeshShape(Spatial spatial) {
        return new PhysicalShape(CollisionShapeFactory.createMeshShape(spatial));
    }

    public static PhysicalShape createDynamicMeshShape(Spatial spatial) {
        return new PhysicalShape(CollisionShapeFactory.createDynamicMeshShape(spatial));
    }

    public static PhysicalShape createCapsuleShape(Spatial spatial) {
        Vector3f extent = ((BoundingBox) spatial.getWorldBound()).getExtent(new Vector3f());
        return new PhysicalShape(new CapsuleCollisionShape(extent.z, (2 * extent.y) - (2 * extent.z)));
    }

}
