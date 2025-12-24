/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.server.IUnitManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.5
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
*/
public interface InternalUnitManager extends IUnitManager
{
  @Override
  public InternalRepository getRepository();

  public List<InternalCDORevisionDelta> getUnitMoves(InternalCDORevisionDelta[] deltas, CDORevisionProvider before, CDORevisionProvider after);

  public InternalObjectAttacher attachObjects(InternalCommitContext commitContext);

  /**
   * @author Eike Stepper
   */
  public interface InternalObjectAttacher
  {
    public void finishedCommit(boolean success);
  }
}
