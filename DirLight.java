import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class DirLight {

  private Material material, materialOn, materialOff;
  private Vec3 ambient, diffuse, specular, ambient0, diffuse0, specular0;
  private Camera camera;
  private Vec3 direction;
  private boolean isOn;

  public DirLight(GL3 gl) {
    // set default on values for material
    ambient = new Vec3(0.2f, 0.2f, 0.2f);
    diffuse = new Vec3(1f, 1f, 1f);
    specular = new Vec3(0.5f, 0.5f, 0.5f);

    // set values for off
    ambient0 = new Vec3(0, 0, 0);
    diffuse0 = new Vec3(0, 0, 0);
    specular0 = new Vec3(0, 0, 0);

    // set material to on values
    material = new Material();
    material.setAmbient(ambient);
    material.setDiffuse(diffuse);
    material.setSpecular(specular);

    direction = new Vec3(1f, -1f, 0);
  }

  public void setDirection(float x, float y, float z) {
    direction.x = x;
    direction.y = y;
    direction.z = z;
  }

  public Vec3 getDirection() {
    return direction;
  }

  /* Turn light on/off */
  public void switchOnOff(boolean on) {
    isOn = on;
    // if light is off set material values to zero
    if (!on) {
      material.setAmbient(ambient0);
      material.setDiffuse(diffuse0);
      material.setSpecular(specular0);
    }
  }

  public Material getMaterial() {
    return material;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  /* Change light intensity values based on parameter */
  public void changeIntensity(float intensity) {
    // check light is on
    if (isOn) {
      // multiply by intensity
      material.setAmbient(Vec3.multiply(ambient, intensity));
      material.setDiffuse(Vec3.multiply(diffuse, intensity));
      material.setSpecular(Vec3.multiply(specular, intensity));
    }
  }
}
