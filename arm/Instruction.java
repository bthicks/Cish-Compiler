package arm;

import java.util.List;

public interface Instruction {
   public String toARM();

   public List<String> getTargets();

   public List<String> getSources();

   public void allocateTarget(String real);

   public void allocateSource(String virtual, String real);
}
