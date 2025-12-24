/*
 * Copyright (c) 2013, 2014, 2018, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;

/**
 * A {@link CDOSessionInvalidationEvent session invalidation event} with {@link #getSecurityImpact() security impact information}.
 *
 * @author Eike Stepper
 * @since 4.3
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOSessionInvalidationEvent extends CDOSessionInvalidationEvent
{
  /**
   * @see CommitNotificationInfo#IMPACT_NONE
   * @see CommitNotificationInfo#IMPACT_PERMISSIONS
   * @see CommitNotificationInfo#IMPACT_REALM
   */
  public byte getSecurityImpact();
}
