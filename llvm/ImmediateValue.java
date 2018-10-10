package llvm;

public class ImmediateValue
   implements Value
{
   private String label;
   private String type;

   public ImmediateValue(String label, String type)
   {
      this.label = label;
      this.type = type;
   }

   public String getLabel() {
      return label;
   }

   public String getType()
   {
      return type;
   }

   public void setLabel(String label)
   {
      this.label = label;
   }
}
