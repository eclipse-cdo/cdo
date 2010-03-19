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
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;

/**
 * The Figure used to visualize a commit info node in the branch tree.
 * 
 * @author Andre Dietisheim
 */
public class CommitInfoFigure extends Ellipse
{
  // private Label label;

  /**
   * Instantiates a new commit info figure.
   * 
   * @param text
   *          the text
   */
  public CommitInfoFigure(String text)
  {
    setAntialias(SWT.ON);
    // ToolbarLayout layout = new ToolbarLayout();
    // setLayoutManager(layout);
    setBackgroundColor(ColorConstants.yellow);
    setForegroundColor(ColorConstants.yellow);
    setOpaque(true);
    // figures have to have impair widths (in order to have connections attached to the center)
    setSize(31, 31);
    // setAlpha(100);
    setLineWidth(6);
    setFill(false);
    setToolTip(new Label(text));
  }
}
