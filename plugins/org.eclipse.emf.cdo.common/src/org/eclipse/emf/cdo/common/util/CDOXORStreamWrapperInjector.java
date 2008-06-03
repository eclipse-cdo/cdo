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
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;

import org.eclipse.net4j.signal.wrapping.XORStreamWrapperInjector;

/**
 * @author Eike Stepper
 */
public class CDOXORStreamWrapperInjector extends XORStreamWrapperInjector
{
  private static final int[] key = { 0xAA, 0x55, 0xAA, 0x55 };

  public CDOXORStreamWrapperInjector()
  {
    super(CDOProtocolConstants.PROTOCOL_NAME, key);
  }
}
