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
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;

import java.io.DataInput;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public interface ExtendedDataInput extends DataInput
{
  public byte[] readByteArray() throws IOException;

  public Object readObject() throws IOException;

  public Object readObject(ClassLoader classLoader) throws IOException;

  public Object readObject(ClassResolver classResolver) throws IOException;

  public String readString() throws IOException;
}
