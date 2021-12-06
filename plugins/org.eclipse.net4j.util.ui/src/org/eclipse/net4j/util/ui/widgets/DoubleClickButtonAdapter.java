/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 * @author Eike Stepper
 * @since 3.13
 */
public abstract class DoubleClickButtonAdapter implements SelectionListener
{
  public DoubleClickButtonAdapter(Button button)
  {
    button.addSelectionListener(this);
    button.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseDoubleClick(MouseEvent e)
      {
        widgetDefaultSelected(null);
      }
    });

  }

  @Override
  public final void widgetDefaultSelected(SelectionEvent e)
  {
    widgetSelected(e);
    widgetDoubleClicked(e);
  }

  public abstract void widgetDoubleClicked(SelectionEvent e);
}
