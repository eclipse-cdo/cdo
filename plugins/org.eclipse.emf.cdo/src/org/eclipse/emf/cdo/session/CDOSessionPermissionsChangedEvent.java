/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;

import java.util.Map;

/**
 * A {@link CDOSessionEvent session event} fired when {@link CDOPermission permissions} of the
 * {@link CDORevision revisions} of this session have changed.
 *
 * @author Eike Stepper
 * @since 4.22
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSessionPermissionsChangedEvent extends CDOSessionEvent
{
  public Map<CDORevision, CDOPermission> getOldPermissions();
}
