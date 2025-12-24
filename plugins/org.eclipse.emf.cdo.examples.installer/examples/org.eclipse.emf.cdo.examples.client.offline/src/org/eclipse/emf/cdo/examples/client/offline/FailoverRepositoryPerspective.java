/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
public class FailoverRepositoryPerspective implements IPerspectiveFactory
{
  @Override
  public void createInitialLayout(IPageLayout layout)
  {
    layout.setEditorAreaVisible(false);
    layout.setFixed(true);
    layout.addView(FailoverRepositoryView.ID, IPageLayout.LEFT, 0.5f, IPageLayout.ID_EDITOR_AREA);
  }
}
