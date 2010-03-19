/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.branch.figure;

import org.eclipse.draw2d.ColorConstants;

/**
 * The Figure used to visualize a BranchPoint
 * 
 * @author Andre Dietisheim
 */
public class BranchPointFigure extends CommitInfoFigure
{
  public BranchPointFigure(String text)
  {
    super(text);
    setBackgroundColor(ColorConstants.red);
    setForegroundColor(ColorConstants.red);
    // figures have to have impair widths (in order to have connections attached to the center)
    setSize(31, 31);
  }
}
