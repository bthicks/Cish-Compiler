package llvm;

public class ReturnVoidInstruction
   extends AbstractInstruction
{
   public ReturnVoidInstruction()
   {

   }

   public String toLLVM()
   {
      return "\tret void\n";
   }
}
