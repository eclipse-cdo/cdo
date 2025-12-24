/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.ecore.EClass;

/**
 * Creates {@link CDORevision revision} instances.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDORevisionFactory
{
  /**
   * @since 3.0
   */
  public static final CDORevisionFactory DEFAULT = new CDORevisionFactory()
  {
    @Override
    public CDORevision createRevision(EClass eClass)
    {
      return new org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl(eClass);
    }
  };

  /**
   * @since 3.0
   */
  public CDORevision createRevision(EClass eClass);
}
