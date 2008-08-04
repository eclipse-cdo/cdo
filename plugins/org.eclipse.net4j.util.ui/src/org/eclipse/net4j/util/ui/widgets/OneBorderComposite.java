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
      throw new IllegalArgumentException("borderPosition: " + borderPosition);
    }

    if (this.borderPosition != borderPosition)
    {
      this.borderPosition = borderPosition;
      switch (borderPosition)
      {
      case SWT.LEFT:
        setVertical(true);
        break;

      case SWT.RIGHT:
        setVertical(false);
        break;

      case SWT.TOP:
        setHorizontal(true);
        break;

      case SWT.BOTTOM:
        setHorizontal(false);
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
      return "LEFT";

    case SWT.RIGHT:
      return "RIGHT";

    case SWT.TOP:
      return "TOP";

    case SWT.BOTTOM:
      return "BOTTOM";
    }

    return super.toString();
  }

  protected abstract Control createUI(Composite parent);

  private void setHorizontal(boolean beginning)
  {
    if (border != null)
    {
      border.dispose();
    }

    layout.numColumns = 1;
    borderData = UIUtil.createGridData();
    borderData.widthHint = SWT.DEFAULT;
    borderData.heightHint = 1;
    borderData.grabExcessHorizontalSpace = true;
    borderData.grabExcessVerticalSpace = false;

    border = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
    border.setLayoutData(borderData);
    if (beginning)
    {
      border.moveAbove(null);
    }
  }

  private void setVertical(boolean beginning)
  {
    if (border != null)
    {
      border.dispose();
    }

    layout.numColumns = 2;
    borderData = UIUtil.createGridData();
    borderData.widthHint = 1;
    borderData.heightHint = SWT.DEFAULT;
    borderData.grabExcessHorizontalSpace = false;
    borderData.grabExcessVerticalSpace = true;

    border = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
    border.setLayoutData(borderData);
    if (beginning)
    {
      border.moveAbove(null);
    }
  }
}
