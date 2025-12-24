/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Represents a reference from one object to another object, possibly {@link CDOID} or CDOObject typed.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOReference<OBJECT>
{
  public static final int NO_INDEX = Notification.NO_INDEX;

  public OBJECT getTargetObject();

  public OBJECT getSourceObject();

  /**
   * Returns the source {@link EReference}.
   * @deprecated As of 4.10 use {@link #getSourceReference()}.
   */
  @Deprecated
  public EStructuralFeature getSourceFeature();

  /**
   * Returns the source {@link EReference}.
   * @since 4.10
   */
  public EReference getSourceReference();

  public int getSourceIndex();
}
