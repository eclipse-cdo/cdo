/*
 * Copyright (c) 2007-2009, 2011, 2012, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.Notifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

/**
 * @author Eike Stepper
 */
public abstract class SashComposite extends Composite implements INotifier
{
  private final SashListener sashListener = new SashListener();

  private final Notifier notifier = new Notifier();

  private int limit;

  private int percent;

  private boolean borders;

  private boolean vertical;

  private boolean showBand;

  private Sash sash;

  private Control control1;

  private Control control2;

  private FormData sashData;

  private FormData control1Data;

  private FormData control2Data;

  public SashComposite(Composite parent, int style, int limit, int percent)
  {
    this(parent, style, limit, percent, false);
  }

  public SashComposite(Composite parent, int style, int limit, int percent, boolean borders)
  {
    this(parent, style, limit, percent, false, false, false);
  }

  /**
   * @since 3.13
   */
  public SashComposite(Composite parent, int style, int limit, int percent, boolean borders, boolean vertical, boolean showBand)
  {
    super(parent, style);
    setLayout(new FormLayout());
    this.limit = limit;
    this.percent = percent;
    this.borders = borders;
    this.vertical = vertical;
    this.showBand = showBand;

    control1Data = new FormData();
    control1 = borders ? new OneBorderComposite(this)
    {
      @Override
      protected Control createUI(Composite parent)
      {
        return createControl1(parent);
      }
    } : createControl1(this);

    control1.setLayoutData(control1Data);

    sashData = new FormData();
    sash = createSash(this);
    sash.setLayoutData(sashData);

    control2Data = new FormData();
    control2 = borders ? new OneBorderComposite(this)
    {
      @Override
      protected Control createUI(Composite parent)
      {
        return createControl2(parent);
      }
    } : createControl2(this);

    control2.setLayoutData(control2Data);

    init();
  }

  /**
   * @since 2.0
   */
  @Override
  public void dispose()
  {
    sash.removeListener(SWT.Selection, sashListener);
    super.dispose();
  }

  /**
   * @since 2.0
   */
  @Override
  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  /**
   * @since 2.0
   */
  @Override
  public IListener[] getListeners()
  {
    return notifier.getListeners();
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean hasListeners()
  {
    return notifier.hasListeners();
  }

  /**
   * @since 2.0
   */
  @Override
  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  public boolean isVertical()
  {
    return vertical;
  }

  public void setVertical(boolean vertical)
  {
    if (this.vertical != vertical)
    {
      this.vertical = vertical;

      Sash newSash = createSash(this);
      newSash.moveBelow(control1);
      newSash.setLayoutData(sash.getLayoutData());

      sash.removeListener(SWT.Selection, sashListener);
      sash.setLayoutData(null);
      sash.dispose();
      sash = newSash;

      init();
      layout();

      IListener[] listeners = notifier.getListeners();
      if (listeners.length != 0)
      {
        notifier.fireEvent(new OrientationChangedEvent(vertical), listeners);
      }
    }
  }

  public Sash getSash()
  {
    return sash;
  }

  public Control getControl1()
  {
    return borders ? ((OneBorderComposite)control1).getClientControl() : control1;
  }

  public Control getControl2()
  {
    return borders ? ((OneBorderComposite)control2).getClientControl() : control2;
  }

  protected void init()
  {
    if (borders)
    {
      ((OneBorderComposite)control1).setBorderPosition(SWT.RIGHT);
      ((OneBorderComposite)control2).setBorderPosition(SWT.LEFT);
    }

    control1Data.left = new FormAttachment(0, 0);
    control1Data.right = new FormAttachment(sash, 0);
    control1Data.top = new FormAttachment(0, 0);
    control1Data.bottom = new FormAttachment(100, 0);

    sashData.left = new FormAttachment(percent, 0);
    sashData.right = null;
    sashData.top = new FormAttachment(0, 0);
    sashData.bottom = new FormAttachment(100, 0);

    control2Data.left = new FormAttachment(sash, 0);
    control2Data.right = new FormAttachment(100, 0);
    control2Data.top = new FormAttachment(0, 0);
    control2Data.bottom = new FormAttachment(100, 0);

    if (vertical)
    {
      swap();
    }
  }

  protected void swap()
  {
    if (borders)
    {
      ((OneBorderComposite)control1).swapBorderPosition();
      ((OneBorderComposite)control2).swapBorderPosition();
    }

    swap(control1Data);
    swap(sashData);
    swap(control2Data);
  }

  protected void swap(FormData formData)
  {
    FormAttachment tmp1 = formData.left;
    formData.left = formData.top;
    formData.top = tmp1;

    FormAttachment tmp2 = formData.right;
    formData.right = formData.bottom;
    formData.bottom = tmp2;
  }

  protected Sash createSash(Composite parent)
  {
    Sash sash = new Sash(parent, (showBand ? SWT.BORDER : SWT.NONE) | (vertical ? SWT.HORIZONTAL : SWT.VERTICAL));
    sash.addListener(SWT.Selection, sashListener);
    return sash;
  }

  protected abstract Control createControl1(Composite parent);

  protected abstract Control createControl2(Composite parent);

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public class OrientationChangedEvent extends org.eclipse.net4j.util.event.Event
  {
    private static final long serialVersionUID = 1L;

    private final boolean vertical;

    public OrientationChangedEvent(boolean vertical)
    {
      super(SashComposite.this);
      this.vertical = vertical;
    }

    @Override
    public SashComposite getSource()
    {
      return (SashComposite)super.getSource();
    }

    public boolean isVertical()
    {
      return vertical;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SashListener implements Listener
  {
    public SashListener()
    {
    }

    @Override
    public void handleEvent(Event e)
    {
      Rectangle sashRect = sash.getBounds();
      Rectangle shellRect = getClientArea();
      if (vertical)
      {
        int bottom = shellRect.height - sashRect.height - limit;
        e.y = Math.max(Math.min(e.y, bottom), limit);
        if (e.y != sashRect.y)
        {
          sashData.top = new FormAttachment(0, e.y);
          layout();
        }
      }
      else
      {
        int right = shellRect.width - sashRect.width - limit;
        e.x = Math.max(Math.min(e.x, right), limit);
        if (e.x != sashRect.x)
        {
          sashData.left = new FormAttachment(0, e.x);
          layout();
        }
      }
    }
  }
}
