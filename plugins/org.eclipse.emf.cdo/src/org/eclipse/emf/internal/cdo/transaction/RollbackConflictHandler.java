/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.transaction.CDOHandlingConflictResolver.ConflictHandler;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public class RollbackConflictHandler implements ConflictHandler
{
  public RollbackConflictHandler()
  {
  }

  @Override
  public String getLabel()
  {
    return "Rollback";
  }

  @Override
  public int getPriority()
  {
    return DEFAULT_PRIORITY;
  }

  @Override
  public boolean canHandleConflict(CDOMergingConflictResolver conflictResolver, long lastNonConflictTimeStamp)
  {
    return true;
  }

  @Override
  public boolean handleConflict(CDOMergingConflictResolver conflictResolver, long lastNonConflictTimeStamp)
  {
    CDOTransaction transaction = conflictResolver.getTransaction();
    transaction.rollback();
    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ConflictHandler.Factory
  {
    public static final String TYPE = "rollback";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public ConflictHandler create(String description) throws ProductCreationException
    {
      return new RollbackConflictHandler();
    }
  }
}
