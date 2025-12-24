/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

/**
 * A cache for the {@link CDOCommitInfo commit infos} of an {@link CDOObject object}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface CDOObjectHistory extends CDOCommitHistory
{
  public CDOObject getCDOObject();
}
