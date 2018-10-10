package org.impstack.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.sim.AbstractGameSystem;
import com.simsilica.sim.SimTime;
import org.impstack.jme.es.EntityContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author remy
 * @since 10/10/18
 */
public class BulletSystem extends AbstractGameSystem {

    private static final Logger LOG = LoggerFactory.getLogger(BulletSystem.class);

    private EntityData entityData;
    private PhysicsSpace physicsSpace;
    private CollisionDispatcher collisionDispatcher = new CollisionDispatcher();
    private PhysicsSpace.BroadphaseType broadphaseType = PhysicsSpace.BroadphaseType.DBVT;
    private Vector3f worldMin = new Vector3f(-10000f, -10000f, -10000f);
    private Vector3f worldMax = new Vector3f(10000f, 10000f, 10000f);
    private float speed = 1.0f;
    // a list of physical entity listeners
    private SafeArrayList<PhysicalEntityListener> physicalEntityListeners = new SafeArrayList<>(PhysicalEntityListener.class);
    // a map of all static and dynamic entities attached to the physics space
    private Map<EntityId, RigidBodyEntity> rigidBodyMap = new ConcurrentHashMap<>();

    private PhysicalEntitiesContainer physicalEntities;

    public BulletSystem() {
    }

    public BulletSystem(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize() {
        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);
        physicsSpace.addCollisionListener(collisionDispatcher);

        physicalEntities = new PhysicalEntitiesContainer(entityData);
    }

    @Override
    public void start() {
        physicalEntities.start();
    }

    @Override
    public void update(SimTime time) {
        startFrame();

        physicalEntities.update();

        float t = (float) time.getTpf() * speed;
        if (t != 0) {
            physicsSpace.update(t);
            physicsSpace.distributeEvents();
        }

        endFrame();
    }

    @Override
    public void stop() {
        physicalEntities.stop();
    }

    @Override
    protected void terminate() {
        physicsSpace.destroy();
    }

    public void setEntityData(EntityData entityData) {
        this.entityData = entityData;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void addPhysicalEntityListener(PhysicalEntityListener physicalEntityListener) {
        physicalEntityListeners.add(physicalEntityListener);
    }

    public void removePhysicalEntityListener(PhysicalEntityListener physicalEntityListener) {
        physicalEntityListeners.remove(physicalEntityListener);
    }

    private void startFrame() {
        for (PhysicalEntityListener listener : physicalEntityListeners.getArray()) {
            listener.startFrame();
        }
    }

    private void physicalObjectAdded(PhysicalEntity physicalEntity) {
        for (PhysicalEntityListener listener : physicalEntityListeners.getArray()) {
            listener.physicalEntityAdded(physicalEntity);
        }
    }

    private void physicalObjectUpdated(PhysicalEntity physicalEntity) {
        for (PhysicalEntityListener listener : physicalEntityListeners.getArray()) {
            listener.physicalEntityUpdated(physicalEntity);
        }
    }

    private void physicalObjectRemoved(PhysicalEntity physicalEntity) {
        for (PhysicalEntityListener listener : physicalEntityListeners.getArray()) {
            listener.physicalEntityRemoved(physicalEntity);
        }
    }

    private void endFrame() {
        for (PhysicalEntityListener listener : physicalEntityListeners.getArray()) {
            listener.endFrame();
        }
    }

    private class PhysicalEntitiesContainer extends EntityContainer {

        public PhysicalEntitiesContainer(EntityData entityData) {
            super(entityData, PhysicalShape.class, Mass.class, SpawnPosition.class);
        }

        @Override
        protected void addEntity(Entity e) {
            RigidBodyEntity rigidBodyEntity = new RigidBodyEntity(e.getId(), e.get(PhysicalShape.class), e.get(Mass.class));

            SpawnPosition position = e.get(SpawnPosition.class);
            rigidBodyEntity.setPhysicsLocation(position.getLocation());
            rigidBodyEntity.setPhysicsRotation(position.getRotation());

            LOG.trace("Adding {} to {}", rigidBodyEntity, physicsSpace);
            physicsSpace.addCollisionObject(rigidBodyEntity);
            rigidBodyMap.put(e.getId(), rigidBodyEntity);

            physicalObjectAdded(rigidBodyEntity);
        }

        @Override
        protected void updateEntity(Entity e) {
            // we only update the position
            SpawnPosition position = e.get(SpawnPosition.class);

            RigidBodyEntity rigidBodyEntity = rigidBodyMap.get(e.getId());
            LOG.trace("Moving {} to {}", rigidBodyEntity, position);
            rigidBodyEntity.setPhysicsLocation(position.getLocation());
            rigidBodyEntity.setPhysicsRotation(position.getRotation());

            physicalObjectUpdated(rigidBodyEntity);
        }

        @Override
        protected void removeEntity(Entity e) {
            RigidBodyEntity rigidBodyEntity = rigidBodyMap.remove(e.getId());
            LOG.trace("Removing {} from {}", rigidBodyEntity, physicsSpace);
            physicsSpace.removeCollisionObject(rigidBodyEntity);

            physicalObjectRemoved(rigidBodyEntity);
        }

    }

}



