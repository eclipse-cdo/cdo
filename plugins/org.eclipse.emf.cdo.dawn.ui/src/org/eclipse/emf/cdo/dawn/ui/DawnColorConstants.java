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
package org.eclipse.emf.cdo.dawn.ui;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class DawnColorConstants
{
  public static final Color COLOR_NO_BORDER = new Color(null, 255, 255, 255);

  public static final Color COLOR_LOCKED_REMOTELY = ColorConstants.yellow;

  public static final Color COLOR_LOCKED_LOCALLY = ColorConstants.green;

  public static final Color COLOR_DELETE_CONFLICT = new Color(null, 255, 0, 0);

  public static final Color COLOR_CHANGE_CONFLICT = new Color(null, 0, 0, 255);

  public static final Color COLOR_NO_CONFLICT = new Color(null, 255, 255, 255);
}
