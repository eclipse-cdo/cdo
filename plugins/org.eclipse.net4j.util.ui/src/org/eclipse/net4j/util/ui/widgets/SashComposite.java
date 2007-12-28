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

public abstract class SashComposite extends Composite
{
  private boolean vertical;

  private FormLayout form;

  private Sash sash;

  private Control control1;

  private Control control2;

  private FormData sashData;

  private FormData control1Data;

  private FormData control2Data;

  private int limit;

  private int percent;

  public SashComposite(Composite parent, int style, int limit, int percent)
  {
    this(parent, style, limit, percent, true);
  }

  public SashComposite(Composite parent, int style, int limit, int percent, boolean vertical)
  {
    super(parent, style);
    this.vertical = vertical;
    this.limit = limit;
    this.percent = percent;

    form = new FormLayout();
    setLayout(form);

    control1Data = new FormData();
    control1 = createControl1(this);
    control1.setLayoutData(control1Data);

    sashData = new FormData();
    sash = createSash(this);
    sash.setLayoutData(sashData);

    control2Data = new FormData();
    control2 = createControl2(this);
    control2.setLayoutData(control2Data);

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
      newSash.moveBelow(control1);
      newSash.setLayoutData(sash.getLayoutData());

      sash.setLayoutData(null);
      sash.dispose();
      sash = newSash;

      init();
      layout();
    }
  }

  public Sash getSash()
  {
    return sash;
  }

  public Control getControl1()
  {
    return control1;
  }

  public Control getControl2()
  {
    return control2;
  }

  protected void init()
  {
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

    if (!vertical)
    {
      swap();
    }
  }

  protected void swap()
  {
    swap(control1Data);
    swap(sashData);
    swap(control2Data);
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
    Sash sash = new Sash(parent, vertical ? SWT.VERTICAL : SWT.HORIZONTAL);
    sash.addListener(SWT.Selection, new SashListener());
    return sash;
  }

  protected abstract Control createControl1(Composite parent);

  protected abstract Control createControl2(Composite parent);

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
      Rectangle shellRect = SashComposite.this.getClientArea();
      if (vertical)
      {
        int right = shellRect.width - sashRect.width - limit;
        e.x = Math.max(Math.min(e.x, right), limit);
        if (e.x != sashRect.x)
        {
          sashData.left = new FormAttachment(0, e.x);
          SashComposite.this.layout();
        }
      }
      else
      {
        int bottom = shellRect.height - sashRect.height - limit;
        e.y = Math.max(Math.min(e.y, bottom), limit);
        if (e.y != sashRect.y)
        {
          sashData.top = new FormAttachment(0, e.y);
          SashComposite.this.layout();
        }
      }
    }
  }
}
