/*
 * Copyright (c) 2014, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
