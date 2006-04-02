/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.server.AttributeInfo;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.PackageInfo;
import org.eclipse.emf.cdo.server.ServerCDOProtocol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DescribePackageIndication extends AbstractIndicationWithResponse
{
  private List infos;

  public short getSignalId()
  {
    return CDOProtocol.DESCRIBE_PACKAGE;
  }

  public void indicate()
  {
    int pid = getMapper().getNextPid();
    String packageName = receiveString();
    if (isDebugEnabled()) debug("Described package " + packageName);

    PackageInfo packageInfo = getMapper().getPackageManager().addPackage(pid, packageName);
    getMapper().insertPackage(packageInfo);

    infos = receiveClasses(packageInfo);
    getMapper().createAttributeTables(packageInfo);
  }

  public void respond()
  {
    transmitInt(infos.size());

    for (Iterator iter = infos.iterator(); iter.hasNext();)
    {
      ClassInfo classInfo = (ClassInfo) iter.next();
      if (isDebugEnabled())
        debug("Responding class " + classInfo.getName() + " = " + classInfo.getCid());

      transmitInt(classInfo.getCid());
      transmitString(classInfo.getName());
    }
  }

  private List receiveClasses(PackageInfo packageInfo)
  {
    List result = new ArrayList();
    int count = receiveInt();

    for (int i = 0; i < count; i++)
    {
      int cid = getMapper().getNextCid();
      String name = receiveString();
      String parentName = receiveString();
      String tableName = receiveString();
      if (isDebugEnabled()) debug("Described class " + name);

      ClassInfo classInfo = packageInfo.addClass(cid, name, parentName, tableName);
      getMapper().insertClass(classInfo);
      receiveAttributes(classInfo);

      result.add(classInfo);
    }

    return result;
  }

  private void receiveAttributes(ClassInfo classInfo)
  {
    int count = receiveInt();
    for (int i = 0; i < count; i++)
    {
      String name = receiveString();
      int featureId = receiveInt();
      int dataType = receiveInt();
      String columnName = receiveString();
      int columnType = receiveInt();
      if (isDebugEnabled()) debug("Described attribute " + name);

      AttributeInfo attributeInfo = classInfo.addAttribute(name, featureId, dataType, columnName,
          columnType);
      getMapper().insertAttribute(attributeInfo, classInfo.getCid());
    }
  }

  private Mapper getMapper()
  {
    return ((ServerCDOProtocol) getProtocol()).getMapper();
  }
}
