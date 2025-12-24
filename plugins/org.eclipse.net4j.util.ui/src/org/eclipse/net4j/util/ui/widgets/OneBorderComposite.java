/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class OneBorderComposite extends Composite
{
  private static final int POSITION_MASK = SWT.LEFT | SWT.RIGHT | SWT.TOP | SWT.BOTTOM;

  private int borderPosition;

  private Control border;

  private Control clientControl;

  private GridLayout layout;

  private GridData borderData;

  private GridData clientControlData;

  public OneBorderComposite(Composite parent)
  {
    super(parent, SWT.NONE);
    layout = UIUtil.createGridLayout(1);
    setLayout(layout);

    clientControlData = UIUtil.createGridData();
    clientControl = createUI(this);
    clientControl.setLayoutData(clientControlData);
  }

  public OneBorderComposite(Composite parent, int borderPosition)
  {
    this(parent);
    setBorderPosition(borderPosition);
  }

  public Control getClientControl()
  {
    return clientControl;
  }

  public int getBorderPosition()
  {
    return borderPosition;
  }

  public void setBorderPosition(int borderPosition)
  {
    borderPosition = borderPosition & POSITION_MASK;
    if (Integer.bitCount(borderPosition) != 1)
    {
      throw new IllegalArgumentException("borderPosition: " + borderPosition); //$NON-NLS-1$
    }

    if (this.borderPosition != borderPosition)
    {
      this.borderPosition = borderPosition;
      switch (borderPosition)
      {
      case SWT.LEFT:
        setBorder(true, true);
        break;

      case SWT.RIGHT:
        setBorder(true, false);
        break;

      case SWT.TOP:
        setBorder(false, true);
        break;

      case SWT.BOTTOM:
        setBorder(false, false);
        break;

      default:
        return;
      }

      layout();
    }
  }

  public void swapBorderPosition()
  {
    switch (borderPosition)
    {
    case SWT.LEFT:
      setBorderPosition(SWT.TOP);
      break;

    case SWT.RIGHT:
      setBorderPosition(SWT.BOTTOM);
      break;

    case SWT.TOP:
      setBorderPosition(SWT.LEFT);
      break;

    case SWT.BOTTOM:
      setBorderPosition(SWT.RIGHT);
      break;
    }
  }

  @Override
  public String toString()
  {
    switch (borderPosition)
    {
    case SWT.LEFT:
      return "LEFT"; //$NON-NLS-1$

    case SWT.RIGHT:
      return "RIGHT"; //$NON-NLS-1$

    case SWT.TOP:
      return "TOP"; //$NON-NLS-1$

    case SWT.BOTTOM:
      return "BOTTOM"; //$NON-NLS-1$
    }

    return super.toString();
  }

  protected abstract Control createUI(Composite parent);

  private void setBorder(boolean vertical, boolean beginning)
  {
    if (border != null)
    {
      border.dispose();
    }

    layout.numColumns = vertical ? 2 : 1;
    borderData = UIUtil.createGridData();
    borderData.widthHint = vertical ? 1 : SWT.DEFAULT;
    borderData.heightHint = vertical ? SWT.DEFAULT : 1;
    borderData.grabExcessHorizontalSpace = !vertical;
    borderData.grabExcessVerticalSpace = vertical;

    int orientation = vertical ? SWT.VERTICAL : SWT.HORIZONTAL;
    border = new Label(this, SWT.SEPARATOR | orientation);
    border.setLayoutData(borderData);
    if (beginning)
    {
      border.moveAbove(null);
    }
  }
}
