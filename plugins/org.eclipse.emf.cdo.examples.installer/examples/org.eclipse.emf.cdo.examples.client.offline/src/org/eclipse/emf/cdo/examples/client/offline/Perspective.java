/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Eike Stepper
 */
public class Perspective implements IPerspectiveFactory
{
  public void createInitialLayout(IPageLayout layout)
  {
    layout.setFixed(true);
    layout.setEditorAreaVisible(false);

    layout.addView("org.myzilla.app.details", IPageLayout.LEFT, 0.5f, IPageLayout.ID_EDITOR_AREA);
    layout.addView("org.myzilla.app.navigator", IPageLayout.LEFT, 0.42f, "org.myzilla.app.details");
  }
}
