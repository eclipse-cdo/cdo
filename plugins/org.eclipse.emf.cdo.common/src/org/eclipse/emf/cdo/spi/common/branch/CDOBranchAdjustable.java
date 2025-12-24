/*
 * Copyright (c) 2014, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.branch;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;

/**
 * An interface for something that carries {@link CDOBranch branch} information that possibly
 * comes from other {@link CDOCommonSession sessions} and needs to be adjusted to the scope of a local session.
 *
 * @author Eike Stepper
 * @since 4.3
 */
public interface CDOBranchAdjustable
{
  public void adjustBranches(CDOBranchManager newBranchManager);
}
