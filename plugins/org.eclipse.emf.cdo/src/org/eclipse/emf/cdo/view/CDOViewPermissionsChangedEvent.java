/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.session.CDOSessionPermissionsChangedEvent;

import org.eclipse.net4j.util.collection.Pair;

import java.util.Map;

/**
 * A {@link CDOViewEvent view event} fired when {@link CDOPermission permissions} of the
 * {@link CDORevision revisions} of this view have changed.
 *
 * @see CDOSessionPermissionsChangedEvent
 * @author Eike Stepper
 * @since 4.22
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewPermissionsChangedEvent extends CDOViewEvent
{
  public Map<CDOID, Pair<CDOPermission, CDOPermission>> getPermissionChanges();
}
