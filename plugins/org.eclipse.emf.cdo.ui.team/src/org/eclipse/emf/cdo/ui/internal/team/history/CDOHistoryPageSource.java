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
package org.eclipse.emf.cdo.ui.internal.team.history;

import org.eclipse.team.ui.history.HistoryPageSource;
import org.eclipse.team.ui.history.IHistoryPageSource;
import org.eclipse.ui.part.Page;

/**
 * @author Eike Stepper
 */
public final class CDOHistoryPageSource extends HistoryPageSource
{
  public static final IHistoryPageSource INSTANCE = new CDOHistoryPageSource();

  private CDOHistoryPageSource()
  {
  }

  @Override
  public boolean canShowHistoryFor(Object object)
  {
    return CDOHistoryPage.canShowHistoryFor(object);
  }

  @Override
  public Page createPage(Object object)
  {
    // Don't set the input, the framework does this for us
    return new CDOHistoryPage();
  }
}
