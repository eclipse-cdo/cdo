/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.security;

/**
 * @author Eike Stepper
 */
public interface IChallengeResponse
{
  public static final int PHASE_CHALLENGE = 1;

  public static final int PHASE_RESPONSE = 2;

  public static final int PHASE_ACKNOWLEDGE = 3;

  public static final byte ACKNOWLEDGE_SUCCESS = 1;

  public static final byte ACKNOWLEDGE_FAILURE = 0;
}
