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


import org.eclipse.net4j.spring.impl.ServiceImpl;

import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.PackageInfo;
import org.eclipse.emf.cdo.server.PackageListener;
import org.eclipse.emf.cdo.server.PackageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PackageManagerImpl extends ServiceImpl implements PackageManager
{
  protected Map cidToClassInfoMap = new HashMap(2111);

  protected Map nameToClassInfoMap = new HashMap(2111);

  protected Map packages = new HashMap();

  protected List listeners = new ArrayList();

  public PackageInfo getPackageInfo(String name)
  {
    return (PackageInfo) packages.get(name);
  }

  public PackageInfo addPackage(int pid, String name)
  {
    PackageInfo packageInfo = new PackageInfoImpl(pid, name, this);
    packages.put(name, packageInfo);
    notifyPackageListeners();
    return packageInfo;
  }

  public void addPackageListener(PackageListener listener)
  {
    listeners.add(listener);
  }

  public void removePackageListener(PackageListener listener)
  {
    listeners.remove(listener);
  }

  public void registerClassInfo(ClassInfo classInfo)
  {
    cidToClassInfoMap.put(new Integer(classInfo.getCID()), classInfo);
    nameToClassInfoMap.put(classInfo.getName(), classInfo);
  }

  public ClassInfo getClassInfo(int cid)
  {
    return (ClassInfo) cidToClassInfoMap.get(new Integer(cid));
  }

  public ClassInfo getClassInfo(String name)
  {
    return (ClassInfo) nameToClassInfoMap.get(name);
  }

  protected void notifyPackageListeners()
  {
    for (Iterator iter = listeners.iterator(); iter.hasNext();)
    {
      PackageListener listener = (PackageListener) iter.next();
      listener.notifyAddedPackage();
    }
  }
}