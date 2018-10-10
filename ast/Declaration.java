package ast;

import java.util.LinkedHashMap;

public class Declaration
{
   private final int lineNum;
   private final Type type;
   private final String name;

   public Declaration(int lineNum, Type type, String name)
   {
      this.lineNum = lineNum;
      this.type = type;
      this.name = name;
   }

   public Type getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public LinkedHashMap<String, Type> typeCheck(LinkedHashMap<String, Type> table)
   {
      if (table.containsKey(name)) {
         typeError();
      }

      table.put(name, type);
      return table;
   }

   private void typeError()
   {
      System.out.println(lineNum + ": Type error in declaration");
      System.exit(1);
   }

   public String paramToLLVM()
   {
      return type.toLLVM() + " %_P_" + name;
   }

   public String localToLLVM() {
      return "%" + name + " = alloca " + type.toLLVM() + "\n";
   }

   public String globalToLLVM()
   {
      return "@" + name + " = common global " + type.toLLVM() + " " + toLLVMHelper() + ", align 8";
   }

   private String toLLVMHelper() {
      if (type instanceof StructType) {
         return "null";
      }
      else {
         return "0";
      }
   }

   public String globalToARM() {
      return "\t\t.comm \t" + name + ",4,4";
   }
}
