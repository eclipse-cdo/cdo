/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.Map;
import java.util.Set;

/**
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author Eike Stepper
 * @since 4.5
 */
public interface IUnitManager extends IContainer<IUnit>
{
  public IRepository getRepository();

  public IUnit createUnit(CDOID rootID, IView view, CDORevisionHandler revisionHandler, OMMonitor monitor);

  public boolean isUnit(CDOID rootID);

  public IUnit getUnit(CDOID rootID);

  public IUnit[] getUnits();

  public Map<CDOID, CDOID> getUnitsOf(Set<CDOID> ids, CDORevisionProvider revisionProvider);
}
