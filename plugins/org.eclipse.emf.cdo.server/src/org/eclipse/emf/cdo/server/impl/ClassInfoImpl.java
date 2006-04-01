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
package org.eclipse.emf.cdo.server.impl;


import org.eclipse.emf.cdo.server.AttributeInfo;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.PackageInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ClassInfoImpl implements ClassInfo
{
  protected int cid;

  protected String name;

  protected String parentName;

  protected String tableName;

  protected PackageInfo packageInfo;

  protected List attributeInfos = new ArrayList();

  private ClassInfo cachedParent;

  private String cachedColumnNames;

  public ClassInfoImpl(int cid, String name, String parentName, String tableName,
      PackageInfo packageInfo)
  {
    this.cid = cid;
    this.cid = cid;
    this.name = name;
    this.parentName = parentName;
    this.tableName = tableName;
    this.packageInfo = packageInfo;
  }

  public int getCid()
  {
    return cid;
  }

  public String getName()
  {
    return name;
  }

  public String getParentName()
  {
    return parentName;
  }

  public String getTableName()
  {
    return tableName;
  }

  public PackageInfo getPackageInfo()
  {
    return packageInfo;
  }

  public AttributeInfo addAttribute(String name, int featureID, int dataType, String columnName,
      int columnType)
  {
    AttributeInfo attributeInfo = new AttributeInfoImpl(name, featureID, dataType, columnName,
        columnType);
    attributeInfos.add(attributeInfo);
    return attributeInfo;
  }

  public AttributeInfo getAttributeInfo(int feature)
  {
    for (Iterator iter = attributeInfos.iterator(); iter.hasNext();)
    {
      AttributeInfo attributeInfo = (AttributeInfo) iter.next();

      if (attributeInfo.getFeatureID() == feature)
      {
        return attributeInfo;
      }
    }

    ClassInfo parentInfo = getParent();
    if (parentInfo != null)
    {
      return parentInfo.getAttributeInfo(feature);
    }

    return null;
  }

  public AttributeInfo[] getAttributeInfos()
  {
    return (AttributeInfo[]) attributeInfos.toArray(new AttributeInfo[attributeInfos.size()]);
  }

  public ClassInfo getParent()
  {
    if (cachedParent == null && parentName != null)
    {
      cachedParent = packageInfo.getPackageManager().getClassInfo(parentName);
    }

    return cachedParent;
  }

  public String getColumnNames()
  {
    if (cachedColumnNames == null)
    {
      StringBuffer buffer = new StringBuffer();
      boolean first = true;

      for (Iterator iter = attributeInfos.iterator(); iter.hasNext();)
      {
        AttributeInfo attributeInfo = (AttributeInfo) iter.next();

        if (first)
        {
          first = false;
        }
        else
        {
          buffer.append(", ");
        }

        buffer.append(attributeInfo.getColumnName());
      }

      cachedColumnNames = buffer.toString();
    }

    return cachedColumnNames;
  }
}