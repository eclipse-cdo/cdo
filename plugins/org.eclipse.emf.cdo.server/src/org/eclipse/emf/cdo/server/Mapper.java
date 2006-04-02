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


import org.eclipse.net4j.core.Channel;
import org.eclipse.net4j.spring.Service;

import org.eclipse.emf.cdo.core.OIDEncoder;


public interface Mapper extends Service
{
  public int getNextPid();

  public int getNextCid();

  public int getNextRid();

  public long getNextOid(int rid);

  public void sql(String sql);

  public void sql(String sql, Object[] args);

  public int getCollectionCount(long oid, int feature);

  public boolean lock(long oid, int oca);

  public void insertPackage(final PackageInfo packageInfo);

  public void insertClass(final ClassInfo classInfo);

  public void insertAttribute(final AttributeInfo attributeInfo, final int cid);

  public ResourceInfo createResource(String resourcePath);

  public ResourceInfo selectResourceInfo(String path);

  public ResourceInfo selectResourceInfo(int rid);

  public void insertReference(long oid, int feature, int ordinal, long target, boolean content);

  public void removeReference(long oid, int feature, int ordinal);

  public void moveReferenceAbsolute(long oid, int feature, int toIndex, int fromIndex);

  public void moveReferencesRelative(long oid, int feature, int startIndex, int endIndex, int offset);

  public void insertObject(long oid, int cid);

  public void insertContent(long oid);

  public void removeObject(long oid);

  public void removeContent(long oid);

  public void transmitContent(Channel channel, ResourceInfo resourceInfo);

  public void transmitObject(Channel channel, long oid);

  public void transmitAttributes(Channel channel, long oid, ClassInfo classInfo);

  public void transmitReferences(Channel channel, long oid);

  public void transmitAllResources(Channel channel);

  public void createAttributeTables(PackageInfo packageInfo);

  public void insertResource(int rid, String path);

  public OIDEncoder getOidEncoder();

  public PackageManager getPackageManager();

  public ResourceManager getResourceManager();

  public ColumnConverter getColumnConverter();

}