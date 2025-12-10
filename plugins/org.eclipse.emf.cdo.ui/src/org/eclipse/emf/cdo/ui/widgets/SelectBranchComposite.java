/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.PreferenceHistory;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.ValidationContext;
import org.eclipse.net4j.util.ui.ValidationParticipant;
import org.eclipse.net4j.util.ui.widgets.HistoryText;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * UI widget that provides visualization of all available {@link org.eclipse.emf.cdo.common.branch.CDOBranch branches},
 * and with the capability to select one.
 *
 * @author Eike Stepper
 * @since 4.0
 * @deprecated As of 4.4 no longer supported.
 */
@Deprecated
public class SelectBranchComposite extends Composite implements ValidationParticipant
{
  private ValidationContext validationContext;

  private CDOSession session;

  private CDOBranch branch;

  private HistoryText branchText;

  private TreeViewer branchViewer;

  @Deprecated
  public SelectBranchComposite(Composite parent, int style, CDOSession session, CDOBranch branch)
  {
    this(parent, style, session, branch, false);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  public SelectBranchComposite(Composite parent, int style, CDOSession session, CDOBranch branch, boolean withHistory)
  {
    super(parent, style);
    this.session = session;
    this.branch = branch;

    GridLayout gridLayout = UIUtil.createGridLayout(1);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 5;

    setLayout(gridLayout);

    if (withHistory)
    {
      String prefName = "PREF_HISTORY_BRANCHES-" + session.getRepositoryInfo().getUUID(); //$NON-NLS-1$
      OMPreference<String[]> pref = OM.PREFS.getArray(prefName);
      if (pref == null)
      {
        pref = OM.PREFS.initArray(prefName);
      }

      IHistory<String> branchHistory = new PreferenceHistory(pref);

      branchText = new HistoryText(this, SWT.BORDER | SWT.SINGLE, branchHistory);
      branchText.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
      branchText.getCombo().addModifyListener(new ModifyListener()
      {
        @Override
        public void modifyText(ModifyEvent e)
        {
          setBranchFromPath();
        }
      });
    }

    CDOItemProvider itemProvider = new CDOItemProvider(null);
    branchViewer = new TreeViewer(this, SWT.BORDER | SWT.SINGLE);
    branchViewer.getTree().setLayoutData(UIUtil.createGridData());
    branchViewer.setLabelProvider(itemProvider);
    branchViewer.setContentProvider(itemProvider);
    branchViewer.setInput(session.getBranchManager());
    branchViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        setBranchFromViewer();
      }
    });

    branchViewer.setSelection(new StructuredSelection(branch));
    // setBranchFromPath();
  }

  @Deprecated
  @Override
  public boolean setFocus()
  {
    if (branchText != null)
    {
      return branchText.setFocus();
    }

    return branchViewer.getTree().setFocus();
  }

  @Deprecated
  @Override
  public ValidationContext getValidationContext()
  {
    return validationContext;
  }

  @Deprecated
  @Override
  public void setValidationContext(ValidationContext validationContext)
  {
    this.validationContext = validationContext;
  }

  @Deprecated
  public CDOSession getSession()
  {
    return session;
  }

  @Deprecated
  public CDOBranch getBranch()
  {
    return branch;
  }

  @Deprecated
  public HistoryText getBranchText()
  {
    return branchText;
  }

  @Deprecated
  public TreeViewer getBranchViewer()
  {
    return branchViewer;
  }

  @Deprecated
  public void rememberSettings()
  {
    if (branchText != null)
    {
      branchText.getHistory().add(branch.getPathName());
    }
  }

  @Deprecated
  @Override
  public void addListener(int eventType, Listener listener)
  {
    super.addListener(eventType, listener);
    branchViewer.getTree().addListener(eventType, listener);
    if (branchText != null)
    {
      branchText.addListener(eventType, listener);
    }
  }

  @Deprecated
  @Override
  public void removeListener(int eventType, Listener listener)
  {
    super.removeListener(eventType, listener);
    branchViewer.getTree().removeListener(eventType, listener);
    if (branchText != null)
    {
      branchText.removeListener(eventType, listener);
    }
  }

  @Deprecated
  protected void branchChanged(CDOBranch newBranch)
  {
  }

  private void setBranchFromPath()
  {
    if (branchText != null)
    {
      String branchPath = branchText.getText();
      CDOBranch newBranch = session.getBranchManager().getBranch(branchPath);
      if (newBranch != branch)
      {
        branch = newBranch;
        if (newBranch != null)
        {
          branchViewer.reveal(branch);
          branchViewer.setSelection(new StructuredSelection(branch));
        }
        else
        {
          branchViewer.setSelection(StructuredSelection.EMPTY);
        }

        branchChanged(newBranch);
      }

      validate();
    }
  }

  private void setBranchFromViewer()
  {
    IStructuredSelection selection = (IStructuredSelection)branchViewer.getSelection();
    CDOBranch newBranch = (CDOBranch)selection.getFirstElement();
    if (newBranch != branch)
    {
      branch = newBranch;
      if (branchText != null)
      {
        branchText.setText(branch.getPathName());
      }

      branchChanged(newBranch);
    }

    validate();
  }

  private void validate()
  {
    if (validationContext != null)
    {
      if (branchText != null)
      {
        validationContext.setValidationError(branchText.getCombo(), branch != null ? null : "Branch does not exist.");
      }
    }
  }
}
