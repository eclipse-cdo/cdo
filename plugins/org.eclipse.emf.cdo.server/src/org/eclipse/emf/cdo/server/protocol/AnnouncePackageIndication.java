/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.om.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import org.eclipse.emf.cdo.core.CDOSignals;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.PackageInfo;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public class AnnouncePackageIndication extends IndicationWithResponse
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      AnnouncePackageIndication.class);

  private Mapper mapper;

  private String packageName;

  public AnnouncePackageIndication(Mapper mapper)
  {
    this.mapper = mapper;
  }

  @Override
  protected short getSignalID()
  {
    return CDOSignals.ANNOUNCE_PACKAGE;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    packageName = in.readString();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Announced package " + packageName);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    PackageInfo packageInfo = mapper.getPackageManager().getPackageInfo(packageName);
    if (packageInfo == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Unknown package " + packageName);
      }

      out.writeInt(-1);
    }
    else
    {
      ClassInfo[] classInfos = packageInfo.getClasses();
      out.writeInt(classInfos.length);
      for (int i = 0; i < classInfos.length; i++)
      {
        ClassInfo classInfo = classInfos[i];
        if (TRACER.isEnabled())
        {
          TRACER.trace("Responding class " + classInfo.getName() + " = " + classInfo.getCID());
        }

        out.writeInt(classInfo.getCID());
        out.writeString(classInfo.getName());
      }
    }
  }
}
