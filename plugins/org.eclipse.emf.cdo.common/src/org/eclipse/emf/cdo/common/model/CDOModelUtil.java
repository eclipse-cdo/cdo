/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.internal.common.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOFeature;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public final class CDOModelUtil
{
  private CDOModelUtil()
  {
  }

  // ///////////////////////////////////////////////

  public static CDOType getType(int typeID)
  {
    CDOTypeImpl type = CDOTypeImpl.ids.get(typeID);
    if (type == null)
    {
      throw new IllegalStateException("No type for id " + typeID);
    }

    return type;
  }

  public static CDOType readType(ExtendedDataInput in) throws IOException
  {
    int typeID = in.readInt();
    return getType(typeID);
  }

  public static void writeType(ExtendedDataOutput out, CDOType type) throws IOException
  {
    ((CDOTypeImpl)type).write(out);
  }

  // ///////////////////////////////////////////////

  public static CDOPackage createPackage(CDOPackageManager packageManager, String packageURI, String name,
      String ecore, boolean dynamic, CDOIDMetaRange metaIDRange, String parentURI)
  {
    return new CDOPackageImpl(packageManager, packageURI, name, ecore, dynamic, metaIDRange, parentURI);
  }

  public static CDOPackage createProxyPackage(CDOPackageManager packageManager, String packageURI, boolean dynamic,
      CDOIDMetaRange metaIDRange, String parentURI)
  {
    return new CDOPackageImpl(packageManager, packageURI, dynamic, metaIDRange, parentURI);
  }

  public static CDOPackage readPackage(CDOPackageManager packageManager, ExtendedDataInput in) throws IOException
  {
    return new CDOPackageImpl(packageManager, in);
  }

  public static void writePackage(ExtendedDataOutput out, CDOPackage cdoPackage) throws IOException
  {
    ((CDOPackageImpl)cdoPackage).write(out);
  }

  // ///////////////////////////////////////////////

  public static CDOClass createClass(CDOPackage containingPackage, int classifierID, String name, boolean isAbstract)
  {
    return new CDOClassImpl(containingPackage, classifierID, name, isAbstract);
  }

  public static CDOClass readClass(CDOPackage containingPackage, ExtendedDataInput in) throws IOException
  {
    return new CDOClassImpl(containingPackage, in);
  }

  public static void writeClass(ExtendedDataOutput out, CDOClass cdoClass) throws IOException
  {
    ((InternalCDOClass)cdoClass).write(out);
  }

  // ///////////////////////////////////////////////

  public static CDOFeature createAttribute(CDOClass containingClass, int featureID, String name, CDOType type,
      boolean many)
  {
    return new CDOFeatureImpl(containingClass, featureID, name, type, many);
  }

  public static CDOFeature createReference(CDOClass containingClass, int featureID, String name,
      CDOClassProxy referenceType, boolean many, boolean containment)
  {
    return new CDOFeatureImpl(containingClass, featureID, name, referenceType, many, containment);
  }

  public static CDOFeature readFeature(CDOClass containingClass, ExtendedDataInput in) throws IOException
  {
    return new CDOFeatureImpl(containingClass, in);
  }

  public static void writeFeature(ExtendedDataOutput out, CDOFeature cdoFeature) throws IOException
  {
    ((InternalCDOFeature)cdoFeature).write(out);
  }

  // ///////////////////////////////////////////////

  public static CDOClassRef createClassRef(String packageURI, int classifierID)
  {
    return new CDOClassRefImpl(packageURI, classifierID);
  }

  public static CDOClassRef readClassRef(ExtendedDataInput in, String defaultURI) throws IOException
  {
    return new CDOClassRefImpl(in, defaultURI);
  }

  public static CDOClassRef readClassRef(ExtendedDataInput in) throws IOException
  {
    return readClassRef(in, null);
  }

  public static void writeClassRef(ExtendedDataOutput out, CDOClassRef classRef, String defaultURI) throws IOException
  {
    ((CDOClassRefImpl)classRef).write(out, defaultURI);
  }

  public static void writeClassRef(ExtendedDataOutput out, CDOClassRef classRef) throws IOException
  {
    writeClassRef(out, classRef, null);
  }
}
