import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import java.util.ArrayList;
import java.util.List;

public class Museum_GLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;
  private static final float SCENE_SCALE = 32f;

  public Museum_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(15f, 25f, 15f));
  }

  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }

  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width / (float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    room.dispose(gl);
    spotlight.dispose(gl);
    robot.dispose(gl);
  }

  // ***************************************************
  /* INTERACTION
   *
   *
   */

  private int[] poses;
  private Vec3 currentPos;
  private ArrayList<Integer> targetPose;
  private boolean isMoving;
  private float sunIntensity;
  private boolean isLightOn, isSunOn, isSpotlightOn;

  public void changePose(int pose) {
    if (!isMoving && pose != poses[2]) {
      currentPos = robot.getPos();
      robot.pose1();
      isMoving = true;
      moveStartTime = getSeconds();

      if (pose == poses[1] || pose == poses[3]) {
        targetPose.add(pose);
        rotateArray(pose == poses[3], 1);
      }
      else if (pose == poses[0]) {
        targetPose.add(poses[1]);
        targetPose.add(pose);
        rotateArray(false, 2);
      }
      else if (pose == poses[4]) {
        targetPose.add(poses[3]);
        targetPose.add(pose);
        rotateArray(true, 2);
      }
    }
  }

  /* Turn light on/off */
  public void lightswitch() {
    isLightOn = !isLightOn;
  }

  /* Turn sun on/off */
  public void sunOnOff() {
    isSunOn = !isSunOn;
  }

  /* Turn spotlight on/off */
  public void spotlightswitch() {
    isSpotlightOn = !isSpotlightOn;
  }

  /* Rotate array by left or right bool */
  private void rotateArray(boolean left, int n) {
    int len = poses.length - 1;

    for (int i = 0; i < n; i++) {
      // rotate left
      if (left) {
        int first = poses[0];
        // move each elem in arr
        for (int j = 0; j < len; j++) {
          poses[j] = poses[j + 1];
        }
        // move first elem to last
        poses[len] = first;
      }
      // rotate right
      else {
        int last = poses[len];
        // move each elem in arr
        for (int j = len; j > 0; j--) {
          poses[j] = poses[j - 1];
        }
        // move last elem to first
        poses[0] = last;
      }
    }
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   */

  private Camera camera;
  private Mat4 perspective;
  private Material lightOn, spotlightOn;
  private Light light, spotlight;
  private DirLight sun;
  private Room room;
  private Exhibits exhibits;
  private ArcLamp arclamp;
  private Robot robot;

  private void initialise(GL3 gl) {
    // create light on and off material
    lightOn = new Material();
    lightOn.setAmbient(0.3f, 0.3f, 0.3f);
    lightOn.setDiffuse(0.8f, 0.8f, 0.8f);
    lightOn.setSpecular(0.8f, 0.8f, 0.8f);

    // create light
    light = new Light(gl, lightOn);
    light.setCamera(camera);
    light.setPosition(0, SCENE_SCALE * 0.6f, 10f);
    isLightOn = true;

    // create directional light sun
    sun = new DirLight(gl);
    isSunOn = true;

    // create light on and off material
    spotlightOn = new Material();
    spotlightOn.setAmbient(0, 0, 0);
    spotlightOn.setDiffuse(0.8f, 0.8f, 0.8f);
    spotlightOn.setSpecular(1.0f, 1.0f, 1.0f);

    // create spotlight
    arclamp = new ArcLamp(gl, camera, light, sun, SCENE_SCALE);
    spotlight = new Light(gl, spotlightOn);
    spotlight.setCamera(camera);
    spotlight.setPosition(arclamp.getLampPosition());
    isSpotlightOn = true;

    // create room, exhibits, and robot
    room = new Room(gl, camera, light, sun, spotlight, SCENE_SCALE);
    exhibits = new Exhibits(gl, camera, light, sun, SCENE_SCALE);
    robot = new Robot(gl, camera, light, sun);

    // define poses array with pose 1 centered
    poses = new int [] {3, 4, 0, 1, 2};
    currentPos = new Vec3();
    isMoving = false;
    targetPose = new ArrayList<>();
  }

  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    // turn light on/off
    light.setMaterial(isLightOn);
    if (isLightOn) {
      light.render(gl);
    }

    // turn sun on/off
    sun.switchOnOff(isSunOn);
    // update sky texture and sun intensity
    sunIntensity = 1 - room.updateSky(startTime, getSeconds());
    sun.changeIntensity(sunIntensity);

    // animate and render spotlight
    arclamp.updateLamp(startTime, getSeconds());
    arclamp.render(gl);
    spotlight.setDirection(arclamp.getLampDirection());
    // turn spotlight on/off
    spotlight.setMaterial(isSpotlightOn);
    if (isSpotlightOn) {
      spotlight.render(gl);
    }

    room.render(gl);
    exhibits.render(gl);
    if (isMoving) {
      isMoving = robot.updatePos(currentPos, targetPose, moveStartTime, getSeconds());

      if (!isMoving && targetPose.size() > 0) {
        currentPos = robot.getPos();
        moveStartTime = getSeconds();
        isMoving = true;
      }
    }
    robot.render(gl);
  }

  // ***************************************************
  /* TIME
   */

  private double startTime;
  private double moveStartTime;

  private double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }
}
