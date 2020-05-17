/*
   Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer. 

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution. 

   Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission. 

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
   laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
   774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
   you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
   Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
   Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 
 */

package com.amd.aparapi.examples.nbody;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;

public abstract class Universe implements GLEventListener{

   // http://www.lighthouse3d.com/opengl/billboarding/index.php3?billSphe
   float mathsInnerProduct(float[] v,float[] q) {
      return ((v[0] * q[0] + v[1] * q[1] + v[2] * q[2]));
   }


  /* a = b x c */

   void mathsCrossProduct(float[] a,float[] b,float[] c) {
      a[0] = b[1] * c[2] - c[1] * b[2];
      a[1] = b[2] * c[0] - c[2] * b[0];
      a[2] = b[0] * c[1] - c[0] * b[1];
   }


  /* vector a = b - c, where b and c represent points*/

   void mathsVector(float[] a,float[] b,float[] c){
      a[0] = b[0] - c[0];
      a[1] = b[1] - c[1];
      a[2] = b[2] - c[2];
   }

   float sqrt(float f){
      return((float)Math.sqrt(f));
   }
   float acos(float f){
      return((float)Math.acos(f));
   }

   void mathsNormalize(float v[]) {
      float d = (sqrt((v[0]*v[0]) + (v[1]*v[1]) + (v[2]*v[2])));
      v[0] = v[0] / d;
      v[1] = v[1] / d;
      v[2] = v[2] / d;
   }


   void billboardBegin(GL2 gl, float camX, float camY, float camZ, float objPosX, float objPosY, float objPosZ) {
      float lookAt[] = new float[3];
      float objToCamProj[] = new float[3];
      float objToCam[] = new float[3];
      float upAux[] = new float[3];
      float modelview[] = new float[16];
      float angleCosine;

      gl.glPushMatrix();

      // objToCamProj is the vector in world coordinates from the
      // local origin to the camera projected in the XZ plane
      objToCamProj[0] = camX - objPosX ;
      objToCamProj[1] = 0;
      objToCamProj[2] = camZ - objPosZ ;

      // This is the original lookAt vector for the object
      // in world coordinates
      lookAt[0] = 0;
      lookAt[1] = 0;
      lookAt[2] = 1;


      // normalize both vectors to get the cosine directly afterwards
      mathsNormalize(objToCamProj);

      // easy fix to determine wether the angle is negative or positive
      // for positive angles upAux will be a vector pointing in the
      // positive y direction, otherwise upAux will point downwards
      // effectively reversing the rotation.

      mathsCrossProduct(upAux,lookAt,objToCamProj);


      // compute the angle
      angleCosine = mathsInnerProduct(lookAt,objToCamProj);


      // perform the rotation. The if statement is used for stability reasons
      // if the lookAt and objToCamProj vectors are too close together then
      // |angleCosine| could be bigger than 1 due to lack of precision
      if ((angleCosine < 0.99990) && (angleCosine > -0.9999)){
         gl.glRotatef(acos(angleCosine)*180f/((float)Math.PI),upAux[0], upAux[1], upAux[2]);
         //  System.out.println("rotated1");
      }

      // so far it is just like the cylindrical billboard. The code for the
      // second rotation comes now
      // The second part tilts the object so that it faces the camera

      // objToCam is the vector in world coordinates from
      // the local origin to the camera
      objToCam[0] = camX - objPosX;
      objToCam[1] = camY - objPosY;
      objToCam[2] = camZ - objPosZ;

      // Normalize to get the cosine afterwards
      mathsNormalize(objToCam);

      // Compute the angle between objToCamProj and objToCam,
      //i.e. compute the required angle for the lookup vector

      angleCosine = mathsInnerProduct(objToCamProj,objToCam);


      // Tilt the object. The test is done to prevent instability
      // when objToCam and objToCamProj have a very small
      // angle between them

      if ((angleCosine < 0.99990) && (angleCosine > -0.9999)){
         if (objToCam[1] < 0){
            gl.glRotatef(acos(angleCosine)*180f/((float)Math.PI),1,0,0);
            //  System.out.println("rotated2a");
         } else {
            gl.glRotatef(acos(angleCosine)*180f/((float)Math.PI),-1,0,0);
            // System.out.println("rotated2b");
         }

      }
   }

   Camera camera;
   GLCanvas canvas;

   Universe(Camera _camera, int _initialWidth, int _initialHeight){
      camera = _camera;
      caps.setDoubleBuffered(true);
      caps.setHardwareAccelerated(true);
      canvas = new GLCanvas(caps);
      canvas.setPreferredSize(new Dimension(_initialWidth, _initialHeight));
      canvas.addKeyListener(camera);
      canvas.addGLEventListener(this);
   }

   GLCanvas getCanvas(){
      return (canvas);
   }


   GLCapabilities caps = new GLCapabilities(null);
   GLProfile profile = caps.getGLProfile();

   private int width;

   private int height;

   @Override public void dispose(GLAutoDrawable drawable){

   }

   public abstract void render(GL2 gl);

   public abstract void setup(GL2 gl);

   @Override public void display(GLAutoDrawable drawable){

      final GL2 gl = drawable.getGL().getGL2();
      gl.glLoadIdentity();
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
      gl.glColor3f(1f, 1f, 1f);

      gl.glLoadIdentity();
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

      GLU glu = new GLU();
      glu.gluPerspective(45f, (double) width / (double) height, 0f, 1000f);
      glu.gluLookAt(camera.getXeye(), camera.getYeye(), camera.getZeye(), camera.getXat(), camera.getYat(), camera.getZat(), 0f, 1f, 0f);

      render(gl);
      gl.glFlush();

   }

   @Override public void init(GLAutoDrawable drawable){
      final GL2 gl = drawable.getGL().getGL2();

      gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
      gl.glEnable(GL.GL_BLEND);
      gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
      gl.glEnable(GL.GL_TEXTURE_2D);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
      setup(gl);


   }

   Texture getTexture(String _name, String _type){
      Texture texture = null;
      try{
         final InputStream textureStream = Universe.class.getResourceAsStream(_name + "." + _type);
         TextureData data = TextureIO.newTextureData(profile, textureStream, false, _type);
         texture = TextureIO.newTexture(data);
      }catch(final IOException e){
         e.printStackTrace();
      }catch(final GLException e){
         e.printStackTrace();
      }
      return (texture);
   }

   @Override public void reshape(GLAutoDrawable drawable, int x, int y, int _width, int _height){
      width = _width;
      height = _height;

      final GL2 gl = drawable.getGL().getGL2();
      gl.glViewport(0, 0, width, height);
   }

}



