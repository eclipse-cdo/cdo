/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public final class ColorStyler extends Styler
{
  private final Color color;

  public ColorStyler(Color color)
  {
    this.color = color;
  }

  @Override
  public void applyStyles(TextStyle textStyle)
  {
    textStyle.foreground = color;
  }
}
