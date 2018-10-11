package org.impstack.es.bullet;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class Position implements EntityComponent {

    private final Vector3f location;
    private final Quaternion rotation;

    public Position(Vector3f location, Quaternion rotation) {
        this.location = location;
        this.rotation = rotation;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Quaternion getRotation() {
        return rotation;
    }
}
