/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

import java.util.concurrent.TimeoutException;

/**
 * A {@link CDOTransactionHandler1 transaction handler} that automatically acquires {@link CDOObject#cdoWriteLock() write locks} when
 * {@link CDOObject objects} are modified.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOAutoLocker extends CDODefaultTransactionHandler1
{
  private long timeout;

  public CDOAutoLocker(long timeout)
  {
    this.timeout = timeout;
  }

  public CDOAutoLocker()
  {
    this(10000);
  }

  @Override
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
  {
    try
    {
      object.cdoWriteLock().lock(timeout);
    }
    catch (TimeoutException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * An {@link CDOAutoLocker auto locker} that only locks objects when their single-valued features are changed.
   * <p>
   * This auto locker is useful in combination with a {@link CDOTransaction.Options#addConflictResolver(CDOConflictResolver) conflict resolver}
   * that is able to automatically resolve possible conflicts in many-valued features, such as {@link CDOMergingConflictResolver}.
   *
   * @author Eike Stepper
   * @since 4.5
   */
  public static class ForSingleValuedChanges extends CDOAutoLocker
  {
    public ForSingleValuedChanges()
    {
    }

    public ForSingleValuedChanges(long timeout)
    {
      super(timeout);
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
    {
      EStructuralFeature feature = featureChange.getFeature();
      if (!feature.isMany())
      {
        super.modifyingObject(transaction, object, featureChange);
      }
    }
  }
}
