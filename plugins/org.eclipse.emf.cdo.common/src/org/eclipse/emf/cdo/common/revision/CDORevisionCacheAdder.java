/*
 * Copyright (c) 2010-2012, 2021 Eike Stepper (Loehne, Germany) and others.
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

/**
 * An entity that {@link CDORevision revisions} can be added to.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public interface CDORevisionCacheAdder
{
  /**
   * Adds the given revision to this cache.
   */
  public void addRevision(CDORevision revision);
}
