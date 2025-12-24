/*
 * Copyright (c) 2009, 2011, 2012, 2014, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.view.CDOViewEvent;

/**
 * A {@link CDOViewEvent view event} fired from a {@link CDOTransaction transaction} for each
 * {@link #getConflictingObject() object} that enters the {@link CDOTransaction#hasConflict() conflict} state.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @deprecated As of 4.19 use {@link CDOTransactionConflictAddedEvent}.
 */
@Deprecated
public interface CDOTransactionConflictEvent extends CDOTransactionConflictAddedEvent
{
}
