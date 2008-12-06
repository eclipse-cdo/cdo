/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOCommitContext;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class CDODefaultTransactionHandler implements CDOTransactionHandler
{
  protected CDODefaultTransactionHandler()
  {
  }

  /*
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    // Do nothing
  }

  /*
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void detachingObject(CDOTransaction transaction, CDOObject object)
  {
    // Do nothing
  }

  /*
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
  {
    // Do nothing
  }

  /*
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    // Do nothing
  }

  /*
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void rolledBackTransaction(CDOTransaction transaction)
  {
    // Do nothing
  }

  /*
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    // Do nothing
  }
}
