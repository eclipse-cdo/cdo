/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Eike Stepper
 */
public class OnlineCDOCheckout extends CDOCheckoutImpl
{
  public OnlineCDOCheckout()
  {
  }

  @Override
  protected CDOView openView(CDOSession session)
  {
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch branch = branchManager.getBranch(getBranchPath());

    if (isReadOnly())
    {
      return session.openView(branch, getTimeStamp());
    }

    return session.openTransaction(branch);
  }
}
