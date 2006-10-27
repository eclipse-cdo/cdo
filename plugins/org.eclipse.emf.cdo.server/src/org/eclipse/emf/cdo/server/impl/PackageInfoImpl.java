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


import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.PackageInfo;
import org.eclipse.emf.cdo.server.PackageManager;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Eike Stepper
 */
public class PackageInfoImpl implements PackageInfo
{
  protected int pid;

  protected String name;

  protected PackageManager packageManager;

  protected List<ClassInfo> classes = new ArrayList<ClassInfo>();

  public PackageInfoImpl(int pid, String name, PackageManager packageManager)
  {
    this.pid = pid;
    this.name = name;
    this.packageManager = packageManager;
  }

  public PackageManager getPackageManager()
  {
    return packageManager;
  }

  public int getPid()
  {
    return pid;
  }

  public String getName()
  {
    return name;
  }

  public ClassInfo addClass(int cid, String name, String parentName, String tableName)
  {
    ClassInfo classInfo = new ClassInfoImpl(cid, name, parentName, tableName, this);
    classes.add(classInfo);
    packageManager.registerClassInfo(classInfo);
    return classInfo;
  }

  public ClassInfo[] getClasses()
  {
    return classes.toArray(new ClassInfo[classes.size()]);
  }
}
