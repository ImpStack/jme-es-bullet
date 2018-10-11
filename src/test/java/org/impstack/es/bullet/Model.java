package org.impstack.es.bullet;

import com.jme3.scene.Geometry;
import com.simsilica.es.EntityComponent;

public class Model implements EntityComponent {

    private final Geometry geometry;

    public Model(Geometry geometry) {
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
