/*
 * Copyright (c) 2015, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.actions;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.explorer.ui.handlers.RepositoryCheckoutHandler;
import org.eclipse.emf.cdo.explorer.ui.handlers.RepositoryCheckoutHandlerQuick;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
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
  public void selectionChanged(IAction action, ISelection selection)
  {
    super.selectionChanged(action, selection);
    action.setEnabled(isValid(getRepositoryElement(selection)));
  }

  /**
   * Captures the current checkout context before handing execution to an asynchronous job.
   */
  @Override
  public void run(IAction action)
  {
    CDORepositoryElement repositoryElement = getRepositoryElement(getSelection());
    if (isValid(repositoryElement))
    {
      new Invocation(this, repositoryElement).run(action);
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    throw new IllegalStateException("Checkout execution requires an invocation snapshot."); //$NON-NLS-1$
  }

  private static CDORepositoryElement getRepositoryElement(ISelection selection)
  {
    if (!(selection instanceof IStructuredSelection))
    {
      return null;
    }

    IStructuredSelection structuredSelection = (IStructuredSelection)selection;
    if (structuredSelection.size() != 1 || !(structuredSelection.getFirstElement() instanceof CDOCommitInfo))
    {
      return null;
    }

    CDOCommitInfo commitInfo = (CDOCommitInfo)structuredSelection.getFirstElement();
    if (commitInfo.getBranch() == null)
    {
      return null;
    }

    CDOSession session = CDOUtil.getSession(commitInfo);
    if (session == null || !session.getRepositoryInfo().isSupportingAudits())
    {
      return null;
    }

    CDORepository repository = (CDORepository)session.properties().get(CDORepositoryImpl.REPOSITORY_KEY);
    CDOID rootResourceID = session.getRepositoryInfo().getRootResourceID();
    if (repository == null || rootResourceID == null)
    {
      return null;
    }

    int branchID = commitInfo.getBranch().getID();
    long timeStamp = commitInfo.getTimeStamp();

    return new CDORepositoryElement()
    {
      @Override
      public CDORepository getRepository()
      {
        return repository;
      }

      @Override
      public int getBranchID()
      {
        return branchID;
      }

      @Override
      public long getTimeStamp()
      {
        return timeStamp;
      }

      @Override
      public CDOID getObjectID()
      {
        return rootResourceID;
      }
    };
  }

  protected void checkout(CDORepositoryElement repositoryElement)
  {
    RepositoryCheckoutHandler.checkout(getShell(), repositoryElement);
  }

  private static boolean isValid(CDORepositoryElement repositoryElement)
  {
    if (repositoryElement == null || repositoryElement.getRepository() == null || repositoryElement.getObjectID() == null)
    {
      return false;
    }

    VersioningMode versioningMode = repositoryElement.getRepository().getVersioningMode();
    return versioningMode == null || versioningMode.isSupportingAudits();
  }

  /**
   * @author Eike Stepper
   */
  public static class Quick extends CheckoutCommitInfoActionDelegate
  {
    public Quick()
    {
    }

    @Override
    protected void checkout(CDORepositoryElement repositoryElement)
    {
      RepositoryCheckoutHandlerQuick.checkout(repositoryElement, CDOCheckout.TYPE_ONLINE_HISTORICAL);
    }
  }

  /**
   * Runs one checkout with the immutable context captured for its invocation.
   *
   * @author Eike Stepper
   */
  private static class Invocation extends LongRunningActionDelegate
  {
    private final CheckoutCommitInfoActionDelegate delegate;

    private final CDORepositoryElement repositoryElement;

    public Invocation(CheckoutCommitInfoActionDelegate delegate, CDORepositoryElement repositoryElement)
    {
      this.delegate = delegate;
      this.repositoryElement = repositoryElement;
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      if (isValid(repositoryElement))
      {
        delegate.checkout(repositoryElement);
      }
    }
  }
}
