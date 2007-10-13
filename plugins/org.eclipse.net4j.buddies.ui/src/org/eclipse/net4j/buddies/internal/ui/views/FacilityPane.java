/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

/**
 * @author Eike Stepper
 */
public abstract class FacilityPane extends Composite
{
  private CoolBarManager coolBarManager;

  private CoolBar coolBarControl;

  private Label separator;

  private Control control;

  public FacilityPane(Composite parent, int style)
  {
    super(parent, style);
    setLayout(new FacilityPaneLayout());

    coolBarManager = new CoolBarManager(SWT.NONE);
    coolBarControl = coolBarManager.createControl(this);
    if (showTopSeperator())
    {
      separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
    }

    Composite composite = new Composite(this, SWT.NONE);
    control = createUI(composite);

    fillCoolBar(coolBarManager);
  }

  @Override
  public void dispose()
  {
    coolBarManager.dispose();
    super.dispose();
  }

  public ICoolBarManager getCoolBarManager()
  {
    return coolBarManager;
  }

  public Control getControl()
  {
    return control;
  }

  public void hidden(FacilityPane newPane)
  {
  }

  public void showed(FacilityPane oldPane)
  {
  }

  protected abstract Control createUI(Composite parent);

  protected void fillCoolBar(ICoolBarManager manager)
  {
  }

  /**
   * Returns whether to show a top separator line between the menu bar and the rest of the window contents. On some
   * platforms such as the Mac, the menu is separated from the main window already, so a separator line is not desired.
   * 
   * @return <code>true</code> to show the top separator, <code>false</code> to not show it
   */
  private boolean showTopSeperator()
  {
    return !"carbon".equals(SWT.getPlatform()); //$NON-NLS-1$
  }

  /**
   * @author Eike Stepper
   * @see org.eclipse.jface.window.ApplicationWindow.ApplicationWindowLayout
   */
  public class FacilityPaneLayout extends Layout
  {
    static final int VGAP = 2;

    static final int BAR_SIZE = 23;

    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache)
    {
      if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
      {
        return new Point(wHint, hHint);
      }

      Point result = new Point(0, 0);
      Control[] ws = composite.getChildren();
      for (int i = 0; i < ws.length; i++)
      {
        Control w = ws[i];
        boolean hide = false;
        if (w == coolBarControl)
        {
          if (!coolBarChildrenExist())
          {
            hide = true;
            result.y += BAR_SIZE;
          }
        }
        else if (w == separator)
        {
          if (!coolBarChildrenExist())
          {
            hide = true;
            // result.y += BAR_SIZE;
          }
        }

        if (!hide)
        {
          Point e = w.computeSize(wHint, hHint, flushCache);
          result.x = Math.max(result.x, e.x);
          result.y += e.y + VGAP;
        }
      }

      if (wHint != SWT.DEFAULT)
      {
        result.x = wHint;
      }
      if (hHint != SWT.DEFAULT)
      {
        result.y = hHint;
      }
      return result;
    }

    @Override
    protected void layout(Composite composite, boolean flushCache)
    {
      Rectangle clientArea = composite.getClientArea();
      Control[] ws = composite.getChildren();
      for (int i = 0; i < ws.length; i++)
      {
        Control w = ws[i];

        if (w == coolBarControl)
        {
          if (coolBarChildrenExist())
          {
            Point e = w.computeSize(clientArea.width, SWT.DEFAULT, flushCache);
            w.setBounds(clientArea.x, clientArea.y, clientArea.width, e.y);
            clientArea.y += e.y + VGAP;
            clientArea.height -= e.y + VGAP;
          }
        }
        else if (w == separator)
        {
          if (coolBarChildrenExist())
          {
            Point e = w.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
            w.setBounds(clientArea.x, clientArea.y, clientArea.width, e.y);
            clientArea.y += e.y;
            clientArea.height -= e.y;
          }
        }
        else
        {
          w.setBounds(clientArea.x, clientArea.y + VGAP, clientArea.width, clientArea.height - VGAP);
        }
      }
    }

    protected boolean coolBarChildrenExist()
    {
      return coolBarControl.getItemCount() > 0;
    }
  }
}
