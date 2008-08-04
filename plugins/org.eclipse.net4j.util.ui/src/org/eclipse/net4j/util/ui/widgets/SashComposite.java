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
public abstract class SashComposite extends Composite implements INotifier.Introspection
{
  private Notifier notifier = new Notifier();

  private boolean vertical = true;

  private FormLayout form;

  private Sash sash;

  private OneBorderComposite composite1;

  private OneBorderComposite composite2;

  private FormData sashData;

  private FormData composite1Data;

  private FormData composite2Data;

  private int limit;

  private int percent;

  public SashComposite(Composite parent, int style, int limit, int percent)
  {
    super(parent, style);
    this.limit = limit;
    this.percent = percent;

    form = new FormLayout();
    setLayout(form);

    composite1 = new OneBorderComposite(this)
    {
      @Override
      protected Control createUI(Composite parent)
      {
        return createControl1(parent);
      }
    };
    composite1.setLayoutData(composite1Data = createFormData());

    sash = createSash(this);
    sash.setLayoutData(sashData = createFormData());

    composite2 = new OneBorderComposite(this)
    {
      @Override
      protected Control createUI(Composite parent)
      {
        return createControl2(parent);
      }
    };
    composite2.setLayoutData(composite2Data = createFormData());

    init();
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
      newSash.moveBelow(composite1);
      newSash.setLayoutData(sash.getLayoutData());

      sash.setLayoutData(null);
      sash.dispose();
      sash = newSash;

      init();
      layout();
      notifier.fireEvent(new OrientationChangedEvent(vertical));
    }
  }

  public Sash getSash()
  {
    return sash;
  }

  public Control getControl1()
  {
    return composite1.getClientControl();
  }

  public Control getControl2()
  {
    return composite2.getClientControl();
  }

  protected void init()
  {
    composite1.setBorderPosition(SWT.RIGHT);
    composite1Data.left = new FormAttachment(0, 0);
    composite1Data.right = new FormAttachment(sash, 0);

    sashData.left = new FormAttachment(percent, 0);
    sashData.right = null;

    composite2.setBorderPosition(SWT.LEFT);
    composite2Data.left = new FormAttachment(sash, 0);
    composite2Data.right = new FormAttachment(100, 0);

    if (vertical)
    {
      swap();
    }
  }

  /**
   * @since 2.0
   */
  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  /**
   * @since 2.0
   */
  public IListener[] getListeners()
  {
    return notifier.getListeners();
  }

  /**
   * @since 2.0
   */
  public boolean hasListeners()
  {
    return notifier.hasListeners();
  }

  /**
   * @since 2.0
   */
  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  protected void swap()
  {
    swap(composite1Data);
    swap(sashData);
    swap(composite2Data);
    composite1.swapBorderPosition();
    composite2.swapBorderPosition();
  }

  protected void swap(FormData formData)
  {
    FormAttachment tmp = formData.left;
    formData.left = formData.top;
    formData.top = tmp;

    tmp = formData.right;
    formData.right = formData.bottom;
    formData.bottom = tmp;
  }

  protected Sash createSash(Composite parent)
  {
    Sash sash = new Sash(parent, vertical ? SWT.HORIZONTAL : SWT.VERTICAL);
    sash.addListener(SWT.Selection, new SashListener());
    return sash;
  }

  protected abstract Control createControl1(Composite parent);

  protected abstract Control createControl2(Composite parent);

  private FormData createFormData()
  {
    FormData formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.bottom = new FormAttachment(100, 0);
    return formData;
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public class OrientationChangedEvent extends org.eclipse.net4j.util.event.Event
  {
    private static final long serialVersionUID = 1L;

    private boolean vertical;

    public OrientationChangedEvent(boolean vertical)
    {
      super(SashComposite.this);
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
          SashComposite.this.layout();
        }
      }
      else
      {
        int right = shellRect.width - sashRect.width - limit;
        e.x = Math.max(Math.min(e.x, right), limit);
        if (e.x != sashRect.x)
        {
          sashData.left = new FormAttachment(0, e.x);
          SashComposite.this.layout();
        }
      }
    }
  }
}
