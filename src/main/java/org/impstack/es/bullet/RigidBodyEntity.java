package org.impstack.es.bullet;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;

/**
 * A bullet rigidbody object directly linked to an entity.
 *
 * @author remy
 * @since 10/10/18
 */
public class RigidBodyEntity extends PhysicsRigidBody implements PhysicalEntity<PhysicsRigidBody> {

    private final EntityId entityId;

    public RigidBodyEntity(EntityId entityId, PhysicalShape shape, Mass mass) {
        super(shape.getCollisionShape(), mass.getMass());
        this.entityId = entityId;
    }

    @Override
    public EntityId getEntityId() {
        return entityId;
    }

    @Override
    public PhysicsRigidBody getPhysicalObject() {
        return this;
    }

    @Override
    public Vector3f getLocation() {
        return getPhysicsLocation();
    }

    @Override
    public Quaternion getRotation() {
        return getPhysicsRotation();
    }

    @Override
    public String toString() {
        return "RigidBodyEntity{" +
                "entityId=" + entityId +
                '}';
    }

}
