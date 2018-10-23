package org.impstack.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;

/**
 * A register of collision shapes that can be retrieved by a key.
 */
public interface PhysicalShapeRegistry {

    /**
     * Register the collision shape with the physical shape component
     * @param physicalShape the physical shape component holding the key
     * @param collisionShape the physical collision shape
     * @return the collision shape linked to the physical shape
     */
    public CollisionShape register(PhysicalShape physicalShape, CollisionShape collisionShape);

    /**
     * Returns the collision shape linked to the physical shape component
     * @param physicalShape the physical shape component holding the key
     * @return the collision shape linked to the physical shape
     */
    public CollisionShape get(PhysicalShape physicalShape);

}
