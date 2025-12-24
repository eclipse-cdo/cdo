/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

/**
 * A revision provider that can also provide {@link SyntheticCDORevision synthetic revisions}.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public interface CDORevisionProviderWithSynthetics extends CDORevisionProvider
{
  public SyntheticCDORevision getSynthetic(CDOID id);
}
