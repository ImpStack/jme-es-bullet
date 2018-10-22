package org.impstack.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;

/**
 * A register of collision shapes that can be retrieved by a key.
 */
public interface PhysicalShapeRegistry {

    public PhysicalShape register(CollisionShape collisionShape);

    public CollisionShape get(PhysicalShape physicalShape);

}
