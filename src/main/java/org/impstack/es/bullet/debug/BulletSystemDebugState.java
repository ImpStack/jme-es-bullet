package org.impstack.es.bullet.debug;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;
import org.impstack.es.bullet.PhysicalShape;
import org.impstack.jme.ApplicationContext;
import org.impstack.jme.es.Position;
import org.impstack.jme.scene.SpatialUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An application state that shows debug meshes of all physical entities that are managed by the ES. Objects that are
 * directly added to the physics space will not be shown.
 * For a physical entity to be rendered by the BulletDebugState, a {@link PhysicalEntityDebugStatus} component and
 * {@link Position} component are required on the entity. Both components can be published by adding the
 * {@link PhysicalEntityDebugStatusPublisher} and {@link org.impstack.es.bullet.PhysicalEntityPositionPublisher} to the
 * {@link org.impstack.es.bullet.BulletSystem}
 */
public class BulletSystemDebugState extends BaseAppState {

    private static final Logger LOG = LoggerFactory.getLogger(BulletSystemDebugState.class);

    private final EntityData entityData;

    private Node debugNode;
    private Material activeMaterial;
    private ColorRGBA activeColor = ColorRGBA.Green;
    private Material inActiveMaterial;
    private ColorRGBA inActiveColor = ColorRGBA.Blue;
    private Material staticMaterial;
    private ColorRGBA staticColor = ColorRGBA.White;
    private DebugObjects debugObjects;

    public BulletSystemDebugState(EntityData entityData) {
        this.entityData = entityData;
    }

    @Override
    protected void initialize(Application app) {
        activeMaterial = app.getAssetManager().loadMaterial("Materials/Wireframe.j3m");
        activeMaterial.setColor("Color", activeColor);
        inActiveMaterial = activeMaterial.clone();
        inActiveMaterial.setColor("Color", inActiveColor);
        staticMaterial = activeMaterial.clone();
        staticMaterial.setColor("Color", staticColor);

        debugNode = new Node("physicsDebugNode");
        debugObjects = new DebugObjects(entityData);
    }

    @Override
    protected void onEnable() {
        ApplicationContext.INSTANCE.getRootNode().attachChild(debugNode);
        debugObjects.start();
    }

    @Override
    public void update(float tpf) {
        debugObjects.update();
    }

    @Override
    protected void onDisable() {
        debugNode.removeFromParent();
        debugObjects.stop();
    }

    @Override
    protected void cleanup(Application app) {
    }

    /**
     * returns the material based on the status of the physical entity.
     */
    private Material getMaterial(int status) {
        if (status == PhysicalEntityDebugStatus.ACTIVE) {
            return activeMaterial;
        } else if (status == PhysicalEntityDebugStatus.INACTIVE) {
            return inActiveMaterial;
        } else if (status == PhysicalEntityDebugStatus.STATIC) {
            return staticMaterial;
        }
        return null;
    }

    private class DebugObjects extends EntityContainer<Spatial> {

        public DebugObjects(EntityData ed) {
            super(ed, PhysicalEntityDebugStatus.class, Position.class);
        }

        @Override
        protected Spatial addObject(Entity e) {
            PhysicalShape physicalShape = entityData.getComponent(e.getId(), PhysicalShape.class);
            Position position = e.get(Position.class);

            Spatial debugShape = DebugShapeFactory.getDebugShape(physicalShape.getCollisionShape());
            debugShape.setName("debug-shape-" + e.getId());
            debugShape.setLocalTranslation(position.getLocation());
            debugShape.setLocalRotation(position.getRotation());
            debugShape.setMaterial(getMaterial(e.get(PhysicalEntityDebugStatus.class).getStatus()));

            LOG.trace("Adding {} to {}", debugShape, debugNode);
            debugNode.attachChild(debugShape);

            return debugShape;
        }

        @Override
        protected void updateObject(Spatial object, Entity e) {
            Position position = e.get(Position.class);

            object.setLocalTranslation(position.getLocation());
            object.setLocalRotation(position.getRotation());

            PhysicalEntityDebugStatus status = e.get(PhysicalEntityDebugStatus.class);
            SpatialUtils.getGeometry(object).ifPresent(g -> {
                Material material = getMaterial(status.getStatus());
                if (!g.getMaterial().equals(material)) {
                    object.setMaterial(material);
                }
            });
        }

        @Override
        protected void removeObject(Spatial object, Entity e) {
            LOG.trace("Removing {} from {}", object, object.getParent());
            object.removeFromParent();
        }

    }

}
