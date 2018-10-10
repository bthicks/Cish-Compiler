package llvm;

public class RegisterValue
   implements Value
{
   private static int count = 0;
   private static int tcount = 0;
   private String label;
   private String type;
   private int value;

   public RegisterValue(String type)
   {
      this.label = "%u" + String.valueOf(count++);
      this.type = type;
   }

   public RegisterValue(int value)
   {
      // for temporary registers
      this.label = "%t" + String.valueOf(tcount++);
      this.value = value;
   }

   public String getLabel() {
      return label;
   }

   public String getType()
   {
      return type;
   }

   public int getValue() {
      return value;
   }

   public void setLabel(String label)
   {
      this.label = label;
   }
}
