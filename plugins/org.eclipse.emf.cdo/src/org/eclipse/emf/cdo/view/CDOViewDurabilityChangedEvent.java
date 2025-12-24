/*
 * Copyright (c) 2011, 2012, 2014 Eike Stepper (Loehne, Germany) and others.
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

/**
 * A {@link CDOViewEvent view event} fired when a {@link CDOView view} has been made
 * {@link CDOView#enableDurableLocking(boolean) durable} or volatile.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewDurabilityChangedEvent extends CDOViewEvent
{
  public String getOldDurableLockingID();

  public String getNewDurableLockingID();
}
