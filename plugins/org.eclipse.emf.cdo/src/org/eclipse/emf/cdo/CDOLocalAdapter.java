/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.common.notify.Adapter;

/**
 * A marker interface for {@link Adapter adapters} to indicate that change subscriptions should <b>not</b> be registered with the
 * repository if they are attached to {@link CDOObject objects}.
 *
 * @author Eike Stepper
 * @since 4.6
 */
public interface CDOLocalAdapter extends Adapter
{
}
