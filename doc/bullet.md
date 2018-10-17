# Bullet documentation
Some information gathered about bullet during the creation of the Bullet Zay-ES integration library.

## Continuous collision detection
CCD is short for Continuous Collision Detection, which is a workaround for a common problem in game physics: a fast 
moving body might not collide with an obstacle if in one frame it is "before" the obstacle, and in the next one it is 
already "behind" the obstacle. At no frame the fast moving body overlaps with the obstacle, and thus no response is 
created. This is what CCD is for. CCD checks for collisions in between frames, and thus can prevent fast moving objects 
from passing through thin obstacles.

Bullet has built-in support for CCD, but bodies have to be configured properly to enable CCD checks.

When checking for collision in between frames Bullet does not use the full collision shape (or shapes) of a body - this 
would make continuous collision detection too slow. Instead Bullet uses a sphere shape, the so-called "swept sphere". 
"swept" because the sphere is swept from the original position to the new position of the body. So, in order to enable 
CCD checks on a body we have to setup this sphere, and a CCD motion threshold:
```
fastMovingRigidBody.setCcdMotionThreshold(1e-7)
fastMovingRigidBody.setCcdSweptSphereRadius(0.50)
```
We have to set up the swept sphere only on the fast moving dynamic bodies. There is no need to do anything for the 
static or slow moving obstacles.

Set the setCcdMotionThreshold to `0` to disable.

## Capsule collision shape
The size of the capsule is set using a radius and a height value. These values are the radius of the top and bottom semi
spheres and the radius and height of the cylinder.
If you want a capsule with a radius of 1 and a height of 5, you should give a radius of 1 and a height of 3 to the
capsule collison constructor. The final height of the capsule is the radius of the bottom sphere + the height of the 
cylinder + the radius of the top sphere. 

This behaviour is simplified in the `PhysicalShapeFactory.createCapsuleShape(float radius, float height)`.
The height value in this factory method is the final totalling height of the capsule.