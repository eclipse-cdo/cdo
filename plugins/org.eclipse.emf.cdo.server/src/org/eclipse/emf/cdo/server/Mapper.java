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


import org.eclipse.net4j.util.stream.ExtendedDataOutput;

import org.eclipse.emf.cdo.core.OID;
import org.eclipse.emf.cdo.core.OIDEncoder;
import org.eclipse.emf.cdo.core.RID;

import java.util.Set;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public interface Mapper
{
  public int getNextPid();

  public int getNextCID();

  public int getNextRID();

  public long getNextOID(int rid);

  public void sql(String sql);

  public void sql(String sql, Object[] args);

  public void sql(String sql, Object[] args, int[] types);

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

  public void transmitContent(ExtendedDataOutput out, ResourceInfo resourceInfo) throws IOException;

  public void transmitObject(ExtendedDataOutput out, long oid) throws IOException;

  public void transmitAttributes(ExtendedDataOutput out, long oid, ClassInfo classInfo)
      throws IOException;

  public void transmitReferences(ExtendedDataOutput out, long oid) throws IOException;

  public void transmitAllResources(ExtendedDataOutput out) throws IOException;

  public void transmitExtent(ExtendedDataOutput out, int cid, boolean exactMatch, int rid)
      throws IOException;

  public void transmitXRefs(ExtendedDataOutput out, long oid, int rid) throws IOException;

  /**
   * Deletes the given resource within a transaction.<p>
   *
   * @param rid The {@link RID} of the resource to be deleted.<p>
   */
  public void deleteResource(int rid);

  /**
   * Removes all references with missing target objects.<p>
   * 
   * @return The {@link Set} of modified {@link OID}s.<p>
   */
  public Set<Long> removeStaleReferences();

  public void createAttributeTables(PackageInfo packageInfo);

  public void insertResource(int rid, String path);

  public OIDEncoder getOidEncoder(); // Don't change case! Spring will be irritated

  public PackageManager getPackageManager();

  public ResourceManager getResourceManager();

  public ColumnConverter getColumnConverter();
}
