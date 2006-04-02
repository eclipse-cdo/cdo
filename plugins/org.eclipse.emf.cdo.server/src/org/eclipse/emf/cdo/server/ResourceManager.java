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
package org.eclipse.emf.cdo.server;


import org.eclipse.net4j.spring.Service;


public interface ResourceManager extends Service
{
  public void registerResourceInfo(ResourceInfo resourceInfo);

  public ResourceInfo registerResourceInfo(String resourcePath, int rid, long nextOIDFragment);

  public ResourceInfo getResourceInfo(int rid, Mapper mapper);

  public ResourceInfo getResourceInfo(String path, Mapper mapper);
}
