package org.impstack.es.bullet;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;
import com.simsilica.sim.AbstractGameSystem;
import com.simsilica.sim.SimTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A bullet implementation that can run as a {@link com.simsilica.sim.GameSystem} and manages physical entities.
 * Entities that have the right entity components, will be picked up and added/updated/removed from the Bullet physics
 * space. Using listeners other systems can be notified about changes of the entities.
 *
 *
 * and handles {@link PhysicalEntity}.
 * An entity that has a {@link PhysicalShape}, {@link Mass} and {@link SpawnPosition} will be handled by the system.
 *
 * @author remy
 * @since 10/10/18
 */
public class BulletSystem extends AbstractGameSystem {

    private static final Logger LOG = LoggerFactory.getLogger(BulletSystem.class);

    private EntityData entityData;
    private PhysicsSpace physicsSpace;
    private PhysicsSpace.BroadphaseType broadphaseType = PhysicsSpace.BroadphaseType.DBVT;
    private Vector3f worldMin = new Vector3f(-10000f, -10000f, -10000f);
    private Vector3f worldMax = new Vector3f(10000f, 10000f, 10000f);
    private float speed = 1.0f;
    private boolean calculateFps = true;
    private float timeCounter;
    private int frameCounter;
    private int fps;

    // a list of physical entity listeners
    private SafeArrayList<PhysicalEntityListener> physicalEntityListeners = new SafeArrayList<>(PhysicalEntityListener.class);
    // the container of all the rigidbodies
    private RigidBodyContainer rigidBodyContainer;

    public BulletSystem() {
    }

    public BulletSystem(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize() {
        if (entityData == null)
            throw new IllegalStateException("EntityData is not set when initializing BulletSystem!");

        physicsSpace = new PhysicsSpace(worldMin, worldMax, broadphaseType);

        rigidBodyContainer = new RigidBodyContainer(entityData);
    }

    @Override
    public void start() {
        rigidBodyContainer.start();
    }

    @Override
    public void update(SimTime time) {
        startFrame();

        // perform fps calculation
        if (calculateFps) {
            timeCounter += time.getTpf();
            frameCounter++;
            if (timeCounter >= 1) {
                // one second has passed
                fps = (int) (frameCounter / timeCounter);
                timeCounter = 0;
                frameCounter = 0;
            }
        }

        rigidBodyContainer.update();

        float t = (float) time.getTpf() * speed;
        if (t != 0) {
            physicsSpace.update(t);
            physicsSpace.distributeEvents();

            for (PhysicalEntity entity : rigidBodyContainer.getArray()) {
                physicalObjectUpdated(entity);
            }

        }

        endFrame();
    }

    @Override
    public void stop() {
        rigidBodyContainer.stop();
    }

    @Override
    protected void terminate() {
        physicsSpace.destroy();
    }

    public void setEntityData(EntityData entityData) {
        if (isInitialized())
            throw new IllegalStateException("BulletSystem is already initialized!");

        this.entityData = entityData;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    public int getFps() {
        return fps;
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

    private class RigidBodyContainer extends EntityContainer<RigidBodyEntity> {

        public RigidBodyContainer(EntityData ed) {
            super(ed, PhysicalShape.class, Mass.class, SpawnPosition.class);
        }

        @Override
        protected RigidBodyEntity[] getArray() {
            return super.getArray();
        }

        @Override
        protected RigidBodyEntity addObject(Entity e) {
            Mass mass = e.get(Mass.class);
            SpawnPosition position = e.get(SpawnPosition.class);

            RigidBodyEntity result = new RigidBodyEntity(e.getId(), e.get(PhysicalShape.class), mass);

            result.setPhysicsLocation(position.getLocation());
            result.setPhysicsRotation(position.getRotation());

            LOG.trace("Adding {} to {}", result, physicsSpace);
            physicsSpace.addCollisionObject(result);
            physicalObjectAdded(result);

            return result;
        }

        @Override
        protected void updateObject(RigidBodyEntity object, Entity e) {
            // we only update the position
            SpawnPosition position = e.get(SpawnPosition.class);

            LOG.trace("Moving {} to {}", object, position);
            object.setPhysicsLocation(position.getLocation());
            object.setPhysicsRotation(position.getRotation());

            physicalObjectUpdated(object);
        }

        @Override
        protected void removeObject(RigidBodyEntity object, Entity e) {
            LOG.trace("Removing {} from {}", object, physicsSpace);
            physicsSpace.removeCollisionObject(object);
            physicalObjectRemoved(object);
        }
    }

}



