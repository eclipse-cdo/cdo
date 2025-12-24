/*
 * Copyright (c) 2009-2012, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.transaction;

/**
 * Creates a save point in a {@link CDOUserTransaction} that can be used to roll back a part of the transaction.
 * <p>
 * <b>Note:</b> Save points do not flush to disk. Everything is done in memory on the client side.
 *
 * @author Simon McDuff
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOUserSavepoint
{
  public CDOUserTransaction getTransaction();

  public CDOUserSavepoint getNextSavepoint();

  public CDOUserSavepoint getPreviousSavepoint();

  /**
   * @since 4.1
   */
  public int getNumber();

  public boolean isValid();

  public void rollback();
}
