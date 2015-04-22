/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common.protocol;

/**
 * @author Eike Stepper
 */
public interface ProtocolConstants
{
  public static final String PROTOCOL_NAME = "buddies"; //$NON-NLS-1$

  public static final short SIGNAL_OPEN_SESSION = 1;

  public static final short SIGNAL_LOAD_ACCOUNT = 2;

  public static final short SIGNAL_BUDDY_ADDED = 3;

  public static final short SIGNAL_BUDDY_REMOVED = 4;

  public static final short SIGNAL_BUDDY_STATE = 5;

  public static final short SIGNAL_INITIATE_COLLABORATION = 6;

  public static final short SIGNAL_COLLABORATION_INITIATED = 7;

  public static final short SIGNAL_COLLABORATION_LEFT = 8;

  public static final short SIGNAL_INVITE_BUDDIES = 9;

  public static final short SIGNAL_BUDDIES_INVITED = 10;

  public static final short SIGNAL_INSTALL_FACILITY = 11;

  public static final short SIGNAL_FACILITY_INSTALLED = 12;

  public static final short SIGNAL_MESSAGE = 13;

  public static final long TIMEOUT = 5000L;

  public static final byte STATE_AVAILABLE = 1;

  public static final byte STATE_LONESOME = 2;

  public static final byte STATE_AWAY = 3;

  public static final byte STATE_DO_NOT_DISTURB = 4;
}
