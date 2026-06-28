# render3d

Rendering engine to render 3D images with a convenient and simple interface that can be used for various purposes.  
To use the rendering engine, programmetically a scene object is created and the following can be specified.

* Camera
  * Location of the camera.
  * Viewing direction of the camera or focus point of the camera (point where the camera is pointing to).
  * Camera viewing angle (or viewing angle per pixel).
  * Optionally, a camera orbit can be specified (orbit around point, radius, tilt in vertical plane, movement speed in horizontal plane, movement speed in vertical plane).
  * Camera position and viewing direction can be updated (for example to simulate movement), for example in response to user inputs.
* Objects.
  * 3D objects can be added. Typical geometric shapes (for example a sphere or cube) can be added using factory methods.
  * Object coordinates can be updated.
* Miscelaneous options
  * A background can be added.
  * Rendering options can be selected (view distance, toggling drawing of object circumference lines, etc).
* Screen coordinates of a point in 3D can be polled and for example be used to add annotations (text) to objects.

## How it works
Objects are composed of:
* A center of mass coordinate
* A collection of surface objects that together compose the 3D object.
* Each surface object is a polygon in 3D space and contains a collection of its vertices relative to the object center of mass in 3D space.
* Each surface objects has attributes for its color (can be translucent or transparent) and instructions to how the color should be modified based on its coordinates (for example based on distance to camera, its relative distance compared to the other surfaces in the object, based on its coordinates)  

To render the 3D images:
* The coordinates of each object and vertex relative to the camera are calculated in spherical coordinates.
* The angle relative to the camera viewing direction is calculated.
* Objects and object surface objects are sorted based on distance to the camera (furthest away objects are rendered first).
* For each object surface, it is calculated if (part) of the surface is on screen. If not, the surface is not rendered.
* For all surfaces objects that are at least partly on screen, these are rendered starting with the furthest away surface.
* The surface objects, its color is modified based on its specified attributes (for example based on its z value for the graph example, or based on distance to the camera for the 1st person game).

## Code example
Code can be called as:  
```
Scene scene = new Scene();
scene.addObject(Object3DFactory.createSphere(8,0,0, 5, new Color(230,250,50)));
scene.addObject(Object3DFactory.createCube(-5, 0, -10));
scene.setCamera(30,30,0);
scene.focusOn(0, 0, 0);
int width = 1000; int height = 1000;
BufferedImage image = scene.get3DView(width, height);
```

## Example
<img width="1131" height="820" alt="graph" src="https://github.com/user-attachments/assets/ebe7f085-915c-4e30-9af1-81e598a8fa68" />
