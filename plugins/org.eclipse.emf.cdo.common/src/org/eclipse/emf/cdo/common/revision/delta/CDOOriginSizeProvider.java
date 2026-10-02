/*
 * Copyright (c) 2013, 2015, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision.delta;

/**
 * Supplies the size of a list-valued feature at the origin of a revision delta. This lets list deltas interpret their
 * indices against the list before the recorded edits were applied.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface CDOOriginSizeProvider
{
  public int getOriginSize();
}
