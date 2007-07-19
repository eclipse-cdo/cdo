package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
@Deprecated
public class TreeTraversal
{
  private InternalCDOObject root;

  private List<InternalCDOObject> objects = new ArrayList(0);

  public TreeTraversal(InternalCDOObject root)
  {
    this.root = root;
    collectContents();
  }

  public InternalCDOObject getRoot()
  {
    return root;
  }

  public List<InternalCDOObject> getObjects()
  {
    return objects;
  }

  public void traverseTopDown(final ObjectHandler handler)
  {
    for (int i = 0; i < objects.size(); i++)
    {
      handler.handleObject(objects.get(i));
    }
  }

  public void traverseBottomUp(final ObjectHandler handler)
  {
    for (int i = objects.size() - 1; i >= 0; i--)
    {
      handler.handleObject(objects.get(i));
    }
  }

  private void collectContents()
  {
    objects.add(root);
    for (TreeIterator<EObject> it = root.eAllContents(); it.hasNext();)
    {
      Object content = it.next();
      if (content instanceof InternalCDOObject)
      {
        objects.add(((InternalCDOObject)content));
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface ObjectHandler
  {
    public void handleObject(final InternalCDOObject object);
  }
}