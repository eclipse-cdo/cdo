/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

/**
 * UI Widget representing all the historical revisions available for a
 * {@link org.eclipse.emf.cdo.common.branch.CDOBranch branch}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
// Under development
@Deprecated
public class AuditScale extends Composite
{
  private int direction;

  private int scaleStyle;

  private Scale scale;

  @Deprecated
  public AuditScale(Composite parent, int scaleStyle)
  {
    super(parent, SWT.NONE);
    setLayout(UIUtil.createGridLayout(1));

    this.scaleStyle = scaleStyle;
    updateDirection();
  }

  /**
   * @since 4.0
   */
  @Deprecated
  public int getDirection()
  {
    return direction;
  }

  @Deprecated
  public int getScaleStyle()
  {
    return scaleStyle;
  }

  @Deprecated
  public Scale getScale()
  {
    return scale;
  }

  @Deprecated
  protected Scale createScale(int style)
  {
    return new Scale(this, style);
  }

  @Deprecated
  @Override
  public void setBounds(int x, int y, int width, int height)
  {
    super.setBounds(x, y, width, height);
    updateDirection();
  }

  private void updateDirection()
  {
    int newDirection;
    if ((scaleStyle & SWT.HORIZONTAL) != 0)
    {
      newDirection = SWT.HORIZONTAL;
    }
    else if ((scaleStyle & SWT.VERTICAL) != 0)
    {
      newDirection = SWT.VERTICAL;
    }
    else
    {
      Rectangle clientArea = getClientArea();
      if (clientArea.height > clientArea.width)
      {
        newDirection = SWT.VERTICAL;
      }
      else
      {
        newDirection = SWT.HORIZONTAL;
      }
    }

    if (direction != newDirection)
    {
      direction = newDirection;
      if (scale != null)
      {
        scale.dispose();
        scale = null;
      }
    }

    if (scale == null)
    {
      int style = scaleStyle & ~(SWT.HORIZONTAL | SWT.VERTICAL) | direction;
      scale = createScale(style);
      scale.setLayoutData(UIUtil.createGridData());
    }
  }

  @Deprecated
  public static void main(String[] args)
  {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(UIUtil.createGridLayout(1));

    AuditScale scale = new AuditScale(shell, SWT.NONE);
    scale.setLayoutData(UIUtil.createGridData());

    // scale.setSize(200, 64);
    // scale.setMaximum(40);
    // scale.setPageIncrement(5);

    shell.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
      {
        display.sleep();
      }
    }

    display.dispose();
  }
}
