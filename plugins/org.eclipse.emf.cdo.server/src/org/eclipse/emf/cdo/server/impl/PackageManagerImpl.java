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


import org.eclipse.net4j.spring.impl.ServiceImpl;

import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.PackageInfo;
import org.eclipse.emf.cdo.server.PackageListener;
import org.eclipse.emf.cdo.server.PackageManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PackageManagerImpl extends ServiceImpl implements PackageManager
{
  protected Map<Integer, ClassInfo> cidToClassInfoMap = new HashMap<Integer, ClassInfo>(2111);

  protected Map<String, ClassInfo> nameToClassInfoMap = new HashMap<String, ClassInfo>(2111);

  protected Map<ClassInfo, List<ClassInfo>> subClassInfoMap;

  protected Map<String, PackageInfo> packages = new HashMap<String, PackageInfo>();

  protected List<PackageListener> listeners = new ArrayList<PackageListener>();

  public PackageInfo getPackageInfo(String name)
  {
    return packages.get(name);
  }

  public PackageInfo addPackage(int pid, String name)
  {
    PackageInfo packageInfo = new PackageInfoImpl(pid, name, this);
    packages.put(name, packageInfo);
    notifyPackageListeners(); // TODO Not useful here because classes are not registered yet
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
    subClassInfoMap = null;
  }

  public ClassInfo getClassInfo(int cid)
  {
    return cidToClassInfoMap.get(new Integer(cid));
  }

  public ClassInfo getClassInfo(String name)
  {
    return nameToClassInfoMap.get(name);
  }

  public List<ClassInfo> getSubClassInfos(ClassInfo base)
  {
    if (subClassInfoMap == null)
    {
      subClassInfoMap = new HashMap<ClassInfo, List<ClassInfo>>();
      Collection<ClassInfo> values = cidToClassInfoMap.values();
      ClassInfo[] array = (ClassInfo[]) values.toArray(new ClassInfo[values.size()]);
      for (int i = 0; i < array.length; i++)
      {
        ClassInfo parent = array[i];
        List<ClassInfo> subClasses = new ArrayList<ClassInfo>();
        for (int j = 0; j < array.length; j++)
        {
          ClassInfo derived = array[j];
          if (parent.isParentOf(derived))
          {
            subClasses.add(derived);
          }
        }

        subClassInfoMap.put(parent, subClasses);
      }
    }

    return subClassInfoMap.get(base);
  }

  protected void notifyPackageListeners()
  {
    for (Iterator<PackageListener> iter = listeners.iterator(); iter.hasNext();)
    {
      PackageListener listener = iter.next();
      listener.notifyAddedPackage();
    }
  }
}
