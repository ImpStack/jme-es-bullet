package org.impstack.es.bullet.debug;

import com.simsilica.es.EntityData;
import org.impstack.es.bullet.PhysicalEntity;
import org.impstack.es.bullet.PhysicalEntityListener;
import org.impstack.es.bullet.RigidBodyEntity;

/**
 * A {@link PhysicalEntityListener} implementation that publishes and updates {@link PhysicalEntityDebugStatus} components
 * based on the status of the {@link PhysicalEntity} in the physics space.
 */
public class PhysicalEntityDebugStatusPublisher implements PhysicalEntityListener {

    private final EntityData entityData;

    public PhysicalEntityDebugStatusPublisher(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    public void startFrame() {
    }

    @Override
    public void physicalEntityAdded(PhysicalEntity physicalEntity) {
        entityData.setComponent(physicalEntity.getEntityId(), new PhysicalEntityDebugStatus(getStatus(physicalEntity)));
    }

    @Override
    public void physicalEntityUpdated(PhysicalEntity physicalEntity) {
        int status = getStatus(physicalEntity);
        PhysicalEntityDebugStatus debugStatus = entityData.getComponent(physicalEntity.getEntityId(), PhysicalEntityDebugStatus.class);
        if (debugStatus == null || debugStatus.getStatus() != status) {
            entityData.setComponent(physicalEntity.getEntityId(), new PhysicalEntityDebugStatus(status));
        }
    }

    @Override
    public void physicalEntityRemoved(PhysicalEntity physicalEntity) {
        entityData.removeComponent(physicalEntity.getEntityId(), PhysicalEntityDebugStatus.class);
    }

    @Override
    public void endFrame() {
    }

    private static int getStatus(PhysicalEntity physicalEntity) {
        if (physicalEntity instanceof RigidBodyEntity) {
            RigidBodyEntity rigidBodyEntity = (RigidBodyEntity) physicalEntity;
            return rigidBodyEntity.getMass() == 0 ? PhysicalEntityDebugStatus.STATIC : rigidBodyEntity.isActive() ?
                    PhysicalEntityDebugStatus.ACTIVE : PhysicalEntityDebugStatus.INACTIVE;
        }
        return -1;
    }

}
