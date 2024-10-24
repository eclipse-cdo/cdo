/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.chat;

import org.eclipse.jface.layout.FillLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scrollable;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public final class EntryField extends Composite
{
  private static final int MAX_HEIGHT = 160;

  private static final int RADIUS = 36;

  private static final int MARGIN = RADIUS / 4;

  private static final int MARGIN_TWICE = MARGIN * 2;

  private final Color entryBackground;

  private final Scrollable control;

  private Point lastComputedSize;

  private boolean verticalBarVisible;

  public EntryField(ChatComposite parent, int style)
  {
    super(parent, style | SWT.DOUBLE_BUFFERED);

    Display display = getDisplay();
    setBackground(display.getSystemColor(SWT.COLOR_WHITE));

    entryBackground = new Color(display, 241, 241, 241);
    addDisposeListener(e -> entryBackground.dispose());

    addPaintListener(e -> {
      Rectangle box = getClientArea();
      e.gc.setAntialias(SWT.ON);
      e.gc.setBackground(entryBackground);
      e.gc.fillRoundRectangle(box.x, box.y, box.width, box.height, RADIUS, RADIUS);
    });

    setLayout(FillLayoutFactory.fillDefaults().margins(MARGIN, MARGIN).create());
    control = parent.getAdvisor().createEntryControl(this);
  }

  @Override
  public ChatComposite getParent()
  {
    return (ChatComposite)super.getParent();
  }

  public Color getEntryBackground()
  {
    return entryBackground;
  }

  public Point getLastComputedSize()
  {
    return lastComputedSize;
  }

  @Override
  public boolean setFocus()
  {
    return control.setFocus();
  }

  @Override
  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    int textWidth = control.getSize().x;
    if (textWidth == 0)
    {
      // Initial computation.
      textWidth = SWT.DEFAULT;

      // Force setVisible(false) on vertical bar, see below.
      verticalBarVisible = true;
    }

    Point newSize = control.computeSize(textWidth, SWT.DEFAULT);
    boolean vBarVisible;

    if (newSize.y > MAX_HEIGHT)
    {
      newSize.y = MAX_HEIGHT;
      vBarVisible = true;
    }
    else
    {
      vBarVisible = false;
    }

    if (verticalBarVisible != vBarVisible)
    {
      verticalBarVisible = vBarVisible;
      control.getVerticalBar().setVisible(vBarVisible);
    }

    newSize.y += MARGIN_TWICE;
    newSize.x += MARGIN_TWICE;

    if (lastComputedSize == null || !lastComputedSize.equals(newSize))
    {
      lastComputedSize = newSize;
    }

    return lastComputedSize;
  }
}
