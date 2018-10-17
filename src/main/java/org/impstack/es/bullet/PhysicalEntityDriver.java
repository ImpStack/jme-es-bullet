package org.impstack.es.bullet;

/**
 * A driver that can control a {@link PhysicalEntity}.
 */
public interface PhysicalEntityDriver {

    /**
     * Called when the driver is attached to the physical entity
     * @param entity the entity where the driver is attached to
     */
    public void initialize(PhysicalEntity entity);

    /**
     * Called each frame in the {@link BulletSystem} before the physics simulation
     * @param tpf the time per frame
     */
    public void update(float tpf);

    /**
     * Called when the driver is removed from the physical entity
     * @param entity the entity where the driver is removed from
     */
    public void cleanup(PhysicalEntity entity);

}
