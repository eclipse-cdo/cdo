/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.server.MEMStore;

/**
 * @author Eike Stepper
 */
public final class StoreUtil
{
  private StoreUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static IMEMStore createMEMStore()
  {
    return new MEMStore();
  }
}
