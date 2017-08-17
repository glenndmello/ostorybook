package storybook.ui.panel.tree;
import java.awt.datatransfer.*;
import javax.swing.tree.*;
import java.util.*;

public class TransferableNode implements Transferable {
     public static final DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node");
     private TreeNode node;
     private DataFlavor[] flavors = { NODE_FLAVOR };

     public TransferableNode(TreeNode draggedNode) {
          node = draggedNode;
     }  

     public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
          if (flavor == NODE_FLAVOR) {
               return node;
          }
          else {
               throw new UnsupportedFlavorException(flavor);     
          }               
     }

     public DataFlavor[] getTransferDataFlavors() {
          return flavors;
     }

     public boolean isDataFlavorSupported(DataFlavor flavor) {
          return Arrays.asList(flavors).contains(flavor);
     }
}