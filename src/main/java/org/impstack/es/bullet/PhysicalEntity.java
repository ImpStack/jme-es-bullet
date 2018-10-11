package org.impstack.es.bullet;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;

/**
 * A bullet object directly linked to an entity.
 *
 * @author remy
 * @since 10/10/18
 */
public interface PhysicalEntity<T extends PhysicsCollisionObject> {

    /**
     * The EntityId of the physical entity
     * @return EntityId
     */
    public EntityId getEntityId();

    /**
     * The physical object handled by the {@link com.jme3.bullet.PhysicsSpace}
     * @return physical object
     */
    public T getPhysicalObject();

    /**
     * The location of the entity in the {@link com.jme3.bullet.PhysicsSpace}
     * @return entity location
     */
    public Vector3f getLocation();

    /**
     * The rotation of the entity in the {@link com.jme3.bullet.PhysicsSpace}
     * @return entity rotation
     */
    public Quaternion getRotation();

}
