/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dnd;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.ui.dnd.DNDDropAdapter;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Eike Stepper
 */
public class CDOMergeDropAdapter extends DNDDropAdapter<TreeSelection>
{
  public static final Transfer[] TRANSFERS = new Transfer[] { org.eclipse.emf.edit.ui.dnd.LocalTransfer.getInstance() };

  protected CDOMergeDropAdapter(StructuredViewer viewer)
  {
    super(TRANSFERS, viewer);
    setExpandEnabled(false);
  }

  @Override
  protected boolean performDrop(TreeSelection data, Object target)
  {
    if (target instanceof CDOTransaction)
    {
      for (Object obj : data.toArray())
      {
        if (obj instanceof CDOBranchPoint)
        {
          CDOBranchPoint sourceBranchPoint = (CDOBranchPoint)obj;
          CDOTransaction targetTransaction = (CDOTransaction)target;

          CDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();
          targetTransaction.merge(sourceBranchPoint, merger);
          return true;
        }
      }
    }

    return false;
  }

  @Override
  protected boolean validateTarget(Object target, int operation)
  {
    return target instanceof CDOTransaction && !((CDOTransaction)target).isDirty();
  }

  public static void support(StructuredViewer viewer)
  {
    int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
    viewer.addDropSupport(dndOperations, TRANSFERS, new CDOMergeDropAdapter(viewer));
  }
}
