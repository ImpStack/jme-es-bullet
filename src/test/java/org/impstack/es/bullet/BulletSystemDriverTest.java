package org.impstack.es.bullet;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.StatsAppState;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.CursorMotionEvent;
import com.simsilica.lemur.event.DefaultCursorListener;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.sim.GameSystemManager;
import org.impstack.es.bullet.debug.BulletSystemDebugState;
import org.impstack.es.bullet.debug.PhysicalEntityDebugStatusPublisher;
import org.impstack.jme.JmeLauncher;
import org.impstack.jme.es.BaseEntityDataState;
import org.impstack.jme.scene.GeometryUtils;
import org.impstack.jme.state.BackgroundSystemsState;
import org.impstack.jme.state.GuiLayoutState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulletSystemDriverTest extends JmeLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(BulletSystemDriverTest.class);

    private boolean bulletAttached = false;
    private boolean bulletStarted = false;
    private boolean sceneSetup = false;
    private DebugWindow debugWindow;
    private BulletSystem bulletSystem;
    private PhysicalShapeRegistry physicalShapeRegistry;
    private BasePhysicalEntityDriver entityDriver;

    public static void main(String[] args) {
        new BulletSystemDriverTest().start();
    }

    @Override
    public void init() {
        getStateManager().attachAll(
                new BackgroundSystemsState(),
                new BaseEntityDataState(),
                new GuiLayoutState(),
                new FlyCamAppState(),
                new StatsAppState());

        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle(BaseStyles.GLASS);
    }

    @Override
    public void simpleUpdate(float tpf) {
        BackgroundSystemsState backgroundSystemsState = stateManager.getState(BackgroundSystemsState.class);
        if (!backgroundSystemsState.isInitialized())
            return;

        GameSystemManager systemManager = backgroundSystemsState.getSystemManager();
        // set the entity data
        EntityData entityData = stateManager.getState(BaseEntityDataState.class).getEntityData();

        if (!bulletAttached) {
            LOG.debug("Start Bullet");

            // register the collision shape
            physicalShapeRegistry = new BasePhysicalShapeRegistry();
            physicalShapeRegistry.register(new PhysicalShape("capsule"), CollisionShapeHelper.createCapsuleShape(0.25f, 1.8f, true));

            bulletSystem = new BulletSystem(entityData, physicalShapeRegistry);
            systemManager.enqueue(() -> systemManager.register(BulletSystem.class, bulletSystem));
            BulletSystemDebugState bulletDebugState = new BulletSystemDebugState(entityData, physicalShapeRegistry);
            bulletDebugState.setDriverDebugOffset(new Vector3f(0, 1, 0));
            stateManager.attach(bulletDebugState);
            bulletAttached = true;
        }

        if (!bulletStarted && bulletSystem.isInitialized()) {
            LOG.debug("Setup Bullet");
            bulletSystem.getPhysicsSpace().setGravity(new Vector3f(0, -20f, 0));
            bulletSystem.addPhysicalEntityListener(new PhysicalEntityPositionPublisher(entityData));
            bulletSystem.addPhysicalEntityListener(new PhysicalEntityDebugStatusPublisher(entityData));
            bulletStarted = true;
        }

        if (bulletStarted && !sceneSetup) {
            LOG.debug("Setup scene");
            getStateManager().getState(FlyCamAppState.class).getCamera().setDragToRotate(true);
            stateManager.attach(new VisualSystem(entityData));

            Geometry floor = new GeometryUtils(this).createGeometry(new Quad(10, 10), ColorRGBA.LightGray);
            floor.move(-5, 0, 5);
            floor.rotate(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
            CursorEventControl.addListenersToSpatial(floor, new DestinationListener());
            rootNode.attachChild(floor);

            PhysicsRigidBody floorPhysicsObject = new PhysicsRigidBody(CollisionShapeFactory.createMeshShape(floor), 0);
            floorPhysicsObject.setPhysicsLocation(floor.getWorldTranslation());
            floorPhysicsObject.setPhysicsRotation(floor.getWorldRotation());
            bulletSystem.getPhysicsSpace().addCollisionObject(floorPhysicsObject);

            EntityId entityId = entityData.createEntity();
            entityData.setComponents(entityId,
                    new SpawnPosition(new Vector3f(0, 0.1f, 0)),
                    new Mass(80),
                    new PhysicalShape("capsule"),
                    new Model(createSpatial())
            );

            entityDriver = new BasePhysicalEntityDriver();
            entityDriver.setEntityData(entityData);
            entityDriver.setDebugEnabled(true);
            bulletSystem.setPhysicalEntityDriver(entityId, entityDriver);

            debugWindow = new DebugWindow();
            stateManager.getState(GuiLayoutState.class).add(debugWindow, BorderLayout.Position.East);
            sceneSetup = true;
        }

        if (bulletStarted) {
            debugWindow.setBulletFps(bulletSystem.getFps());
        }
    }

    private Spatial createSpatial() {
        Geometry sphere = new GeometryUtils(this).createGeometry(new Sphere(16, 16, 0.2f), ColorRGBA.Blue);
        sphere.move(0, 0.2f, 0);

        Geometry arrow = new GeometryUtils(this).createGeometry(new Arrow(Vector3f.UNIT_Z), ColorRGBA.Red);
        arrow.move(0, 0.4f, 0);

        Node node = new Node("player-debug");
        node.attachChild(sphere);
        node.attachChild(arrow);

        return node;
    }

    private class DestinationListener extends DefaultCursorListener {

        private CollisionResult collisionResult;

        @Override
        protected void click(CursorButtonEvent event, Spatial target, Spatial capture) {
            if (collisionResult == null)
                return;

            Vector3f location = entityDriver.getRigidBodyEntity().getLocation().setY(0);
            Vector3f targetLocation = collisionResult.getContactPoint().setY(0);
            Vector3f direction = targetLocation.subtract(location);

            LOG.info("Direction: {}", direction);

            entityDriver.setMoveDirection(direction);
            entityDriver.setMoveSpeed(1);
            entityDriver.setViewDirection(direction);
            entityDriver.setTurningSpeed(5);
        }

        @Override
        public void cursorMoved(CursorMotionEvent event, Spatial target, Spatial capture) {
            collisionResult = event.getCollision();
        }
    }

    private void setDebugView(boolean enabled) {
        BulletSystemDebugState debugAppState = stateManager.getState(BulletSystemDebugState.class);
        debugAppState.setEnabled(enabled);
        LOG.debug("Physics debug {}", debugAppState.isEnabled());
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
