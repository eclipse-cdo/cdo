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

public class CommitInfoFigure extends Ellipse
{
  // private Label label;

  public CommitInfoFigure(String text)
  {
    setAntialias(SWT.ON);
    // ToolbarLayout layout = new ToolbarLayout();
    // setLayoutManager(layout);
    setBackgroundColor(ColorConstants.yellow);
    setForegroundColor(ColorConstants.yellow);
    setOpaque(true);
    setSize(30, 30);
    // setAlpha(100);
    setLineWidth(6);
    setFill(false);
    setToolTip(new Label(text));
  }
}

