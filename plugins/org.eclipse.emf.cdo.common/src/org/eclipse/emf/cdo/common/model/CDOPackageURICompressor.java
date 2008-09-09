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
package org.eclipse.emf.cdo.common.model;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOPackageURICompressor
{
  public static final CDOPackageURICompressor UNCOMPRESSED = new CDOPackageURICompressor()
  {
    public String readPackageURI(ExtendedDataInput in) throws IOException
    {
      return in.readString();
    }

    public void writePackageURI(ExtendedDataOutput out, String uri) throws IOException
    {
      out.writeString(uri);
    }

    @Override
    public String toString()
    {
      return "UNCOMPRESSED";
    }
  };

  public void writePackageURI(ExtendedDataOutput out, String uri) throws IOException;

  public String readPackageURI(ExtendedDataInput in) throws IOException;
}
