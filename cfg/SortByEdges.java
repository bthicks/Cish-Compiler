package cfg;

import java.util.Comparator;

public class SortByEdges implements Comparator<Node>
{
   // used for sorting Nodes in ascending order of edge number
   public int compare(Node a, Node b)
   {
      return a.getSources().size() - b.getSources().size();
   }
}
