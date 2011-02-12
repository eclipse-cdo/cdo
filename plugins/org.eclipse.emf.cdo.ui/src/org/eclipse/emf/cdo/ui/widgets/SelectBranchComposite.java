/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.collection.IHistory;
import org.eclipse.net4j.util.collection.PreferenceHistory;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.HistoryText;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class SelectBranchComposite extends Composite
{
  private IHistory<String> branchHistory = new PreferenceHistory(OM.PREF_HISTORY_BRANCHES);

  private HistoryText branchText;

  private TreeViewer branchViewer;

  private CDOBranch branch;

  private CDOBranchManager branchManager;

  public SelectBranchComposite(Composite parent, int style, CDOBranchManager branchManager)
  {
    super(parent, style);
    this.branchManager = branchManager;

    // setLayoutData(UIUtil.createGridData());
    setLayout(new GridLayout(1, false));

    branchText = new HistoryText(this, SWT.BORDER | SWT.SINGLE, branchHistory);
    branchText.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    branchText.getCombo().addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        setBranchFromPath();
      }
    });

    CDOItemProvider itemProvider = new CDOItemProvider(null);
    branchViewer = new TreeViewer(this, SWT.BORDER | SWT.SINGLE);
    branchViewer.getTree().setLayoutData(UIUtil.createGridData());
    branchViewer.setLabelProvider(itemProvider);
    branchViewer.setContentProvider(itemProvider);
    branchViewer.setInput(branchManager);

    branchText.setFocus();
    setBranchFromPath();
  }

  public IHistory<String> getBranchHistory()
  {
    return branchHistory;
  }

  public HistoryText getBranchText()
  {
    return branchText;
  }

  public TreeViewer getBranchViewer()
  {
    return branchViewer;
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  public boolean isBranchValid()
  {
    return branch != null;
  }

  public void rememberSettings()
  {
    branchText.getHistory().add(branch.getPathName());
  }

  @Override
  public void addListener(int eventType, Listener listener)
  {
    super.addListener(eventType, listener);
    branchText.addListener(eventType, listener);
    branchViewer.getTree().addListener(eventType, listener);
  }

  @Override
  public void removeListener(int eventType, Listener listener)
  {
    super.removeListener(eventType, listener);
    branchText.removeListener(eventType, listener);
    branchViewer.getTree().removeListener(eventType, listener);
  }

  private void setBranchFromPath()
  {
    String branchPath = branchText.getText();
    branch = SelectBranchComposite.this.branchManager.getBranch(branchPath);
  }
}
