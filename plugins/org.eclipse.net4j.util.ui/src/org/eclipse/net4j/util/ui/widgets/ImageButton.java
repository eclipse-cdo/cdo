/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public class ImageButton extends Label implements MouseTrackListener, MouseMoveListener, MouseListener
{
  private final Image image;

  private final Image grayImage;

  private boolean inImage;

  public ImageButton(Composite parent, Image image)
  {
    this(parent, image, new Image(parent.getDisplay(), image, SWT.IMAGE_GRAY));
  }

  public ImageButton(Composite parent, Image image, Image grayImage)
  {
    super(parent, SWT.NONE);
    this.image = image;
    this.grayImage = grayImage;

    setImage(grayImage);

    addMouseTrackListener(this);
    addMouseMoveListener(this);
    addMouseListener(this);
  }

  @Override
  public void mouseEnter(MouseEvent e)
  {
    mouseMove(e);
  }

  @Override
  public void mouseExit(MouseEvent e)
  {
    mouseMove(e);
  }

  @Override
  public void mouseHover(MouseEvent e)
  {
    // Do nothing.
  }

  @Override
  public void mouseMove(MouseEvent e)
  {
    Rectangle bounds = getBounds();
    bounds.x = 0;
    bounds.y = 0;

    inImage = bounds.contains(e.x, e.y);
    if (inImage)
    {
      setImage(image);
    }
    else
    {
      setImage(grayImage);
    }
  }

  @Override
  public void mouseDoubleClick(MouseEvent e)
  {
    // Do nothing.
  }

  @Override
  public void mouseDown(MouseEvent e)
  {
    // Do nothing.
  }

  @Override
  public void mouseUp(MouseEvent e)
  {
    if (inImage)
    {
      widgetSelected();
    }
  }

  @Override
  protected void checkSubclass()
  {
    // Disable the check that prevents subclassing of SWT components.
  }

  protected void widgetSelected()
  {
  }
}
