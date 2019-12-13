/*
 * Copyright (c) 2009, 2011, 2012, 2014, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.util.CDOTimeProvider;

import org.eclipse.emf.common.notify.Adapter;

/**
 * A {@link CDOViewEvent view event} fired when a {@link CDOView view} has finished notifying EMF {@link Adapter
 * adapters} about remote changes.
 *
 * @see CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewAdaptersNotifiedEvent extends CDOViewEvent, CDOTimeProvider
{
  /**
   * This is the time stamp of the commit operation that caused this client side event. It can be used to correlate this
   * event to the preceding {@link CDOViewInvalidationEvent invalidation event}.
   */
  @Override
  public long getTimeStamp();
}
