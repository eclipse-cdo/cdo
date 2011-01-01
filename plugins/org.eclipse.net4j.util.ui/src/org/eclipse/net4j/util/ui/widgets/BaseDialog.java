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
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public abstract class BaseDialog<VIEWER extends Viewer> extends TitleAreaDialog
{
  public static final int DEFAULT_SHELL_STYLE = SWT.SHELL_TRIM;

  private String title;

  private String message;

  private IDialogSettings settings;

  private VIEWER currentViewer;

  private MenuManager contextMenu;

  public BaseDialog(Shell parentShell, int shellStyle, String title, String message, IDialogSettings settings)
  {
    super(parentShell);
    setShellStyle(shellStyle);
    this.title = title;
    this.message = message;
    this.settings = settings;
  }

  public BaseDialog(Shell parentShell, String title, String message, IDialogSettings settings)
  {
    this(parentShell, DEFAULT_SHELL_STYLE, title, message, settings);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(title);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);
    setTitle(title);
    setMessage(message);

    contextMenu = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    contextMenu.setRemoveAllWhenShown(true);
    contextMenu.addMenuListener(new IMenuListener()
    {
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
      public void menuAboutToShow(IMenuManager manager)
      {
        fillContextMenu(manager, getCurrentViewer());
      }
    });
  }

  protected void fillContextMenu(IMenuManager manager, VIEWER viewer)
  {
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings()
  {
    if (settings == null)
    {
      return null;
    }

    IDialogSettings section = settings.getSection(title);
    if (section == null)
    {
      section = settings.addNewSection(title);
    }

    return section;
  }
}
