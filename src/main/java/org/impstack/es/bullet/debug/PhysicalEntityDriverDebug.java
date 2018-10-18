package org.impstack.es.bullet.debug;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying the linear velocity and the direction a physical entity is facing.
 */
public class PhysicalEntityDriverDebug implements EntityComponent {

    private final Vector3f linearVelocity;
    private final Vector3f viewDirection;

    public PhysicalEntityDriverDebug(Vector3f linearVelocity, Vector3f viewDirection) {
        this.linearVelocity = linearVelocity;
        this.viewDirection = viewDirection;
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    @Override
    public String toString() {
        return "PhysicalEntityDriverDebug{" +
                "linearVelocity=" + linearVelocity +
                ", viewDirection=" + viewDirection +
                '}';
    }

}
