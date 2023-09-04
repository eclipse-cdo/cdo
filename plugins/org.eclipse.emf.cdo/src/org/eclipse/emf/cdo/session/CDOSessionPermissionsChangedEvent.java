/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
