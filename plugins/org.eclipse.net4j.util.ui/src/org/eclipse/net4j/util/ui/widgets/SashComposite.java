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
  private Sash sash;

  private Control control1;

  private Control control2;

  public SashComposite(Composite parent, int style, final int limit, final int percent)
  {
    super(parent, style);
    final FormLayout form = new FormLayout();
    setLayout(form);

    control1 = createControl1(this);
    sash = new Sash(this, SWT.VERTICAL);
    control2 = createControl2(this);

    FormData leftControlData = new FormData();
    leftControlData.left = new FormAttachment(0, 0);
    leftControlData.right = new FormAttachment(sash, 0);
    leftControlData.top = new FormAttachment(0, 0);
    leftControlData.bottom = new FormAttachment(100, 0);
    control1.setLayoutData(leftControlData);

    final FormData sashData = new FormData();
    sashData.left = new FormAttachment(percent, 0);
    sashData.top = new FormAttachment(0, 0);
    sashData.bottom = new FormAttachment(100, 0);
    sash.setLayoutData(sashData);
    sash.addListener(SWT.Selection, new Listener()
    {
      public void handleEvent(Event e)
      {
        Rectangle sashRect = sash.getBounds();
        Rectangle shellRect = SashComposite.this.getClientArea();
        int right = shellRect.width - sashRect.width - limit;
        e.x = Math.max(Math.min(e.x, right), limit);
        if (e.x != sashRect.x)
        {
          sashData.left = new FormAttachment(0, e.x);
          SashComposite.this.layout();
        }
      }
    });

    FormData rightControlData = new FormData();
    rightControlData.left = new FormAttachment(sash, 0);
    rightControlData.right = new FormAttachment(100, 0);
    rightControlData.top = new FormAttachment(0, 0);
    rightControlData.bottom = new FormAttachment(100, 0);
    control2.setLayoutData(rightControlData);
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

  protected abstract Control createControl2(Composite parent);

  protected abstract Control createControl1(Composite parent);
}