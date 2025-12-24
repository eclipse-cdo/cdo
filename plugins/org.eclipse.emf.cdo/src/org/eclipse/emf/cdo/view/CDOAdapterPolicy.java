/*
 * Copyright (c) 2009-2012, 2014, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOAdapter;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

/**
 * A policy that allows to specify valid {@link Adapter} / {@link EObject} combinations.
 *
 * @author Simon McDuff
 * @see CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
 * @see CDOView.Options#setStrongReferencePolicy(CDOAdapterPolicy)
 * @since 2.0
 */
public interface CDOAdapterPolicy
{
  /**
   * A default adapter policy that never triggers any special behavior.
   */
  public static final CDOAdapterPolicy NONE = new CDOAdapterPolicy()
  {
    /**
     * Always returns <code>false</code>.
     */
    @Override
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return false;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.1"); //$NON-NLS-1$
    }
  };

  /**
   * A default adapter policy that only triggers special behavior if the adapter under test implements
   * {@link CDOAdapter}.
   */
  public static final CDOAdapterPolicy CDO = new CDOAdapterPolicy()
  {
    /**
     * Returns <code>true</code> if the given adapter implements {@link CDOAdapter}.
     */
    @Override
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return adapter instanceof CDOAdapter;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.0"); //$NON-NLS-1$
    }
  };

  /**
   * A default adapter policy that always triggers special behavior.
   */
  public static final CDOAdapterPolicy ALL = new CDOAdapterPolicy()
  {
    /**
     * Always returns <code>true</code>.
     */
    @Override
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return true;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.2"); //$NON-NLS-1$
    }
  };

  /**
   * Returns <code>true</code> if the given adapter on the given object should trigger a certain operation or behavior,
   * <code>false</code> otherwise.
   *
   * @see CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
   * @see CDOView.Options#setStrongReferencePolicy(CDOAdapterPolicy)
   */
  public boolean isValid(EObject eObject, Adapter adapter);
}
