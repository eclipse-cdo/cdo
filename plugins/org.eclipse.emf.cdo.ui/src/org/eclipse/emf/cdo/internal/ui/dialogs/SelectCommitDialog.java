/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.ui.widgets.CommitHistoryComposite;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class SelectCommitDialog extends TitleAreaDialog
{
  private static final String TITLE = "Select Commit";

  private final CDOSession session;

  private CommitHistoryComposite commitHistoryComposite;

  private CDOCommitInfo commitInfo;

  public SelectCommitDialog(IWorkbenchPage page, CDOSession session)
  {
    super(page.getWorkbenchWindow().getShell());
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);

    this.session = session;
  }

  public CDOSession getSession()
  {
    return session;
  }

  public final CDOCommitInfo getCommitInfo()
  {
    return commitInfo;
  }

  public final void setCommitInfo(CDOCommitInfo commitInfo)
  {
    this.commitInfo = commitInfo;
    validate();
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(750, 600);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(TITLE);
    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_COMMIT));
    setMessage("Select a commit from the history.");

    Composite area = (Composite)super.createDialogArea(parent);

    GridLayout containerGridLayout = new GridLayout(1, false);
    containerGridLayout.marginWidth = 10;
    containerGridLayout.marginHeight = 10;

    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    container.setLayout(containerGridLayout);

    commitHistoryComposite = new CommitHistoryComposite(container, SWT.BORDER)
    {
      @Override
      protected void commitInfoChanged(CDOCommitInfo commitInfo)
      {
        setCommitInfo(commitInfo);
      }

      @Override
      protected void doubleClicked(CDOCommitInfo commitInfo)
      {
        close();
      }
    };

    commitHistoryComposite.setLayoutData(UIUtil.createGridData());
    commitHistoryComposite.setInput(new CommitHistoryComposite.Input(session, null, null));

    final Display display = parent.getDisplay();
    display.asyncExec(new Runnable()
    {
      private long end = System.currentTimeMillis() + 5000L;

      @Override
      public void run()
      {
        if (!commitHistoryComposite.isDisposed())
        {
          Table table = commitHistoryComposite.getTableViewer().getTable();
          if (table.getItemCount() != 0)
          {
            Object data = table.getItem(0).getData();
            if (data instanceof CDOCommitInfo)
            {
              setCommitInfo((CDOCommitInfo)data);
              table.select(0);
              return;
            }
          }

          if (System.currentTimeMillis() < end)
          {
            display.asyncExec(this);
          }
        }
      }
    });

    return area;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    super.createButtonsForButtonBar(parent);
    validate();
  }

  protected void validate()
  {
    Button button = getButton(IDialogConstants.OK_ID);
    if (button != null)
    {
      button.setEnabled(commitInfo != null);
    }
  }
}
