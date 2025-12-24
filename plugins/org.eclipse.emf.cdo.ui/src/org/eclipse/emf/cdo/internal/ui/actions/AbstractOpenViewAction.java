/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOpenViewAction extends SessionAction
{
  public AbstractOpenViewAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOSession session)
  {
    super(page, text, toolTipText, image, session);
  }

  @Override
  public boolean isEnabled()
  {
    CDOSession session = getSession();
    if (session.getRepositoryInfo().getState() == State.INITIAL)
    {
      return false;
    }

    if (session.getPackageRegistry().isEmpty())
    {
      return false;
    }

    return super.isEnabled();
  }
}
