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
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public class ImageButton extends Label implements MouseTrackListener, MouseMoveListener, MouseListener
{
  private static final SelectionMode DEFAULT_SELECTION_MODE = SelectionMode.MouseUp;

  private DisposeListener disposeListener;

  private Image hoverImage;

  private Image grayImage;

  private SelectionMode selectionMode = DEFAULT_SELECTION_MODE;

  private Runnable selectionRunnable;

  private boolean inImage;

  public ImageButton(Composite parent, Image hoverImage)
  {
    this(parent, hoverImage, null);
  }

  public ImageButton(Composite parent, Image hoverImage, Image grayImage)
  {
    super(parent, SWT.NONE);

    if (grayImage == null)
    {
      grayImage = generateGrayImage(hoverImage);
      disposeListener = e -> this.grayImage.dispose();
      addDisposeListener(disposeListener);
    }

    this.hoverImage = Objects.requireNonNull(hoverImage);
    this.grayImage = grayImage;

    setImage(grayImage);

    addMouseTrackListener(this);
    addMouseMoveListener(this);
    addMouseListener(this);
  }

  /**
   * @since 3.19
   */
  public Image getHoverImage()
  {
    return hoverImage;
  }

  /**
   * @since 3.19
   */
  public void setHoverImage(Image hoverImage)
  {
    this.hoverImage = Objects.requireNonNull(hoverImage);

    if (disposeListener != null)
    {
      grayImage.dispose();
      grayImage = generateGrayImage(hoverImage);
    }

    setImage(inImage ? hoverImage : grayImage);
  }

  /**
   * @since 3.19
   */
  public Image getGrayImage()
  {
    return grayImage;
  }

  /**
   * @since 3.19
   */
  public void setGrayImage(Image grayImage)
  {
    if (disposeListener != null)
    {
      grayImage.dispose();
      disposeListener = null;
    }

    this.grayImage = Objects.requireNonNull(grayImage);
    setImage(inImage ? hoverImage : grayImage);
  }

  /**
   * @since 3.19
   */
  public SelectionMode getSelectionMode()
  {
    return selectionMode;
  }

  /**
   * @since 3.19
   */
  public void setSelectionMode(SelectionMode selectionMode)
  {
    this.selectionMode = Objects.requireNonNullElse(selectionMode, DEFAULT_SELECTION_MODE);
  }

  /**
   * @since 3.19
   */
  public Runnable getSelectionRunnable()
  {
    return selectionRunnable;
  }

  /**
   * @since 3.19
   */
  public void setSelectionRunnable(Runnable selectionRunnable)
  {
    this.selectionRunnable = selectionRunnable;
  }

  /**
   * @since 3.19
   */

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
      setImage(hoverImage);
    }
    else
    {
      setImage(grayImage);
    }
  }

  @Override
  public void mouseUp(MouseEvent e)
  {
    if (inImage && selectionMode == SelectionMode.MouseUp)
    {
      widgetSelected();
    }
  }

  @Override
  public void mouseDown(MouseEvent e)
  {
    if (inImage && selectionMode == SelectionMode.MouseDown)
    {
      widgetSelected();
    }
  }

  @Override
  public void mouseDoubleClick(MouseEvent e)
  {
    if (inImage && selectionMode == SelectionMode.DoubleClick)
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
    if (selectionRunnable != null)
    {
      selectionRunnable.run();
    }
  }

  private Image generateGrayImage(Image hoverImage)
  {
    return new Image(getDisplay(), hoverImage, SWT.IMAGE_GRAY);
  }

  /**
   * @author Eike Stepper
   * @since 3.19
   */
  public enum SelectionMode
  {
    MouseUp, MouseDown, DoubleClick;
  }
}
