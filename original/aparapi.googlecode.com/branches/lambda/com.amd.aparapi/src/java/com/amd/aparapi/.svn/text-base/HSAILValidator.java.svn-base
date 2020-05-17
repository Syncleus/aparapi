package com.amd.aparapi;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HSAILValidator {
    static enum State {NONE, KERNEL_ARGS, BODY, LABEL, MULTILINE_COMMENT}

    ;

    static abstract class Operand{
        String name;
        Operand(String _name){
            name = _name;
        }
        @Override public String toString(){
            return(name);
        }
    }

    static class RegisterOperand extends Operand{

        RegisterOperand(String _name){
            super(_name);
        }

    }
    static class ConstantOperand extends Operand{

        ConstantOperand(String _name){
            super(_name);
        }

    }
    static class LabelOperand extends Operand{

        LabelOperand(String _name){
            super(_name);
        }

    }
    static class MemOperand extends Operand{
        RegisterOperand reg;
        ConstantOperand con;

        MemOperand(String _name){
            super(_name);
        }

    }



    static class Instruction {
        int lineNumber;
        Label label;
        String content;
        String tailComment;
        String mnemonic;
        Operand[] sourceOperands;
        Operand[] destinationOperands;
        boolean special = false;
        static Pattern tailCommentPattern = Pattern.compile("^ *(.*) *; *//(.*)");
        static Pattern noTailCommentPattern = Pattern.compile("^ *(.*) *; *");

        Instruction(int _lineNumber, String _content, String _tailComment, String _mnemonic, Operand[] _sourceOperands, Operand[] _destinationOperands,  Label _label) {
            lineNumber = _lineNumber;
            content = _content;

            tailComment = _tailComment;
            mnemonic = _mnemonic;
            sourceOperands = _sourceOperands;
            destinationOperands = _destinationOperands;
            label = _label;

        }
        static Instruction create(int _lineNumber, String _content) {
            return(create(_lineNumber, _content, null));
        }
        static Instruction create(int _lineNumber, String _content, Label _label) {
            String content=null;
            String tailComment = null;
            Matcher matcher = tailCommentPattern.matcher(_content);
            if (matcher.matches()) {
                content = matcher.group(1);
                tailComment = matcher.group(2);
            } else {
                matcher = noTailCommentPattern.matcher(_content);
                if (matcher.matches()) {
                    content = matcher.group(1);
                    tailComment = null;

                } else {
                   throw new IllegalStateException("what?");
                }

            }

            String mnemonic = null;

            Operand[] operands = null;
            if (content.contains(",")){
                int firstSpace = content.indexOf(' ');
                mnemonic = content.substring(0,firstSpace);
                String[] operandStrings = content.substring(firstSpace).split(",");
                operands = new Operand[operandStrings.length];
                for (int i=0; i< operandStrings.length; i++){
                    String operandString =  operandStrings[i].trim();
                    if (operandString.startsWith("@")){
                        operands[i]=new LabelOperand(operandStrings[i].trim());
                    }else if (operandString.startsWith("[")){
                        operands[i]=new MemOperand(operandStrings[i].trim());
                    } else if (operandString.startsWith("$")){
                        operands[i]=new RegisterOperand(operandStrings[i].trim().substring(1));
                    } else {
                        operands[i]=new ConstantOperand(operandStrings[i].trim());
                    }
                }
            }             else{
                mnemonic = content;
                operands = new Operand[0];
            }

            Operand[] source = null;
            Operand[] dest = null;
            switch (operands.length){
                case 0:
                    source = new Operand[0];
                    dest = new Operand[0];
                    break;
                case 1:
                    source = new Operand[1];
                    dest = new Operand[1];
                    source[0] = operands[0];
                    dest[0] = operands[0];
                    break;
                case 2:
                    source = new Operand[1];
                    dest = new Operand[1];


                if (mnemonic.startsWith("st_")){
                   source[0] = operands[0];
                   dest[0] = operands[1];
                }else{
                    source[0] = operands[1];
                    dest[0] = operands[0];
                }
                    break;
                case 3:
                    source = new Operand[2];
                    dest = new Operand[1];



                        source[0] = operands[1];
                        source[1] = operands[2];
                        dest[0] = operands[0];

                    break;
                case 4:
                    source = new Operand[3];
                    dest = new Operand[1];



                    source[0] = operands[1];
                    source[1] = operands[2];
                    source[2] = operands[3];
                    dest[0] = operands[0];

                    break;
            }


            return new Instruction(_lineNumber, content, tailComment, mnemonic, source, dest, _label);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (label != null) {
                sb.append(label.name).append(":\n");
            }
          //  sb.append("   " + content +" // ");
            sb.append("   ");
            for (Operand operand:destinationOperands){
                sb.append("{"+operand+"} ");
            }
            sb.append("<- {"+mnemonic+"} <- ");

            for (Operand operand:sourceOperands){
                sb.append("{"+operand+"} ");
            }

            return (sb.toString());
        }
    }

    static class Label {
        String name;

        Label(String _name) {
            name = _name;
        }
    }

    static String labelRegexCapture = "(\\@L[a-zA-Z0-9_]+)";

    static class LineMatcher {
        Pattern pattern;
        Matcher matcher;

        String getGroup(int group) {
            return (matcher.group(group));
        }

        boolean matches(String line) {
            Matcher lineMatcher = pattern.matcher(line);
            if (lineMatcher.matches()) {
                matcher = lineMatcher;

            } else {
                matcher = null;

            }
            return (matcher != null);
        }

        LineMatcher(Pattern _pattern) {
            pattern = _pattern;
        }
    }

    static LineMatcher labelMatcher = new LineMatcher(Pattern.compile("^ *" + labelRegexCapture + ": *"));
    static LineMatcher whiteSpaceMatcher = new LineMatcher(Pattern.compile("^ *//(.*)"));
    static LineMatcher multiLineStartMatcher = new LineMatcher(Pattern.compile("^ */\\*(.*)"));
    static LineMatcher multiLineEndMatcher = new LineMatcher(Pattern.compile("^ *\\*/(.*)"));
    static LineMatcher versionMatcher = new LineMatcher(Pattern.compile("^ *version *([0-9]+:[0-9]+:) *(\\$[a-z]+) *: *(\\$[a-z]+).*"));
    static LineMatcher kernelMatcher = new LineMatcher(Pattern.compile("^ *kernel.*"));

    static LineMatcher kernelArgMatcher = new LineMatcher(Pattern.compile("^ *kernarg_([usb](64|32|16|8)) *(\\%_arg[0-9]+).*"));
    static LineMatcher bodyStartMatcher = new LineMatcher(Pattern.compile("^ *\\)\\{ *"));
    static LineMatcher bodyEndMatcher = new LineMatcher(Pattern.compile("^ *\\}; *"));

    public static void main(String[] _args) throws IOException, ClassNotFoundException, ClassParseException {

       String className = _args.length>0?_args[0]:"hsailtest.StringLambda";

       ClassModel classModel = ClassModel.getClassModel(Class.forName(className));

       String methodName = null;
       if (_args.length>1){
          methodName = _args[1];
       }else{
          System.out.println("methods");
          for (ClassModel.ClassModelMethod m:classModel.getMethods()){
              if (m.getName().startsWith("lambda")){
                 System.out.println(m.getName()+","+m.getDescriptor());
              }
              
          }
          System.exit(1);
       }
       String methodSignature = null;
       if (_args.length>2){
          methodSignature = _args[2];
       }else{
          for (ClassModel.ClassModelMethod m:classModel.getMethods()){
              if (m.getName().equals(methodName)){
                 methodSignature = m.getDescriptor();
                 System.out.println("using descriptor "+methodSignature);
                 break;
              }
          }
          if (methodSignature==null){
          for (ClassModel.ClassModelMethod m:classModel.getMethods()){
              System.out.println(m.getName()+","+m.getDescriptor());
          }
          System.exit(1);
          }
       }

       ClassModel.ClassModelMethod method = classModel.getMethod(methodName, methodSignature);


       HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);

       HSAILMethod.getHSAILMethod(method).render(renderer);
        List<String> input = new ArrayList<String>();
       for (String s:renderer.toString().split("\n")){
      
        //String fileName = "C:\\Users\\user1\\aparapi\\branches\\lambda\\sindexof.hsail";
        //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

        //for (String line = br.readLine(); line != null; line = br.readLine()) {
            input.add(s);
        }
        //br.close();
        Label label = null;
        int lineNumber = 0;
        Stack<State> state = new Stack<State>();
        state.push(State.NONE);
        List<Instruction> instructions = new ArrayList<Instruction>();
        for (String line : input) {
            if (line.trim().equals("")) {
                // skip
            } else if (whiteSpaceMatcher.matches(line)) {
                // skip
            } else {
                switch (state.peek()) {
                    case MULTILINE_COMMENT:
                        if (multiLineEndMatcher.matches(line)) {
                            state.pop();
                        } else {
                            // skip
                        }
                        break;
                    case NONE:
                        if (versionMatcher.matches(line)) {
                        } else if (kernelMatcher.matches(line)) {
                            state.pop(); // replace PREAMBLE with ARGS
                            state.push(State.KERNEL_ARGS);
                        } else if (multiLineStartMatcher.matches(line)) {
                            state.push(State.MULTILINE_COMMENT);
                        } else {
                            throw new IllegalStateException("what is this doing here!");
                        }
                        break;
                    case KERNEL_ARGS:
                        if (kernelArgMatcher.matches(line)) {
                        } else if (bodyStartMatcher.matches(line)) {
                            state.pop(); // replace ARGS with BODY!
                            state.push(State.BODY);
                        } else if (multiLineStartMatcher.matches(line)) {
                            state.push(State.MULTILINE_COMMENT);
                        } else {
                            throw new IllegalStateException("what is this doing here!");
                        }
                        break;
                    case BODY:
                        if (bodyEndMatcher.matches(line)) {
                            state.pop();
                            state.push(State.NONE);
                        } else if (multiLineStartMatcher.matches(line)) {
                            state.push(State.MULTILINE_COMMENT);
                        } else if (labelMatcher.matches(line)) {
                            label = new Label(labelMatcher.getGroup(1));
                            state.push(State.LABEL);
                        } else {
                            instructions.add(Instruction.create( lineNumber, line));
                        }
                        break;
                    case LABEL:
                        if (multiLineStartMatcher.matches(line)) {
                            state.push(State.MULTILINE_COMMENT);
                        } else {
                            instructions.add(Instruction.create(lineNumber, line, label));
                            state.pop();
                        }
                        break;

                }
            }
            lineNumber++;
        }
        for (Instruction i : instructions) {
            System.out.println(i);
        }


    }

}
