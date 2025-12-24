/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

/**
 * A {@link CDOViewEvent view event} fired when the {@link CDOView#getProvider() provider} of a {@link CDOView view} has changed.
 *
 * @author Eike Stepper
 * @since 4.24
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewProviderChangedEvent extends CDOViewEvent
{
  public CDOViewProvider getOldProvider();

  public CDOViewProvider getProvider();
}
