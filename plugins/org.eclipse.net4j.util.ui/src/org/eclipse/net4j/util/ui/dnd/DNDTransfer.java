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
package org.eclipse.net4j.util.ui.dnd;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class DNDTransfer<TYPE> extends ByteArrayTransfer
{
  private String typeName;

  private int typeID;

  protected DNDTransfer(String typeName)
  {
    this.typeName = typeName;
    typeID = registerType(typeName);
  }

  @Override
  protected int[] getTypeIds()
  {
    return new int[] { typeID };
  }

  @Override
  protected String[] getTypeNames()
  {
    return new String[] { typeName };
  }

  @Override
  protected void javaToNative(Object object, TransferData transferData)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream out = new ExtendedDataOutputStream(baos);
    byte[] bytes = null;

    try
    {
      @SuppressWarnings("unchecked")
      TYPE typed = (TYPE)object;
      writeObject(out, typed);
      out.close();
      bytes = baos.toByteArray();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    if (bytes != null)
    {
      super.javaToNative(bytes, transferData);
    }
  }

  @Override
  protected Object nativeToJava(TransferData transferData)
  {
    byte[] bytes = (byte[])super.nativeToJava(transferData);
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    ExtendedDataInputStream in = new ExtendedDataInputStream(bais);

    try
    {
      return readObject(in);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return null;
    }
  }

  protected abstract void writeObject(ExtendedDataOutputStream out, TYPE object) throws IOException;

  protected abstract TYPE readObject(ExtendedDataInputStream in) throws IOException;
}
