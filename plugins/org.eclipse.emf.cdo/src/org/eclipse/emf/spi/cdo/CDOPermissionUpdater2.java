/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.util.Map;
import java.util.Set;

/**
 * A {@link CDOPermissionUpdater permission updater} that can take a {@link CDOCommitInfo commit info} into account.
 *
 * @author Eike Stepper
 * @since 4.18
 */
public interface CDOPermissionUpdater2 extends CDOPermissionUpdater
{
  public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session, Set<InternalCDORevision> revisions, CDOCommitInfo commitInfo);
}
