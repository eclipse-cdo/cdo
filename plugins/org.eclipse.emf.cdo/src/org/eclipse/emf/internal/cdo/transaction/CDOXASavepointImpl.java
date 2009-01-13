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
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOUserTransaction;

import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOXASavepointImpl extends AbstractSavepoint
{
  private List<CDOSavepoint> savepoints;

  public CDOXASavepointImpl(CDOUserTransaction transaction, AbstractSavepoint lastSavepoint)
  {
    super(transaction, lastSavepoint);

  }

  public List<CDOSavepoint> getSavepoints()
  {
    return savepoints;
  }

  public void setSavepoints(List<CDOSavepoint> savepoints)
  {
    this.savepoints = savepoints;
  }
}
