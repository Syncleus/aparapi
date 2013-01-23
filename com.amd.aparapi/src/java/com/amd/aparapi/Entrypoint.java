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
package com.amd.aparapi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amd.aparapi.ClassModel.ClassModelField;
import com.amd.aparapi.ClassModel.ClassModelMethod;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodReferenceEntry.Arg;
import com.amd.aparapi.InstructionSet.AccessArrayElement;
import com.amd.aparapi.InstructionSet.AccessField;
import com.amd.aparapi.InstructionSet.AssignToArrayElement;
import com.amd.aparapi.InstructionSet.AssignToField;
import com.amd.aparapi.InstructionSet.I_ARRAYLENGTH;
import com.amd.aparapi.InstructionSet.I_GETFIELD;
import com.amd.aparapi.InstructionSet.I_INVOKESPECIAL;
import com.amd.aparapi.InstructionSet.I_INVOKESTATIC;
import com.amd.aparapi.InstructionSet.I_INVOKEVIRTUAL;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.TypeSpec;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

class Entrypoint{

   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   private List<ClassModel.ClassModelField> referencedClassModelFields = new ArrayList<ClassModel.ClassModelField>();

   private List<Field> referencedFields = new ArrayList<Field>();

   private ClassModel classModel;

   private Object kernelInstance = null;

   private boolean fallback = false;

   private Set<String> referencedFieldNames = new LinkedHashSet<String>();

   private Set<String> arrayFieldAssignments = new LinkedHashSet<String>();

   private Set<String> arrayFieldAccesses = new LinkedHashSet<String>();

   // Classes of object array members
   private HashMap<String, ClassModel> objectArrayFieldsClasses = new HashMap<String, ClassModel>();

   // Supporting classes of object array members like supers
   private HashMap<String, ClassModel> allFieldsClasses = new HashMap<String, ClassModel>();

   // Keep track of arrays whose length is taken via foo.length
   private Set<String> arrayFieldArrayLengthUsed = new LinkedHashSet<String>();

   private List<MethodModel> calledMethods = new ArrayList<MethodModel>();

   private MethodModel methodModel;

   /**
      True is an indication to use the fp64 pragma
   */
   private boolean usesDoubles;

   /**
      True is an indication to use the byte addressable store pragma
   */
   private boolean usesByteWrites;

   /**
      True is an indication to use the atomics pragmas
   */
   private boolean usesAtomic32;

   private boolean usesAtomic64;

   boolean requiresDoublePragma() {
      return usesDoubles;
   }

   boolean requiresByteAddressableStorePragma() {
      return usesByteWrites;
   }

   /* Atomics are detected in Entrypoint */
   void setRequiresAtomics32Pragma(boolean newVal) {
      usesAtomic32 = newVal;
   }

   void setRequiresAtomics64Pragma(boolean newVal) {
      usesAtomic64 = newVal;
   }

   boolean requiresAtomic32Pragma() {
      return usesAtomic32;
   }

   boolean requiresAtomic64Pragma() {
      return usesAtomic64;
   }

   Object getKernelInstance() {
      return kernelInstance;
   }

   void setKernelInstance(Object _k) {
      kernelInstance = _k;
   }

   Map<String, ClassModel> getObjectArrayFieldsClasses() {
      return objectArrayFieldsClasses;
   }

   static Field getFieldFromClassHierarchy(Class<?> _clazz, String _name) throws AparapiException {

      // look in self
      // if found, done

      // get superclass of curr class
      // while not found
      //  get its fields
      //  if found
      //   if not private, done
      //  if private, failure
      //  if not found, get next superclass

      Field field = null;

      assert _name != null : "_name should not be null";

      if (logger.isLoggable(Level.FINE)) {
         logger.fine("looking for " + _name + " in " + _clazz.getName());
      }

      try {
         field = _clazz.getDeclaredField(_name);
         Class<?> type = field.getType();
         if (type.isPrimitive() || type.isArray()) {
            return field;
         }
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("field type is " + type.getName());
         }
         throw new ClassParseException(ClassParseException.TYPE.OBJECTFIELDREFERENCE);
      } catch (NoSuchFieldException nsfe) {
         // This should be looger fine...
         //System.out.println("no " + _name + " in " + _clazz.getName());
      }

      Class<?> mySuper = _clazz.getSuperclass();

      if (logger.isLoggable(Level.FINE)) {
         logger.fine("looking for " + _name + " in " + mySuper.getName());
      }

      // Find better way to do this check
      while (!mySuper.getName().equals(Kernel.class.getName())) {
         try {
            field = mySuper.getDeclaredField(_name);
            int modifiers = field.getModifiers();
            if ((Modifier.isStatic(modifiers) == false) && (Modifier.isPrivate(modifiers) == false)) {
               Class<?> type = field.getType();
               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("field type is " + type.getName());
               }
               if (type.isPrimitive() || type.isArray()) {
                  return field;
               }
               throw new ClassParseException(ClassParseException.TYPE.OBJECTFIELDREFERENCE);
            } else {
               // This should be looger fine...
               //System.out.println("field " + _name + " not suitable: " + java.lang.reflect.Modifier.toString(modifiers));
               return null;
            }
         } catch (NoSuchFieldException nsfe) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("no " + _name + " in " + mySuper.getName());
            }
            mySuper = mySuper.getSuperclass();
            assert mySuper != null : "mySuper is null!";
         }
      }
      return null;
   }

   /*
    * Update the list of object array member classes and all the superclasses
    * of those classes and the fields in each class. 
    * 
    * It is important to have only one ClassModel for each class used in the kernel
    * and only one MethodModel per method, so comparison operations work properly.
    */
   ClassModel getOrUpdateAllClassAccesses(String className) throws AparapiException {
      ClassModel memberClassModel = allFieldsClasses.get(className);
      if (memberClassModel == null) {
         try {
            Class<?> memberClass = Class.forName(className);

            // Immediately add this class and all its supers if necessary
            memberClassModel = new ClassModel(memberClass);
            if (logger.isLoggable(Level.FINEST)) {
               logger.finest("adding class " + className);
            }
            allFieldsClasses.put(className, memberClassModel);
            ClassModel superModel = memberClassModel.getSuperClazz();
            while (superModel != null) {
               // See if super is already added
               ClassModel oldSuper = allFieldsClasses.get(superModel.getClassWeAreModelling().getName());
               if (oldSuper != null) {
                  if (oldSuper != superModel) {
                     memberClassModel.replaceSuperClazz(oldSuper);
                     if (logger.isLoggable(Level.FINEST)) {
                        logger.finest("replaced super " + oldSuper.getClassWeAreModelling().getName() + " for " + className);
                     }
                  }
               } else {
                  allFieldsClasses.put(superModel.getClassWeAreModelling().getName(), superModel);
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("add new super " + superModel.getClassWeAreModelling().getName() + " for " + className);
                  }
               }

               superModel = superModel.getSuperClazz();
            }
         } catch (Exception e) {
            if (logger.isLoggable(Level.INFO)) {
               logger.info("Cannot find: " + className);
            }
            throw new AparapiException(e);
         }
      }

      return memberClassModel;
   }

   ClassModelMethod resolveAccessorCandidate(MethodCall _methodCall, MethodEntry _methodEntry) throws AparapiException {
      String methodsActualClassName = (_methodEntry.getClassEntry().getNameUTF8Entry().getUTF8()).replace('/', '.');

      if (_methodCall instanceof VirtualMethodCall) {
         Instruction callInstance = ((VirtualMethodCall) _methodCall).getInstanceReference();
         if (callInstance instanceof AccessArrayElement) {
            AccessArrayElement arrayAccess = (AccessArrayElement) callInstance;
            Instruction refAccess = arrayAccess.getArrayRef();
            if (refAccess instanceof I_GETFIELD) {

               // It is a call from a member obj array element
               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Looking for class in accessor call: " + methodsActualClassName);
               }
               ClassModel memberClassModel = getOrUpdateAllClassAccesses(methodsActualClassName);

               // false = no invokespecial allowed here
               return memberClassModel.getMethod(_methodEntry, false);
            }
         }
      }
      return null;
   }

   /*
    * Update accessor structures when there is a direct access to an 
    * obect array element's data members
    */
   void updateObjectMemberFieldAccesses(String className, FieldEntry field) throws AparapiException {
      String accessedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();

      // Quickly bail if it is a ref
      if (field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8().startsWith("L")
            || field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8().startsWith("[L")) {
         throw new ClassParseException(ClassParseException.TYPE.OBJECTARRAYFIELDREFERENCE);
      }

      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("Updating access: " + className + " field:" + accessedFieldName);
      }

      ClassModel memberClassModel = getOrUpdateAllClassAccesses(className);
      Class<?> memberClass = memberClassModel.getClassWeAreModelling();
      ClassModel superCandidate = null;

      // We may add this field if no superclass match
      boolean add = true;

      // No exact match, look for a superclass
      for (ClassModel c : allFieldsClasses.values()) {
         if (logger.isLoggable(Level.FINEST)) {
            logger.finest(" super: " + c.getClassWeAreModelling().getName() + " for " + className);
         }
         if (c.isSuperClass(memberClass)) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("selected super: " + c.getClassWeAreModelling().getName() + " for " + className);
            }
            superCandidate = c;
            break;
         }

         if (logger.isLoggable(Level.FINEST)) {
            logger.finest(" no super match for " + memberClass.getName());
         }
      }

      // Look at super's fields for a match
      if (superCandidate != null) {
         ArrayList<FieldEntry> structMemberSet = superCandidate.getStructMembers();
         for (FieldEntry f : structMemberSet) {
            if (f.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(accessedFieldName)
                  && f.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8()
                        .equals(field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8())) {

               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Found match: " + accessedFieldName + " class: " + field.getClassEntry().getNameUTF8Entry().getUTF8()
                        + " to class: " + f.getClassEntry().getNameUTF8Entry().getUTF8());
               }

               if (!f.getClassEntry().getNameUTF8Entry().getUTF8().equals(field.getClassEntry().getNameUTF8Entry().getUTF8())) {
                  // Look up in class hierarchy to ensure it is the same field
                  Field superField = getFieldFromClassHierarchy(superCandidate.getClassWeAreModelling(), f.getNameAndTypeEntry()
                        .getNameUTF8Entry().getUTF8());
                  Field classField = getFieldFromClassHierarchy(memberClass, f.getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
                  if (!superField.equals(classField)) {
                     throw new ClassParseException(ClassParseException.TYPE.OVERRIDENFIELD);
                  }
               }

               add = false;
               break;
            }
         }
      }

      // There was no matching field in the supers, add it to the memberClassModel
      // if not already there
      if (add) {
         boolean found = false;
         ArrayList<FieldEntry> structMemberSet = memberClassModel.getStructMembers();
         for (FieldEntry f : structMemberSet) {
            if (f.getNameAndTypeEntry().getNameUTF8Entry().getUTF8().equals(accessedFieldName)
                  && f.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8()
                        .equals(field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8())) {
               found = true;
            }
         }
         if (!found) {
            structMemberSet.add(field);
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("Adding assigned field " + field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + " type: "
                     + field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8() + " to "
                     + memberClassModel.getClassWeAreModelling().getName());
            }
         }
      }
   }
   
   /*
    * Find a suitable call target in the kernel class, supers, object members or static calls
    */
   ClassModelMethod resolveCalledMethod(MethodCall methodCall, ClassModel classModel) throws AparapiException {
      MethodEntry methodEntry = methodCall.getConstantPoolMethodEntry();
      int thisClassIndex = classModel.getThisClassConstantPoolIndex();//arf
      boolean isMapped = (thisClassIndex != methodEntry.getClassIndex()) && Kernel.isMappedMethod(methodEntry);
      if (logger.isLoggable(Level.FINE)) {
         if (methodCall instanceof I_INVOKESPECIAL) {
            logger.fine("Method call to super: " + methodEntry);
         } else if (thisClassIndex != methodEntry.getClassIndex()) {
            logger.fine("Method call to ??: " + methodEntry + ", isMappedMethod=" + isMapped);
         } else {
            logger.fine("Method call in kernel class: " + methodEntry);
         }
      }

      ClassModelMethod m = classModel.getMethod(methodEntry, (methodCall instanceof I_INVOKESPECIAL) ? true : false);

      // Did not find method in this class or supers. Look for data member object arrays
      if (m == null && !isMapped) {
         m = resolveAccessorCandidate(methodCall, methodEntry);
      }

      // Look for a intra-object call in a object member
      if (m == null && !isMapped) {
         for (ClassModel c : allFieldsClasses.values()) {
            if (c.getClassWeAreModelling().getName()
                  .equals(methodEntry.getClassEntry().getNameUTF8Entry().getUTF8().replace('/', '.'))) {
               m = c.getMethod(methodEntry, (methodCall instanceof I_INVOKESPECIAL) ? true : false);
               assert m != null;
               break;
            }
         }
      }
      
      // Look for static call to some other class
      if ((m == null) && !isMapped && (methodCall instanceof I_INVOKESTATIC)) {
         String otherClassName = methodEntry.getClassEntry().getNameUTF8Entry().getUTF8().replace('/', '.');
         ClassModel otherClassModel = getOrUpdateAllClassAccesses(otherClassName);
        
         //if (logger.isLoggable(Level.FINE)) {
         //   logger.fine("Looking for: " + methodEntry + " in other class " + otherClass.getName());
         //}
         // false because INVOKESPECIAL not allowed here 
         m = otherClassModel.getMethod(methodEntry, false);
      }

      if (logger.isLoggable(Level.INFO)) {
         logger.fine("Selected method for: " + methodEntry + " is " + m);
      }
      
      return m;
   }

   Entrypoint(ClassModel _classModel, MethodModel _methodModel, Object _k) throws AparapiException {
      classModel = _classModel;
      methodModel = _methodModel;
      kernelInstance = _k;

      Map<ClassModelMethod, MethodModel> methodMap = new LinkedHashMap<ClassModelMethod, MethodModel>();

      boolean discovered = true;

      // Record which pragmas we need to enable
      if (methodModel.requiresDoublePragma()) {
         usesDoubles = true;
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("Enabling doubles on " + methodModel.getName());
         }

      }
      if (methodModel.requiresByteAddressableStorePragma()) {
         usesByteWrites = true;
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("Enabling byte addressable on " + methodModel.getName());
         }
      }

      // Collect all methods called directly from kernel's run method
      for (MethodCall methodCall : methodModel.getMethodCalls()) {
         ClassModelMethod m = resolveCalledMethod(methodCall, classModel);
         if (m != null && !methodMap.keySet().contains(m)) {
            MethodModel target = new MethodModel(m, this);
            methodMap.put(m, target);
            methodModel.getCalledMethods().add(target);
            discovered = true;
         }         
      }

      // methodMap now contains a list of method called by run itself().
      // Walk the whole graph of called methods and add them to the methodMap
      while (!fallback && discovered) {
         discovered = false;
         for (MethodModel mm : new ArrayList<MethodModel>(methodMap.values())) {
            for (MethodCall methodCall : mm.getMethodCalls()) {
               ClassModelMethod m = resolveCalledMethod(methodCall, classModel);
               if (m != null) {
                  MethodModel target = null;
                  if (methodMap.keySet().contains(m)) {
                     // we remove and then add again.  Because this is a LinkedHashMap this 
                     // places this at the end of the list underlying the map
                     // then when we reverse the collection (below) we get the method 
                     // declarations in the correct order.  We are trying to avoid creating forward references
                     target = methodMap.remove(m);
                     if (logger.isLoggable(Level.FINEST)) {
                        logger.fine("repositioning : " + m.getClassModel().getClassWeAreModelling().getName() + " " + m.getName()
                              + " " + m.getDescriptor());
                     }
                  } else {
                     target = new MethodModel(m, this);
                     discovered = true;
                  }
                  methodMap.put(m, target);
                  // Build graph of call targets to look for recursion
                  mm.getCalledMethods().add(target);
               }
            }
         }
      }

      methodModel.checkForRecursion(new HashSet<MethodModel>());

      if (logger.isLoggable(Level.FINE)) {
         logger.fine("fallback=" + fallback);
      }

      if (!fallback) {
         calledMethods.addAll(methodMap.values());
         Collections.reverse(calledMethods);
         List<MethodModel> methods = new ArrayList<MethodModel>(calledMethods);

         // add method to the calledMethods so we can include in this list
         methods.add(methodModel);
         Set<String> fieldAssignments = new HashSet<String>();

         Set<String> fieldAccesses = new HashSet<String>();

         for (MethodModel methodModel : methods) {

            // Record which pragmas we need to enable
            if (methodModel.requiresDoublePragma()) {
               usesDoubles = true;
               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Enabling doubles on " + methodModel.getName());
               }

            }
            if (methodModel.requiresByteAddressableStorePragma()) {
               usesByteWrites = true;
               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Enabling byte addressable on " + methodModel.getName());
               }
            }

            for (Instruction instruction = methodModel.getPCHead(); instruction != null; instruction = instruction.getNextPC()) {

               if (instruction instanceof AssignToArrayElement) {
                  AssignToArrayElement assignment = (AssignToArrayElement) instruction;

                  Instruction arrayRef = assignment.getArrayRef();
                  // AccessField here allows instance and static array refs
                  if (arrayRef instanceof AccessField) {
                     AccessField getField = (AccessField) arrayRef;
                     FieldEntry field = getField.getConstantPoolFieldEntry();
                     String assignedArrayFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                     arrayFieldAssignments.add(assignedArrayFieldName);
                     referencedFieldNames.add(assignedArrayFieldName);

                  }
               } else if (instruction instanceof AccessArrayElement) {
                  AccessArrayElement access = (AccessArrayElement) instruction;

                  Instruction arrayRef = access.getArrayRef();
                  // AccessField here allows instance and static array refs
                  if (arrayRef instanceof AccessField) {
                     AccessField getField = (AccessField) arrayRef;
                     FieldEntry field = getField.getConstantPoolFieldEntry();
                     String accessedArrayFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                     arrayFieldAccesses.add(accessedArrayFieldName);
                     referencedFieldNames.add(accessedArrayFieldName);

                  }
               } else if (instruction instanceof I_ARRAYLENGTH) {
                  if (!(instruction.getFirstChild() instanceof AccessField)) {
                     throw new ClassParseException(ClassParseException.TYPE.LOCALARRAYLENGTHACCESS);
                  }
                  AccessField child = (AccessField) instruction.getFirstChild();
                  String arrayName = child.getConstantPoolFieldEntry().getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                  arrayFieldArrayLengthUsed.add(arrayName);
                  if (logger.isLoggable(Level.FINE)) {
                     logger.fine("Noted arraylength in " + methodModel.getName() + " on " + arrayName);
                  }
               } else if (instruction instanceof AccessField) {
                  AccessField access = (AccessField) instruction;
                  FieldEntry field = access.getConstantPoolFieldEntry();
                  String accessedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                  fieldAccesses.add(accessedFieldName);
                  referencedFieldNames.add(accessedFieldName);

                  String signature = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
                  if (logger.isLoggable(Level.FINE)) {
                     logger.fine("AccessField field type= " + signature + " in " + methodModel.getName());
                  }

                  // Add the class model for the referenced obj array
                  if (signature.startsWith("[L")) {
                     // Turn [Lcom/amd/javalabs/opencl/demo/DummyOOA; into com.amd.javalabs.opencl.demo.DummyOOA for example
                     String className = (signature.substring(2, signature.length() - 1)).replace("/", ".");
                     ClassModel arrayFieldModel = getOrUpdateAllClassAccesses(className);
                     if (arrayFieldModel != null) {
                        Class<?> memberClass = arrayFieldModel.getClassWeAreModelling();
                        int modifiers = memberClass.getModifiers();
                        if (!Modifier.isFinal(modifiers)) {
                           throw new ClassParseException(ClassParseException.TYPE.ACCESSEDOBJECTNONFINAL);
                        }

                        ClassModel refModel = objectArrayFieldsClasses.get(className);
                        if (refModel == null) {

                           // Verify no other member with common parent
                           for (ClassModel memberObjClass : objectArrayFieldsClasses.values()) {
                              ClassModel superModel = memberObjClass;
                              while (superModel != null) {
                                 if (superModel.isSuperClass(memberClass)) {
                                    throw new ClassParseException(ClassParseException.TYPE.ACCESSEDOBJECTFIELDNAMECONFLICT);
                                 }
                                 superModel = superModel.getSuperClazz();
                              }
                           }

                           objectArrayFieldsClasses.put(className, arrayFieldModel);
                           if (logger.isLoggable(Level.FINE)) {
                              logger.fine("adding class to objectArrayFields: " + className);
                           }
                        }
                     }
                  } else {
                     String className = (field.getClassEntry().getNameUTF8Entry().getUTF8()).replace("/", ".");
                     // Look for object data member access
                     if (!className.equals(this.getClassModel().getClassWeAreModelling().getName())
                           && getFieldFromClassHierarchy(getClassModel().getClassWeAreModelling(), accessedFieldName) == null) {
                        updateObjectMemberFieldAccesses(className, field);
                     }
                  }

               } else if (instruction instanceof AssignToField) {
                  AssignToField assignment = (AssignToField) instruction;
                  FieldEntry field = assignment.getConstantPoolFieldEntry();
                  String assignedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                  fieldAssignments.add(assignedFieldName);
                  referencedFieldNames.add(assignedFieldName);

                  String className = (field.getClassEntry().getNameUTF8Entry().getUTF8()).replace("/", ".");
                  // Look for object data member access
                  if (!className.equals(this.getClassModel().getClassWeAreModelling().getName())
                        && getFieldFromClassHierarchy(getClassModel().getClassWeAreModelling(), assignedFieldName) == null) {
                     updateObjectMemberFieldAccesses(className, field);
                  } else {

                     if ((!Config.enablePUTFIELD) && methodModel.methodUsesPutfield() && !methodModel.isSetter()) {
                        throw new ClassParseException(ClassParseException.TYPE.ACCESSEDOBJECTONLYSUPPORTSSIMPLEPUTFIELD);
                     }

                  }

               } else if (instruction instanceof I_INVOKEVIRTUAL) {
                  I_INVOKEVIRTUAL invokeInstruction = (I_INVOKEVIRTUAL) instruction;
                  MethodEntry methodEntry = invokeInstruction.getConstantPoolMethodEntry();
                  if (Kernel.isMappedMethod(methodEntry)) { //only do this for intrinsics

                     if (Kernel.usesAtomic32(methodEntry)) {
                        setRequiresAtomics32Pragma(true);
                     }

                     Arg methodArgs[] = methodEntry.getArgs();
                     if (methodArgs.length > 0 && methodArgs[0].isArray()) { //currently array arg can only take slot 0
                        Instruction arrInstruction = invokeInstruction.getArg(0);
                        if (arrInstruction instanceof AccessField) {
                           AccessField access = (AccessField) arrInstruction;
                           FieldEntry field = access.getConstantPoolFieldEntry();
                           String accessedFieldName = field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                           arrayFieldAssignments.add(accessedFieldName);
                           referencedFieldNames.add(accessedFieldName);
                        } else {
                           throw new ClassParseException(ClassParseException.TYPE.ACCESSEDOBJECTSETTERARRAY);
                        }
                     }
                  }

               }
            }
         }

         for (String referencedFieldName : referencedFieldNames) {

            try {
               Class<?> clazz = classModel.getClassWeAreModelling();
               Field field = getFieldFromClassHierarchy(clazz, referencedFieldName);
               if (field != null) {
                  referencedFields.add(field);
                  ClassModelField ff = classModel.getField(referencedFieldName);
                  assert ff != null : "ff should not be null for " + clazz.getName() + "." + referencedFieldName;
                  referencedClassModelFields.add(ff);
               }
            } catch (SecurityException e) {
               e.printStackTrace();
            }
         }

         // Build data needed for oop form transforms if necessary
         if (!objectArrayFieldsClasses.keySet().isEmpty()) {

            for (ClassModel memberObjClass : objectArrayFieldsClasses.values()) {

               // At this point we have already done the field override safety check, so 
               // add all the superclass fields into the kernel member class to be
               // sorted by size and emitted into the struct
               ClassModel superModel = memberObjClass.getSuperClazz();
               while (superModel != null) {
                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("adding = " + superModel.getClassWeAreModelling().getName() + " fields into "
                           + memberObjClass.getClassWeAreModelling().getName());
                  }
                  memberObjClass.getStructMembers().addAll(superModel.getStructMembers());
                  superModel = superModel.getSuperClazz();
               }
            }

            // Sort fields of each class biggest->smallest
            final Comparator<FieldEntry> fieldSizeComparator = new Comparator<FieldEntry>(){
               public int compare(FieldEntry aa, FieldEntry bb) {
                  String aType = aa.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
                  String bType = bb.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();

                  // Booleans get converted down to bytes
                  int aSize = InstructionSet.TypeSpec.valueOf(aType.equals("Z") ? "B" : aType).getSize();
                  int bSize = InstructionSet.TypeSpec.valueOf(bType.equals("Z") ? "B" : bType).getSize();

                  if (logger.isLoggable(Level.FINEST)) {
                     logger.finest("aType= " + aType + " aSize= " + aSize + " . . bType= " + bType + " bSize= " + bSize);
                  }

                  // Note this is sorting in reverse order so the biggest is first
                  if (aSize > bSize) {
                     return -1;
                  } else if (aSize == bSize) {
                     return 0;
                  } else {
                     return 1;
                  }
               }
            };

            for (ClassModel c : objectArrayFieldsClasses.values()) {
               ArrayList<FieldEntry> fields = c.getStructMembers();
               if (fields.size() > 0) {
                  Collections.sort(fields, fieldSizeComparator);

                  // Now compute the total size for the struct
                  int totalSize = 0;
                  int alignTo = 0;

                  for (FieldEntry f : fields) {
                     // Record field offset for use while copying
                     // Get field we will copy out of the kernel member object
                     Field rfield = getFieldFromClassHierarchy(c.getClassWeAreModelling(), f.getNameAndTypeEntry()
                           .getNameUTF8Entry().getUTF8());

                     c.getStructMemberOffsets().add(UnsafeWrapper.objectFieldOffset(rfield));

                     String fType = f.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
                     //c.getStructMemberTypes().add(TypeSpec.valueOf(fType.equals("Z") ? "B" : fType));
                     c.getStructMemberTypes().add(TypeSpec.valueOf(fType));
                     int fSize = TypeSpec.valueOf(fType.equals("Z") ? "B" : fType).getSize();
                     if (fSize > alignTo) {
                        alignTo = fSize;
                     }

                     totalSize += fSize;
                     if (logger.isLoggable(Level.FINEST)) {
                        logger.finest("Field = " + f.getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + " size=" + fSize
                              + " totalSize=" + totalSize);
                     }
                  }

                  // compute total size for OpenCL buffer
                  int totalStructSize = 0;
                  if (totalSize % alignTo == 0) {
                     totalStructSize = totalSize;
                  } else {
                     // Pad up if necessary
                     totalStructSize = ((totalSize / alignTo) + 1) * alignTo;
                  }
                  c.setTotalStructSize(totalStructSize);
               }
            }
         }

      }
   }

   boolean shouldFallback() {
      return (fallback);
   }

   List<ClassModel.ClassModelField> getReferencedClassModelFields() {
      return (referencedClassModelFields);
   }

   List<Field> getReferencedFields() {
      return (referencedFields);
   }

   List<MethodModel> getCalledMethods() {
      return calledMethods;
   }

   Set<String> getReferencedFieldNames() {
      return (referencedFieldNames);
   }

   Set<String> getArrayFieldAssignments() {
      return (arrayFieldAssignments);
   }

   Set<String> getArrayFieldAccesses() {
      return (arrayFieldAccesses);
   }

   Set<String> getArrayFieldArrayLengthUsed() {
      return (arrayFieldArrayLengthUsed);
   }

   MethodModel getMethodModel() {
      return (methodModel);
   }

   ClassModel getClassModel() {
      return (classModel);
   }

   /*
    * Return the best call target MethodModel by looking in the class hierarchy
    * @param _methodEntry MethodEntry for the desired target
    * @return the fully qualified name such as "com_amd_javalabs_opencl_demo_PaternityTest$SimpleKernel__actuallyDoIt"
    */
   MethodModel getCallTarget(MethodEntry _methodEntry, boolean _isSpecial) {
      ClassModelMethod target = getClassModel().getMethod(_methodEntry, _isSpecial);
      boolean isMapped = Kernel.isMappedMethod(_methodEntry);

      if (logger.isLoggable(Level.FINE) && target == null) {
         logger.fine("Did not find call target: " + _methodEntry + " in " + 
            getClassModel().getClassWeAreModelling().getName() + " isMapped=" + isMapped);
      }

      if (target == null) {
         // Look for member obj accessor calls
         for (ClassModel memberObjClass : objectArrayFieldsClasses.values()) {
            String entryClassNameInDotForm = _methodEntry.getClassEntry().getNameUTF8Entry().getUTF8().replace('/', '.');
            if (entryClassNameInDotForm.equals(memberObjClass.getClassWeAreModelling().getName())) {
               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Searching for call target: " + _methodEntry + " in "
                        + memberObjClass.getClassWeAreModelling().getName());
               }

               target = memberObjClass.getMethod(_methodEntry, false);
               if (target != null) {
                  break;
               }
            }
         }
      }

      if (target != null) {
         for (MethodModel m : calledMethods) {
            if (m.getMethod() == target) {
               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("selected from called methods = " + m.getName());
               }
               return m;
            }
         }
      }
            
      // Search for static calls to other classes
      for (MethodModel m : calledMethods) {
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("Searching for call target: " + _methodEntry + " in " + m.getName());
         }         
         if (m.getMethod().getName().equals(_methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8())
                 && m.getMethod().getDescriptor().equals(_methodEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8())) {
            if (logger.isLoggable(Level.FINE)) {
               logger.fine("Found " + m.getMethod().getClassModel().getClassWeAreModelling().getName() + 
            		   "." + m.getMethod().getName() + " " + m.getMethod().getDescriptor());
            }
            return m;
         }
      }

      assert target == null : "Should not have missed a method in calledMethods";

      return null;
   }
}
