/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOExtentCreator implements OCLExtentCreator
{
  private CDOView view;

  private CDORevisionCacheAdder revisionCacheAdder;

  public CDOExtentCreator(CDOView view, CDORevisionCacheAdder revisionCacheAdder)
  {
    this.view = view;
    this.revisionCacheAdder = revisionCacheAdder;
  }

  public CDOExtentCreator(CDOView view)
  {
    this(view, null);
  }

  public Set<EObject> createExtent(EClass eClass)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();

    CDOBranch branch = view.getBranch();
    long timeStamp = view.getTimeStamp();

    final Set<EObject> extent = new HashSet<EObject>();
    accessor.handleRevisions(eClass, branch, timeStamp, new CDORevisionHandler()
    {
      public void handleRevision(CDORevision revision)
      {
        if (revisionCacheAdder != null)
        {
          revisionCacheAdder.addRevision(revision);
        }

        extent.add(view.getObject(revision.getID()));
      }
    });

    return extent;
  }
}
