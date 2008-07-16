/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

/**
 * TODO Enhance API: Return exceptions instead of letting them be thrown
 * 
 * @author Eike Stepper
 */
public interface CDOTransactionHandler
{
  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is added. The implementor of this method is allowed
   * to throw an unchecked exception that will propagate up to the operation that is about to add the object.
   */
  public void addingObject(CDOTransaction transaction, CDOObject object);

  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is modified. The implementor of this method is
   * allowed to throw an unchecked exception that will propagate up to the operation that is about to modify the object.
   * <p>
   * Note: This method will be called at most once per object until the associated transaction is committed.
   */
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta);

  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> it is being committed. The implementor of this method is
   * allowed to throw an unchecked exception that will propagate up to the operation that is about to commit the
   * transaction.
   */
  public void committingTransaction(CDOTransaction transaction);
}
