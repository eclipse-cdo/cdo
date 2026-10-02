/*
 * Copyright (c) 2011, 2012, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.spi.common.revision;

/**
 * Implemented by revision data structures whose stored object references can be rewritten through a
 * {@link CDOReferenceAdjuster}, for example when temporary IDs are replaced after commit.
 *
 * @author Simon McDuff
 * @since 4.0
 */
public interface CDOReferenceAdjustable
{
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster);
}
