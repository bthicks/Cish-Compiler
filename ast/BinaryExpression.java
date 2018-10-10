package ast;

import cfg.Block;
import llvm.*;

import java.util.LinkedHashMap;
import java.util.List;

public class BinaryExpression
   extends AbstractExpression
{
   private final Operator operator;
   private final Expression left;
   private final Expression right;

   private BinaryExpression(int lineNum, Operator operator,
      Expression left, Expression right)
   {
      super(lineNum);
      this.operator = operator;
      this.left = left;
      this.right = right;
   }

   public static BinaryExpression create(int lineNum, String opStr,
      Expression left, Expression right)
   {
      switch (opStr)
      {
         case TIMES_OPERATOR:
            return new BinaryExpression(lineNum, Operator.TIMES, left, right);
         case DIVIDE_OPERATOR:
            return new BinaryExpression(lineNum, Operator.DIVIDE, left, right);
         case PLUS_OPERATOR:
            return new BinaryExpression(lineNum, Operator.PLUS, left, right);
         case MINUS_OPERATOR:
            return new BinaryExpression(lineNum, Operator.MINUS, left, right);
         case LT_OPERATOR:
            return new BinaryExpression(lineNum, Operator.LT, left, right);
         case LE_OPERATOR:
            return new BinaryExpression(lineNum, Operator.LE, left, right);
         case GT_OPERATOR:
            return new BinaryExpression(lineNum, Operator.GT, left, right);
         case GE_OPERATOR:
            return new BinaryExpression(lineNum, Operator.GE, left, right);
         case EQ_OPERATOR:
            return new BinaryExpression(lineNum, Operator.EQ, left, right);
         case NE_OPERATOR:
            return new BinaryExpression(lineNum, Operator.NE, left, right);
         case AND_OPERATOR:
            return new BinaryExpression(lineNum, Operator.AND, left, right);
         case OR_OPERATOR:
            return new BinaryExpression(lineNum, Operator.OR, left, right);
         default:
            throw new IllegalArgumentException();
      }
   }

   private static final String TIMES_OPERATOR = "*";
   private static final String DIVIDE_OPERATOR = "/";
   private static final String PLUS_OPERATOR = "+";
   private static final String MINUS_OPERATOR = "-";
   private static final String LT_OPERATOR = "<";
   private static final String LE_OPERATOR = "<=";
   private static final String GT_OPERATOR = ">";
   private static final String GE_OPERATOR = ">=";
   private static final String EQ_OPERATOR = "==";
   private static final String NE_OPERATOR = "!=";
   private static final String AND_OPERATOR = "&&";
   private static final String OR_OPERATOR = "||";

   public static enum Operator
   {
      TIMES, DIVIDE, PLUS, MINUS, LT, GT, LE, GE, EQ, NE, AND, OR
   }

   public Type typeCheck(LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
      LinkedHashMap<String, LinkedHashMap<String, Type>> structs)
   {
      switch (operator)
      {
         case TIMES:
         case DIVIDE:
         case PLUS:
         case MINUS:
            if (left.typeCheck(global, local, structs) instanceof IntType &&
                    right.typeCheck(global, local, structs) instanceof IntType) {
               return new IntType();
            }
            else {
               typeError();
            }
         case EQ:
         case NE:
            if (left.typeCheck(global, local, structs) instanceof IntType &&
                    right.typeCheck(global, local, structs) instanceof IntType) {
               return new BoolType();
            }
            else if (left.typeCheck(global, local, structs) instanceof StructType) {
               Type rt = right.typeCheck(global, local, structs);

               if (rt instanceof StructType || rt instanceof VoidType) {
                  return new BoolType();
               }
               else {
                  typeError();
               }
            }
            else {
               typeError();
            }
         case LT:
         case GT:
         case LE:
         case GE:
            if (left.typeCheck(global, local, structs) instanceof IntType &&
                    right.typeCheck(global, local, structs) instanceof IntType) {
               return new BoolType();
            }
            else {
               typeError();
            }
         case AND:
         case OR:
            if (left.typeCheck(global, local, structs) instanceof BoolType &&
                    right.typeCheck(global, local, structs) instanceof BoolType) {
               return new BoolType();
            }
            else {
               typeError();
            }
         default:
            return new VoidType();
      }
   }

   private void typeError() {
      System.out.println(super.getLineNum() + ": Type error in binary expression");
      System.exit(1);
   }

   public Value toLLVM(Block block, LinkedHashMap<String, Type> global, LinkedHashMap<String, Type> local,
                       LinkedHashMap<String, LinkedHashMap<String, Type>> structs, List<Declaration> params)
   {
      Value v1 = left.toLLVM(block, global, local, structs, params);
      Value v2 = right.toLLVM(block, global, local, structs, params);

      if (v2 == null) {
         v2 = new ImmediateValue("null", v1.getType());
      }

      String ty = v1.getType();
      String op1 = v1.getLabel();
      String op2 = v2.getLabel();
      RegisterValue v3 = new RegisterValue(ty);
      String result = v3.getLabel();
      RegisterValue v4;
      String result2;

      switch (operator)
      {
         case TIMES:
            block.addInstruction(new MultiplyInstruction(result, ty, op1, op2));
            return v3;
         case DIVIDE:
            block.addInstruction(new DivideInstruction(result, ty, op1, op2));
            return v3;
         case PLUS:
            block.addInstruction(new AddInstruction(result, ty, op1, op2));
            return v3;
         case MINUS:
            block.addInstruction(new SubtractInstruction(result, ty, op1, op2));
            return v3;
         case AND:
            block.addInstruction(new AndInstruction(result, ty, op1, op2));
            return v3;
         case OR:
            block.addInstruction(new OrInstruction(result, ty, op1, op2));
            return v3;
         case EQ:
            v4 = new RegisterValue(ty);
            result2 = v4.getLabel();
            block.addInstruction(new ComparisonInstruction(result, "eq", ty, op1, op2));
            block.addInstruction(new ZeroExtendInstruction(result2, "i1", result, ty));
            return v4;
         case NE:
            v4 = new RegisterValue(ty);
            result2 = v4.getLabel();
            block.addInstruction(new ComparisonInstruction(result, "ne", ty, op1, op2));
            block.addInstruction(new ZeroExtendInstruction(result2, "i1", result, ty));
            return v4;
         case LT:
            v4 = new RegisterValue(ty);
            result2 = v4.getLabel();
            block.addInstruction(new ComparisonInstruction(result, "slt", ty, op1, op2));
            block.addInstruction(new ZeroExtendInstruction(result2, "i1", result, ty));
            return v4;
         case GT:
            v4 = new RegisterValue(ty);
            result2 = v4.getLabel();
            block.addInstruction(new ComparisonInstruction(result, "sgt", ty, op1, op2));
            block.addInstruction(new ZeroExtendInstruction(result2, "i1", result, ty));
            return v4;
         case LE:
            v4 = new RegisterValue(ty);
            result2 = v4.getLabel();
            block.addInstruction(new ComparisonInstruction(result, "sle", ty, op1, op2));
            block.addInstruction(new ZeroExtendInstruction(result2, "i1", result, ty));
            return v4;
         case GE:
            v4 = new RegisterValue(ty);
            result2 = v4.getLabel();
            block.addInstruction(new ComparisonInstruction(result, "sge", ty, op1, op2));
            block.addInstruction(new ZeroExtendInstruction(result2, "i1", result, ty));
            return v4;
         default:
            return null;
      }
   }
}
