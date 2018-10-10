package ast;

public class StructType
   implements Type
{
   private final int lineNum;
   private final String name;

   public StructType(int lineNum, String name)
   {
      this.lineNum = lineNum;
      this.name = name;
   }

   public Type typeCheck()
   {
      return new StructType(lineNum, name);
   }

   public String getName() {
      return name;
   }

   public String toLLVM()
   {
      return "%struct." + name + "*";
   }
}
