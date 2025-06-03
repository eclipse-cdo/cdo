/*
 * Copyright (c) 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

/**
 * {@link #internRevision(CDORevision) Interns} revisions.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public interface CDORevisionInterner
{
  /**
   * Interns the given revision and returns either the given revision or
   * a revision with the same {@link CDORevisionKey key} that was interned previously.
   * <p>
   * The returned revision is only different from the passed revision if this interner
   * contains a previously cached, different revision instance with an equal
   * {@link CDORevisionKey revision key}.
   */
  public CDORevision internRevision(CDORevision revision);
}
