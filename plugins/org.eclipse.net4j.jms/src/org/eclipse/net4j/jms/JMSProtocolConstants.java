/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms;

/**
 * @author Eike Stepper
 */
public interface JMSProtocolConstants
{
  public static final String PROTOCOL_NAME = "jms"; //$NON-NLS-1$

  // Signals

  public static final short SIGNAL_SYNC = 1;

  public static final short SIGNAL_LOGON = 2;

  public static final short SIGNAL_LOGOFF = 3;

  public static final short SIGNAL_OPEN_SESSION = 4;

  public static final short SIGNAL_CLOSE_SESSION = 5;

  public static final short SIGNAL_REGISTER_CONSUMER = 6;

  public static final short SIGNAL_DEREGISTER_CONSUMER = 7;

  public static final short SIGNAL_CLIENT_MESSAGE = 8;

  public static final short SIGNAL_SERVER_MESSAGE = 9;

  public static final short SIGNAL_ACKNOWLEDGE = 10;

  public static final short SIGNAL_RECOVER = 11;

  public static final short SIGNAL_COMMIT = 12;

  public static final short SIGNAL_ROLLBACK = 13;

  // Message Types

  public static final byte MESSAGE_TYPE_BYTES = 1;

  public static final byte MESSAGE_TYPE_MAP = 2;

  public static final byte MESSAGE_TYPE_OBJECT = 3;

  public static final byte MESSAGE_TYPE_STREAM = 4;

  public static final byte MESSAGE_TYPE_TEXT = 5;

  // Destination Types

  public static final byte DESTINATION_TYPE_NULL = 0;

  public static final byte DESTINATION_TYPE_QUEUE = 1;

  public static final byte DESTINATION_TYPE_TOPIC = 2;

  // Data Types

  public static final byte TYPE_BOOLEAN = 1;

  public static final byte TYPE_BYTE = 2;

  public static final byte TYPE_CHAR = 3;

  public static final byte TYPE_DOUBLE = 4;

  public static final byte TYPE_FLOAT = 5;

  public static final byte TYPE_LONG = 6;

  public static final byte TYPE_SHORT = 7;

  public static final byte TYPE_STRING = 8;
}
