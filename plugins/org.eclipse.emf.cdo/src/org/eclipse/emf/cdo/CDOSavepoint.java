/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **************************************************************************/
package org.eclipse.emf.cdo;

/**
 * Creates a save point in the {@link CDOTransaction} that can be used to roll back a part of the transaction, and
 * specifies the save point.
 * <p>
 * <b>Note:</b> Save point do not flush to disk. Everything is done on the client side.
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
