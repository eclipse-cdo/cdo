/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;

/**
 * Creates {@link CDOList list} instances.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOListFactory
{
  public static final CDOListFactory DEFAULT = CDOListImpl.FACTORY;

  public CDOList createList(int intitialCapacity, int size, int initialChunk);
}
