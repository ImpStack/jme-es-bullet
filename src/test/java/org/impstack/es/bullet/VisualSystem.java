package org.impstack.es.bullet;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityContainer;
import com.simsilica.es.EntityData;
import org.impstack.jme.ApplicationContext;
import org.impstack.jme.es.Position;

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

    private class Models extends EntityContainer<Geometry> {

        public Models(EntityData entityData) {
            super(entityData, Model.class, Position.class);
        }

        @Override
        protected Geometry addObject(Entity e) {
            Geometry geometry = e.get(Model.class).getGeometry();
            geometry.setLocalTranslation(e.get(Position.class).getLocation());
            geometry.setLocalRotation(e.get(Position.class).getRotation());
            node.attachChild(geometry);

            return geometry;
        }

        @Override
        protected void updateObject(Geometry object, Entity e) {
            object.setLocalTranslation(e.get(Position.class).getLocation());
            object.setLocalRotation(e.get(Position.class).getRotation());
        }

        @Override
        protected void removeObject(Geometry object, Entity e) {
            object.removeFromParent();
        }

    }
}
