package ast;

import cfg.Block;
import llvm.RegisterValue;
import llvm.SubtractInstruction;
import llvm.Value;
import llvm.XorInstruction;

import java.util.LinkedHashMap;
import java.util.List;

public class UnaryExpression
   extends AbstractExpression
{
   private final Operator operator;
   private final Expression operand;

   private UnaryExpression(int lineNum, Operator operator, Expression operand)
   {
      super(lineNum);
      this.operator = operator;
      this.operand = operand;
   }

   public static UnaryExpression create(int lineNum, String opStr,
      Expression operand)
   {
      if (opStr.equals(NOT_OPERATOR))
      {
         return new UnaryExpression(lineNum, Operator.NOT, operand);
      }
      else if (opStr.equals(MINUS_OPERATOR))
      {
         return new UnaryExpression(lineNum, Operator.MINUS, operand);
      }
      else
      {
         throw new IllegalArgumentException();
      }
   }

   private static final String NOT_OPERATOR = "!";
   private static final String MINUS_OPERATOR = "-";

   public static enum Operator
   {
      NOT, MINUS
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      switch (operator)
      {
         case NOT:
            if (operand.typeCheck(global, local, structs) instanceof BoolType) {
               return new BoolType();
            }
            else {
               typeError();
            }
         case MINUS:
            if (operand.typeCheck(global, local, structs) instanceof IntType) {
               return new IntType();
            }
            else {
               typeError();
            }
         default:
            return new IntType();
      }
   }

   private void typeError() {
      System.out.println(super.getLineNum() + ": Type error in unary expression");
      System.exit(1);
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      Value v1 = operand.toLLVM(block, global, local, structs, params);
      RegisterValue v2 = new RegisterValue(v1.getType());

      switch (operator)
      {
         case NOT:
            block.addInstruction(new XorInstruction(v2.getLabel(), "i32", v1.getLabel(), "1"));
            return v2;
         case MINUS:
            block.addInstruction(new SubtractInstruction(v2.getLabel(), v1.getType(), "0", v1.getLabel()));
            return v2;
      }
      return null;
   }
}
