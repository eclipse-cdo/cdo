/*
 * Copyright (c) 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.actions;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.explorer.ui.handlers.RepositoryCheckoutHandler;
import org.eclipse.emf.cdo.explorer.ui.handlers.RepositoryCheckoutHandlerQuick;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Eike Stepper
 */
public class CheckoutCommitInfoActionDelegate extends LongRunningActionDelegate
{
  public CheckoutCommitInfoActionDelegate()
  {
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    ISelection selection = getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (ssel.size() == 1)
      {
        Object element = ssel.getFirstElement();
        if (element instanceof CDOCommitInfo)
        {
          CDOCommitInfo commitInfo = (CDOCommitInfo)element;

          CDOSession session = CDOUtil.getSession(commitInfo);
          if (session != null)
          {
            final CDORepository repository = (CDORepository)session.properties().get(CDORepositoryImpl.REPOSITORY_KEY);
            if (repository != null)
            {
              CDORepositoryElement repositoryElement = new CDORepositoryElement()
              {
                @Override
                public CDORepository getRepository()
                {
                  return repository;
                }

                @Override
                public int getBranchID()
                {
                  return commitInfo.getBranch().getID();
                }

                @Override
                public long getTimeStamp()
                {
                  return commitInfo.getTimeStamp();
                }

                @Override
                public CDOID getObjectID()
                {
                  return session.getRepositoryInfo().getRootResourceID();
                }
              };

              checkout(repositoryElement);
            }
          }
        }
      }
    }
  }

  protected void checkout(CDORepositoryElement repositoryElement)
  {
    RepositoryCheckoutHandler.checkout(getShell(), repositoryElement);
  }

  /**
   * @author Eike Stepper
   */
  public static class Quick extends CheckoutCommitInfoActionDelegate
  {
    @Override
    protected void checkout(CDORepositoryElement repositoryElement)
    {
      RepositoryCheckoutHandlerQuick.checkout(repositoryElement, CDOCheckout.TYPE_ONLINE_HISTORICAL);
    }
  }
}
