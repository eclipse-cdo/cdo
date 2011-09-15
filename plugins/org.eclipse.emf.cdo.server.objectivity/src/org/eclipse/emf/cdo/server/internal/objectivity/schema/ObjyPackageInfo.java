/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import com.objy.db.app.ManyToOne;
import com.objy.db.app.Relationship;
import com.objy.db.app.ToOneRelationship;
import com.objy.db.app.ooObj;

public class ObjyPackageInfo extends ooObj
{

  protected String packageURI;

  protected String parentURI;

  protected String unitID;

  protected ToOneRelationship packageUnit;

  private String name;

  public static ManyToOne packageUnit_Relationship()
  {
    return new ManyToOne("packageUnit", // field name
        "org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyPackageUnit", // name of related class
        "packageInfos", // inverse relationship field name
        Relationship.COPY_DELETE, Relationship.VERSION_DELETE, false, false, Relationship.INLINE_NONE); // none-inline
                                                                                                        // association
  }

  /**
   * set parent packageUnit
   */
  public void setPackageUnit(ObjyPackageUnit objyPackageUnit)
  {
    markModified();
    packageUnit.form(objyPackageUnit);
  }

  /**
   * get related parent Node
   */
  public ObjyPackageUnit getPackageUnit()
  {
    fetch();
    return (ObjyPackageUnit)packageUnit.get();
  }

  /**
   * clear parent relationship
   */
  public void removePackageUnit()
  {
    markModified();
    packageUnit.clear();
  }

  public String getPackageURI()
  {
    fetch();
    return packageURI;
  }

  public void setPackageURI(String packageURI)
  {
    markModified();
    this.packageURI = packageURI;
  }

  public String getParentURI()
  {
    fetch();
    return parentURI;
  }

  public void setParentURI(String parentURI)
  {
    markModified();
    this.parentURI = parentURI;
  }

  public String getUnitID()
  {
    fetch();
    return unitID;
  }

  public void setUnitID(String unitID)
  {
    markModified();
    this.unitID = unitID;
  }

  public void setPackageName(String name)
  {
    markModified();
    this.name = name;
  }

  public String getPackageName()
  {
    fetch();
    return name;
  }

  // package unique name is Hash of the URI.
  public String getPackageUniqueName()
  {
    fetch();
    String uriHash = new Integer(Math.abs(getPackageURI().hashCode())).toString();
    return uriHash;
  }
}
