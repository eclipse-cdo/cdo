/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.event.IEvent;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface CDOViewCommitedEvent extends IEvent
{
  public CDOView getView();

  public Map<CDOID, CDOID> getIDMappings();
}
