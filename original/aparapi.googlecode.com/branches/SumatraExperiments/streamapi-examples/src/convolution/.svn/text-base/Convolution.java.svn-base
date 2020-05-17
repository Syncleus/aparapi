package convolution;

import java.io.File;

public class Convolution{

  final static class ImageConvolution{
    private float convMatrix3x3[];

    private int width, height;

    private byte imageIn[], imageOut[];

    public void processPixel(int x, int y, int w, int h) {
      float accum = 0f;
      int count = 0;
      for (int dx = -3; dx < 6; dx += 3) {
        for (int dy = -1; dy < 2; dy += 1) {
          int rgb = 0xff & imageIn[((y + dy) * w) + (x + dx)];

          accum += rgb * convMatrix3x3[count++];
        }
      }
      byte value = (byte) (Math.max(0, Math.min((int) accum, 255)));
      imageOut[y * w + x] = value;

    }

    public void applyConvolution(float[] _convMatrix3x3, byte[] _imageIn, byte[] _imageOut, int _width, int _height) {
      imageIn = _imageIn;
      imageOut = _imageOut;
      width = _width;
      height = _height;
      convMatrix3x3 = _convMatrix3x3;
      java.util.stream.Streams.intRange(0, width*height*3).forEach((i)->{
      //java.util.stream.primitive.PrimitiveStreams.parRange(0, width*height*3).forEach((i)->{
        int x = i % (width * 3);
        int y = i/ (width * 3);

        if (x > 3 && x < (width * 3 - 3) && y > 1 && y < (height - 1)) {
          processPixel(x, y, width * 3, height);
        }
      });

    }
  }

  public static void main(final String[] _args) {
    File file = new File(_args.length == 1 ? _args[0] : "testcard.jpg");

    final ImageConvolution convolution = new ImageConvolution();

    float convMatrix3x3[] = new float[] {
      0f,
        -10f,
        0f,
        -10f,
        40f,
        -10f,
        0f,
        -10f,
        0f,
    };



    new ConvolutionViewer(file, convMatrix3x3){
      @Override protected void applyConvolution(float[] _convMatrix3x3, byte[] _inBytes, byte[] _outBytes, int _width,
          int _height) {
        convolution.applyConvolution(_convMatrix3x3, _inBytes, _outBytes, _width, _height);
      }
    };

  }

}
