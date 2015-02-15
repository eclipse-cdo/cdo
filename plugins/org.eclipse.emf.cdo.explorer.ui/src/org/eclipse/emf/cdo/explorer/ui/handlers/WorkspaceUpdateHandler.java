/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 */
public class WorkspaceUpdateHandler extends AbstractBaseHandler<OfflineCDOCheckout>
{
  public WorkspaceUpdateHandler()
  {
    super(OfflineCDOCheckout.class, false);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    OfflineCDOCheckout checkout = elements.get(0);
    CDOWorkspace workspace = checkout.getWorkspace();
    if (workspace != null)
    {
      // TODO Use progress monitor in update().
      CDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();
      CDOTransaction transaction = workspace.update(merger);
      transaction.commit(monitor);
    }
  }
}
