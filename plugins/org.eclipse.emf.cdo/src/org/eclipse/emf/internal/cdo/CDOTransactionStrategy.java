/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSavepoint;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOTransactionStrategy
{
  public static final CDOTransactionStrategy DEFAULT = CDOSingleTransactionStrategy.INSTANCE;

  public void setTarget(InternalCDOTransaction transaction);

  public void unsetTarget(InternalCDOTransaction transaction);

  public void commit(InternalCDOTransaction transaction, IProgressMonitor progressMonitor) throws Exception;

  public void rollback(InternalCDOTransaction transaction, CDOSavepoint savepoint);

  public CDOSavepoint setSavepoint(InternalCDOTransaction transaction);
}
