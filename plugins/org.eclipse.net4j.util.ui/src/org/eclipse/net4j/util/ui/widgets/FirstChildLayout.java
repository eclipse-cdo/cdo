/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public class FirstChildLayout extends Layout
{
  @Override
  protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache)
  {
    Control children[] = composite.getChildren();
    if (children.length != 0)
    {
      return children[0].computeSize(wHint, hHint, flushCache);
    }

    return new Point(0, 0);
  }

  @Override
  protected boolean flushCache(Control control)
  {
    return true;
  }

  @Override
  protected void layout(Composite composite, boolean flushCache)
  {
    Rectangle rect = composite.getClientArea();

    Control children[] = composite.getChildren();
    for (int i = 0; i < children.length; i++)
    {
      children[i].setBounds(rect);
      children[i].setVisible(i == 0);
    }
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName();
  }
}
