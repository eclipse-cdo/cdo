/*
 * Copyright (c) 2010-2012, 2015, 2016, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager;

/**
 * Target-side callback contract for replaying repository history into another repository. It receives branch and
 * commit information together with durable lock-area data as the replication process walks the source history.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOReplicationContext extends CDOReplicationInfo, CDOBranchHandler, CDOCommitInfoHandler, IDurableLockingManager.LockArea.Handler
{
}
