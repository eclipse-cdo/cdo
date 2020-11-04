/*
 * Copyright (c) 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 */
public class OnlineCDOCheckout extends CDOCheckoutImpl
{
  public OnlineCDOCheckout()
  {
  }

  public OnlineCDOCheckout(boolean readOnly)
  {
    super(readOnly);
  }

  @Override
  public boolean isOffline()
  {
    return false;
  }

  @Override
  public boolean isOnline()
  {
    return true;
  }

  @Override
  public final boolean isDirty()
  {
    return false;
  }

  @Override
  public CDOState getState(Object object)
  {
    // Online checkout objects are always clean. Don't show that.
    return null;
  }

  @Override
  public String getBranchPath()
  {
    CDOView view = getView();
    if (view != null)
    {
      CDOBranch branch = view.getBranch();
      if (branch != null)
      {
        return branch.getPathName();
      }
    }

    return super.getBranchPath();
  }

  @Override
  protected CDOView openView(CDOSession session)
  {
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch branch = branchManager.getBranch(getBranchID());
    setBranchPath(branch.getPathName());

    CDOView view = openView(session, branch);
    view.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOViewTargetChangedEvent)
        {
          CDOCheckoutManagerImpl manager = getManager();
          if (manager != null)
          {
            manager.fireElementChangedEvent(ElementsChangedEvent.StructuralImpact.ELEMENT, OnlineCDOCheckout.this);
          }
        }
      }
    });

    return view;
  }

  private CDOView openView(CDOSession session, CDOBranch branch)
  {
    long timeStamp = getTimeStamp();
    return session.openView(branch, timeStamp);
  }

  @Override
  protected void prepareOpen()
  {
    CDORepository repository = getRepository();
    repository.connect();

    super.prepareOpen();
  }
}
