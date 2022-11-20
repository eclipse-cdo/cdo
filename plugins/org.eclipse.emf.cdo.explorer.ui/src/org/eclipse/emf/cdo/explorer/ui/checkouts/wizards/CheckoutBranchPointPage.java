/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.widgets.ComposeBranchPointComposite;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutBranchPointPage extends CheckoutWizardPage
{
  private static final IContainer<Object> CONNECTING_INPUT = ContainerItemProvider.createSlowInput("Connecting...");

  private int branchID = CDOBranch.MAIN_BRANCH_ID;

  private long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

  private ComposeBranchPointComposite branchPointComposite;

  private String timeStampError;

  public CheckoutBranchPointPage()
  {
    super("Branch Point", "Select the branch point of the new checkout.");
  }

  public final int getBranchID()
  {
    return branchID;
  }

  public final long getTimeStamp()
  {
    return timeStamp;
  }

  public final CDOBranchPoint getBranchPoint()
  {
    CheckoutRepositoryPage repositoryPage = getWizard().getRepositoryPage();
    CDOSession session = repositoryPage.getSession();

    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch branch = branchManager.getBranch(branchID);

    return branch.getPoint(timeStamp);
  }

  public void setBranchPoint(int branchID, long timeStamp)
  {
    if (this.branchID != branchID || this.timeStamp != timeStamp)
    {
      log("Setting branch point to " + branchID + "/" + CDOCommonUtil.formatTimeStamp(timeStamp));
      this.branchID = branchID;
      this.timeStamp = timeStamp;

      if (branchPointComposite != null)
      {
        CDOBranchPoint branchPoint = getBranchPoint();
        CDOBranch branch = branchPoint.getBranch();

        TreeViewer branchViewer = branchPointComposite.getBranchViewer();
        branchViewer.setSelection(new StructuredSelection(branch));
        branchViewer.setExpandedState(branch, true);
      }

      branchPointChanged(branchID, timeStamp);
    }
  }

  @Override
  protected void createUI(Composite parent)
  {
    branchPointComposite = new ComposeBranchPointComposite(parent, true, null)
    {
      @Override
      protected void timeStampError(String message)
      {
        timeStampError = message;
        validate();
      }

      @Override
      protected void branchPointChanged(CDOBranchPoint branchPoint)
      {
        int id = branchPoint.getBranch().getID();
        long timeStamp = branchPoint.getTimeStamp();
        CheckoutBranchPointPage.this.setBranchPoint(id, timeStamp);
        validate();
      }

      @Override
      protected void doubleClicked()
      {
        showNextPage();
      }
    };

    branchPointComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    branchPointComposite.getBranchViewer().setInput(CONNECTING_INPUT);

  }

  @Override
  protected void repositoryChanged(CDORepository repository)
  {
    if (branchPointComposite != null)
    {
      TreeViewer branchViewer = branchPointComposite.getBranchViewer();
      branchViewer.setSelection(StructuredSelection.EMPTY);
      branchViewer.setInput(CONNECTING_INPUT);
    }

    super.repositoryChanged(repository);
  }

  @Override
  protected void pageActivated()
  {
    String type = getWizard().getTypePage().getType();

    if (CDOCheckout.TYPE_ONLINE_TRANSACTIONAL.equals(type))
    {
      branchPointComposite.setAllowTimeStamp(false);
    }
    else
    {
      branchPointComposite.setAllowTimeStamp(true);
      branchPointComposite.getSelectTimeComposite().setTimeStamp(timeStamp);
    }

    Display display = branchPointComposite.getDisplay();
    display.asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        CDOSession session = getSession();
        if (session == null)
        {
          int answer = MessageDialog.open(MessageDialog.ERROR, getShell(), "Connection Problem", "The repository could not be connected.", SWT.NONE, "&Retry",
              "Go &Back");
          if (answer == 1)
          {
          }
        }

        CDOBranchPoint branchPoint;
        if (branchPointComposite.getBranchPoint() == null)
        {
          branchPoint = getBranchPoint();
          branchPointComposite.setBranchPoint(branchPoint);
        }
        else
        {
          branchPoint = CheckoutBranchPointPage.this.getBranchPoint();
        }

        TreeViewer branchViewer = branchPointComposite.getBranchViewer();
        branchViewer.setInput(session.getBranchManager());

        CDOBranch branch = branchPoint.getBranch();

        display.asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            branchViewer.setSelection(new StructuredSelection(branch));
            branchViewer.expandToLevel(branch, 1);
            validate();
          }
        });
      }

      private CDOSession getSession()
      {
        try
        {
          return getWizard().getRepositoryPage().getSession();
        }
        catch (Exception ex)
        {
          return null;
        }
      }
    });
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    TreeViewer branchViewer = branchPointComposite.getBranchViewer();
    if (branchViewer.getInput() == null)
    {
      return false;
    }

    ISelection selection = branchViewer.getSelection();
    if (selection == null || selection.isEmpty())
    {
      return false;
    }

    if (timeStampError != null)
    {
      throw new ValidationProblem(timeStampError);
    }

    return true;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty(CDOCheckoutImpl.PROP_BRANCH_ID, Integer.toString(branchID));
    properties.setProperty(CDOCheckoutImpl.PROP_TIME_STAMP, Long.toString(timeStamp));
  }
}
