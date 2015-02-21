/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
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

  public OverlayImage(Object image, Object overlayImage, int x, int y)
  {
    super(Arrays.asList(image, overlayImage));
    this.x = x;
    this.y = y;
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
}
