/*
 * Copyright (c) 2015, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.swt.graphics.Image;

import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public final class OverlayImage extends ComposedImage
{
  private final int x;

  private final int y;

  /**
   * Overlays the given <code>image</code> with the given <code>overlayImage</code>
   * at the given position (relative to the top-left corner of the image).
   */
  public OverlayImage(Object image, Object overlayImage, int x, int y)
  {
    super(Arrays.asList(image, overlayImage));
    this.x = x;
    this.y = y;
  }

  /**
   * @since 4.17
   */
  public int getX()
  {
    return x;
  }

  /**
   * @since 4.17
   */
  public int getY()
  {
    return y;
  }

  @Override
  public List<ComposedImage.Point> getDrawPoints(Size size)
  {
    List<ComposedImage.Point> result = super.getDrawPoints(size);
    Point overLayPoint = result.get(1);
    overLayPoint.x = x;
    overLayPoint.y = y;
    return result;
  }

  /**
   * @since 4.17
   */
  public Image compose()
  {
    return ExtendedImageRegistry.INSTANCE.getImage(this);
  }
}
