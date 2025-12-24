/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.appearance;

import org.eclipse.emf.cdo.dawn.spi.DawnState;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * This interface is used do influence the appearance of an UI element by a certain state. Implementations allow to
 * change the appearance for the adapted types of editors.
 *
 * @author Martin Fluegge
 * @since 2.0
 */
public interface DawnElementStylizer
{
  public void setDefault(Object element);

  public void setConflicted(Object element, int type);

  public void setLocked(Object element, int type);

  /**
   * Returns the image that represents the state for the given object.
   */
  public Image getImage(Object element, DawnState state);

  /**
   * Returns the foreground color that represents the state for the given object.
   */
  public Color getForegroundColor(Object element, DawnState state);

  /**
   * Returns the background color that represents the state for the given object.
   */
  public Color getBackgroundColor(Object element, DawnState state);
}
