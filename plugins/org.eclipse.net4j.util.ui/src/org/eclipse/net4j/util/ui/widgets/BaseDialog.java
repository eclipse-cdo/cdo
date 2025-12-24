/*
 * Copyright (c) 2007, 2009, 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 * @since 3.4
 */
public abstract class BaseDialog<VIEWER extends Viewer> extends TitleAreaDialog
{
  public static final int DEFAULT_SHELL_STYLE = SWT.SHELL_TRIM;

  private String title;

  private String message;

  private Image image;

  private IDialogSettings settings;

  private VIEWER currentViewer;

  private MenuManager contextMenu;

  /**
   * @since 3.4
   */
  public BaseDialog(Shell parentShell, int shellStyle, String title, String message, IDialogSettings settings, ImageDescriptor descriptor)
  {
    super(parentShell);
    setShellStyle(shellStyle);
    this.title = title;
    this.message = message;
    this.settings = settings;

    if (descriptor != null)
    {
      image = descriptor.createImage(parentShell.getDisplay());
    }
  }

  public BaseDialog(Shell parentShell, int shellStyle, String title, String message, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, settings, null);
  }

  public BaseDialog(Shell parentShell, String title, String message, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, settings);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(title);
    setTitle(title);
    setMessage(message);
    if (image != null)
    {
      setTitleImage(image);
    }

    Composite composite = (Composite)super.createDialogArea(parent);

    contextMenu = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    contextMenu.setRemoveAllWhenShown(true);
    contextMenu.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        VIEWER viewer = getCurrentViewer();
        if (viewer != null)
        {
          fillContextMenu(manager, viewer);
        }
      }
    });

    createUI(composite);
    return composite;
  }

  protected abstract void createUI(Composite parent);

  public VIEWER getCurrentViewer()
  {
    return currentViewer;
  }

  public void setCurrentViewer(VIEWER currentViewer)
  {
    if (this.currentViewer != null)
    {
      Control control = this.currentViewer.getControl();
      control.setMenu(null);
    }

    this.currentViewer = currentViewer;
    if (this.currentViewer != null)
    {
      Control control = this.currentViewer.getControl();
      Menu menu = contextMenu.createContextMenu(control);
      control.setMenu(menu);
    }
  }

  protected void hookContextMenu()
  {
    contextMenu = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    contextMenu.setRemoveAllWhenShown(true);
    contextMenu.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        fillContextMenu(manager, getCurrentViewer());
      }
    });
  }

  protected void fillContextMenu(IMenuManager manager, VIEWER viewer)
  {
  }

  /**
   * @since 3.4
   */
  protected IDialogSettings getDialogSettings()
  {
    return settings;
  }

  /**
   * @since 3.4
   */
  protected IDialogSettings getDialogSettings(String sectionName)
  {
    IDialogSettings settings = getDialogSettings();
    if (settings == null)
    {
      return null;
    }

    sectionName = getClass().getName() + "_" + sectionName;
    IDialogSettings section = settings.getSection(sectionName);
    if (section == null)
    {
      section = settings.addNewSection(sectionName);
    }

    return section;
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings()
  {
    return getDialogSettings("bounds");
  }

  @Override
  public boolean close()
  {
    if (image != null)
    {
      image.dispose();
      image = null;
    }

    return super.close();
  }
}
