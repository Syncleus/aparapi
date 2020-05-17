#define UP    1<<0
#define DOWN  1<<1
#define LEFT  1<<2 
#define RIGHT 1<<3

#define FROM  1<<4
#define TO    1<<5

__kernel void leeMazeRouter( __global int* grid, __global int* xy, int width, int height, int pass){
    unsigned int tid = get_local_id(0);
    unsigned int gid = get_global_id(0);
    unsigned int localSize = get_local_size(0);
    int current = gid*localSize + tid;
    int size = width*height;
    int x = gid%height;
    int y = gid/height;
    int max = 0;
    int odds = 0;
    int evens = 0;

    if ((grid[(x*height)+y])==0){
       for (int dx=x-1; dx<x+2; dx++){
          if (dx>=0 && dx<width){
            for (int dy=y-1; dy<y+2; dy++){
               if ((dy != y || dx != x) && dy>=0 && dy<height){
                  int val = grid[(dx*height)+dy];
                  if (val == pass || val == pass+1){
                  if (val %2 == 1){
                     odds++;
                  }
                  if (val %2 == 0){
                     evens++;
                  }
                     max = val;
                  }
               }
            }
          }
       }
       if (max>0){
          grid[(x*height)+y] = max+2;
       }
       if (odds>0 && evens>0){
          xy[0]=x;
          xy[1]=y;
       }
   }
}

