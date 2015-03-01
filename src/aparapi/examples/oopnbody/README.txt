oopnbody readme - December 2012

This demo is an example of an Aparapi experimental feature to allow some 
limited use of java objects in kernels,  based on the regular nbody demo. 

There is limited support in Aparapi for using arrays of java objects in kernels, 
where a kernel accesses the objects by indexing into the object array and the 
only member functions on the objects that can be called
are bean-style getters and setters, where if the object
has a data member "foo" the getter will be named "getFoo" and so on. 

The types of the object fields accessed in a kernel in this way must be basic 
types otherwise in line with Aparapi usage.

This may allow a more natural java programming style. This is implemented by 
copying the referenced object fields into C-style arrays of structs to pass to 
OpenCL. Then when the kernel completes the data is replaced into the original 
java object. The extra copying may result in lower performance than explicit 
use of basic type arrays in the original java source.

