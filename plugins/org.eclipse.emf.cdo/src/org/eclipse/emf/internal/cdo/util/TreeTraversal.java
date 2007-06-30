package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
@Deprecated
public class TreeTraversal
{
  private CDOObjectImpl root;

  private List<CDOObjectImpl> objects = new ArrayList();

  public TreeTraversal(CDOObjectImpl root)
  {
    this.root = root;
    collectContents();
  }

  public CDOObjectImpl getRoot()
  {
    return root;
  }

  public List<CDOObjectImpl> getObjects()
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
      if (content instanceof CDOObjectImpl)
      {
        objects.add(((CDOObjectImpl)content));
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface ObjectHandler
  {
    public void handleObject(final CDOObjectImpl object);
  }
}