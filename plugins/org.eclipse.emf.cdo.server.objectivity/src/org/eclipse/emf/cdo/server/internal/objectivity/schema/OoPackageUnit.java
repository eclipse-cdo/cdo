/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import com.objy.db.app.OneToMany;
import com.objy.db.app.Relationship;
import com.objy.db.app.ToManyRelationship;
import com.objy.db.app.ooObj;

import java.util.ArrayList;
import java.util.List;

public class OoPackageUnit extends ooObj
{

  protected String id;

  protected int ordinal;

  protected long timeStamp;

  protected byte[] packageAsBytes;

  private ToManyRelationship packageInfos;

  public OoPackageUnit(int bufferSize)
  {
    packageAsBytes = new byte[bufferSize];
  }

  public static OneToMany packageInfos_Relationship()
  {
    return new OneToMany("packageInfos", // field name
        "org.eclipse.emf.cdo.server.internal.objectivity.schema.OoPackageInfo", // name of related class
        "packageUnit", // inverse relationship field name
        Relationship.COPY_DELETE, Relationship.VERSION_DELETE, false, false, Relationship.INLINE_NONE); // none-inline
                                                                                                        // association
  }

  /**
   * get all related children
   * 
   * @return list of OoPackageInfo objects.
   */
  public List<OoPackageInfo> getPackageInfos()
  {
    List<OoPackageInfo> list = new ArrayList<OoPackageInfo>();
    fetch();
    com.objy.db.app.Iterator itr;
    itr = packageInfos.scan();
    while (itr.hasNext())
    {
      list.add((OoPackageInfo)itr.next());
    }

    return list;
  }

  /**
   * add packageInfo
   * 
   * @param packgeInfo
   */
  public void addPackageInfo(OoPackageInfo packageInfo)
  {
    markModified();
    packageInfos.add(packageInfo);
  }

  /**
   * clear all related packageInfo
   */
  public void clearChildren()
  {
    markModified();
    packageInfos.clear();
  }

  /**
   * removePackageInfo.
   * 
   * @param node
   *          child Node to be dropped
   */
  public void removePackageInfo(OoPackageInfo packageInfo)
  {
    markModified();
    packageInfos.remove(packageInfo);
  }

  public String getId()
  {
    fetch();
    return id;
  }

  public void setId(String id)
  {
    markModified();
    this.id = id;
  }

  public int getOrdinal()
  {
    fetch();
    return ordinal;
  }

  public void setOrdinal(int ordinal)
  {
    markModified();
    this.ordinal = ordinal;
  }

  public long getTimeStamp()
  {
    fetch();
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    markModified();
    this.timeStamp = timeStamp;
  }

  public byte[] getPackageAsBytes()
  {
    fetch();
    return packageAsBytes;
  }

  public void setPackageAsBytes(byte[] packageAsBytes)
  {
    markModified();
    this.packageAsBytes = packageAsBytes;
  }

}
