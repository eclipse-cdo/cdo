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
package org.eclipse.emf.cdo.server;


import org.eclipse.net4j.spring.Service;

import java.util.List;


public interface PackageManager extends Service
{
  public void addPackageListener(PackageListener listener);

  public void removePackageListener(PackageListener listener);

  public PackageInfo addPackage(int pid, String name);

  public PackageInfo getPackageInfo(String name);

  public void registerClassInfo(ClassInfo classInfo);

  public ClassInfo getClassInfo(int cid);

  public ClassInfo getClassInfo(String name);

  public List<ClassInfo> getSubClassInfos(ClassInfo base);
}
