package llvm;

public class AllocationInstruction
   extends AbstractInstruction
{
   private final String result;
   private final String name;
   private final String ty;

   public AllocationInstruction(String result, String ty)
   {
      this.result = result;
      this.name = "alloca";
      this.ty = ty;
   }

   public String toLLVM()
   {
      return "\t%" + result + " = alloca " + ty + "\n";
   }
}
