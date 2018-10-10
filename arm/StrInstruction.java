package arm;

import java.util.ArrayList;
import java.util.List;

public class StrInstruction
   extends AbstractInstruction
{
   private String r1;
   private String address;

   public StrInstruction(String r1, String address)
   {
      this.r1 = r1;
      this.address = address;
   }

   public String toARM()
   {
      if (address.startsWith("#")) {
         return "str\t" + r1 + ", [fp, " + address + "]\n";
      }
      else {
         return "str\t" + r1 + ", [" + address + "]\n";
      }

   }

   public List<String> getSources() {
      List<String> sources = new ArrayList<>();

      if (r1.startsWith("%") || r1.startsWith("r")) {
         sources.add(r1);
      }
      if (address.startsWith("%") || address.startsWith("r")) {
         sources.add(address);
      }

      return sources;
   }

   public void allocateSource(String virtual, String real)
   {
      if (virtual.equals(r1)) {
         r1 = real;
      }
      else if (virtual.equals(address)) {
         address = real;
      }
   }

}
