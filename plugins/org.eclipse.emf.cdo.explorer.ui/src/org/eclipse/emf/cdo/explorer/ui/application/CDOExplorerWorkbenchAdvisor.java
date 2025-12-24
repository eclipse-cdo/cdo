/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.application;

import org.eclipse.emf.cdo.internal.ui.perspectives.CDOPerspective;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author Eike Stepper
 */
public class CDOExplorerWorkbenchAdvisor extends WorkbenchAdvisor
{
  @Override
  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
  {
    return new CDOExplorerWorkbenchWindowAdvisor(configurer);
  }

  @Override
  public void initialize(IWorkbenchConfigurer configurer)
  {
    super.initialize(configurer);
    configurer.setSaveAndRestore(true);
  }

  @Override
  public String getInitialWindowPerspectiveId()
  {
    return CDOPerspective.ID;
  }
}
