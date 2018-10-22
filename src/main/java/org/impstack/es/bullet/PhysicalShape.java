package org.impstack.es.bullet;

import com.simsilica.es.EntityComponent;

/**
 * An entity component specifying the collision shape of a physical entity. The collision shape can be retrieved with
 * the given id from the {@link PhysicalShapeRegistry}
 *
 * @author remy
 * @since 10/10/18
 */
public class PhysicalShape implements EntityComponent {

    private final String shapeId;

    public PhysicalShape(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getShapeId() {
        return shapeId;
    }

    @Override
    public String toString() {
        return "PhysicalShape{" +
                "shapeId='" + shapeId + '\'' +
                '}';
    }

}
