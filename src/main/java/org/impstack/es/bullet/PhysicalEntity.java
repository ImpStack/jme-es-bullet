package org.impstack.es.bullet;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;

/**
 * A bullet object linked to an entity
 *
 * @author remy
 * @since 10/10/18
 */
public interface PhysicalEntity<T extends PhysicsCollisionObject> {

    public EntityId getEntityId();

    public T getPhysicalObject();

    public Vector3f getLocation();

    public Quaternion getRotation();

}
