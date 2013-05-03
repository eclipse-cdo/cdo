/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
