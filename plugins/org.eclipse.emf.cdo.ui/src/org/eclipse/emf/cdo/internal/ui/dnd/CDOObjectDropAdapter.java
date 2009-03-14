/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.dnd;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.dnd.DNDDropAdapter;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import java.util.ArrayList;

/**
 * @author Victor Roldan Betancort
 */
public class CDOObjectDropAdapter extends DNDDropAdapter<TreeSelection>
{
  private static final Transfer[] TRANSFERS = new Transfer[] { org.eclipse.emf.edit.ui.dnd.LocalTransfer.getInstance() };

  protected CDOObjectDropAdapter(StructuredViewer viewer)
  {
    super(org.eclipse.emf.edit.ui.dnd.LocalTransfer.getInstance(), viewer);
    setExpandEnabled(false);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected boolean performDrop(TreeSelection data, Object target)
  {
    if (target instanceof IContainer.Modifiable<?>)
    {
      IContainer.Modifiable<CDOObject> objectContainer = (IContainer.Modifiable<CDOObject>)target;
      ArrayList<CDOObject> elementsToAdd = new ArrayList<CDOObject>();
      for (Object obj : data.toArray())
      {
        if (FSMUtil.isWatchable(obj))
        {
          elementsToAdd.add((CDOObject)obj);
        }
      }

      objectContainer.addAllElements(elementsToAdd);
      return true;
    }

    return false;
  }

  @Override
  protected boolean validateTarget(Object target, int operation)
  {
    return false;
  }

  @Override
  public boolean validateDrop(Object target, int operation, TransferData type)
  {
    return getTransfer().isSupportedType(type);
  }

  public static void support(StructuredViewer viewer)
  {
    int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
    viewer.addDropSupport(dndOperations, TRANSFERS, new CDOObjectDropAdapter(viewer));
  }
}
