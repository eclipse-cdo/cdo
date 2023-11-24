/*
 * Copyright (c) 2015, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.transaction.CDOConflictResolver3;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ResolutionPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.4
 */
public class CDOHandlingConflictResolver extends CDOMergingConflictResolver implements CDOConflictResolver3
{
  private ConflictHandlerSelector conflictHandlerSelector;

  public CDOHandlingConflictResolver()
  {
  }

  public CDOHandlingConflictResolver(boolean ensureRemoteNotifications)
  {
    super(ensureRemoteNotifications);
  }

  public CDOHandlingConflictResolver(CDOMerger merger, boolean ensureRemoteNotifications)
  {
    super(merger, ensureRemoteNotifications);
  }

  public CDOHandlingConflictResolver(CDOMerger merger)
  {
    super(merger);
  }

  public CDOHandlingConflictResolver(ResolutionPreference resolutionPreference, boolean ensureRemoteNotifications, ConflictHandler conflictHandler)
  {
    super(resolutionPreference, ensureRemoteNotifications);
  }

  public CDOHandlingConflictResolver(ResolutionPreference resolutionPreference)
  {
    super(resolutionPreference);
  }

  public final ConflictHandlerSelector getConflictHandlerSelector()
  {
    return conflictHandlerSelector;
  }

  public final void setConflictHandlerSelector(ConflictHandlerSelector conflictHandlerSelector)
  {
    this.conflictHandlerSelector = conflictHandlerSelector;
  }

  @Override
  public boolean preCommit()
  {
    if (isConflict())
    {
      CDOTransaction transaction = getTransaction();
      ConflictHandler conflictHandler;

      try
      {
        conflictHandler = getConflictHandler(transaction);
      }
      catch (CancelException ex)
      {
        return false;
      }

      if (conflictHandler != null)
      {
        long lastNonConflictTimeStamp = getLastNonConflictTimeStamp();
        return handleConflict(conflictHandler, lastNonConflictTimeStamp);
      }
    }

    return true;
  }

  protected boolean handleConflict(ConflictHandler conflictHandler, long lastNonConflictTimeStamp)
  {
    return conflictHandler.handleConflict(this, lastNonConflictTimeStamp);
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected ConflictHandler getConflictHandler(CDOTransaction transaction) throws CancelException
  {
    if (conflictHandlerSelector == null)
    {
      return null;
    }

    List<ConflictHandler> conflictHandlers = getConflictHandlers(transaction);
    if (conflictHandlers.isEmpty())
    {
      return null;
    }

    ConflictHandler conflictHandler = conflictHandlerSelector.selectConflictHandler(transaction, conflictHandlers);
    if (conflictHandler == null)
    {
      throw new CancelException();
    }

    return conflictHandler;
  }

  protected List<ConflictHandler> getConflictHandlers(CDOTransaction transaction)
  {
    List<ConflictHandler> result = new ArrayList<>();

    IManagedContainer container = getContainer();
    String productGroup = ConflictHandler.Factory.PRODUCT_GROUP;

    for (String factoryType : container.getFactoryTypes(productGroup))
    {
      try
      {
        ConflictHandler conflictHandler = (ConflictHandler)container.getElement(productGroup, factoryType, null);
        result.add(conflictHandler);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    Collections.sort(result, new Comparator<ConflictHandler>()
    {
      @Override
      public int compare(ConflictHandler h1, ConflictHandler h2)
      {
        return h1.getPriority() - h2.getPriority();
      }
    });

    return result;
  }

  /**
   * @author Eike Stepper
   */
  protected static final class CancelException extends Exception
  {
    private static final long serialVersionUID = 1L;

    public CancelException()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface ConflictHandler
  {
    public static final int DEFAULT_PRIORITY = 500;

    public String getLabel();

    public int getPriority();

    public boolean canHandleConflict(CDOMergingConflictResolver conflictResolver, long lastNonConflictTimeStamp);

    /**
     * @return <code>false</code> to abort the commit operation, <code>true</code> otherwise.
     */
    public boolean handleConflict(CDOMergingConflictResolver conflictResolver, long lastNonConflictTimeStamp);

    /**
     * @author Eike Stepper
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.conflictHandlers"; //$NON-NLS-1$

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract ConflictHandler create(String description) throws ProductCreationException;
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface ConflictHandlerSelector
  {
    public ConflictHandler selectConflictHandler(CDOTransaction transaction, List<ConflictHandler> choices);
  }
}
