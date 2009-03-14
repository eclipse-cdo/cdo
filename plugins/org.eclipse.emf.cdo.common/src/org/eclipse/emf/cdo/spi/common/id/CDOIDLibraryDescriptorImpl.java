/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.id;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.internal.common.bundle.OM;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOIDLibraryDescriptorImpl implements CDOIDLibraryDescriptor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDOIDLibraryDescriptorImpl.class);

  private String factoryName;

  private String[] libraryNames;

  public CDOIDLibraryDescriptorImpl(String factoryName, String[] libraryNames)
  {
    this.factoryName = factoryName;
    this.libraryNames = libraryNames == null ? new String[0] : libraryNames;
  }

  public CDOIDLibraryDescriptorImpl(ExtendedDataInput in) throws IOException
  {
    factoryName = in.readString();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read factory name: {0}", factoryName);
    }

    int size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} library names", size);
    }

    libraryNames = new String[size];
    for (int i = 0; i < size; i++)
    {
      libraryNames[i] = in.readString();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read library name: {0}", libraryNames[i]);
      }
    }
  }

  public String getFactoryName()
  {
    return factoryName;
  }

  public String[] getLibraryNames()
  {
    return libraryNames;
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing factory name: {0}", factoryName);
    }

    out.writeString(factoryName);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} library names", libraryNames.length);
    }

    out.writeInt(libraryNames.length);
    for (String libraryName : libraryNames)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing library name: {0}", libraryName);
      }

      out.writeString(libraryName);
    }
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(getClass().getSimpleName());
    builder.append("[");
    builder.append(factoryName);
    if (libraryNames.length != 0)
    {
      builder.append(" -> ");
      for (int i = 0; i < libraryNames.length; i++)
      {
        if (i != 0)
        {
          builder.append(", ");
        }

        builder.append(libraryNames[i]);
      }
    }

    builder.append("]");
    return builder.toString();
  }
}
