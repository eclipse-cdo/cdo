/*
 * Copyright (c) 2016, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.ecore.EObject;

/**
 * A disjunct {@link EObject} subtree of a model repository with the following additional characteristics:
 * <ul>
 * <li> All {@link #getRoot() root} / subtree element relations of a unit are physically stored and managed in the model repository.
 * <li> All elements of a unit are loaded very quickly in a single server request when the unit is {@link CDOUnitManager#openUnit(EObject, boolean, org.eclipse.core.runtime.IProgressMonitor) opened}.
 * <li> All elements of an open unit stay loaded until the unit is {@link #close() closed}.
 * <li> While a unit is open all its elements receive change notifications from the server without the need for a
 *      {@link CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy) change subscription policy}.
 * <li> Units can not overlap, that is, their element subtrees are disjunct.
 * <li> Units are locally managed by the {@link #getManager() unit manager} of the {@link CDOUnitManager#getView() view}.
 * </ul>
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author Eike Stepper
 * @since 4.5
 */
public interface CDOUnit
{
  public CDOUnitManager getManager();

  public EObject getRoot();

  public int getElements();

  /**
   * Same as calling {@link #close() close(true}.
   */
  public void close();

  /**
   * Closes this unit and optionally {@link CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy) resubscribes}
   * all contained objects for change notifications.
   */
  public void close(boolean resubscribe);
}
