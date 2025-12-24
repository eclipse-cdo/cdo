/*
 * Copyright (c) 2009-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOXASavepoint;

import java.util.List;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOXASavepoint extends CDOXASavepoint, InternalCDOUserSavepoint
{
  @Override
  public InternalCDOXATransaction getTransaction();

  @Override
  public InternalCDOXASavepoint getFirstSavePoint();

  @Override
  public InternalCDOXASavepoint getPreviousSavepoint();

  @Override
  public InternalCDOXASavepoint getNextSavepoint();

  @Override
  public List<CDOSavepoint> getSavepoints();

  public void setSavepoints(List<CDOSavepoint> savepoints);
}
