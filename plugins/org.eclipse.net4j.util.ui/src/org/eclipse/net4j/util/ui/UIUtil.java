/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.ui.security.InteractiveCredentialsProvider;

import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.util.List;

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
      throw new IllegalStateException("No display available"); //$NON-NLS-1$
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
      throw new IllegalStateException("No workbench available"); //$NON-NLS-1$
    }

    return workbench;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchWindow getActiveWorkbenchWindow()
  {
    IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
    if (window == null)
    {
      throw new IllegalStateException("No active window available"); //$NON-NLS-1$
    }

    return window;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchPage getActiveWorkbenchPage()
  {
    IWorkbenchPage page = getActiveWorkbenchWindow().getActivePage();
    if (page == null)
    {
      throw new IllegalStateException("No active page available"); //$NON-NLS-1$
    }

    return page;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchPart getActiveWorkbenchPart()
  {
    IWorkbenchPart part = getActiveWorkbenchPage().getActivePart();
    if (part == null)
    {
      throw new IllegalStateException("No active part available"); //$NON-NLS-1$
    }

    return part;
  }

  /**
   * @since 2.0
   */
  public static Object getElement(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      return ssel.getFirstElement();
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static <T> T getElement(ISelection selection, Class<T> type)
  {
    Object element = getElement(selection);
    if (element != null && type.isInstance(element))
    {
      @SuppressWarnings("unchecked")
      T result = (T)element;
      return result;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> getElements(ISelection selection, Class<T> type)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      return ssel.toList();
    }

    return null;
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

  /**
   * Adds indentation to the control. if indent value is < 0, the control indentation is left unchanged.
   * 
   * @since 2.0
   */
  public static void setIndentation(Control control, int horizontalIndent, int verticalIndent)
  {
    if (control == null)
    {
      throw new IllegalArgumentException("control == null"); //$NON-NLS-1$
    }

    Object data = control.getLayoutData();
    if (data instanceof GridData)
    {
      GridData gd = (GridData)data;
      if (verticalIndent >= 0)
      {
        gd.verticalIndent = verticalIndent;
      }

      if (horizontalIndent >= 0)
      {
        gd.horizontalIndent = horizontalIndent;
      }
    }
  }

  /**
   * @since 2.0
   */
  public static void refreshViewer(final Viewer viewer)
  {
    try
    {
      viewer.getControl().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            viewer.refresh();
          }
          catch (RuntimeException ignore)
          {
            // Do nothing
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
      // Do nothing
    }
  }

  /**
   * Shows a message in the StatusBar. Image can be omitted by passing a null parameter
   * 
   * @since 2.0
   */
  public static void setStatusBarMessage(final String message, final Image image)
  {
    getDisplay().syncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          IViewSite site = (IViewSite)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .getActivePart().getSite();
          if (image == null)
          {
            site.getActionBars().getStatusLineManager().setMessage(message);
          }
          else
          {
            site.getActionBars().getStatusLineManager().setMessage(image, message);
          }
        }
        catch (RuntimeException ignore)
        {
          // Do nothing
        }
      }
    });
  }
}
