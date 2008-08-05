/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.ui.security.InteractiveCredentialsProvider;

import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Eike Stepper
 */
public final class UIUtil
{
  private UIUtil()
  {
  }

  public static void dispose(Font font)
  {
    if (font != null)
    {
      font.dispose();
    }
  }

  public static void dispose(Color color)
  {
    if (color != null)
    {
      color.dispose();
    }
  }

  public static void dispose(Widget widget)
  {
    if (widget != null)
    {
      widget.dispose();
    }
  }

  public static Font getBoldFont(Control control)
  {
    FontData[] datas = control.getFont().getFontData().clone();
    datas[0].setStyle(SWT.BOLD);
    Display display = control.getShell().getDisplay();
    Font font = new Font(display, datas);
    return font;
  }

  public static Display getDisplay()
  {
    Display display = Display.getCurrent();
    if (display == null)
    {
      try
      {
        display = PlatformUI.getWorkbench().getDisplay();
      }
      catch (RuntimeException ignore)
      {
      }
    }

    if (display == null)
    {
      display = Display.getDefault();
    }

    if (display == null)
    {
      display = new Display();
    }

    if (display == null)
    {
      throw new IllegalStateException("No display available");
    }

    return display;
  }

  /**
   * @since 2.0
   */
  public static IWorkbench getWorkbench()
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    if (workbench == null)
    {
      throw new IllegalStateException("No workbench available");
    }

    return workbench;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchWindow getActiveWorkbenchWindow()
  {
    IWorkbench workbench = getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    if (window == null)
    {
      throw new IllegalStateException("No active window available");
    }
    return window;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchPage getActiveWorkbenchPage()
  {
    IWorkbenchWindow window = getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();
    if (page == null)
    {
      throw new IllegalStateException("No active page available");
    }

    return page;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchPart getActiveWorkbenchPart()
  {
    IWorkbenchPage page = getActiveWorkbenchPage();
    IWorkbenchPart part = page.getActivePart();
    if (part == null)
    {
      throw new IllegalStateException("No active part available");
    }

    return part;
  }

  public static IPasswordCredentialsProvider createInteractiveCredentialsProvider()
  {
    return new InteractiveCredentialsProvider();
  }

  public static Composite createGridComposite(Composite parent, int columns)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(createGridLayout(columns));
    return composite;
  }

  public static GridLayout createGridLayout(int columns)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = columns;
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.verticalSpacing = 0;
    layout.horizontalSpacing = 0;
    return layout;
  }

  public static GridData createGridData()
  {
    return new GridData(SWT.FILL, SWT.FILL, true, true);
  }

  public static GridData createGridData(boolean grabHorizontal, boolean grabVertical)
  {
    return new GridData(SWT.FILL, SWT.FILL, grabHorizontal, grabVertical);
  }

  public static void addDecorationMargin(Control control)
  {
    Object data = control.getLayoutData();
    if (data instanceof GridData)
    {
      GridData gd = (GridData)data;
      FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
      FieldDecoration dec = registry.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
      gd.horizontalIndent = dec.getImage().getBounds().width;
    }
  }
}
