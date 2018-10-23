package org.impstack.es.bullet;

import com.jme3.app.StatsAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.BaseStyles;
import org.impstack.es.bullet.debug.BulletSystemDebugState;
import org.impstack.es.bullet.debug.PhysicalEntityDebugStatusPublisher;
import org.impstack.jme.JmeLauncher;
import org.impstack.jme.es.BaseEntityDataState;
import org.impstack.jme.es.Decay;
import org.impstack.jme.es.DecaySystem;
import org.impstack.jme.es.Position;
import org.impstack.jme.state.BackgroundSystemsState;
import org.impstack.jme.state.GuiLayoutState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * An application to test the {@link BulletSystem}
 * A static solid quad is drawn, using the left mouse button you can drop boxes on the quad, using the right mouse
 * button you can drop spheres on the quad.
 * A debug window is shown to toggle the physics debug view and display the bullet fps.
 */
public class BulletSystemTest extends JmeLauncher implements ActionListener {

    private static final Logger LOG = LoggerFactory.getLogger(BulletSystemTest.class);
    private static final String ADD_BOX_ENTITY_MAPPING = "addBoxEntity";
    private static final String ADD_SPHERE_ENTITY_MAPPING = "addSphereEntity";
    private static final Trigger ADD_BOX_ENTITY_TRIGGER = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private static final Trigger ADD_SPHERE_ENTITY_TRIGGER = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);

    private boolean bulletAttached = false;
    private boolean bulletStarted = false;
    private boolean sceneSetup = false;

    private DebugWindow debugWindow;
    private BulletSystem bulletSystem;
    private PhysicalShapeRegistry physicalShapeRegistry;
    private int entitiesPerClick = 5;

    public static void main(String[] args) {
        new BulletSystemTest().start();
    }

    @Override
    public void init() {
        getStateManager().attachAll(
                new BackgroundSystemsState(),
                new BaseEntityDataState(),
                new GuiLayoutState(),
                new StatsAppState());

        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle(BaseStyles.GLASS);

        rootNode.addLight(new AmbientLight(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f)));
        rootNode.addLight(new DirectionalLight(new Vector3f(-0.1f, -1f, -0.1f).normalizeLocal(), ColorRGBA.White));

        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 1024, 4);
        shadowFilter.setLight((DirectionalLight) rootNode.getLocalLightList().get(1));
        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        shadowFilter.setEdgesThickness(2);
        shadowFilter.setShadowIntensity(0.75f);
        shadowFilter.setLambda(0.65f);
        shadowFilter.setShadowZExtend(75);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(shadowFilter);

        viewPort.addProcessor(fpp);

        inputManager.addMapping(ADD_BOX_ENTITY_MAPPING, ADD_BOX_ENTITY_TRIGGER);
        inputManager.addMapping(ADD_SPHERE_ENTITY_MAPPING, ADD_SPHERE_ENTITY_TRIGGER);
        inputManager.addListener(this, ADD_BOX_ENTITY_MAPPING, ADD_SPHERE_ENTITY_MAPPING);
    }

    @Override
    public void simpleUpdate(float tpf) {
        BackgroundSystemsState backgroundSystemsState = stateManager.getState(BackgroundSystemsState.class);
        if (!backgroundSystemsState.isInitialized())
            return;

        // get the entity data
        EntityData entityData = stateManager.getState(BaseEntityDataState.class).getEntityData();

        if (!bulletAttached) {
            LOG.debug("Start Bullet");

            // register physical shapes
            physicalShapeRegistry = new BasePhysicalShapeRegistry();
            physicalShapeRegistry.register(new PhysicalShape("static-box"), new BoxCollisionShape(Vector3f.UNIT_XYZ));
            physicalShapeRegistry.register(new PhysicalShape("box"), new BoxCollisionShape(new Vector3f(.5f, .5f, .5f)));
            physicalShapeRegistry.register(new PhysicalShape("sphere"), new SphereCollisionShape(.5f));

            bulletSystem = new BulletSystem(entityData, physicalShapeRegistry);
            backgroundSystemsState.enqueue(() -> backgroundSystemsState.attach(bulletSystem));
            bulletAttached = true;
        }

        if (!bulletStarted && bulletSystem.isInitialized()) {
            LOG.debug("Setup Bullet");
            bulletSystem.getPhysicsSpace().setGravity(new Vector3f(0, -20f, 0));
            bulletSystem.addPhysicalEntityListener(new PhysicalEntityPositionPublisher(entityData));
            bulletSystem.addPhysicalEntityListener(new PhysicalEntityDebugStatusPublisher(entityData));
            bulletStarted = true;
        }

        if (!sceneSetup && bulletStarted) {
            LOG.debug("Setup Scene");
            stateManager.attach(new VisualSystem(entityData));
            stateManager.attach(new BulletSystemDebugState(entityData, physicalShapeRegistry));
            backgroundSystemsState.enqueue(() -> backgroundSystemsState.attach(new DecaySystem(entityData)));

            Geometry floor = new Geometry("floor", new Quad(30, 30));
            floor.setMaterial(getMaterial(ColorRGBA.LightGray));
            floor.setShadowMode(RenderQueue.ShadowMode.Receive);
            floor.move(-15, 0, 15);
            floor.rotate(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
            rootNode.attachChild(floor);
            PhysicsRigidBody floorPhysicsObject = new PhysicsRigidBody(CollisionShapeFactory.createMeshShape(floor), 0);
            floorPhysicsObject.setPhysicsLocation(floor.getWorldTranslation());
            floorPhysicsObject.setPhysicsRotation(floor.getWorldRotation());
            bulletSystem.getPhysicsSpace().addCollisionObject(floorPhysicsObject);

            // a few static physical entities
            int total = 10;
            for (int i = 0; i < total; i++) {
                Geometry box = new Geometry("box", new Box(1, 1, 1));
                box.setMaterial(getMaterial(ColorRGBA.Brown));
                box.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

                entityData.setComponents(entityData.createEntity(),
                        new Mass(0),
                        new SpawnPosition(new Vector3f(FastMath.nextRandomInt(-14, 14), 1, FastMath.nextRandomInt(-14, 14))),
                        new PhysicalShape("static-box"),
                        new Model(box));
            }

            // some camera stuff
            cam.setLocation(new Vector3f(-30, 30, 30));
            cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            sceneSetup = true;

            // setup the debug window
            debugWindow = new DebugWindow();
            stateManager.getState(GuiLayoutState.class).add(debugWindow, BorderLayout.Position.East);
        }

        if (bulletStarted) {
            debugWindow.setBulletFps(bulletSystem.getFps());
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (ADD_BOX_ENTITY_MAPPING.equals(name) && !isPressed) {
            for (int i = 0; i < entitiesPerClick; i++) {
                addEntity("box");
            }
        } else if (ADD_SPHERE_ENTITY_MAPPING.equals(name) && !isPressed) {
            for (int i = 0; i < entitiesPerClick; i++) {
                addEntity("sphere");
            }
        }
    }

    private void setDebugView(boolean enabled) {
        BulletSystemDebugState debugAppState = stateManager.getState(BulletSystemDebugState.class);
        debugAppState.setEnabled(enabled);
        LOG.debug("Physics debug {}", debugAppState.isEnabled());
    }

    private Material getMaterial(ColorRGBA color) {
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Ambient", color);
        material.setColor("Diffuse", color);
        return material;
    }

    private void addEntity(String type) {
        float radius = 0.5f;
        float mass = 50;
        int x = FastMath.nextRandomInt(0, 20) - 10;
        int y = FastMath.nextRandomInt(10, 15);
        int z = FastMath.nextRandomInt(0, 20) - 10;

        Vector3f location = new Vector3f(x, y, z);

        Geometry geometry;
        PhysicalShape physicalShape;

        if ("box".equals(type)) {
            geometry = new Geometry("box", new Box(radius, radius, radius));
        } else if ("sphere".equals(type)) {
            geometry = new Geometry("sphere", new Sphere(16, 16, radius));
        } else {
            throw new NotImplementedException();
        }

        geometry.setMaterial(getMaterial(ColorRGBA.randomColor()));
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        // create an entity
        EntityData entityData = stateManager.getState(BaseEntityDataState.class).getEntityData();
        EntityId box = entityData.createEntity();
        entityData.setComponents(box,
                new Mass(mass),
                new SpawnPosition(location),
                new PhysicalShape(type),
                new Model(geometry),
                new Position(location.clone(), new Quaternion()),
                new Decay(60000));

        LOG.info("Adding entity at {}", new Vector3f(x, y, z));
    }

    private class DebugWindow extends Container {

        private Checkbox bulletDebugCheckbox;
        private Label bulletFpsLabel;

        public DebugWindow() {
            super(new SpringGridLayout(Axis.Y, Axis.X));

            bulletDebugCheckbox = addChild(new Checkbox("Debug view"));
            bulletDebugCheckbox.setChecked(false);
            bulletDebugCheckbox.addClickCommands(cmd -> setDebugView(bulletDebugCheckbox.isChecked()));
            setDebugView(bulletDebugCheckbox.isChecked());
            bulletDebugCheckbox.setInsets(new Insets3f(5, 5, 5, 5));

            bulletFpsLabel = addChild(new Label("Bullet fps"));
            bulletFpsLabel.setInsets(new Insets3f(5, 5, 5, 5));
        }

        public void setBulletFps(int fps) {
            bulletFpsLabel.setText(String.format("%d Bullet fps", fps));
        }

    }

}
