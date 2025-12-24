/*
 * Copyright (c) 2009-2012, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import java.util.List;

/**
 * Creates a save point in a {@link CDOXATransaction} that can be used to roll back a part of the transaction.
 * <p>
 * <b>Note:</b> Save points do not flush to disk. Everything is done in memory on the client side.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOXASavepoint extends CDOUserSavepoint
{
  @Override
  public CDOXATransaction getTransaction();

  @Override
  public CDOXASavepoint getNextSavepoint();

  @Override
  public CDOXASavepoint getPreviousSavepoint();

  public List<CDOSavepoint> getSavepoints();
}
