/*
 * Copyright (c) 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

/**
 * An empty default implementation of {@link CDOTransactionHandler1}.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class CDODefaultTransactionHandler1 implements CDOTransactionHandler1.WithUndo
{
  protected CDODefaultTransactionHandler1()
  {
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    handleDefault(transaction);
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void detachingObject(CDOTransaction transaction, CDOObject object)
  {
    handleDefault(transaction);
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   */
  @Override
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
  {
    handleDefault(transaction);
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behavior.
   * @since 4.6
   */
  @Override
  public void undoingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
  {
    handleDefault(transaction);
  }

  /**
   * @since 4.2
   */
  protected void handleDefault(CDOTransaction transaction)
  {
    // Do nothing
  }
}
