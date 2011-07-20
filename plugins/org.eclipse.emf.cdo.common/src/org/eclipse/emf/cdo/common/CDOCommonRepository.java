/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
 * Abstracts the information about CDO repositories that is common to both client and server side.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
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
   * @since 4.1
   */
  public IDGenerationLocation getIDGenerationLocation();

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
   * Enumerates the possible {@link CDOCommonRepository#getIDGenerationLocation() ID generation locations} of a CDO
   * repository.
   * 
   * @author Eike Stepper
   * @since 4.1
   */
  public enum IDGenerationLocation
  {
    STORE, CLIENT
  }

  /**
   * Enumerates the possible {@link CDOCommonRepository#getType() types} of a CDO repository.
   * 
   * @author Eike Stepper
   */
  public enum Type
  {
    MASTER, BACKUP, CLONE
  }

  /**
   * Enumerates the possible {@link CDOCommonRepository#getState() states} of a CDO repository.
   * 
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
   * An {@link IEvent event} fired when the {@link Type type} of a CDO repository has changed. This usually happens only
   * for repository fail-over participants.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface TypeChangedEvent extends IEvent
  {
    public Type getOldType();

    public Type getNewType();
  }

  /**
   * An {@link IEvent event} fired when the {@link State state} of a CDO repository has changed.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface StateChangedEvent extends IEvent
  {
    public State getOldState();

    public State getNewState();
  }
}
