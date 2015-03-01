/* Auto Generated APARAPI-UCores OpenCL Kernel */
typedef struct This_s{
   int width;
   float scale;
   float offsetx;
   int height;
   float offsety;
   __global int *rgb;
   __global int *pallette;
   int passid;
}This;
int get_pass_id(This *this){
   return this->passid;
}
int com_amd_aparapi_sample_mandel_Main$MandelKernel__getCount(This *this, float x, float y){
   int count = 0;
   float zx = x;
   float zy = y;
   float new_zx = 0.0f;
   for (; count<64 && ((zx * zx) + (zy * zy))<8.0f; count++){
      new_zx = ((zx * zx) - (zy * zy)) + x;
      zy = ((2.0f * zx) * zy) + y;
      zx = new_zx;
   }
   return(count);
}
__kernel void run(
   int width, 
   float scale, 
   float offsetx, 
   int height, 
   float offsety, 
   __global int *rgb, 
   __global int *pallette, 
   int passid
){
   This thisStruct;
   This* this=&thisStruct;
   this->width = width;
   this->scale = scale;
   this->offsetx = offsetx;
   this->height = height;
   this->offsety = offsety;
   this->rgb = rgb;
   this->pallette = pallette;
   this->passid = passid;
   {
      int gid = get_global_id(0);
      float x = ((((float)(gid % this->width) * this->scale) - ((this->scale / 2.0f) * (float)this->width)) / (float)this->width) + this->offsetx;
      float y = ((((float)(gid / this->width) * this->scale) - ((this->scale / 2.0f) * (float)this->height)) / (float)this->height) + this->offsety;
      int count = com_amd_aparapi_sample_mandel_Main$MandelKernel__getCount(this, x, y);
      this->rgb[gid]  = this->pallette[count];
      return;
   }
}
