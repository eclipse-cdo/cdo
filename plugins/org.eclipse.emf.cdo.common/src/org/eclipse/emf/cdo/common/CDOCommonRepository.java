/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;

import org.eclipse.net4j.util.event.IEvent;

import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOCommonRepository extends CDOTimeProvider
{
  /**
   * Returns the name of this repository.
   */
  public String getName();

  /**
   * Returns the UUID of this repository.
   */
  public String getUUID();

  /**
   * Returns the type of this repository.
   */
  public Type getType();

  /**
   * Returns the state of this repository.
   */
  public State getState();

  /**
   * Returns the creation time of this repository.
   */
  public long getCreationTime();

  /**
   * Returns the type of the store of this repository.
   */
  public String getStoreType();

  /**
   * Returns the type of CDOIDs created by the store of this repository.
   */
  public Set<CDOID.ObjectType> getObjectIDTypes();

  /**
   * Returns the ID of the root resource of this repository.
   */
  public CDOID getRootResourceID();

  /**
   * Returns <code>true</code> if this repository supports auditing, <code>false</code> otherwise.
   */
  public boolean isSupportingAudits();

  /**
   * Returns <code>true</code> if this repository supports branching, <code>false</code> otherwise.
   */
  public boolean isSupportingBranches();

  /**
   * Returns <code>true</code> if this repository supports instances of Ecore, <code>false</code> otherwise.
   * 
   * @since 4.0
   */
  public boolean isSupportingEcore();

  /**
   * Returns <code>true</code> if this repository ensures referential integrity, <code>false</code> otherwise.
   * 
   * @since 4.0
   */
  public boolean isEnsuringReferentialIntegrity();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    MASTER, BACKUP, CLONE
  }

  /**
   * @author Eike Stepper
   */
  public static enum State
  {
    INITIAL, OFFLINE, SYNCING, ONLINE;

    public boolean isConnected()
    {
      return this == SYNCING || this == ONLINE;
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface TypeChangedEvent extends IEvent
  {
    public Type getOldType();

    public Type getNewType();
  }

  /**
   * @author Eike Stepper
   */
  public interface StateChangedEvent extends IEvent
  {
    public State getOldState();

    public State getNewState();
  }
}
