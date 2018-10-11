package org.impstack.es.bullet;

/**
 * A listener that hooks into the {@link BulletSystem} update loop and notifies about physical object changes.
 * The listeners are called multiple times each frame and should be efficient and few.
 *
 * @author remy
 * @since 10/10/18
 */
public interface PhysicalEntityListener {

    /**
     * Called at the start of the physics frame, before the physics calculation.
     */
    public void startFrame();

    /**
     * Called when a physical entity is added to the physics space
     * @param physicalEntity the added physical entity
     */
    public void physicalEntityAdded(PhysicalEntity physicalEntity);

    /**
     * Called each frame for all attached physical entities to the physics space after the physics calculation.
     * @param physicalEntity the updated physical entity
     */
    public void physicalEntityUpdated(PhysicalEntity physicalEntity);

    /**
     * Called when a physical entity is removed from the physics space
     * @param physicalEntity the remove physical entity
     */
    public void physicalEntityRemoved(PhysicalEntity physicalEntity);

    /**
     * Called at the end of the physics frame
     */
    public void endFrame();
}
