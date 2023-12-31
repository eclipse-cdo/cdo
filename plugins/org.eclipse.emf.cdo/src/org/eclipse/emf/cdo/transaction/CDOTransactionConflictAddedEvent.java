/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * @since 4.19
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransactionConflictAddedEvent extends CDOTransactionConflictChangedEvent
{
  public boolean isFirstConflict();
}
