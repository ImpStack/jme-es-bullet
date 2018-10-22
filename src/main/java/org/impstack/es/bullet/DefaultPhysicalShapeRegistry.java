package org.impstack.es.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPhysicalShapeRegistry implements PhysicalShapeRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPhysicalShapeRegistry.class);

    private static int counter = 0;
    private final Map<String, CollisionShape> shapes = new ConcurrentHashMap<>();

    @Override
    public PhysicalShape register(CollisionShape collisionShape) {
        return register("collision-shape-" + generateId(), collisionShape);
    }

    public PhysicalShape register(String key, CollisionShape collisionShape) {
        shapes.put(key, collisionShape);
        return new PhysicalShape(key);
    }

    @Override
    public CollisionShape get(PhysicalShape physicalShape) {
        return get(physicalShape.getShapeId());
    }

    public CollisionShape get(String key) {
        return shapes.get(key);
    }

    private static synchronized int generateId() {
        return counter++;
    }
}
