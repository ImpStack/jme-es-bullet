package org.impstack.es.bullet.debug;

import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying the status of the physical entity in the physics space.
 */
public class PhysicalDebugStatus implements EntityComponent {

    public static final int STATIC = 0;
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 2;

    private final int status;

    public PhysicalDebugStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "PhysicalDebugStatus{" +
                "status=" + status +
                '}';
    }

}
