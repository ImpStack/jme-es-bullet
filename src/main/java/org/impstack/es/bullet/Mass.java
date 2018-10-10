package org.impstack.es.bullet;

import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying the mass of a physical entity. When setting a mass of zero the physical entity will
 * be static.
 *
 * @author remy
 * @since 10/10/18
 */
public class Mass implements EntityComponent {

    private final float mass;

    public Mass(float mass) {
        this.mass = mass;
    }

    public float getMass() {
        return mass;
    }

    @Override
    public String toString() {
        return "Mass{" +
                "mass=" + mass +
                '}';
    }

}
