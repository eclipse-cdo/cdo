/*
 * Copyright (c) 2013, 2014 Eike Stepper (Loehne, Germany) and others.
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

/**
 * A mix-in interface implemented by repositories and sessions to support resolving of list element proxies.
 *
 * @author Eike Stepper
 * @since 4.3
 */
public interface CDORevisionUnchunker
{
  public void ensureChunks(InternalCDORevision revision, int chunkSize);
}
