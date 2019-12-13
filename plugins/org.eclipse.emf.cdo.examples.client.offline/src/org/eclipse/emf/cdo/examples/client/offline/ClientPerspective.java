/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
public class ClientPerspective implements IPerspectiveFactory
{
  @Override
  public void createInitialLayout(IPageLayout layout)
  {
    layout.setFixed(true);
    layout.addView("org.eclipse.emf.cdo.examples.client.offline.ClientView", IPageLayout.LEFT, 0.5f, IPageLayout.ID_EDITOR_AREA);
    layout.addView("org.eclipse.emf.cdo.examples.client.offline.CloneRepositoryView", IPageLayout.BOTTOM, 0.46f,
        "org.eclipse.emf.cdo.examples.client.offline.ClientView");
    layout.addView("org.eclipse.ui.views.PropertySheet", IPageLayout.BOTTOM, 0.67f, IPageLayout.ID_EDITOR_AREA);
  }
}
