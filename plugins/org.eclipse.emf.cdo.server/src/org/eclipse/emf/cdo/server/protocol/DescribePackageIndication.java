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
import org.eclipse.emf.cdo.server.AttributeInfo;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.PackageInfo;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public class DescribePackageIndication extends IndicationWithResponse
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      DescribePackageIndication.class);

  private Mapper mapper;

  private List<ClassInfo> infos;

  public DescribePackageIndication(Mapper mapper)
  {
    this.mapper = mapper;
  }

  @Override
  protected short getSignalID()
  {
    return CDOSignals.DESCRIBE_PACKAGE;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    int pid = mapper.getNextPid();
    String packageName = in.readString();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Described package " + packageName);
    }

    PackageInfo packageInfo = mapper.getPackageManager().addPackage(pid, packageName);
    mapper.insertPackage(packageInfo);

    infos = receiveClasses(in, packageInfo);
    mapper.createAttributeTables(packageInfo);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(infos.size());
    for (Iterator<ClassInfo> iter = infos.iterator(); iter.hasNext();)
    {
      ClassInfo classInfo = iter.next();
      if (TRACER.isEnabled())
      {
        TRACER.trace("Responding class " + classInfo.getName() + " = " + classInfo.getCID());
      }

      out.writeInt(classInfo.getCID());
      out.writeString(classInfo.getName());
    }
  }

  private List<ClassInfo> receiveClasses(ExtendedDataInputStream in, PackageInfo packageInfo)
      throws IOException
  {
    List<ClassInfo> result = new ArrayList<ClassInfo>();
    int count = in.readInt();
    for (int i = 0; i < count; i++)
    {
      int cid = mapper.getNextCID();
      String name = in.readString();
      String parentName = in.readString();
      String tableName = in.readString();
      if (TRACER.isEnabled())
      {
        TRACER.trace("Described class " + name);
      }

      ClassInfo classInfo = packageInfo.addClass(cid, name, parentName, tableName);
      mapper.insertClass(classInfo);
      receiveAttributes(in, classInfo);

      result.add(classInfo);
    }

    return result;
  }

  private void receiveAttributes(ExtendedDataInputStream in, ClassInfo classInfo)
      throws IOException
  {
    int count = in.readInt();
    for (int i = 0; i < count; i++)
    {
      String name = in.readString();
      int featureId = in.readInt();
      int dataType = in.readInt();
      String columnName = in.readString();
      int columnType = in.readInt();
      if (TRACER.isEnabled())
      {
        TRACER.trace("Described attribute " + name);
      }

      AttributeInfo attributeInfo = classInfo.addAttribute(name, featureId, dataType, columnName,
          columnType);
      mapper.insertAttribute(attributeInfo, classInfo.getCID());
    }
  }
}
