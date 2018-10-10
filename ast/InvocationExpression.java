package ast;

import cfg.Block;
import llvm.CallInstruction;
import llvm.ImmediateValue;
import llvm.RegisterValue;
import llvm.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InvocationExpression
   extends AbstractExpression
{
   private final String name;
   private final List<Expression> arguments;

   public InvocationExpression(int lineNum, String name,
      List<Expression> arguments)
   {
      super(lineNum);
      this.name = name;
      this.arguments = arguments;
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      if (!global.containsKey(name)) {
         typeError();
      }

      FunctionType fields = (FunctionType) global.get(name);
      List<Declaration> params = fields.getParams();

      if (params.size() != arguments.size()) {
         typeError();
      }

      for (int i = 0; i < params.size() - 1; i++) {
         if (params.get(i).getType().getClass() != arguments.get(i).typeCheck(global, local, structs).getClass()) {
            if (params.get(i).getType() instanceof StructType && arguments.get(i).typeCheck(global, local, structs) instanceof VoidType) {
               continue;
            }
            typeError();
         }
      }

      return fields.getRetType();
   }

   private void typeError() {
      System.out.println(super.getLineNum() + ": Type error in invocation expression");
      System.exit(1);
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      Type retType = ((FunctionType) global.get(name)).getRetType();
      List<Declaration> p = ((FunctionType) global.get(name)).getParams();
      List<String> argValues = new ArrayList<>();
      String args = "";
      Value v;
      int i;

      if (arguments.size() > 0) {
         for (i = 0; i < arguments.size() - 1; i++) {
            v = arguments.get(i).toLLVM(block, global, local, structs, params);

            if (v == null) {
               v = new ImmediateValue("null", p.get(i).getType().toLLVM());
            }

            argValues.add(v.getLabel());
            args = args.concat(v.getType() + " " + v.getLabel() + ", ");
         }
         v = arguments.get(arguments.size() - 1).toLLVM(block, global, local, structs, params);

         if (v == null) {
            v = new ImmediateValue("null", p.get(arguments.size() - 1).getType().toLLVM());
         }

         argValues.add(v.getLabel());
         args = args.concat(v.getType() + " " + v.getLabel());
      }

      if (retType instanceof VoidType) {
         block.addInstruction(new CallInstruction("void", retType.toLLVM(), "@" + name, args, argValues));
         return null;
      }
      else {
         RegisterValue result = new RegisterValue(retType.toLLVM());
         block.addInstruction(new CallInstruction(result.getLabel(), retType.toLLVM(), "@" + name, args, argValues));
         return result;
      }
   }
}
