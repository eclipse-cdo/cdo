/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
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


public class AttributeInfoImpl implements AttributeInfo
{
  protected String name;

  protected int featureID;

  protected int dataType;

  protected String columnName;

  protected int columnType;

  public AttributeInfoImpl(String name, int featureID, int dataType, String columnName,
      int columnType)
  {
    this.name = name;
    this.featureID = featureID;
    this.dataType = dataType;
    this.columnName = columnName;
    this.columnType = columnType;
  }

  public String getName()
  {
    return name;
  }

  public int getFeatureID()
  {
    return featureID;
  }

  public int getDataType()
  {
    return dataType;
  }

  public String getColumnName()
  {
    return columnName;
  }

  public int getColumnType()
  {
    return columnType;
  }
}
