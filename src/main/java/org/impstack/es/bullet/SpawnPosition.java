package org.impstack.es.bullet;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying the location and rotation of a physical entity.
 *
 * @author remy
 * @since 10/10/18
 */
public class SpawnPosition implements EntityComponent {

    private final Vector3f location;
    private final Quaternion rotation;

    public SpawnPosition() {
        this(new Vector3f(), new Quaternion());
    }

    public SpawnPosition(Vector3f location) {
        this(location, new Quaternion());
    }

    public SpawnPosition(Quaternion rotation) {
        this(new Vector3f(), rotation);
    }

    public SpawnPosition(Vector3f location, Quaternion rotation) {
        this.location = location;
        this.rotation = rotation;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "SpawnPosition{" +
                "location=" + location +
                ", rotation=" + rotation +
                '}';
    }
}
