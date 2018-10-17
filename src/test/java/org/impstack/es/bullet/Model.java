package org.impstack.es.bullet;

import com.jme3.scene.Spatial;
import com.simsilica.es.EntityComponent;

public class Model implements EntityComponent {

    private final Spatial spatial;

    public Model(Spatial spatial) {
        this.spatial = spatial;
    }

    public Spatial getSpatial() {
        return spatial;
    }
}
