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
import org.eclipse.emf.cdo.releng.setup.SetupConstants;

import org.eclipse.net4j.util.ReflectUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import org.osgi.framework.Bundle;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

/**
 * @author Eike Stepper
 */
public abstract class AbstractSetupDialog extends TitleAreaDialog
{
  public static final String SHELL_TEXT = "Eclipse Oomph" + (SetupConstants.SETUP_IDE ? "" : " Installer");

  private String title;

  private int width;

  private int height;

  private Bundle bundle;

  private String help;

  protected AbstractSetupDialog(Shell parentShell, String title, int width, int height, Bundle bundle, String help)
  {
    super(parentShell);
    this.title = title;
    this.width = width;
    this.height = height;
    this.bundle = bundle;
    this.help = help;

    setHelpAvailable(help != null);
    setShellStyle(SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
  }

  protected AbstractSetupDialog(Shell parentShell, String title, int width, int height, Bundle bundle)
  {
    this(parentShell, title, width, height, bundle, null);
  }

  protected AbstractSetupDialog(Shell parentShell, String title, int width, int height)
  {
    this(parentShell, title, width, height, Activator.getDefault().getBundle());
  }

  public String getTitle()
  {
    return title;
  }

  public int getWidth()
  {
    return width;
  }

  public int getHeight()
  {
    return height;
  }

  public Bundle getBundle()
  {
    return bundle;
  }

  public String getHelp()
  {
    return help;
  }

  @Override
  public void openTray(DialogTray tray) throws IllegalStateException, UnsupportedOperationException
  {
    super.openTray(tray);

    final Control trayControl = getFieldValue("trayControl");
    final Label rightSeparator = getFieldValue("rightSeparator");
    final Sash sash = getFieldValue("sash");
    if (trayControl == null || rightSeparator == null || sash == null)
    {
      return;
    }

    final GridData data = (GridData)trayControl.getLayoutData();
    sash.addListener(SWT.Selection, new Listener()
    {
      public void handleEvent(Event event)
      {
        if (event.detail == SWT.DRAG)
        {
          Shell shell = getShell();
          Rectangle clientArea = shell.getClientArea();
          int newWidth = clientArea.width - event.x - (sash.getSize().x + rightSeparator.getSize().x);
          if (newWidth != data.widthHint)
          {
            data.widthHint = newWidth;
            shell.layout();
          }
        }
      }
    });
  }

  private <T> T getFieldValue(String name)
  {
    try
    {
      Field field = ReflectUtil.getField(TrayDialog.class, name);
      if (field != null)
      {
        @SuppressWarnings("unchecked")
        T value = (T)ReflectUtil.getValue(field, this);
        return value;
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    return null;
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Shell shell = getShell();
    shell.setText(SHELL_TEXT);

    setTitle(title);
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

    if (getContainerMargin() == 0)
    {
      createSeparator(container);
    }

    shell.addHelpListener(new HelpListener()
    {
      public void helpRequested(HelpEvent e)
      {
        if (getTray() != null)
        {
          closeTray();
          updatedHelpButton(false);
          return;
        }

        DialogTray tray = new DialogTray()
        {
          @Override
          protected Control createContents(Composite parent)
          {
            URL resource = bundle.getResource(help);

            try
            {
              resource = FileLocator.resolve(resource);
            }
            catch (IOException ex)
            {
              Activator.log(ex);
            }

            Browser browser = new Browser(parent, SWT.NONE);
            browser.setSize(500, 800);
            browser.setUrl(resource.toString());
            return browser;
          }
        };

        openTray(tray);
        updatedHelpButton(true);
      }

      private void updatedHelpButton(boolean pushed)
      {
        try
        {
          Field field = ReflectUtil.getField(TrayDialog.class, "fHelpButton");
          ToolItem fHelpButton = (ToolItem)ReflectUtil.getValue(field, AbstractSetupDialog.this);
          fHelpButton.setSelection(pushed);
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    });

    shell.setActive();
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
    return Activator.PLUGIN_ID;
  }

  protected String getImagePath()
  {
    return "icons/install_wiz.gif";
  }

  protected final Image getDefaultImage(String path)
  {
    return ResourceManager.getPluginImage(getImagePlugin(), path);
  }

  protected abstract String getDefaultMessage();

  @Override
  protected final Point getInitialSize()
  {
    return new Point(width, height);
  }

  protected abstract void createUI(Composite parent);
}
