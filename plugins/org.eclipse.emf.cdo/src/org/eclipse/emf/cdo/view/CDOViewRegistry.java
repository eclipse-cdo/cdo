/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewRegistry.Registration;

import org.eclipse.net4j.util.container.IContainer;

/**
 * A global registry of all open {@link CDOView views} and {@link CDOTransaction transactions}.
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewRegistry extends IContainer<Registration>
{
  /**
   * The {@link ClassLoader class loader}-wide singleton instance of the {@link CDOViewRegistry view registry}.
   */
  public static final CDOViewRegistry INSTANCE = org.eclipse.emf.internal.cdo.view.CDOViewRegistryImpl.INSTANCE;

  /**
   * A symbolic constant returned from {@link #getID(CDOView)} if the view is not registered.
   */
  public static final int NOT_REGISTERED = 0;

  /**
   * Returns the {@link Registration#getID() IDs} of all registered {@link CDOView views}.
   */
  public int[] getIDs();

  /**
   * Returns all registered {@link CDOView views}.
   */
  public CDOView[] getViews();

  /**
   * Returns the {@link Registration#getID() ID} of the given {@link CDOView view} if it is registered, {@value #NOT_REGISTERED} otherwise.
   */
  public int getID(CDOView view);

  /**
   * Returns the {@link CDOView view} with the given {@link Registration#getID() ID} if it is registered, <code>null</code> otherwise.
   */
  public CDOView getView(int id);

  /**
   * A bidirectional mapping between a registered {@link CDOView view} and its {@link ClassLoader class loader}-wide {@link #getID() ID}.
   *
   * @author Eike Stepper
   */
  public interface Registration
  {
    /**
     * Returns the ID of this registration.
     */
    public int getID();

    /**
     * Returns the {@link CDOView} of this registration.
     */
    public CDOView getView();
  }
}
