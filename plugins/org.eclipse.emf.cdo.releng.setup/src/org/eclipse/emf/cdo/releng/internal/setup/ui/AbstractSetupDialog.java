/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class AbstractSetupDialog extends TitleAreaDialog
{
  public static final String TITLE = "Development Environment " + (Activator.SETUP_IDE ? "Setup" : "Installer");

  protected AbstractSetupDialog(Shell parentShell)
  {
    super(parentShell);
    setHelpAvailable(false);
    setShellStyle(SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(TITLE);
    setTitle(getDefaultTitle());
    setTitleImage(getDefaultImage(getImagePath()));
    setMessage(getDefaultMessage());

    Composite area = (Composite)super.createDialogArea(parent);

    GridLayout layout = new GridLayout(1, false);
    layout.marginWidth = getContainerMargin();
    layout.marginHeight = getContainerMargin();
    layout.verticalSpacing = 0;

    Composite container = new Composite(area, SWT.NONE);
    container.setLayout(layout);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    createUI(container);
    return area;
  }

  protected Button createCheckbox(Composite parent, String label)
  {
    ((GridLayout)parent.getLayout()).numColumns++;

    Button button = new Button(parent, SWT.CHECK);
    button.setText(label);
    button.setFont(JFaceResources.getDialogFont());

    setButtonLayoutData(button);
    return button;
  }

  @Override
  protected Control createHelpControl(Composite parent)
  {
    ToolBar toolBar = (ToolBar)super.createHelpControl(parent);
    createToolItemsForToolBar(toolBar);
    return toolBar;
  }

  protected void createToolItemsForToolBar(ToolBar toolBar)
  {
  }

  protected final ToolItem createToolItem(ToolBar toolBar, String label)
  {
    return createToolItem(toolBar, null, label);
  }

  protected final ToolItem createToolItem(ToolBar toolBar, String iconPath, String toolTip)
  {
    ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
    if (iconPath == null)
    {
      toolItem.setText(toolTip);
    }
    else
    {
      Image image = getDefaultImage(iconPath);
      toolItem.setImage(image);
      toolItem.setToolTipText(toolTip);
    }

    return toolItem;
  }

  protected Label createSeparator(Composite parent)
  {
    Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
    separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return separator;
  }

  protected int getContainerMargin()
  {
    return 0;
  }

  protected String getImagePlugin()
  {
    return "org.eclipse.emf.cdo.releng.setup";
  }

  protected String getImagePath()
  {
    return "icons/install_wiz.gif";
  }

  protected final Image getDefaultImage(String path)
  {
    return ResourceManager.getPluginImage(getImagePlugin(), path);
  }

  protected String getDefaultTitle()
  {
    return TITLE;
  }

  protected abstract String getDefaultMessage();

  @Override
  protected abstract Point getInitialSize();

  protected abstract void createUI(Composite parent);
}
