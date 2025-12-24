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
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Interface to complement {@link IMappingStrategy}.
 *
 * @author Eike Stepper
 * @since 4.5
 */
public interface IMappingStrategy2 extends IMappingStrategy
{
  public boolean needsRevisionPostProcessing();

  public void postProcessRevisions(IDBStoreAccessor accessor, CommitContext context, OMMonitor monitor);
}
