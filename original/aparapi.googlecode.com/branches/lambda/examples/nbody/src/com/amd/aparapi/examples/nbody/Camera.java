package com.amd.aparapi.examples.nbody;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class Camera extends KeyAdapter{

    private float xeye = 0f;

    private float yeye = 0f;

    private float zeye = 0f;

    private float xat = 0f;

    private float yat = 0f;

    private float zat = 0f;

    private float phi = 0f;
    private float theta = 0f;
    private float radius = 100f;

    private float INC = (float) (Math.PI/(18*5)); // 5 deg?

    float cos(float v){
      return((float)Math.cos( v));
    }
    float sin(float v){
      return((float)Math.sin( v));
    }

    Camera(){
      xeye =  radius*cos(phi)*sin(theta);
      yeye =  radius*sin(phi)*sin(theta);
      zeye =  radius*cos(theta);
    }

    @Override public void keyPressed(KeyEvent e){
      int keyCode = e.getKeyCode();
      if (e.isControlDown()) { }
      switch (keyCode){
        case KeyEvent.VK_LEFT: phi -= INC; break;
        case KeyEvent.VK_RIGHT: phi += INC; break;
        case KeyEvent.VK_UP: theta -= INC; break;
        case KeyEvent.VK_DOWN: theta += INC; break;
        case '=' : case KeyEvent.VK_ADD: radius += 20f; break;
        case '-' : case KeyEvent.VK_SUBTRACT: radius -= 20f; break;
      }
      xeye =  radius*cos(phi)*sin(theta);
      yeye =  radius*sin(phi)*sin(theta);
      zeye =  radius*cos(theta);
    }

    float getXeye(){return(xeye);}
    float getYeye(){return(yeye);}
    float getZeye(){return(zeye);}
    float getXat(){return(xat);}
    float getYat(){return(yat);}
    float getZat(){return(zat);}
    float getTheta(){return(theta);}
    float getPhi(){return(phi);}

  }


