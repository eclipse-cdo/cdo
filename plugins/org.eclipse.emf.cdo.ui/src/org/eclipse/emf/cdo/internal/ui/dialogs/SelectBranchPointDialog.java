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
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.ui.widgets.SelectBranchComposite;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class SelectBranchPointDialog extends TitleAreaDialog
{
  private CDOSession session;

  private CDOBranchPoint branchPoint;

  private SelectBranchComposite selectBranchComposite;

  private Button headRadio;

  private Button baseRadio;

  private Button timeRadio;

  private Text timeText;

  private Button browseTimeButton;

  public SelectBranchPointDialog(IWorkbenchPage page, CDOSession session, CDOBranchPoint branchPoint)
  {
    super(page.getWorkbenchWindow().getShell());
    this.session = session;
    this.branchPoint = branchPoint;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(1, false));

    createBranchPointArea(composite);

    return composite;
  }

  protected void createBranchPointArea(Composite parent)
  {
    selectBranchComposite = new SelectBranchComposite(parent, SWT.NONE, session.getBranchManager());
    selectBranchComposite.setLayoutData(UIUtil.createGridData());

    Group pointGroup = new Group(parent, SWT.NONE);
    pointGroup.setText(Messages.getString("BranchSelectionDialog.0")); //$NON-NLS-1$
    pointGroup.setLayout(new GridLayout(3, false));
    pointGroup.setLayoutData(UIUtil.createGridData());

    headRadio = new Button(pointGroup, SWT.RADIO);
    headRadio.setText(Messages.getString("BranchSelectionDialog.1")); //$NON-NLS-1$
    new Label(pointGroup, SWT.NONE);
    new Label(pointGroup, SWT.NONE);

    baseRadio = new Button(pointGroup, SWT.RADIO);
    baseRadio.setText(Messages.getString("BranchSelectionDialog.2")); //$NON-NLS-1$
    new Label(pointGroup, SWT.NONE);
    new Label(pointGroup, SWT.NONE);

    timeRadio = new Button(pointGroup, SWT.RADIO);
    timeRadio.setText(Messages.getString("BranchSelectionDialog.3")); //$NON-NLS-1$
    timeText = new Text(pointGroup, SWT.BORDER);
    timeText.setText(new Date(session.getRepositoryInfo().getCreationTime()).toString());
    timeText.setEditable(false);

    browseTimeButton = new Button(pointGroup, SWT.NONE);
    browseTimeButton.setImage(SharedIcons.getImage(SharedIcons.ETOOL_TIME_PICK_BUTTON_ICON));

    timeText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        if (timeRadio.getSelection())
        {
          // updateSelectedPoint();
        }
      }
    });

    // head as default selection
    headRadio.setSelection(true);

    browseTimeButton.addSelectionListener(new SelectionListener()
    {
      public void widgetSelected(SelectionEvent e)
      {
        OpenAuditDialog dialog = new OpenAuditDialog(UIUtil.getActiveWorkbenchPage());
        if (dialog.open() == IDialogConstants.OK_ID)
        {
          timeText.setText(new Date(dialog.getTimeStamp()).toString());
        }
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
    });

    selectBranchComposite.getBranchViewer().expandAll();
  }

  /**
   * @author Eike Stepper
   */
  public static class WithName extends SelectBranchPointDialog
  {
    private String name;

    private Text nameText;

    public WithName(IWorkbenchPage page, CDOSession session, CDOBranchPoint branchPoint, String name)
    {
      super(page, session, branchPoint);
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    @Override
    protected void createBranchPointArea(Composite parent)
    {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayoutData(UIUtil.createGridData(true, false));
      composite.setLayout(new GridLayout(2, false));

      Label label = new Label(composite, SWT.NONE);
      label.setLayoutData(UIUtil.createGridData(false, false));
      label.setText("Name:");

      nameText = new Text(composite, SWT.BORDER);
      nameText.setLayoutData(UIUtil.createGridData(true, false));

      super.createBranchPointArea(parent);
    }
  }
}
