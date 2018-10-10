package arm;

import java.util.ArrayList;
import java.util.List;

public class CmpInstruction
   extends AbstractInstruction
{
   private String r1;
   private String operand2;
   private List<String> sources;

   public CmpInstruction(String r1, String operand2)
   {
      this.r1 = r1;
      this.operand2 = operand2;
      this.sources = new ArrayList<>();

      sources.add(r1);
      if (operand2.startsWith("%") || operand2.startsWith("r")) {
         sources.add(operand2);
      }
   }

   public String toARM()
   {
      return "cmp\t" + r1 + ", " + operand2 + "\n";
   }

   public List<String> getSources() {
      return sources;
   }

   public void allocateSource(String virtual, String real)
   {
      if (virtual.equals(r1)) {
         r1 = real;
      }
      else if (virtual.equals(operand2)) {
         operand2 = real;
      }
   }
}
