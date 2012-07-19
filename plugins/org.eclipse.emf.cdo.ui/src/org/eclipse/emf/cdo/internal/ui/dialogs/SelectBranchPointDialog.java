/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.widgets.ComposeBranchPointComposite;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class SelectBranchPointDialog extends TitleAreaDialog implements ValidationContext
{
  protected final ValidationContext aggregator = new ValidationContext.Aggregator(this);

  private CDOSession session;

  private CDOBranchPoint branchPoint;

  private boolean allowTimeStamp;

  private CTabItem composeTab;

  private CTabItem commitsTab;

  private CTabItem tagsTab;

  private CTabItem viewsTab;

  public SelectBranchPointDialog(IWorkbenchPage page, CDOSession session, CDOBranchPoint branchPoint,
      boolean allowTimeStamp)
  {
    super(page.getWorkbenchWindow().getShell());
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);

    this.session = session;
    this.branchPoint = branchPoint;
    this.allowTimeStamp = allowTimeStamp;
  }

  public CDOSession getSession()
  {
    return session;
  }

  public boolean isAllowTimeStamp()
  {
    return allowTimeStamp;
  }

  public CTabItem getComposeTab()
  {
    return composeTab;
  }

  public CTabItem getCommitsTab()
  {
    return commitsTab;
  }

  public CTabItem getTagsTab()
  {
    return tagsTab;
  }

  public CTabItem getViewsTab()
  {
    return viewsTab;
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    this.branchPoint = branchPoint;
    validate();
  }

  public void setValidationError(Object source, String message)
  {
    setMessage(message, IMessageProvider.ERROR);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setSize(500, 500);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(1, false));

    createBranchPointArea(composite);
    UIUtil.setValidationContext(composite, aggregator);

    return composite;
  }

  protected void createBranchPointArea(Composite parent)
  {
    CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER);
    tabFolder.setSimple(false);
    tabFolder.setLayoutData(UIUtil.createGridData());
    tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

    composeTab = new CTabItem(tabFolder, SWT.NONE);
    composeTab.setText(getComposeTabTitle());
    Composite composite_0 = new Composite(tabFolder, SWT.NONE);
    composite_0.setLayout(new GridLayout(1, false));
    createComposeTab(composite_0);
    composeTab.setControl(composite_0);

    commitsTab = new CTabItem(tabFolder, SWT.NONE);
    commitsTab.setText("Commits");
    Composite composite_1 = new Composite(tabFolder, SWT.NONE);
    composite_1.setLayout(new GridLayout(1, false));
    createCommitsTab(composite_1);
    commitsTab.setControl(composite_1);

    tagsTab = new CTabItem(tabFolder, SWT.NONE);
    tagsTab.setText("Tags");
    Composite composite_2 = new Composite(tabFolder, SWT.NONE);
    composite_2.setLayout(new GridLayout(1, false));
    createTagsTab(composite_2);
    tagsTab.setControl(composite_2);

    viewsTab = new CTabItem(tabFolder, SWT.NONE);
    viewsTab.setText("Views");
    Composite composite_3 = new Composite(tabFolder, SWT.NONE);
    composite_3.setLayout(new GridLayout(1, false));
    createViewsTab(composite_3);
    viewsTab.setControl(composite_3);

    tabFolder.setSelection(composeTab);
  }

  protected void createComposeTab(Composite parent)
  {
    Control control = new ComposeBranchPointComposite(parent, SWT.NONE, session, null, allowTimeStamp)
    {
      @Override
      protected void branchPointChanged(CDOBranchPoint newBranchPoint)
      {
        setBranchPoint(newBranchPoint);
      }
    };

    control.setLayoutData(UIUtil.createGridData());
  }

  protected void createCommitsTab(Composite parent)
  {
  }

  protected void createTagsTab(Composite parent)
  {
  }

  protected void createViewsTab(Composite parent)
  {
  }

  protected String getComposeTabTitle()
  {
    return "Compose";
  }

  protected void validate()
  {
  }

  /**
   * @author Eike Stepper
   */
  public static class WithName extends SelectBranchPointDialog
  {
    private String name;

    private Text nameText;

    public WithName(IWorkbenchPage page, CDOSession session, CDOBranchPoint branchPoint, boolean allowTimeStamp,
        String name)
    {
      super(page, session, branchPoint, allowTimeStamp);
      this.name = StringUtil.safe(name);
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
      validate();
    }

    public Text getNameText()
    {
      return nameText;
    }

    @Override
    protected void createBranchPointArea(Composite parent)
    {
      GridLayout gridLayout = UIUtil.createGridLayout(2);
      gridLayout.marginHeight = 5;
      gridLayout.horizontalSpacing = 5;

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayoutData(UIUtil.createGridData(true, false));
      composite.setLayout(gridLayout);

      Label label = new Label(composite, SWT.NONE);
      label.setLayoutData(UIUtil.createGridData(false, false));
      label.setText("Name:");

      nameText = new Text(composite, SWT.BORDER);
      nameText.setLayoutData(UIUtil.createGridData(true, false));
      nameText.setText(name);
      nameText.addModifyListener(new ModifyListener()
      {
        public void modifyText(ModifyEvent e)
        {
          setName(nameText.getText());
        }
      });

      super.createBranchPointArea(parent);
    }
  }
}
