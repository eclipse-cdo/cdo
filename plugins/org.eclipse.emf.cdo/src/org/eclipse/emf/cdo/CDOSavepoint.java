/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo;

/**
 * Creates a save point in a {@link CDOTransaction} that can be used to roll back a part of the transaction.
 * <p>
 * <b>Note:</b> Save points do not flush to disk. Everything is done in memory on the client side.
 * 
 * @author Simon McDuff
 * @since 2.0
 */

public interface CDOSavepoint
{
  public CDOTransaction getTransaction();

  public CDOSavepoint getNextSavepoint();

  public CDOSavepoint getPreviousSavepoint();

  public boolean isValid();

  public void rollback(boolean remote);

  public void rollback();
}
