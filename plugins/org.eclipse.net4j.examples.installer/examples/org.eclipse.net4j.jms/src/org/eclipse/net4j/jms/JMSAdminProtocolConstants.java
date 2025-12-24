/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms;

/**
 * @author Eike Stepper
 */
public interface JMSAdminProtocolConstants
{
  public static final String PROTOCOL_NAME = "jmsadmin"; //$NON-NLS-1$

  public static final short SIGNAL_CREATE_DESTINATION = 1;

  public static final byte DESTINATION_TYPE_QUEUE = JMSProtocolConstants.DESTINATION_TYPE_QUEUE;

  public static final byte DESTINATION_TYPE_TOPIC = JMSProtocolConstants.DESTINATION_TYPE_TOPIC;
}
