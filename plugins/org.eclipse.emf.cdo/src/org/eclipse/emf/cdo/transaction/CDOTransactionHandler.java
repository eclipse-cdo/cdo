/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/233314
 *    Simon McDuff - http://bugs.eclipse.org/247143
 **************************************************************************/
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

/**
 * @author Eike Stepper
 */
public interface CDOTransactionHandler
{
  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is added. The implementor of this method is allowed
   * to throw an unchecked exception that will propagate up to the operation that is about to add the object.
   * 
   * @since 2.0
   */
  public void attachingObject(CDOTransaction transaction, CDOObject object);

  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is detached. The implementor of this method is
   * allowed to throw an unchecked exception that will propagate up to the operation that is about to remove the object.
   * 
   * @since 2.0
   */
  public void detachingObject(CDOTransaction transaction, CDOObject object);

  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> an object is modified. The implementor of this method is
   * allowed to throw an unchecked exception that will propagate up to the operation that is about to modify the object.
   * <p>
   * <b>Note:</b> This method will be called at most once per object until the associated transaction is committed.
   */
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta);

  /**
   * Called by a <code>CDOTransaction</code> <b>before</b> it is being committed. The implementor of this method is
   * allowed to throw an unchecked exception that will propagate up to the operation that is about to commit the
   * transaction.
   * 
   * @since 2.0
   */
  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext);

  /**
   * Called by a <code>CDOTransaction</code> <b>after</b> it is being committed. The implementor of this method is
   * <b>not</b> allowed to throw an unchecked exception.
   * 
   * @since 2.0
   */
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext);

  /**
   * Called by a <code>CDOTransaction</code> <b>after</b> it is rolled back. If the implementor of this method throws an
   * exception it will be logged as an error and subsequent handlers will be further called.
   * 
   * @since 2.0
   */
  public void rolledBackTransaction(CDOTransaction transaction);
}
