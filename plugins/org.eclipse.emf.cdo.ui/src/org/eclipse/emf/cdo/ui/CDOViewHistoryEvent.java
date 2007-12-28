/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory.Entry;

import org.eclipse.net4j.util.event.IEvent;

/**
 * @author Eike Stepper
 */
public interface CDOViewHistoryEvent extends IEvent
{
  public CDOViewHistory getViewHistory();

  public Entry getAddedEntry();
}
