/*
 * Copyright (c) 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

/**
 * Provides the inverse-close operation used when a resource must be closed from the opposite side of a bidirectional lifecycle relationship.
 * Implementations use {@link #inverseClose()} to perform that direction-specific close action.
 *
 * @author Eike Stepper
 * @since 4.10
 */
public interface InverseCloseable
{
  public void inverseClose();
}
