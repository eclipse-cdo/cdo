/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.mem;

import org.eclipse.emf.cdo.internal.server.mem.MEMStore;

/**
 * Creates {@link IMEMStore} instances.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public final class MEMStoreUtil
{
  private MEMStoreUtil()
  {
  }

  /**
   * Creates a {@link IMEMStore} instance.
   *
   * @since 4.0
   */
  public static IMEMStore createMEMStore()
  {
    return new MEMStore();
  }
}
