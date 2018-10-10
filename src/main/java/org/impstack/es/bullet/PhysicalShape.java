package org.impstack.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying the collision shape of a physical entity.
 *
 * @author remy
 * @since 10/10/18
 */
public class PhysicalShape implements EntityComponent {

    private final CollisionShape collisionShape;

    public PhysicalShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public CollisionShape getCollisionShape() {
        return collisionShape;
    }

    @Override
    public String toString() {
        return "PhysicalShape{" +
                "collisionShape=" + collisionShape +
                '}';
    }
}
