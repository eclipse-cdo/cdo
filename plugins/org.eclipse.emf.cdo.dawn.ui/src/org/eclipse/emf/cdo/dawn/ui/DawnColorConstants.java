/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
