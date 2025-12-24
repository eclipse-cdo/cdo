/*
 * Copyright (c) 2016, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.util.CDOException;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Locally manages the open {@link CDOUnit units} of a {@link #getView() view}.
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author Eike Stepper
 * @since 4.5
 */
public interface CDOUnitManager extends IContainer<CDOUnit>
{
  public CDOView getView();

  public boolean isUnit(EObject root);

  public CDOUnit createUnit(EObject root, boolean open, IProgressMonitor monitor) throws UnitExistsException;

  public CDOUnit openUnit(EObject root, boolean createOnDemand, IProgressMonitor monitor) throws UnitNotFoundException;

  public CDOUnit getOpenUnit(EObject object);

  public CDOUnit[] getOpenUnits();

  /**
   * @since 4.23
   */
  public boolean isPrefetchLockStates();

  /**
   * @since 4.23
   */
  public void setPrefetchLockStates(boolean prefetchLockStates);

  public boolean isAutoResourceUnitsEnabled();

  public void setAutoResourceUnitsEnabled(boolean enabled);

  /**
   * An unchecked {@link CDOException} being thrown from
   * {@link CDOUnitManager#createUnit(EObject, boolean, IProgressMonitor) CDOUnitManager.createUnit()}
   * to indicate that the given root object already is the {@link CDOUnit#getRoot() root}
   * of an existing {@link CDOUnit unit}.
   *
   * @author Eike Stepper
   */
  public static final class UnitExistsException extends CDOException
  {
    private static final long serialVersionUID = 1L;
  }

  /**
   * An unchecked {@link CDOException} being thrown from
   * {@link CDOUnitManager#openUnit(EObject, boolean, IProgressMonitor) CDOUnitManager.openUnit()}
   * to indicate that no {@link CDOUnit unit} exists for the given root object.
   *
   * @author Eike Stepper
   */
  public static final class UnitNotFoundException extends CDOException
  {
    private static final long serialVersionUID = 1L;
  }
}
