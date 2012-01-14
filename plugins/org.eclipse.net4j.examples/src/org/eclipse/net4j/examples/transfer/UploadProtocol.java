/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.examples.transfer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface UploadProtocol
{
  public static final String PROTOCOL_NAME = "upload"; //$NON-NLS-1$

  public static final short UPLOAD_SIGNAL_ID = 1;

  public static final int BUFFER_SIZE = 8192;
}
