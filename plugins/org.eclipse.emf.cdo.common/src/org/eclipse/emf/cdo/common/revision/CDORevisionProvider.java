/*
 * Copyright (c) 2010-2012, 2015, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;

/**
 * Provides consumers with the {@link CDORevision revisions} of {@link CDOID identifiable} CDO objects by selecting a
 * particular one from several possible {@link CDOBranchPoint branch points}.
 *
 * @author Eike Stepper
 * @since 3.0
 */
@FunctionalInterface
public interface CDORevisionProvider
{
  public CDORevision getRevision(CDOID id);
}
