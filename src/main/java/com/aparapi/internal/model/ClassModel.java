/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
package com.aparapi.internal.model;

import com.aparapi.*;
import com.aparapi.internal.annotation.*;
import com.aparapi.internal.exception.*;
import com.aparapi.internal.instruction.InstructionSet.*;
import com.aparapi.internal.model.ValueCache.ThrowingValueComputer;
import com.aparapi.internal.model.ClassModel.AttributePool.*;
import com.aparapi.internal.model.ClassModel.ConstantPool.*;
import com.aparapi.internal.reader.*;
import com.aparapi.internal.util.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

/**
 * Class represents a ClassFile (MyClass.class).
 * <p>
 * A ClassModel is constructed from an instance of a <code>java.lang.Class</code>.
 * <p>
 * If the java class mode changes we may need to modify this to accommodate.
 *
 * @author gfrost
 * @see <a href="http://java.sun.com/docs/books/jvms/second_edition/ClassFileFormat-Java5.pdf">Java 5 Class File Format</a>
 * + * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html"> Java 7 Class File Format</a>
 */
public class ClassModel {

    /** reflection cache */
    private Method[] _methods;

    public interface LocalVariableInfo {

        int getStart();

        boolean isArray();

        int getEnd();

        String getVariableName();

        String getVariableDescriptor();

        int getVariableIndex();

        int getLength();

    }

    public interface LocalVariableTableEntry<T extends LocalVariableInfo> extends Iterable<T> {
        LocalVariableInfo getVariable(int _pc, int _index);

    }

    private static final char SIGC_VOID = 'V';

    private static final char SIGC_BOOLEAN = 'Z';

    private static final char SIGC_BYTE = 'B';

    private static final char SIGC_CHAR = 'C';

    private static final char SIGC_SHORT = 'S';

    private static final char SIGC_INT = 'I';

    private static final char SIGC_LONG = 'J';

    private static final char SIGC_FLOAT = 'F';

    private static final char SIGC_DOUBLE = 'D';

    private static final char SIGC_ARRAY = '[';

    private static final char SIGC_CLASS = 'L';

    private static final char SIGC_START_METHOD = '(';

    private static final char SIGC_END_CLASS = ';';

    private static final char SIGC_END_METHOD = ')';

    private static final char SIGC_PACKAGE = '/';

    private static final Logger logger = Logger.getLogger(Config.getLoggerName());

    private ClassModel superClazz;

    //   private Memoizer<Set<String>> noClMethods = Memoizer.of(this::computeNoCLMethods);
//    private final Memoizer<Set<String>> noClMethods = Memoizer.Impl.of(new Supplier<Set<String>>() {
//        @Override
//        public Set<String> get() {
//            return computeNoCLMethods();
//        }
//    });


    private final Memoizer<Map<String, Kernel.PrivateMemorySpace>> privateMemoryFields = Memoizer.Impl
        .of(this::computePrivateMemoryFields);

    private final ValueCache<String, Integer, ClassParseException> privateMemorySizes = ValueCache
        .on(this::computePrivateMemorySize);

    /**
     * Create a ClassModel representing a given Class.
     * <p>
     * The class's classfile must be available from the class's classloader via <code>getClassLoader().getResourceAsStream(name))</code>.
     * For dynamic languages creating classes on the fly we may need another approach.
     *
     * @param _class The class we will extract the model from
     * @throws ClassParseException
     */

    private ClassModel(Class<?> _class) throws ClassParseException {

        clazz = _class;
        parse(_class.getClassLoader(), _class.getName());

        final Class<?> mySuper = _class.getSuperclass();
        // Find better way to do this check
        // The java.lang.Object test is for unit test framework to succeed - should
        // not occur in normal use
        if ((mySuper != null) && (!mySuper.getName().equals(Kernel.class.getName()))
            && (!mySuper.getName().equals("java.lang.Object"))) {
            superClazz = createClassModel(mySuper);
        } else {
            superClazz = null;
        }
    }

//    private ClassModel(InputStream _inputStream) {
//
//        parse(_inputStream);
//
//    }

//    private ClassModel(Class<?> _clazz, byte[] _bytes) {
//        clazz = _clazz;
//        parse(new ByteArrayInputStream(_bytes));
//    }

    /**
     * Determine if this is the superclass of some other named class.
     *
     * @param otherClassName The name of the class to compare against
     * @return true if 'this' a superclass of another named class
     */
    private boolean isSuperClass(String otherClassName) {
        ClassModel other = this;
        while (true) {
            if (other.clazz.getName().equals(otherClassName)) {
                return true;
            } else if (other.superClazz != null) {
                other = other.superClazz;
            } else {
                return false;
            }
        }
    }

    /**
     * Determine if this is the superclass of some other class.
     *
     * @param other The class to compare against
     * @return true if 'this' a superclass of another class
     */
    public boolean isSuperClass(Class<?> other) {
        Class<?> s = other.getSuperclass();
        while (s != null) {
            if ((clazz == s) || (clazz.getName().equals(s.getName()))) {
                return true;
            }
            s = s.getSuperclass();
        }
        return false;
    }

    /**
     * Getter for superClazz
     *
     * @return the superClazz ClassModel
     */
    public ClassModel getSuperClazz() {
        return superClazz;
    }

    @DocMe
    public void replaceSuperClazz(ClassModel c) {
        if (superClazz != null) {
            assert c.isSuperClass(clazz) == true : "not my super";
            if (superClazz.clazz.getName().equals(c.clazz.getName())) {
                superClazz = c;
            } else {
                superClazz.replaceSuperClazz(c);
            }
        }
    }

    /**
     * Convert a given JNI character type (say 'I') to its type name ('int').
     *
     * @param _typeChar
     * @return either a mapped type name or null if no mapping exists.
     */
    public static String typeName(char _typeChar) {
        String returnName = null;
        switch (_typeChar) {
            case SIGC_VOID:
                returnName = "void";
                break;
            case SIGC_INT:
                returnName = "int";
                break;
            case SIGC_DOUBLE:
                returnName = "double";
                break;
            case SIGC_FLOAT:
                returnName = "float";
                break;
            case SIGC_SHORT:
                returnName = "short";
                break;
            case SIGC_CHAR:
                returnName = "char";
                break;
            case SIGC_BYTE:
                returnName = "byte";
                break;
            case SIGC_LONG:
                returnName = "long";
                break;
            case SIGC_BOOLEAN:
                returnName = "boolean";
                break;
        }

        return (returnName);
    }

    /**
     * If a field does not satisfy the private memory conditions, null, otherwise the size of private memory required.
     */
    public Integer getPrivateMemorySize(String fieldName) throws ClassParseException {
        if (CacheEnabler.areCachesEnabled())
            return privateMemorySizes.computeIfAbsent(fieldName);
        return computePrivateMemorySize(fieldName);
    }

    private Integer computePrivateMemorySize(String fieldName) throws ClassParseException {
        Kernel.PrivateMemorySpace annotation = privateMemoryFields.get().get(fieldName);
        if (annotation != null) {
            return annotation.value();
        }
        return getPrivateMemorySizeFromFieldName(fieldName);
    }

    private Map<String, Kernel.PrivateMemorySpace> computePrivateMemoryFields() {
        Map<String, Kernel.PrivateMemorySpace> tempPrivateMemoryFields = new HashMap<>();
        Map<Field, Kernel.PrivateMemorySpace> privateMemoryFields = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Kernel.PrivateMemorySpace privateMemorySpace = field.getAnnotation(Kernel.PrivateMemorySpace.class);
            if (privateMemorySpace != null) {
                privateMemoryFields.put(field, privateMemorySpace);
            }
        }
        for (Field field : clazz.getFields()) {
            Kernel.PrivateMemorySpace privateMemorySpace = field.getAnnotation(Kernel.PrivateMemorySpace.class);
            if (privateMemorySpace != null) {
                privateMemoryFields.put(field, privateMemorySpace);
            }
        }
        for (Map.Entry<Field, Kernel.PrivateMemorySpace> entry : privateMemoryFields.entrySet()) {
            tempPrivateMemoryFields.put(entry.getKey().getName(), entry.getValue());
        }
        return tempPrivateMemoryFields;
    }

    public static Integer getPrivateMemorySizeFromField(Field field) {
        Kernel.PrivateMemorySpace privateMemorySpace = field.getAnnotation(Kernel.PrivateMemorySpace.class);
        if (privateMemorySpace != null) {
            return privateMemorySpace.value();
        } else {
            return null;
        }
    }

    public static Integer getPrivateMemorySizeFromFieldName(String fieldName) throws ClassParseException {
        if (fieldName.contains(Kernel.PRIVATE_SUFFIX)) {
            int lastDollar = fieldName.lastIndexOf('$');
            String sizeText = fieldName.substring(lastDollar + 1);
            try {
                return Integer.parseInt(sizeText);
            } catch (NumberFormatException e) {
                throw new ClassParseException(ClassParseException.TYPE.IMPROPERPRIVATENAMEMANGLING, fieldName);
            }
        }
        return null;
    }

    public boolean isNoCLMethod(String name) {
//        return computeNoCLMethods();
//    }
//
//    private Set<String> computeNoCLMethods() {
//        Set<String> tempNoClMethods = new HashSet<>();
//        HashSet<Method> methods = new HashSet<>();
//        for (Method method : getClassWeAreModelling().getDeclaredMethods()) {
//            if (method.getAnnotation(Kernel.NoCL.class) != null) {
//                methods.add(method);
//            }
//        }
        if (_methods == null) {
            _methods = clazz.getMethods();
        }
        for (Method method : _methods) {
            if (method.getName().equals(name) && method.getAnnotation(Kernel.NoCL.class) != null) {
                //methods.add(method);
                return true;
            }
        }
//        for (Method method : methods) {
//            tempNoClMethods.add(method.getName());
//        }
        return false;
    }

    public static String convert(String _string) {
        return (convert(_string, "", false));
    }

    private static String convert(String _string, String _insert) {
        return (convert(_string, _insert, false));
    }

    public static String convert(String _string, String _insert, boolean _showFullClassName) {
        List<String> stringStack = new ArrayList<>();
        List<String> methodStack = null;
        final int length = _string.length();
        final char[] chars = _string.toCharArray();
        int i = 0;
        boolean inArray = false;
        boolean inMethod = false;
        boolean inArgs = false;
        int args = 0;

        while (i < length) {
            switch (chars[i]) {
                case SIGC_CLASS: {
                    final StringBuilder classNameBuffer = new StringBuilder();
                    i++;
                    while ((i < length) && (chars[i] != SIGC_END_CLASS)) {
                        if (chars[i] == SIGC_PACKAGE) {
                            classNameBuffer.append('.');
                        } else {
                            classNameBuffer.append(chars[i]);
                        }
                        i++;
                    }
                    i++; // step over SIGC_ENDCLASS
                    String className = classNameBuffer.toString();
                    if (_showFullClassName) {
                        if (className.startsWith("java.lang")) {
                            className = className.substring("java.lang.".length());
                        }
                    } else {
                        final int lastDot = className.lastIndexOf('.');
                        if (lastDot > 0) {
                            className = className.substring(lastDot + 1);
                        }
                    }
                    if (inArray) {
                        // swap the stack items
                        final String popped = pop(stringStack);
                        if (inArgs && (args > 0)) {
                            stringStack.add(", ");
                        }
                        stringStack.add(className);
                        stringStack.add(popped);
                        inArray = false;
                    } else {
                        if (inArgs && (args > 0)) {
                            stringStack.add(", ");
                        }
                        stringStack.add(className);
                    }
                    args++;
                }
                break;
                case SIGC_ARRAY: {
                    final StringBuilder arrayDims = new StringBuilder();
                    while ((i < length) && (chars[i] == SIGC_ARRAY)) {
                        arrayDims.append("[]");
                        i++;
                    }
                    stringStack.add(arrayDims.toString());
                    inArray = true;
                }
                break;
                case SIGC_VOID:
                case SIGC_INT:
                case SIGC_DOUBLE:
                case SIGC_FLOAT:
                case SIGC_SHORT:
                case SIGC_CHAR:
                case SIGC_BYTE:
                case SIGC_LONG:
                case SIGC_BOOLEAN: {
                    if (inArray) {
                        // swap the stack items
                        final String popped = pop(stringStack);
                        if (inArgs && (args > 0)) {
                            stringStack.add(", ");
                        }
                        stringStack.add(typeName(chars[i]));
                        stringStack.add(popped);
                        inArray = false;
                    } else {
                        if (inArgs && (args > 0)) {
                            stringStack.add(", ");
                        }
                        stringStack.add(typeName(chars[i]));
                    }
                    i++; // step over this
                }
                break;
                case SIGC_START_METHOD: {
                    stringStack.add("(");
                    i++; // step over this
                    inArgs = true;
                    args = 0;
                }
                break;
                case SIGC_END_METHOD: {
                    inMethod = true;
                    inArgs = false;
                    stringStack.add(")");
                    methodStack = stringStack;
                    stringStack = new Stack<>();
                    i++; // step over this
                }
                break;
            }
        }

        final StringBuilder returnValue = new StringBuilder();
        for (final String s : stringStack) {
            returnValue.append(s);
            returnValue.append(' ');

        }

        if (inMethod) {
            for (final String s : methodStack) {
                returnValue.append(s);
                returnValue.append(' ');
            }
        } else {
            returnValue.append(_insert);
        }

        return (returnValue.toString());
    }

    public static class MethodDescription {
        private final String className;

        private final String methodName;

        private final String type;

        private final String[] args;

        MethodDescription(String _className, String _methodName, String _type, String[] _args) {
            methodName = _methodName;
            className = _className;
            type = _type;
            args = _args;
        }

        public String[] getArgs() {
            return (args);
        }

        public String getType() {
            return (type);
        }

        public String getClassName() {
            return (className);
        }

        public String getMethodName() {
            return (methodName);
        }
    }

    public static MethodDescription getMethodDescription(String _string) {
        String className = null;
        String methodName = null;
        String descriptor = null;
        MethodDescription methodDescription = null;

        if (_string.startsWith("(")) {
            className = "?";
            methodName = "?";
            descriptor = _string;
        } else {
            final int parenIndex = _string.indexOf('(');
            final int dotIndex = _string.indexOf('.');
            descriptor = _string.substring(parenIndex);
            className = _string.substring(0, dotIndex);
            methodName = _string.substring(dotIndex + 1, parenIndex);
        }

        List<String> stringStack = new ArrayList<>();
        List<String> methodStack = null;
        final int length = descriptor.length();
        final char[] chars = new char[descriptor.length()];
        descriptor.getChars(0, descriptor.length(), chars, 0);
        int i = 0;
        boolean inArray = false;
        boolean inMethod = false;

        while (i < length) {
            switch (chars[i]) {
                case SIGC_CLASS: {
                    StringBuilder stringBuffer = null;
                    if (inArray) {
                        stringBuffer = new StringBuilder(pop(stringStack));
                    } else {
                        stringBuffer = new StringBuilder();
                    }
                    stringBuffer.ensureCapacity(length);
                    while ((i < length) && (chars[i] != SIGC_END_CLASS)) {
                        stringBuffer.append(chars[i]);
                        i++;
                    }
                    stringBuffer.append(chars[i]);
                    i++; // step over SIGC_ENDCLASS
                    stringStack.add(stringBuffer.toString());
                    inArray = false;
                }
                break;
                case SIGC_ARRAY: {
                    final StringBuilder stringBuffer = new StringBuilder(length);
                    while ((i < length) && (chars[i] == SIGC_ARRAY)) {
                        stringBuffer.append(chars[i]);
                        i++;
                    }
                    stringStack.add(stringBuffer.toString());
                    inArray = true;
                }
                break;
                case SIGC_VOID:
                case SIGC_INT:
                case SIGC_DOUBLE:
                case SIGC_FLOAT:
                case SIGC_SHORT:
                case SIGC_CHAR:
                case SIGC_BYTE:
                case SIGC_LONG:
                case SIGC_BOOLEAN: {
                    StringBuilder stringBuffer = null;
                    if (inArray) {
                        stringBuffer = new StringBuilder(pop(stringStack));
                    } else {
                        stringBuffer = new StringBuilder();
                    }
                    stringBuffer.append(chars[i]);
                    i++; // step over this
                    stringStack.add(stringBuffer.toString());
                    inArray = false;
                }
                break;
                case SIGC_START_METHOD: {
                    i++; // step over this
                }
                break;
                case SIGC_END_METHOD: {
                    inMethod = true;
                    inArray = false;
                    methodStack = stringStack;
                    stringStack = new ArrayList<>();
                    i++; // step over this
                }
                break;
            }
        }

        if (inMethod) {
            methodDescription = new MethodDescription(className, methodName, stringStack.toArray(new String[stringStack.size()])[0],
                methodStack.toArray(new String[methodStack.size()]));
        } else {
            //System.out.println("can't convert to a description");
            throw new UnsupportedOperationException("can't create MethodDescription: " + methodName);
        }

        return (methodDescription);
    }

    private static String pop(List<String> stringStack) {
        return stringStack.remove(stringStack.size()-1);
    }

    private static final ValueCache<Class<?>, ClassModel, ClassParseException> classModelCache = ValueCache
        .on(ClassModel::createClassModelInternal);

    private static ClassModel createClassModelInternal(Class<?> key) throws ClassParseException {
        return new ClassModel(key);
    }

    public static ClassModel createClassModel(Class<?> _class) throws ClassParseException {
        if (CacheEnabler.areCachesEnabled()) {
            return classModelCache.computeIfAbsent(_class);
        } else {
            return createClassModelInternal(_class);
        }
    }

    private int magic;

    private int minorVersion;

    private int majorVersion;

    private ConstantPool constantPool;

    private int accessFlags;

    private int thisClassConstantPoolIndex;

    private int superClassConstantPoolIndex;

    private final List<ClassModelInterface> interfaces = new ArrayList<>();

    private final List<ClassModelField> fields = new ArrayList<>();

    private final List<ClassModelMethod> methods = new ArrayList<>();

    private AttributePool attributePool;

    public enum ConstantPoolType {
        EMPTY, //0
        UTF8, //1
        UNICODE, //2
        INTEGER, //3
        FLOAT, //4
        LONG, //5
        DOUBLE, //6
        CLASS, //7
        STRING, //8
        FIELD, //9
        METHOD, //10
        INTERFACEMETHOD, //11
        NAMEANDTYPE, //12
        UNUSED13,
        UNUSED14,
        METHODHANDLE, //15
        METHODTYPE, //16
        UNUSED17,
        INVOKEDYNAMIC//18
    }

    public enum Access {
        PUBLIC(0x00000001),
        PRIVATE(0x00000002),
        PROTECTED(0x00000004),
        STATIC(0x00000008),
        FINAL(0x00000010),
        ACC_SYNCHRONIZED(0x00000020),
        ACC_VOLATILE(0x00000040),
        BRIDGE(0x00000040),
        TRANSIENT(0x00000080),
        VARARGS(0x00000080),
        NATIVE(0x00000100),
        INTERFACE(0x00000200),
        ABSTRACT(0x00000400),
        SUPER(0x00000020),
        STRICT(0x00000800),
        ANNOTATION(0x00002000),
        ACC_ENUM(0x00004000);
        final int bits;

        Access(int _bits) {
            bits = _bits;
        }

        boolean bitIsSet(int _accessFlags) {
            return ((bits & _accessFlags) == bits);
        }

        public static String convert(int _accessFlags) {
            final StringBuilder stringBuffer = new StringBuilder();
            for (final Access access : Access.values()) {
                if (access.bitIsSet(_accessFlags)) {
                    stringBuffer.append(' ').append(access.name().toLowerCase());
                }
            }

            return (stringBuffer.toString());
        }
    }

    private enum SignatureParseState {
        skipping,
        counting,
        inclass,
        inArray,
        done
    }

    public class ConstantPool implements Iterable<ConstantPool.Entry> {

        private final List<Entry> entries = new ArrayList<>();

        public abstract class Entry {
            private final ConstantPoolType constantPoolType;

            private final int slot;

            Entry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType) {
                constantPoolType = _constantPoolType;
                slot = _slot;
            }

            ConstantPoolType getConstantPoolType() {
                return (constantPoolType);
            }

            public int getSlot() {
                return (slot);
            }

            public ClassModel getOwnerClassModel() {
                return ClassModel.this;
            }
        }

        public class ClassEntry extends Entry {
            private final int nameIndex;

            ClassEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.CLASS);
                nameIndex = _byteReader.u2();
            }

            int getNameIndex() {
                return (nameIndex);
            }

            public UTF8Entry getNameUTF8Entry() {
                return (getUTF8Entry(nameIndex));
            }
        }

        public class DoubleEntry extends Entry {
            private final double doubleValue;

            DoubleEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.DOUBLE);
                doubleValue = _byteReader.d8();
            }

            double getDoubleValue() {
                return (doubleValue);
            }
        }

        class EmptyEntry extends Entry {
            EmptyEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.EMPTY);
            }
        }

        public class FieldEntry extends ReferenceEntry {
            FieldEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.FIELD);
            }
        }

        public class FloatEntry extends Entry {
            private final float floatValue;

            FloatEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.FLOAT);
                floatValue = _byteReader.f4();
            }

            float getFloatValue() {
                return (floatValue);
            }
        }

        public class IntegerEntry extends Entry {
            private final int intValue;

            IntegerEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.INTEGER);
                intValue = _byteReader.u4();
            }

            int getIntValue() {
                return (intValue);
            }
        }

        public class InterfaceMethodEntry extends MethodReferenceEntry {
            InterfaceMethodEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.INTERFACEMETHOD);
            }
        }

        public class LongEntry extends Entry {
            private final long longValue;

            LongEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.LONG);
                longValue = _byteReader.u8();
            }

            long getLongValue() {
                return (longValue);
            }
        }

        public class MethodEntry extends MethodReferenceEntry {
            MethodEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.METHOD);
            }

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append(getClassEntry().getNameUTF8Entry().getUTF8());
                sb.append('.');
                sb.append(getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
                sb.append(getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
                return (sb.toString());
            }
        }

        public class NameAndTypeEntry extends Entry {
            private final int descriptorIndex;

            private final int nameIndex;

            NameAndTypeEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.NAMEANDTYPE);
                nameIndex = _byteReader.u2();
                descriptorIndex = _byteReader.u2();
            }

            int getDescriptorIndex() {
                return (descriptorIndex);
            }

            public UTF8Entry getDescriptorUTF8Entry() {
                return (getUTF8Entry(descriptorIndex));
            }

            int getNameIndex() {
                return (nameIndex);
            }

            public UTF8Entry getNameUTF8Entry() {
                return (getUTF8Entry(nameIndex));
            }
        }

        class MethodTypeEntry extends Entry {
            private final int descriptorIndex;

            MethodTypeEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.METHODTYPE);
                descriptorIndex = _byteReader.u2();
            }

            int getDescriptorIndex() {
                return (descriptorIndex);
            }

            UTF8Entry getDescriptorUTF8Entry() {
                return (ConstantPool.this.getUTF8Entry(descriptorIndex));
            }

        }

        class MethodHandleEntry extends Entry {
            // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4

            private final int referenceKind;

            private final int referenceIndex;

            MethodHandleEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.METHODHANDLE);
                referenceKind = _byteReader.u1();
                referenceIndex = _byteReader.u2();
            }

            int getReferenceIndex() {
                return (referenceIndex);
            }

            int getReferenceKind() {
                return (referenceKind);
            }

        }

        class InvokeDynamicEntry extends Entry {
            // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4

            private final int bootstrapMethodAttrIndex;

            private final int nameAndTypeIndex;

            InvokeDynamicEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.INVOKEDYNAMIC);
                bootstrapMethodAttrIndex = _byteReader.u2();
                nameAndTypeIndex = _byteReader.u2();
            }

            int getBootstrapMethodAttrIndex() {
                return (bootstrapMethodAttrIndex);
            }

            int getNameAndTypeIndex() {
                return (nameAndTypeIndex);
            }

        }

        public abstract class MethodReferenceEntry extends ReferenceEntry {

            public class Arg extends Type {
                Arg(String _signature, int _start, int _pos, int _argc) {
                    super(_signature.substring(_start, _pos + 1));
                    argc = _argc;
                }

                private final int argc;

                int getArgc() {
                    return (argc);
                }
            }

            private Arg[] args = null;

            private Type returnType = null;

            @Override
            public int hashCode() {
                final NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

                return ((((nameAndTypeEntry.getNameIndex() * 31) + nameAndTypeEntry.getDescriptorIndex()) * 31) + getClassIndex());
            }

            @Override
            public boolean equals(Object _other) {
                if ((_other == null) || !(_other instanceof MethodReferenceEntry)) {
                    return (false);
                } else {
                    final MethodReferenceEntry otherMethodReferenceEntry = (MethodReferenceEntry) _other;
                    return ((otherMethodReferenceEntry.getNameAndTypeEntry().getNameIndex() == getNameAndTypeEntry().getNameIndex())
                        && (otherMethodReferenceEntry.getNameAndTypeEntry().getDescriptorIndex() == getNameAndTypeEntry()
                        .getDescriptorIndex()) && (otherMethodReferenceEntry.getClassIndex() == getClassIndex()));
                }
            }

            MethodReferenceEntry(ByteReader byteReader, int slot, ConstantPoolType constantPoolType) {
                super(byteReader, slot, constantPoolType);

            }

            public int getStackProduceCount() {
                return (getReturnType().isVoid() ? 0 : 1);
            }

            Type getReturnType() {
                if (returnType == null) {
                    getArgs();
                }

                return (returnType);
            }

            public Arg[] getArgs() {
                if ((args == null) || (returnType == null)) {
                    final List<Arg> argList = new ArrayList<>();
                    final NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

                    final String signature = nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8();// "([[IF)V" for a method that takes an int[][], float and returns void.
                    // Sadly we need to parse this, we need the # of arguments for the call
                    SignatureParseState state = SignatureParseState.skipping;
                    int start = 0;

                    for (int pos = 0; state != SignatureParseState.done; pos++) {
                        final char ch = signature.charAt(pos);
                        switch (ch) {
                            case '(':
                                state = SignatureParseState.counting;
                                break;
                            case ')':
                                state = SignatureParseState.done;
                                returnType = new Type(signature.substring(pos + 1));
                                break;
                            case '[':
                                switch (state) {
                                    case counting:
                                        state = SignatureParseState.inArray;
                                        start = pos;
                                        break;

                                }
                                // we don't care about arrays
                                break;
                            case 'L':
                                // beginning of Ljava/lang/String; or something

                                switch (state) {
                                    case counting:
                                        start = pos;
                                        // fallthrough intended!!
                                    case inArray:
                                        state = SignatureParseState.inclass;
                                        break;
                                }
                                break;
                            case ';':
                                // note we will only be in 'inclass' if we were previously counting, so this is safe
                                switch (state) {
                                    case inclass:
                                        argList.add(new Arg(signature, start, pos, argList.size()));
                                        state = SignatureParseState.counting;
                                        break;
                                }
                                break;

                            default:
                                // we have IJBZDF so inc counter if we are still counting
                                switch (state) {
                                    case counting:
                                        start = pos;
                                        // fallthrough intended!!
                                    case inArray:
                                        argList.add(new Arg(signature, start, pos, argList.size()));
                                        break;

                                }
                                break;
                        }
                    }
                    // System.out.println("method "+name+" has signature of "+signature+" which has "+count+" args");

                    args = argList.toArray(new Arg[argList.size()]);
                }

                return (args);
            }

            public int getStackConsumeCount() {
                return (getArgs().length);
            }
        }

        public abstract class ReferenceEntry extends Entry {
            final int referenceClassIndex;

            final int nameAndTypeIndex;

            protected int argCount = -1;

            ReferenceEntry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType) {
                super(_byteReader, _slot, _constantPoolType);
                referenceClassIndex = _byteReader.u2();
                nameAndTypeIndex = _byteReader.u2();
            }

            public ClassEntry getClassEntry() {
                return (ConstantPool.this.getClassEntry(referenceClassIndex));
            }

            public int getClassIndex() {
                return (referenceClassIndex);
            }

            public NameAndTypeEntry getNameAndTypeEntry() {
                return (ConstantPool.this.getNameAndTypeEntry(nameAndTypeIndex));
            }

            int getNameAndTypeIndex() {
                return (nameAndTypeIndex);
            }

            public boolean same(Entry _entry) {
                if (_entry instanceof ReferenceEntry) {
                    final ReferenceEntry entry = (ReferenceEntry) _entry;
                    return ((referenceClassIndex == entry.referenceClassIndex) && (nameAndTypeIndex == entry.nameAndTypeIndex));
                }

                return (false);
            }

            public class Type {
                private int arrayDimensions = 0;

                Type(String _type) {
                    type = _type;

                    while (type.charAt(arrayDimensions) == '[') {
                        arrayDimensions++;
                    }
                    type = type.substring(arrayDimensions);
                }

                public String getType() {
                    return (type);
                }

                boolean isVoid() {
                    return (type.equals("V"));
                }

                private String type;

                public boolean isArray() {
                    return (arrayDimensions > 0);
                }

                public int getArrayDimensions() {
                    return (arrayDimensions);
                }
            }
        }

        public class StringEntry extends Entry {
            private final int utf8Index;

            StringEntry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.STRING);
                utf8Index = _byteReader.u2();
            }

            int getUTF8Index() {
                return (utf8Index);
            }

            UTF8Entry getStringUTF8Entry() {
                return (getUTF8Entry(utf8Index));
            }
        }

        public class UTF8Entry extends Entry {
            private final String UTF8;

            UTF8Entry(ByteReader _byteReader, int _slot) {
                super(_byteReader, _slot, ConstantPoolType.UTF8);
                UTF8 = _byteReader.utf8();
            }

            public String getUTF8() {
                return (UTF8);
            }
        }

        ConstantPool(ByteReader _byteReader) {
            final int size = _byteReader.u2();
            add(new EmptyEntry(_byteReader, 0)); // slot 0

            for (int i = 1; i < size; i++) {
                final ConstantPoolType constantPoolType = ConstantPoolType.values()[_byteReader.u1()];

                switch (constantPoolType) {
                    case UTF8:
                        add(new UTF8Entry(_byteReader, i));
                        break;
                    case INTEGER:
                        add(new IntegerEntry(_byteReader, i));
                        break;
                    case FLOAT:
                        add(new FloatEntry(_byteReader, i));
                        break;
                    case LONG:
                        add(new LongEntry(_byteReader, i));
                        i++;// Longs take two slots in the ConstantPool
                        add(new EmptyEntry(_byteReader, i));
                        break;
                    case DOUBLE:
                        add(new DoubleEntry(_byteReader, i));
                        i++; // Doubles take two slots in the ConstantPool
                        add(new EmptyEntry(_byteReader, i));
                        break;
                    case CLASS:
                        add(new ClassEntry(_byteReader, i));
                        break;
                    case STRING:
                        add(new StringEntry(_byteReader, i));
                        break;
                    case FIELD:
                        add(new FieldEntry(_byteReader, i));
                        break;
                    case METHOD:
                        add(new MethodEntry(_byteReader, i));
                        break;
                    case INTERFACEMETHOD:
                        add(new InterfaceMethodEntry(_byteReader, i));
                        break;
                    case NAMEANDTYPE:
                        add(new NameAndTypeEntry(_byteReader, i));
                        break;
                    case METHODHANDLE:
                        add(new MethodHandleEntry(_byteReader, i));
                        break;
                    case METHODTYPE:
                        add(new MethodTypeEntry(_byteReader, i));
                        break;
                    case INVOKEDYNAMIC:
                        add(new InvokeDynamicEntry(_byteReader, i));
                        break;
                    default:
                        System.out.printf("slot %04x unexpected Constant constantPoolType = %s\n", i, constantPoolType);
                }
            }
        }

        ClassEntry getClassEntry(int _index) {
            try {
                return ((ClassEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        public DoubleEntry getDoubleEntry(int _index) {
            try {
                return ((DoubleEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        public FieldEntry getFieldEntry(int _index) {
            try {
                return ((FieldEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        FieldEntry getFieldEntry(String _name) {
            for (Entry entry : entries) {
                if (entry instanceof FieldEntry) {
                    String fieldName = ((ReferenceEntry) entry).getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                    if (_name.equals(fieldName)) {
                        return (FieldEntry) entry;
                    }
                }
            }
            return null;
        }

        public FloatEntry getFloatEntry(int _index) {
            try {
                return ((FloatEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        public IntegerEntry getIntegerEntry(int _index) {
            try {
                return ((IntegerEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        public InterfaceMethodEntry getInterfaceMethodEntry(int _index) {
            try {
                return ((InterfaceMethodEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        public LongEntry getLongEntry(int _index) {
            try {
                return ((LongEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        public MethodEntry getMethodEntry(int _index) {
            try {
                return ((MethodEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        NameAndTypeEntry getNameAndTypeEntry(int _index) {
            try {
                return ((NameAndTypeEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        public StringEntry getStringEntry(int _index) {
            try {
                return ((StringEntry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        UTF8Entry getUTF8Entry(int _index) {
            try {
                return ((UTF8Entry) entries.get(_index));
            } catch (final ClassCastException e) {
                return (null);
            }
        }

        void add(Entry _entry) {
            entries.add(_entry);

        }

        @Override
        public Iterator<Entry> iterator() {
            return (entries.iterator());
        }

        public Entry get(int _index) {
            return (entries.get(_index));
        }

        public String getDescription(ConstantPool.Entry _entry) {
            final StringBuilder sb = new StringBuilder();
            if (_entry instanceof ConstantPool.EmptyEntry) {
            } else if (_entry instanceof ConstantPool.DoubleEntry) {
                final ConstantPool.DoubleEntry doubleEntry = (ConstantPool.DoubleEntry) _entry;
                sb.append(doubleEntry.getDoubleValue());
            } else if (_entry instanceof ConstantPool.FloatEntry) {
                final ConstantPool.FloatEntry floatEntry = (ConstantPool.FloatEntry) _entry;
                sb.append(floatEntry.getFloatValue());
            } else if (_entry instanceof ConstantPool.IntegerEntry) {
                final ConstantPool.IntegerEntry integerEntry = (ConstantPool.IntegerEntry) _entry;
                sb.append(integerEntry.getIntValue());
            } else if (_entry instanceof ConstantPool.LongEntry) {
                final ConstantPool.LongEntry longEntry = (ConstantPool.LongEntry) _entry;
                sb.append(longEntry.getLongValue());
            } else if (_entry instanceof ConstantPool.UTF8Entry) {
                final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) _entry;
                sb.append(utf8Entry.getUTF8());
            } else if (_entry instanceof ConstantPool.StringEntry) {
                final ConstantPool.StringEntry stringEntry = (ConstantPool.StringEntry) _entry;
                final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(stringEntry.getUTF8Index());
                sb.append(utf8Entry.getUTF8());
            } else if (_entry instanceof ConstantPool.ClassEntry) {
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) _entry;
                final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
                sb.append(utf8Entry.getUTF8());
            } else if (_entry instanceof ConstantPool.NameAndTypeEntry) {
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) _entry;
                final ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
                final ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
                sb.append(utf8NameEntry.getUTF8()).append('.').append(utf8DescriptorEntry.getUTF8());
            } else if (_entry instanceof ConstantPool.MethodEntry) {
                final ConstantPool.MethodEntry methodEntry = (ConstantPool.MethodEntry) _entry;
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(methodEntry.getClassIndex());
                final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(methodEntry
                    .getNameAndTypeIndex());
                final ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
                final ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
                sb.append(convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + '.' + utf8NameEntry.getUTF8()));
            } else if (_entry instanceof ConstantPool.InterfaceMethodEntry) {
                final ConstantPool.InterfaceMethodEntry interfaceMethodEntry = (ConstantPool.InterfaceMethodEntry) _entry;
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(interfaceMethodEntry.getClassIndex());
                final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(interfaceMethodEntry
                    .getNameAndTypeIndex());
                final ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
                final ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
                sb.append(convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + '.' + utf8NameEntry.getUTF8()));
            } else if (_entry instanceof ConstantPool.FieldEntry) {
                final ConstantPool.FieldEntry fieldEntry = (ConstantPool.FieldEntry) _entry;
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(fieldEntry.getClassIndex());
                final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(fieldEntry
                    .getNameAndTypeIndex());
                final ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
                final ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
                sb.append(convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + '.' + utf8NameEntry.getUTF8()));
            }

            return (sb.toString());
        }


        public int[] getConstantPoolReferences(ConstantPool.Entry _entry) {
            int[] references;
            if (_entry instanceof ConstantPool.StringEntry) {
                final ConstantPool.StringEntry stringEntry = (ConstantPool.StringEntry) _entry;
                references = new int[]{
                    stringEntry.getUTF8Index()
                };
            } else if (_entry instanceof ConstantPool.ClassEntry) {
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) _entry;
                references = new int[]{
                    classEntry.getNameIndex()
                };
            } else if (_entry instanceof ConstantPool.NameAndTypeEntry) {
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) _entry;
                references = new int[]{
                    nameAndTypeEntry.getNameIndex(), nameAndTypeEntry.getDescriptorIndex()
                };
            } else if (_entry instanceof ConstantPool.MethodEntry) {
                final ConstantPool.MethodEntry methodEntry = (ConstantPool.MethodEntry) _entry;
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(methodEntry.getClassIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(methodEntry
                    .getNameAndTypeIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
                references = new int[]{
                    methodEntry.getClassIndex(), classEntry.getNameIndex(), nameAndTypeEntry.getNameIndex(),
                    nameAndTypeEntry.getDescriptorIndex()
                };
            } else if (_entry instanceof ConstantPool.InterfaceMethodEntry) {
                final ConstantPool.InterfaceMethodEntry interfaceMethodEntry = (ConstantPool.InterfaceMethodEntry) _entry;
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(interfaceMethodEntry.getClassIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(interfaceMethodEntry
                    .getNameAndTypeIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
                references = new int[]{
                    interfaceMethodEntry.getClassIndex(), classEntry.getNameIndex(), nameAndTypeEntry.getNameIndex(),
                    nameAndTypeEntry.getDescriptorIndex()
                };
            } else if (_entry instanceof ConstantPool.FieldEntry) {
                final ConstantPool.FieldEntry fieldEntry = (ConstantPool.FieldEntry) _entry;
                final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(fieldEntry.getClassIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
                final ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(fieldEntry
                    .getNameAndTypeIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
                @SuppressWarnings("unused") final ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
                references = new int[]{
                    fieldEntry.getClassIndex(), classEntry.getNameIndex(), nameAndTypeEntry.getNameIndex(),
                    nameAndTypeEntry.getDescriptorIndex()
                };
            } else {
                references = new int[0];
            }

            return (references);
        }

        public String getType(ConstantPool.Entry _entry) {
            final StringBuilder sb = new StringBuilder();
            if (_entry instanceof ConstantPool.EmptyEntry) {
                sb.append("empty");
            } else if (_entry instanceof ConstantPool.DoubleEntry) {
                sb.append("double");
            } else if (_entry instanceof ConstantPool.FloatEntry) {
                sb.append("float");
            } else if (_entry instanceof ConstantPool.IntegerEntry) {
                sb.append("int");
            } else if (_entry instanceof ConstantPool.LongEntry) {
                sb.append("long");
            } else if (_entry instanceof ConstantPool.UTF8Entry) {
                sb.append("utf8");
            } else if (_entry instanceof ConstantPool.StringEntry) {
                sb.append("string");
            } else if (_entry instanceof ConstantPool.ClassEntry) {
                sb.append("class");
            } else if (_entry instanceof ConstantPool.NameAndTypeEntry) {
                sb.append("name/type");
            } else if (_entry instanceof ConstantPool.MethodEntry) {
                sb.append("method");
            } else if (_entry instanceof ConstantPool.InterfaceMethodEntry) {
                sb.append("interface method");
            } else if (_entry instanceof ConstantPool.FieldEntry) {
                sb.append("field");
            }

            return (sb.toString());
        }

        public Object getConstantEntry(int _constantPoolIndex) {
            final Entry entry = get(_constantPoolIndex);
            Object object = null;
            switch (entry.getConstantPoolType()) {
                case FLOAT:
                    object = ((FloatEntry) entry).getFloatValue();
                    break;
                case DOUBLE:
                    object = ((DoubleEntry) entry).getDoubleValue();
                    break;
                case INTEGER:
                    object = ((IntegerEntry) entry).getIntValue();
                    break;
                case LONG:
                    object = ((LongEntry) entry).getLongValue();
                    break;
                case STRING:
                    object = ((StringEntry) entry).getStringUTF8Entry().getUTF8();
                    break;
            }

            return (object);
        }
    }

    public class AttributePool {
        private final List<AttributePoolEntry> attributePoolEntries = new ArrayList<>();

        public class CodeEntry extends AttributePoolEntry {

            class ExceptionPoolEntry {
                private final int exceptionClassIndex;

                private final int end;

                private final int handler;

                private final int start;

                ExceptionPoolEntry(ByteReader _byteReader) {
                    start = _byteReader.u2();
                    end = _byteReader.u2();
                    handler = _byteReader.u2();
                    exceptionClassIndex = _byteReader.u2();
                }

                public ConstantPool.ClassEntry getClassEntry() {
                    return (constantPool.getClassEntry(exceptionClassIndex));
                }

                public int getClassIndex() {
                    return (exceptionClassIndex);
                }

                public int getEnd() {
                    return (end);
                }

                public int getHandler() {
                    return (handler);
                }

                public int getStart() {
                    return (start);
                }
            }

            private final List<ExceptionPoolEntry> exceptionPoolEntries = new ArrayList<>();

            private final AttributePool codeEntryAttributePool;

            private final byte[] code;

            private final int maxLocals;

            private final int maxStack;

            CodeEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                maxStack = _byteReader.u2();
                maxLocals = _byteReader.u2();
                final int codeLength = _byteReader.u4();
                code = _byteReader.bytes(codeLength);
                final int exceptionTableLength = _byteReader.u2();

                for (int i = 0; i < exceptionTableLength; i++) {
                    exceptionPoolEntries.add(new ExceptionPoolEntry(_byteReader));
                }

                codeEntryAttributePool = new AttributePool(_byteReader, getName());
            }

            @Override
            public AttributePool getAttributePool() {
                return (codeEntryAttributePool);
            }

            public LineNumberTableEntry getLineNumberTableEntry() {
                return (codeEntryAttributePool.getLineNumberTableEntry());
            }

            public int getMaxLocals() {
                return (maxLocals);
            }

            public int getMaxStack() {
                return (maxStack);
            }

            byte[] getCode() {
                return code;
            }

            public List<ExceptionPoolEntry> getExceptionPoolEntries() {
                return exceptionPoolEntries;
            }
        }

        class ConstantValueEntry extends AttributePoolEntry {
            private final int index;

            ConstantValueEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                index = _byteReader.u2();
            }

            public int getIndex() {
                return (index);
            }

        }

        public class DeprecatedEntry extends AttributePoolEntry {
            DeprecatedEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
            }
        }

        abstract class AttributePoolEntry {
            final int length;

            final int nameIndex;

            AttributePoolEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                nameIndex = _nameIndex;
                length = _length;
            }

            public AttributePool getAttributePool() {
                return (null);
            }

            public int getLength() {
                return (length);
            }

            String getName() {
                return (constantPool.getUTF8Entry(nameIndex).getUTF8());
            }

            public int getNameIndex() {
                return (nameIndex);
            }
        }

        abstract class PoolEntry<T> extends AttributePoolEntry implements Iterable<T> {
            private final List<T> pool = new ArrayList<>();

            List<T> getPool() {
                return (pool);
            }

            PoolEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
            }

            @Override
            public Iterator<T> iterator() {
                return (pool.iterator());
            }
        }

        public class ExceptionEntry extends PoolEntry<Integer> {
            ExceptionEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                final int exceptionTableLength = _byteReader.u2();
                for (int i = 0; i < exceptionTableLength; i++) {
                    getPool().add(_byteReader.u2());
                }
            }
        }

        class InnerClassesEntry extends PoolEntry<InnerClassesEntry.InnerClassInfo> {
            class InnerClassInfo {
                private final int innerAccess;

                private final int innerIndex;

                private final int innerNameIndex;

                private final int outerIndex;

                InnerClassInfo(ByteReader _byteReader) {
                    innerIndex = _byteReader.u2();
                    outerIndex = _byteReader.u2();
                    innerNameIndex = _byteReader.u2();
                    innerAccess = _byteReader.u2();
                }

                public int getInnerAccess() {
                    return (innerAccess);
                }

                public int getInnerIndex() {
                    return (innerIndex);
                }

                public int getInnerNameIndex() {
                    return (innerNameIndex);
                }

                public int getOuterIndex() {
                    return (outerIndex);
                }
            }

            InnerClassesEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                final int innerClassesTableLength = _byteReader.u2();
                for (int i = 0; i < innerClassesTableLength; i++) {
                    getPool().add(new InnerClassInfo(_byteReader));
                }
            }
        }

        public class LineNumberTableEntry extends PoolEntry<LineNumberTableEntry.StartLineNumberPair> {

            class StartLineNumberPair {
                private final int lineNumber;

                private final int start;

                StartLineNumberPair(ByteReader _byteReader) {
                    start = _byteReader.u2();
                    lineNumber = _byteReader.u2();
                }

                int getLineNumber() {
                    return (lineNumber);
                }

                int getStart() {
                    return (start);
                }
            }

            LineNumberTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                final int lineNumberTableLength = _byteReader.u2();
                for (int i = 0; i < lineNumberTableLength; i++) {
                    getPool().add(new StartLineNumberPair(_byteReader));
                }
            }

            public int getSourceLineNumber(int _start, boolean _exact) {
                final Iterator<StartLineNumberPair> i = getPool().iterator();
                if (i.hasNext()) {
                    StartLineNumberPair from = i.next();
                    while (i.hasNext()) {
                        final StartLineNumberPair to = i.next();
                        if (_exact) {
                            if (_start == from.getStart()) {
                                return (from.getLineNumber());
                            }
                        } else if ((_start >= from.getStart()) && (_start < to.getStart())) {
                            return (from.getLineNumber());
                        }
                        from = to;
                    }
                    if (_exact) {
                        if (_start == from.getStart()) {
                            return (from.getLineNumber());
                        }
                    } else if (_start >= from.getStart()) {
                        return (from.getLineNumber());
                    }
                }

                return (-1);
            }
        }

        class EnclosingMethodEntry extends AttributePoolEntry {

            EnclosingMethodEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                enclosingClassIndex = _byteReader.u2();
                enclosingMethodIndex = _byteReader.u2();
            }

            private final int enclosingClassIndex;

            public int getClassIndex() {
                return (enclosingClassIndex);
            }

            private final int enclosingMethodIndex;

            public int getMethodIndex() {
                return (enclosingMethodIndex);
            }
        }

        class SignatureEntry extends AttributePoolEntry {

            SignatureEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                signatureIndex = _byteReader.u2();
            }

            private final int signatureIndex;

            int getSignatureIndex() {
                return (signatureIndex);
            }
        }

        public class RealLocalVariableTableEntry extends PoolEntry<RealLocalVariableTableEntry.RealLocalVariableInfo> implements
            LocalVariableTableEntry<RealLocalVariableTableEntry.RealLocalVariableInfo> {

            class RealLocalVariableInfo implements LocalVariableInfo {
                private final int descriptorIndex;

                private final int usageLength;

                private final int variableNameIndex;

                private final int start;

                private final int variableIndex;

                RealLocalVariableInfo(ByteReader _byteReader) {
                    start = _byteReader.u2();
                    usageLength = _byteReader.u2();
                    variableNameIndex = _byteReader.u2();
                    descriptorIndex = _byteReader.u2();
                    variableIndex = _byteReader.u2();
                }

                int getDescriptorIndex() {
                    return (descriptorIndex);
                }

                @Override
                public int getLength() {
                    return (usageLength);
                }

                int getNameIndex() {
                    return (variableNameIndex);
                }

                @Override
                public int getStart() {
                    return (start);
                }

                @Override
                public int getVariableIndex() {
                    return (variableIndex);
                }

                @Override
                public String getVariableName() {
                    return (constantPool.getUTF8Entry(variableNameIndex).getUTF8());
                }

                @Override
                public String getVariableDescriptor() {
                    return (constantPool.getUTF8Entry(descriptorIndex).getUTF8());
                }

                @Override
                public int getEnd() {
                    return (start + usageLength);
                }

                @Override
                public boolean isArray() {
                    return (getVariableDescriptor().startsWith("["));
                }
            }

            RealLocalVariableTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                final int localVariableTableLength = _byteReader.u2();
                for (int i = 0; i < localVariableTableLength; i++) {
                    getPool().add(new RealLocalVariableInfo(_byteReader));
                }
            }

            @Override
            public RealLocalVariableInfo getVariable(int _pc, int _index) {
                RealLocalVariableInfo returnValue = null;
                // System.out.println("pc = " + _pc + " index = " + _index);
                for (final RealLocalVariableInfo localVariableInfo : getPool()) {
                    // System.out.println("   start=" + localVariableInfo.getStart() + " length=" + localVariableInfo.getLength()
                    // + " varidx=" + localVariableInfo.getVariableIndex());
                    if ((_pc >= (localVariableInfo.getStart() - 1))
                        && (_pc <= (localVariableInfo.getStart() + localVariableInfo.getLength()))
                        && (_index == localVariableInfo.getVariableIndex())) {
                        returnValue = localVariableInfo;
                        break;
                    }
                }

                // System.out.println("returning " + returnValue);
                return (returnValue);
            }

            public String getVariableName(int _pc, int _index) {
                String returnValue = "unknown";
                final RealLocalVariableInfo localVariableInfo = getVariable(_pc, _index);
                if (localVariableInfo != null) {
                    returnValue = convert(constantPool.getUTF8Entry(localVariableInfo.getDescriptorIndex()).getUTF8(), constantPool
                        .getUTF8Entry(localVariableInfo.getNameIndex()).getUTF8());
                }
                // System.out.println("returning " + returnValue);
                return (returnValue);
            }
        }

        class BootstrapMethodsEntry extends AttributePoolEntry {
            // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
            class BootstrapMethod {
                class BootstrapArgument {
                    BootstrapArgument(ByteReader _byteReader) {
                        argument = _byteReader.u2();
                    }

                    final int argument;// u2;
                }

                BootstrapMethod(ByteReader _byteReader) {
                    bootstrapMethodRef = _byteReader.u2();
                    numBootstrapArguments = _byteReader.u2();
                    bootstrapArguments = new BootstrapArgument[numBootstrapArguments];
                    for (int i = 0; i < numBootstrapArguments; i++) {
                        bootstrapArguments[i] = new BootstrapArgument(_byteReader);
                    }
                }

                final int bootstrapMethodRef; //u2

                final int numBootstrapArguments; //u2

                final BootstrapArgument[] bootstrapArguments;
            }

            BootstrapMethodsEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                numBootstrapMethods = _byteReader.u2();
                bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
                for (int i = 0; i < numBootstrapMethods; i++) {
                    bootstrapMethods[i] = new BootstrapMethod(_byteReader);
                }
            }

            private final int numBootstrapMethods;

            final BootstrapMethod[] bootstrapMethods;

            int getNumBootstrapMethods() {
                return (numBootstrapMethods);
            }

        }

        class OtherEntry extends AttributePoolEntry {
            private final byte[] bytes;

            OtherEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                bytes = _byteReader.bytes(_length);
            }

            public byte[] getBytes() {
                return (bytes);
            }

            @Override
            public String toString() {
                return (new String(bytes));
            }

        }

        class StackMapTableEntry extends AttributePoolEntry {
            private final byte[] bytes;

            StackMapTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                bytes = _byteReader.bytes(_length);
            }

            byte[] getBytes() {
                return (bytes);
            }

            @Override
            public String toString() {
                return (new String(bytes));
            }
        }

        class LocalVariableTypeTableEntry extends AttributePoolEntry {
            private final byte[] bytes;

            LocalVariableTypeTableEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                bytes = _byteReader.bytes(_length);
            }

            public byte[] getBytes() {
                return (bytes);
            }

            @Override
            public String toString() {
                return (new String(bytes));
            }
        }

        public class SourceFileEntry extends AttributePoolEntry {
            private final int sourceFileIndex;

            SourceFileEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                sourceFileIndex = _byteReader.u2();
            }

            public int getSourceFileIndex() {
                return (sourceFileIndex);
            }

            public String getSourceFileName() {
                return (constantPool.getUTF8Entry(sourceFileIndex).getUTF8());
            }
        }

        public class SyntheticEntry extends AttributePoolEntry {
            SyntheticEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
            }
        }

        public class RuntimeAnnotationsEntry extends PoolEntry<RuntimeAnnotationsEntry.AnnotationInfo> {

            public class AnnotationInfo {
                private final int typeIndex;

                private final int elementValuePairCount;

                public class ElementValuePair {
                    class Value {
                        Value(int _tag) {
                            tag = _tag;
                        }

                        final int tag;

                    }

                    class PrimitiveValue extends Value {
                        private final int typeNameIndex;

                        private final int constNameIndex;

                        PrimitiveValue(int _tag, ByteReader _byteReader) {
                            super(_tag);
                            typeNameIndex = _byteReader.u2();
                            //constNameIndex = _byteReader.u2();
                            constNameIndex = 0;
                        }

                        public int getConstNameIndex() {
                            return (constNameIndex);
                        }

                        public int getTypeNameIndex() {
                            return (typeNameIndex);
                        }
                    }

                    class EnumValue extends Value {
                        EnumValue(int _tag, ByteReader _byteReader) {
                            super(_tag);
                        }
                    }

                    class ArrayValue extends Value {
                        ArrayValue(int _tag, ByteReader _byteReader) {
                            super(_tag);
                        }
                    }

                    class ClassValue extends Value {
                        ClassValue(int _tag, ByteReader _byteReader) {
                            super(_tag);
                        }
                    }

                    class AnnotationValue extends Value {
                        AnnotationValue(int _tag, ByteReader _byteReader) {
                            super(_tag);
                        }
                    }

                    @SuppressWarnings("unused")
                    private final int elementNameIndex;

                    @SuppressWarnings("unused")
                    private Value value;

                    ElementValuePair(ByteReader _byteReader) {
                        elementNameIndex = _byteReader.u2();
                        final int tag = _byteReader.u1();

                        switch (tag) {
                            case SIGC_BYTE:
                            case SIGC_CHAR:
                            case SIGC_INT:
                            case SIGC_LONG:
                            case SIGC_DOUBLE:
                            case SIGC_FLOAT:
                            case SIGC_SHORT:
                            case SIGC_BOOLEAN:
                            case 's': // special for String
                                value = new PrimitiveValue(tag, _byteReader);
                                break;
                            case 'e': // special for Enum
                                value = new EnumValue(tag, _byteReader);
                                break;
                            case 'c': // special for class
                                value = new ClassValue(tag, _byteReader);
                                break;
                            case '@': // special for Annotation
                                value = new AnnotationValue(tag, _byteReader);
                                break;
                            case 'a': // special for array
                                value = new ArrayValue(tag, _byteReader);
                                break;
                        }
                    }
                }

                private final ElementValuePair[] elementValuePairs;

                AnnotationInfo(ByteReader _byteReader) {
                    typeIndex = _byteReader.u2();
                    elementValuePairCount = _byteReader.u2();
                    elementValuePairs = new ElementValuePair[elementValuePairCount];
                    for (int i = 0; i < elementValuePairCount; i++) {
                        elementValuePairs[i] = new ElementValuePair(_byteReader);
                    }
                }

                public int getTypeIndex() {
                    return (typeIndex);
                }

                public String getTypeDescriptor() {
                    return (constantPool.getUTF8Entry(typeIndex).getUTF8());
                }
            }

            RuntimeAnnotationsEntry(ByteReader _byteReader, int _nameIndex, int _length) {
                super(_byteReader, _nameIndex, _length);
                final int localVariableTableLength = _byteReader.u2();
                for (int i = 0; i < localVariableTableLength; i++) {
                    getPool().add(new AnnotationInfo(_byteReader));
                }
            }

        }

        private CodeEntry codeEntry = null;

        private EnclosingMethodEntry enclosingMethodEntry = null;

        private DeprecatedEntry deprecatedEntry = null;

        private ExceptionEntry exceptionEntry = null;

        private LineNumberTableEntry lineNumberTableEntry = null;

        private LocalVariableTableEntry localVariableTableEntry = null;

        private RuntimeAnnotationsEntry runtimeVisibleAnnotationsEntry;

        private RuntimeAnnotationsEntry runtimeInvisibleAnnotationsEntry;

        private SourceFileEntry sourceFileEntry = null;

        private SyntheticEntry syntheticEntry = null;

        private BootstrapMethodsEntry bootstrapMethodsEntry = null;

        private final static String LOCALVARIABLETABLE_TAG = "LocalVariableTable";

        private final static String CONSTANTVALUE_TAG = "ConstantValue";

        private final static String LINENUMBERTABLE_TAG = "LineNumberTable";

        private final static String SOURCEFILE_TAG = "SourceFile";

        private final static String SYNTHETIC_TAG = "Synthetic";

        private final static String EXCEPTIONS_TAG = "Exceptions";

        private final static String INNERCLASSES_TAG = "InnerClasses";

        private final static String DEPRECATED_TAG = "Deprecated";

        private final static String CODE_TAG = "Code";

        private final static String ENCLOSINGMETHOD_TAG = "EnclosingMethod";

        private final static String SIGNATURE_TAG = "Signature";

        private final static String RUNTIMEINVISIBLEANNOTATIONS_TAG = "RuntimeInvisibleAnnotations";

        private final static String RUNTIMEVISIBLEANNOTATIONS_TAG = "RuntimeVisibleAnnotations";

        private final static String BOOTSTRAPMETHODS_TAG = "BootstrapMethods";

        private final static String STACKMAPTABLE_TAG = "StackMapTable";

        private final static String LOCALVARIABLETYPETABLE_TAG = "LocalVariableTypeTable";

        AttributePool(ByteReader _byteReader, String name) {
            final int attributeCount = _byteReader.u2();
            AttributePoolEntry entry = null;
            for (int i = 0; i < attributeCount; i++) {
                final int attributeNameIndex = _byteReader.u2();
                final int length = _byteReader.u4();
                UTF8Entry utf8Entry = constantPool.getUTF8Entry(attributeNameIndex);
                if (utf8Entry == null) {
                    throw new IllegalStateException("corrupted state reading attributes for " + name);
                }
                final String attributeName = utf8Entry.getUTF8();
                switch (attributeName) {
                    case LOCALVARIABLETABLE_TAG:
                        localVariableTableEntry = new RealLocalVariableTableEntry(_byteReader, attributeNameIndex, length);
                        entry = (AttributePoolEntry) localVariableTableEntry;
                        break;
                    case CONSTANTVALUE_TAG:
                        entry = new ConstantValueEntry(_byteReader, attributeNameIndex, length);
                        break;
                    case LINENUMBERTABLE_TAG:
                        lineNumberTableEntry = new LineNumberTableEntry(_byteReader, attributeNameIndex, length);
                        entry = lineNumberTableEntry;
                        break;
                    case SOURCEFILE_TAG:
                        sourceFileEntry = new SourceFileEntry(_byteReader, attributeNameIndex, length);
                        entry = sourceFileEntry;
                        break;
                    case SYNTHETIC_TAG:
                        syntheticEntry = new SyntheticEntry(_byteReader, attributeNameIndex, length);
                        entry = syntheticEntry;
                        break;
                    case EXCEPTIONS_TAG:
                        exceptionEntry = new ExceptionEntry(_byteReader, attributeNameIndex, length);
                        entry = exceptionEntry;
                        break;
                    case INNERCLASSES_TAG:
                        entry = new InnerClassesEntry(_byteReader, attributeNameIndex, length);
                        break;
                    case DEPRECATED_TAG:
                        deprecatedEntry = new DeprecatedEntry(_byteReader, attributeNameIndex, length);
                        entry = deprecatedEntry;
                        break;
                    case CODE_TAG:
                        codeEntry = new CodeEntry(_byteReader, attributeNameIndex, length);
                        entry = codeEntry;
                        break;
                    case ENCLOSINGMETHOD_TAG:
                        enclosingMethodEntry = new EnclosingMethodEntry(_byteReader, attributeNameIndex, length);
                        entry = enclosingMethodEntry;
                        break;
                    case SIGNATURE_TAG:
                        entry = new SignatureEntry(_byteReader, attributeNameIndex, length);
                        break;
                    case RUNTIMEINVISIBLEANNOTATIONS_TAG:
                        runtimeInvisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
                        entry = runtimeInvisibleAnnotationsEntry;
                        break;
                    case RUNTIMEVISIBLEANNOTATIONS_TAG:
                        runtimeVisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
                        entry = runtimeVisibleAnnotationsEntry;
                        break;
                    case BOOTSTRAPMETHODS_TAG:
                        bootstrapMethodsEntry = new BootstrapMethodsEntry(_byteReader, attributeNameIndex, length);
                        entry = bootstrapMethodsEntry;
                        // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
                        break;
                    case STACKMAPTABLE_TAG:
                        // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.4

                        entry = new StackMapTableEntry(_byteReader, attributeNameIndex, length);
                        break;
                    case LOCALVARIABLETYPETABLE_TAG:
                        // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.14
                        entry = new LocalVariableTypeTableEntry(_byteReader, attributeNameIndex, length);
                        break;
                    default:
                        logger.warning("Found unexpected Attribute (name = " + attributeName + ')');
                        entry = new OtherEntry(_byteReader, attributeNameIndex, length);
                        break;
                }
                attributePoolEntries.add(entry);

            }
        }

        CodeEntry getCodeEntry() {
            return (codeEntry);
        }

        public DeprecatedEntry getDeprecatedEntry() {
            return (deprecatedEntry);
        }

        public ExceptionEntry getExceptionEntry() {
            return (exceptionEntry);
        }

        LineNumberTableEntry getLineNumberTableEntry() {
            return (lineNumberTableEntry);
        }

        public LocalVariableTableEntry getLocalVariableTableEntry() {
            return (localVariableTableEntry);
        }

        public SourceFileEntry getSourceFileEntry() {
            return (sourceFileEntry);
        }

        public SyntheticEntry getSyntheticEntry() {
            return (syntheticEntry);
        }

        public RuntimeAnnotationsEntry getRuntimeInvisibleAnnotationsEntry() {
            return (runtimeInvisibleAnnotationsEntry);
        }

        public RuntimeAnnotationsEntry getRuntimeVisibleAnnotationsEntry() {
            return (runtimeVisibleAnnotationsEntry);
        }

        public RuntimeAnnotationsEntry getBootstrap() {
            return (runtimeVisibleAnnotationsEntry);
        }

    }

    private static final ClassLoader classModelLoader = ClassModel.class.getClassLoader();

    public class ClassModelField {
        private final int fieldAccessFlags;

        final AttributePool fieldAttributePool;

        private final int descriptorIndex;

        private final int index;

        private final int nameIndex;

        ClassModelField(ByteReader _byteReader, int _index) {
            index = _index;
            fieldAccessFlags = _byteReader.u2();
            nameIndex = _byteReader.u2();
            descriptorIndex = _byteReader.u2();
            fieldAttributePool = new AttributePool(_byteReader, getName());
        }

        public int getAccessFlags() {
            return (fieldAccessFlags);
        }

        public AttributePool getAttributePool() {
            return (fieldAttributePool);
        }

        public String getDescriptor() {
            return (getDescriptorUTF8Entry().getUTF8());
        }

        public int getDescriptorIndex() {
            return (descriptorIndex);
        }

        ConstantPool.UTF8Entry getDescriptorUTF8Entry() {
            return (constantPool.getUTF8Entry(descriptorIndex));
        }

        public int getIndex() {
            return (index);
        }

        public String getName() {
            return (getNameUTF8Entry().getUTF8());
        }

        public int getNameIndex() {
            return (nameIndex);
        }

        ConstantPool.UTF8Entry getNameUTF8Entry() {
            return (constantPool.getUTF8Entry(nameIndex));
        }

        public Class<?> getDeclaringClass() {
            final String clazzName = getDescriptor().replaceAll("^L", "").replaceAll("/", ".").replaceAll(";$", "");
            try {
                return (Class.forName(clazzName, true, classModelLoader));
            } catch (final ClassNotFoundException e) {
                System.out.println("no class found for " + clazzName);
                e.printStackTrace();
                return null;
            }
        }
    }

    public class ClassModelMethod {

        private final int methodAccessFlags;

        private final AttributePool methodAttributePool;

        private final int descriptorIndex;

        private final int index;

        private final int nameIndex;

        private final CodeEntry codeEntry;

        ClassModelMethod(ByteReader _byteReader, int _index) {
            index = _index;
            methodAccessFlags = _byteReader.u2();
            nameIndex = _byteReader.u2();
            descriptorIndex = _byteReader.u2();
            methodAttributePool = new AttributePool(_byteReader, getName());
            codeEntry = methodAttributePool.getCodeEntry();
        }

        @Override
        public int hashCode() {
            return index;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        public int getAccessFlags() {
            return (methodAccessFlags);
        }

        public boolean isStatic() {
            return (Access.STATIC.bitIsSet(methodAccessFlags));
        }

        AttributePool getAttributePool() {
            return (methodAttributePool);
        }

        public AttributePool.CodeEntry getCodeEntry() {
            return (methodAttributePool.getCodeEntry());
        }

        public String getDescriptor() {
            return (getDescriptorUTF8Entry().getUTF8());
        }

        public int getDescriptorIndex() {
            return (descriptorIndex);
        }

        public ConstantPool.UTF8Entry getDescriptorUTF8Entry() {
            return (constantPool.getUTF8Entry(descriptorIndex));
        }

        public int getIndex() {
            return (index);
        }

        public String getName() {
            return (getNameUTF8Entry().getUTF8());
        }

        public int getNameIndex() {
            return (nameIndex);
        }

        ConstantPool.UTF8Entry getNameUTF8Entry() {
            return (constantPool.getUTF8Entry(nameIndex));
        }

        public ConstantPool getConstantPool() {
            return (constantPool);
        }

        public AttributePool.LineNumberTableEntry getLineNumberTableEntry() {
            return (getAttributePool().codeEntry.codeEntryAttributePool.lineNumberTableEntry);
        }

        public LocalVariableTableEntry getLocalVariableTableEntry() {
            return (getAttributePool().codeEntry.codeEntryAttributePool.localVariableTableEntry);
        }

        void setLocalVariableTableEntry(LocalVariableTableEntry _localVariableTableEntry) {
            getAttributePool().codeEntry.codeEntryAttributePool.localVariableTableEntry = _localVariableTableEntry;
        }

        public LocalVariableInfo getLocalVariable(int _pc, int _index) {
            return (getLocalVariableTableEntry().getVariable(_pc, _index));
        }

        public byte[] getCode() {
            return (codeEntry.getCode());
        }

        public ClassModel getClassModel() {
            return (ClassModel.this);
        }

        public String toString() {
            return getClassModel().clazz.getName() + '.' + getName() + ' ' + getDescriptor();
        }

        public ClassModel getOwnerClassModel() {
            return ClassModel.this;
        }
    }

    class ClassModelInterface {
        private final int interfaceIndex;

        ClassModelInterface(ByteReader _byteReader) {
            interfaceIndex = _byteReader.u2();
        }

        ConstantPool.ClassEntry getClassEntry() {
            return (constantPool.getClassEntry(interfaceIndex));
        }

        int getInterfaceIndex() {
            return (interfaceIndex);
        }

    }

    public final Class<?> clazz;

    /**
     * Populate this model by parsing a given classfile from the given classloader.
     * <p>
     * We create a ByteReader (wrapper around the bytes representing the classfile) and pass it to local inner classes to handle the various sections of the class file.
     *
     * @param _classLoader The classloader to access the classfile
     * @param _className   The name of the class to load (we convert '.' to '/' and append ".class" so you don't have to).
     * @throws ClassParseException
     * @see ByteReader
     * @see <a href="http://java.sun.com/docs/books/jvms/second_edition/ClassFileFormat-Java5.pdf">Java 5 Class File Format</a>
     */
    private void parse(ClassLoader _classLoader, String _className) {

        parse(_classLoader.getResourceAsStream(_className.replace('.', '/') + ".class"));
    }

    private void parse(InputStream _inputStream) {

        ByteReader byteReader = new ByteReader(_inputStream);
        magic = byteReader.u4();
        minorVersion = byteReader.u2();
        majorVersion = byteReader.u2();
        constantPool = new ConstantPool(byteReader);

        accessFlags = byteReader.u2();
        thisClassConstantPoolIndex = byteReader.u2();
        superClassConstantPoolIndex = byteReader.u2();

        final int interfaceCount = byteReader.u2();
        for (int i = 0; i < interfaceCount; i++) {
            interfaces.add(new ClassModelInterface(byteReader));
        }

        final int fieldCount = byteReader.u2();
        for (int i = 0; i < fieldCount; i++) {
            fields.add(new ClassModelField(byteReader, i));
        }

        final int methodPoolLength = byteReader.u2();
        for (int i = 0; i < methodPoolLength; i++) {
            methods.add(new ClassModelMethod(byteReader, i));
        }

        attributePool = new AttributePool(byteReader, Reflection.getSimpleName(clazz));
    }

    public int getMagic() {
        return (magic);
    }

    public int getMajorVersion() {
        return (majorVersion);
    }

    public int getMinorVersion() {
        return (minorVersion);
    }

    public int getAccessFlags() {
        return (accessFlags);
    }

    public ConstantPool getConstantPool() {
        return (constantPool);
    }

    public int getThisClassConstantPoolIndex() {
        return (thisClassConstantPoolIndex);
    }

    public int getSuperClassConstantPoolIndex() {
        return (superClassConstantPoolIndex);
    }

    public AttributePool getAttributePool() {
        return (attributePool);
    }

    private ClassModelField getField(String _name, String _descriptor) {
        ClassModel other = this;
        while (true) {
            for (final ClassModelField entry : other.fields) {
                if (entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)) {
                    return (entry);
                }
            }
            other = other.superClazz;
        }
    }

    public ClassModelField getField(String _name) {
        ClassModel other = this;
        while (true) {
            for (final ClassModelField entry : other.fields) {
                if (entry.getName().equals(_name)) {
                    return (entry);
                }
            }
            other = other.superClazz;
        }
    }

    public ClassModelMethod getMethod(String _name, String _descriptor) {
        ClassModelMethod methodOrNull = getMethodOrNull(_name, _descriptor);
        if (methodOrNull == null)
            return superClazz != null ? superClazz.getMethod(_name, _descriptor) : (null);
        else
            return methodOrNull;
    }

    private ClassModelMethod getMethodOrNull(String _name, String _descriptor) {
        for (final ClassModelMethod entry : methods) {
            if (entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)) {
//            if (logger.isLoggable(Level.FINE)) {
//               logger.fine("Found " + clazz.getName() + '.' + entry.getName() + ' ' + entry.getDescriptor() + " for "
//                     + _name.replace('/', '.'));
//            }
                return (entry);
            }
        }
        return null;
    }


    /**
     * Look up a ConstantPool MethodEntry and return the corresponding Method.
     *
     * @param _methodEntry The ConstantPool MethodEntry we want.
     * @param _isSpecial   True if we wish to delegate to super (to support <code>super.foo()</code>)
     * @return The Method or null if we fail to locate a given method.
     */
    public ClassModelMethod getMethod(MethodEntry _methodEntry, boolean _isSpecial) {
        final String entryClassNameInDotForm = _methodEntry.getClassEntry().getNameUTF8Entry().getUTF8().replace('/', '.');

        // Shortcut direct calls to supers to allow "foo() { super.foo() }" type stuff to work
        if (_isSpecial && (superClazz != null) && superClazz.isSuperClass(entryClassNameInDotForm)) {
//         if (logger.isLoggable(Level.FINE)) {
//            logger.fine("going to look in super:" + superClazz.getClassWeAreModelling().getName() + " on behalf of "
//                  + entryClassNameInDotForm);
//         }
            return superClazz.getMethod(_methodEntry, false);
        }

        NameAndTypeEntry nameAndTypeEntry = _methodEntry.getNameAndTypeEntry();
        ClassModelMethod methodOrNull = getMethodOrNull(nameAndTypeEntry.getNameUTF8Entry().getUTF8(), nameAndTypeEntry
            .getDescriptorUTF8Entry().getUTF8());
        if (methodOrNull == null)
            return superClazz != null ? superClazz.getMethod(_methodEntry, false) : (null);
        return methodOrNull;
    }


    /**
     * Look up a ConstantPool MethodEntry and return the corresponding Method.
     *
     * @param _methodEntry The ConstantPool MethodEntry we want.
     * @param _isSpecial   True if we wish to delegate to super (to support <code>super.foo()</code>)
     * @return The Method or null if we fail to locate a given method.
     */
    public ClassModelMethod getMethod0(final MethodEntry _methodEntry, boolean _isSpecial) {
        ClassModel other = this;
        final NameAndTypeEntry meNT = _methodEntry.getNameAndTypeEntry();
        String meNT8 = meNT.getNameUTF8Entry().getUTF8();
        final String entryClassNameInDotForm = meNT8.replace('/', '.');
        while (true) {

            // Shortcut direct calls to supers to allow "foo() { super.foo() }" type stuff to work
            if (_isSpecial && (other.superClazz != null) && other.superClazz.isSuperClass(entryClassNameInDotForm)) {
//            if (other.logger.isLoggable(Level.FINE)) {
//               other.logger.fine("going to look in super:" + other.superClazz.getClassWeAreModelling().getName() + " on behalf of "
//                       + entryClassNameInDotForm);
//            }
                _isSpecial = false;
                other = other.superClazz;
                continue;
            }

            NameAndTypeEntry nameAndTypeEntry = meNT;
            ClassModelMethod methodOrNull = other.getMethodOrNull(nameAndTypeEntry.getNameUTF8Entry().getUTF8(), nameAndTypeEntry
                .getDescriptorUTF8Entry().getUTF8());
            if (methodOrNull == null)
                return other.superClazz != null ? other.superClazz.getMethod(_methodEntry, false) : (null);
            else
                return methodOrNull;
        }
    }


    private final ValueCache<MethodKey, MethodModel, AparapiException> methodModelCache = ValueCache
        .on(this::computeMethodModel);

    /**
     * Create a MethodModel for a given method name and signature.
     *
     * @param _name
     * @param _signature
     * @return
     * @throws AparapiException
     */
    private MethodModel getMethodModel(String _name, String _signature) throws AparapiException {
        if (CacheEnabler.areCachesEnabled())
            return methodModelCache.computeIfAbsent(MethodKey.of(_name, _signature));
        else {
            final ClassModelMethod method = getMethod(_name, _signature);
            return new MethodModel(method);
        }
    }

    private MethodModel computeMethodModel(MethodKey methodKey) throws AparapiException {
        final ClassModelMethod method = getMethod(methodKey.getName(), methodKey.getSignature());
        return new MethodModel(method);
    }

    // These fields use for accessor conversion
    private final ArrayList<FieldEntry> structMembers = new ArrayList<>();

    private final ArrayList<Long> structMemberOffsets = new ArrayList<>();

    private final ArrayList<TypeSpec> structMemberTypes = new ArrayList<>();

    private int totalStructSize = 0;

    public ArrayList<FieldEntry> getStructMembers() {
        return structMembers;
    }

    public ArrayList<Long> getStructMemberOffsets() {
        return structMemberOffsets;
    }

    public ArrayList<TypeSpec> getStructMemberTypes() {
        return structMemberTypes;
    }

    public int getTotalStructSize() {
        return totalStructSize;
    }

    public void setTotalStructSize(int x) {
        totalStructSize = x;
    }

    //   private final ValueCache<EntrypointKey, Entrypoint, AparapiException> entrypointCache = ValueCache.on(this::computeBasicEntrypoint);
    private final ValueCache<EntrypointKey, Entrypoint, AparapiException> entrypointCache = ValueCache
        .on(this::computeBasicEntrypoint);

    private Entrypoint getEntrypoint(String _entrypointName, String _descriptor, Object _k) throws AparapiException {
        if (CacheEnabler.areCachesEnabled()) {
            EntrypointKey key = EntrypointKey.of(_entrypointName, _descriptor);
            //long s = System.nanoTime();
            Entrypoint entrypointWithoutKernel = entrypointCache.computeIfAbsent(key);
            //long e = System.nanoTime() - s;
            return entrypointWithoutKernel.cloneForKernel(_k);
        } else {
            final MethodModel method = getMethodModel(_entrypointName, _descriptor);
            return new Entrypoint(this, method, _k);
        }
    }

    private Entrypoint computeBasicEntrypoint(EntrypointKey entrypointKey) throws AparapiException {
        return new Entrypoint(this,
            getMethodModel(entrypointKey.getEntrypointName(), entrypointKey.getDescriptor()),
            null);
    }

    public Entrypoint getEntrypoint(String _entrypointName, Object _k) throws AparapiException {
        return (getEntrypoint(_entrypointName, "()V", _k));
    }

    public Entrypoint getEntrypoint() throws AparapiException {
        return (getEntrypoint("run", "()V", null));
    }

    public static void invalidateCaches() {
        classModelCache.invalidate();
    }

    @Override
    public String toString() {
        return "ClassModel of " + clazz;
    }
}
