public final class Walls {

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public static final float[] verticesDoor = {      // position, colour, tex coords
          -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.45f,  // top left
          -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
          0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.9f, 0.0f,  // bottom right
          0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.9f, 0.45f   // top right
  };

  public static final float[] verticesLeft = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.45f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
     0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.3f, 0.0f,  // bottom right
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.3f, 0.45f   // top right
  };

  public static final float[] verticesRight = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.6f, 0.45f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.6f, 0.0f,  // bottom left
    0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.9f, 0.0f,  // bottom right
    0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.9f, 0.45f   // top right
  };

  public static final float[] verticesUp = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.3f, 0.45f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.3f, 0.3f,  // bottom left
    0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.6f, 0.3f,  // bottom right
    0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.6f, 0.45f   // top right
  };

  public static final float[] verticesDown = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.3f, 0.15f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.3f, 0.0f,  // bottom left
    0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.6f, 0.0f,  // bottom right
    0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.6f, 0.15f   // top right
  };

  public static final float[][] verticesWindow = {verticesLeft, verticesRight, verticesUp, verticesDown};

  public static final int[] indices = {
      0, 1, 2,
      0, 2, 3
  };
}
