/*
 * Copyright (c) 2007-2009, 2011, 2012, 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOView.Options;

import org.eclipse.emf.common.notify.Adapter;

/**
 * A marker interface for {@link Adapter adapters} to indicate that change subscriptions should be registered with the
 * repository if they are attached to {@link CDOObject objects}.
 * <p>
 * This special marker interface is intended to be used with {@link CDOAdapterPolicy#CDO}. Note that you can also define
 * your own {@link CDOAdapterPolicy adapter policy} and {@link Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
 * register} it with the {@link CDOView view} to make your own adapters trigger change subscriptions.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOAdapter extends Adapter
{
}
