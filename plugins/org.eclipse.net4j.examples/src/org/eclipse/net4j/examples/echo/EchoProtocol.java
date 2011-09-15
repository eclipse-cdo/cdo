/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.examples.echo;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface EchoProtocol
{
  public static final String PROTOCOL_NAME = "echo"; //$NON-NLS-1$

  public static final short ECHO_SIGNAL = 1;
}
