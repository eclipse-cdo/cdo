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
package org.eclipse.emf.cdo.server;


public interface ClassInfo
{
  public int getCid();

  public String getName();

  public String getParentName();

  public String getTableName();

  public PackageInfo getPackageInfo();

  public AttributeInfo addAttribute(String name, int featureID, int dataType, String columnName,
      int columnType);

  public AttributeInfo getAttributeInfo(int feature);

  public AttributeInfo[] getAttributeInfos();

  public ClassInfo getParent();

  public String getColumnNames();
}