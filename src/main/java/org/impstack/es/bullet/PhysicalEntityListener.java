package org.impstack.es.bullet;

/**
 * @author remy
 * @since 10/10/18
 */
public interface PhysicalEntityListener {

    /**
     * Called at the start of the physics frame
     */
    public void startFrame();

    /**
     * Called when a physical entity is added to the physics space
     * @param physicalEntity the added physical entity
     */
    public void physicalEntityAdded(PhysicalEntity physicalEntity);

    /**
     * Called each physics frame for all attached physical entities to the physics space
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
