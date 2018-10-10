package cfg;

import java.util.ArrayList;
import java.util.List;

public class Node {
   private final String name;
   private List<Node> sources;
   private String color;
   private boolean constrained;
   private boolean isReal;

   public Node(String name)
   {
      this.name = name;
      this.sources = new ArrayList<>();
      this.color = null;

      if (sources.size() >= 6) {
         this.constrained = true;
      }
      else {
         this.constrained = false;
      }

      if (name.startsWith("r")) {
         isReal = true;
      }
      else {
         isReal = false;
      }
   }

   public String getName()
   {
      return name;
   }

   public List<Node> getSources()
   {
      return sources;
   }

   public void addSource(Node n)
   {
      if (!sources.contains(n)){
         sources.add(n);
      }
   }

   public void removeSource(Node n)
   {
      sources.remove(n);
   }

   public String getColor()
   {
      return color;
   }

   public void setColor(String color)
   {
      this.color = color;
   }

   public boolean isConstrained() {
      return constrained;
   }

   public boolean isReal() {
      return isReal;
   }
}
