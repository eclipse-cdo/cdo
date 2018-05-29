/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public class StackComposite extends Composite
{
  private final StackLayout layout = new StackLayout();

  public StackComposite(Composite parent, int style)
  {
    super(parent, style);
    setLayout(layout);
  }

  public Control getTopControl()
  {
    return layout.topControl;
  }

  public void setTopControl(Control topControl)
  {
    if (layout.topControl != topControl)
    {
      layout.topControl = topControl;
      layout();
    }
  }

  @Override
  protected void checkSubclass()
  {
    // Do nothing.
  }
}
