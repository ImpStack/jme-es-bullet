package org.impstack.es.bullet;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import org.impstack.jme.ApplicationContext;
import org.impstack.jme.es.EntityContainer;

public class VisualSystem extends BaseAppState {

    private final EntityData entityData;
    private Models models;
    private Node node;

    public VisualSystem(EntityData entityData) {
        this.entityData = entityData;
        models = new Models(entityData);
        node = ApplicationContext.INSTANCE.getRootNode();
    }

    @Override
    protected void initialize(Application app) {
        models.start();
    }

    @Override
    protected void cleanup(Application app) {
        models.stop();
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        models.update();
    }

    private class Models extends EntityContainer {

        public Models(EntityData entityData) {
            super(entityData, Model.class, Position.class);
        }

        @Override
        protected void addEntity(Entity e) {
            Geometry geometry = e.get(Model.class).getGeometry();
            geometry.setLocalTranslation(e.get(Position.class).getLocation());
            geometry.setLocalRotation(e.get(Position.class).getRotation());

            node.attachChild(geometry);
        }

        @Override
        protected void updateEntity(Entity e) {
            Geometry geometry = e.get(Model.class).getGeometry();
            geometry.setLocalTranslation(e.get(Position.class).getLocation());
            geometry.setLocalRotation(e.get(Position.class).getRotation());
        }

        @Override
        protected void removeEntity(Entity e) {
            Geometry geometry = e.get(Model.class).getGeometry();
            geometry.removeFromParent();
        }

    }
}
