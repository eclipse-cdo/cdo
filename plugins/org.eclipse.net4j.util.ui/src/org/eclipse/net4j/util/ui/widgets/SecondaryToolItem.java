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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public class SecondaryToolItem extends ToolItem
{
  private Image grayImage;

  public SecondaryToolItem(ToolBar parent, int style, Image image)
  {
    super(parent, style);
    init(image);
  }

  public SecondaryToolItem(ToolBar parent, int style, int index, Image image)
  {
    super(parent, style, index);
    init(image);
  }

  @Override
  public void dispose()
  {
    grayImage.dispose();
    super.dispose();
  }

  @Override
  protected void checkSubclass()
  {
    // Do nothing.
  }

  public void init(Image image)
  {
    if (grayImage != null)
    {
      grayImage.dispose();
    }

    grayImage = new Image(getDisplay(), image, SWT.IMAGE_GRAY);
    setImage(grayImage);
    setHotImage(image);
  }
}
