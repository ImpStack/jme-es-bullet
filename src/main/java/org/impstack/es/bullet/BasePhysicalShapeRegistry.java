package org.impstack.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A base physical shape registry implementation that uses an internal thread-safe index to look up collision shapes.
 * The load method can be overwritten to allow for custom load behaviour for collision shapes that aren't found in the
 * registry.
 */
public class BasePhysicalShapeRegistry implements PhysicalShapeRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(BasePhysicalShapeRegistry.class);

    private final Map<String, CollisionShape> registry = new ConcurrentHashMap<>();

    @Override
    public CollisionShape register(PhysicalShape physicalShape, CollisionShape collisionShape) {
        registry.put(physicalShape.getShapeId(), collisionShape);
        LOG.trace("Registering {} -> {}", physicalShape, collisionShape);
        return collisionShape;
    }

    @Override
    public CollisionShape get(PhysicalShape physicalShape) {
        CollisionShape collisionShape = registry.get(physicalShape.getShapeId());
        if (collisionShape != null) {
            LOG.trace("Retrieving {} -> {}", physicalShape, collisionShape);
            return collisionShape;
        }

        // collision shape isn't found in the registry. Use the custom loadCollisionShape() method.
        collisionShape = loadCollisionShape(physicalShape);
        if (collisionShape == null) {
            throw new IllegalArgumentException("No collision shape could be retrieved for " + physicalShape);
        }

        return register(physicalShape, collisionShape);
    }

    protected CollisionShape loadCollisionShape(PhysicalShape physicalShape) {
        return null;
    }

}
