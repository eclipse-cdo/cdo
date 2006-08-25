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

  protected List<AttributeInfo> attributeInfos = new ArrayList<AttributeInfo>();

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

  public int getCID()
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
    for (Iterator<AttributeInfo> iter = attributeInfos.iterator(); iter.hasNext();)
    {
      AttributeInfo attributeInfo = iter.next();

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
    return attributeInfos.toArray(new AttributeInfo[attributeInfos.size()]);
  }

  public ClassInfo getParent()
  {
    if (cachedParent == null && parentName != null)
    {
      cachedParent = packageInfo.getPackageManager().getClassInfo(parentName);
    }

    return cachedParent;
  }

  public boolean isParentOf(ClassInfo derived)
  {
    if (derived == null)
    {
      return false;
    }

    ClassInfo parent = derived.getParent();
    return parent == this || isParentOf(parent);
  }

  public List<ClassInfo> getSubClasses()
  {
    return packageInfo.getPackageManager().getSubClassInfos(this);
  }

  public String getColumnNames()
  {
    if (cachedColumnNames == null)
    {
      StringBuffer buffer = new StringBuffer();
      boolean first = true;

      for (Iterator<AttributeInfo> iter = attributeInfos.iterator(); iter.hasNext();)
      {
        AttributeInfo attributeInfo = iter.next();

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
