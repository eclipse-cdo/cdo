/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.model.CDOClassInfoImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageInfoImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageUnitImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOModelUtil
{
  /**
   * @since 2.0
   */
  public static final String CORE_PACKAGE_URI = "http://www.eclipse.org/emf/2002/Ecore"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final String RESOURCE_PACKAGE_URI = "http://www.eclipse.org/emf/CDO/Eresource/2.0.0"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final String RESOURCE_NODE_CLASS_NAME = "CDOResourceNode"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final String RESOURCE_FOLDER_CLASS_NAME = "CDOResourceFolder"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final String RESOURCE_CLASS_NAME = "CDOResource"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final String ROOT_CLASS_NAME = "EObject"; //$NON-NLS-1$

  private static CDOType[] coreTypes;

  static
  {
    List<CDOType> types = new ArrayList<CDOType>();
    registerCoreType(types, EcorePackage.eINSTANCE.getEBigDecimal(), CDOType.BIG_DECIMAL);
    registerCoreType(types, EcorePackage.eINSTANCE.getEBigInteger(), CDOType.BIG_INTEGER);
    registerCoreType(types, EcorePackage.eINSTANCE.getEBooleanObject(), CDOType.BOOLEAN_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEBoolean(), CDOType.BOOLEAN);
    registerCoreType(types, EcorePackage.eINSTANCE.getEByteArray(), CDOType.BYTE_ARRAY);
    registerCoreType(types, EcorePackage.eINSTANCE.getEByteObject(), CDOType.BYTE_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEByte(), CDOType.BYTE);
    registerCoreType(types, EcorePackage.eINSTANCE.getECharacterObject(), CDOType.CHARACTER_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEChar(), CDOType.CHAR);
    registerCoreType(types, EcorePackage.eINSTANCE.getEDate(), CDOType.DATE);
    registerCoreType(types, EcorePackage.eINSTANCE.getEDoubleObject(), CDOType.DOUBLE_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEDouble(), CDOType.DOUBLE);
    registerCoreType(types, EcorePackage.eINSTANCE.getEFloatObject(), CDOType.FLOAT_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEFloat(), CDOType.FLOAT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEIntegerObject(), CDOType.INTEGER_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEInt(), CDOType.INT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEJavaClass(), null);
    registerCoreType(types, EcorePackage.eINSTANCE.getEJavaObject(), null);
    registerCoreType(types, EcorePackage.eINSTANCE.getELongObject(), CDOType.LONG_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getELong(), CDOType.LONG);
    registerCoreType(types, EcorePackage.eINSTANCE.getEShortObject(), CDOType.SHORT_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEShort(), CDOType.SHORT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEString(), CDOType.STRING);
    coreTypes = types.toArray(new CDOType[types.size()]);
  }

  private static void registerCoreType(List<CDOType> types, EClassifier classifier, CDOType type)
  {
    int index = classifier.getClassifierID();
    while (index >= types.size())
    {
      types.add(null);
    }

    types.set(index, type);
  }

  private CDOModelUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static boolean isCorePackage(EPackage ePackage)
  {
    return CORE_PACKAGE_URI.equals(ePackage.getNsURI());
  }

  /**
   * @since 2.0
   */
  public static boolean isResourcePackage(EPackage ePackage)
  {
    return RESOURCE_PACKAGE_URI.equals(ePackage.getNsURI());
  }

  /**
   * @since 2.0
   */
  public static boolean isSystemPackage(EPackage ePackage)
  {
    return isCorePackage(ePackage) || isResourcePackage(ePackage);
  }

  /**
   * @since 2.0
   */
  public static boolean isResource(EClass eClass)
  {
    return isResourcePackage(eClass.getEPackage()) && RESOURCE_CLASS_NAME.equals(eClass.getName());
  }

  /**
   * @since 2.0
   */
  public static boolean isResourceFolder(EClass eClass)
  {
    return isResourcePackage(eClass.getEPackage()) && RESOURCE_FOLDER_CLASS_NAME.equals(eClass.getName());
  }

  /**
   * @since 2.0
   */
  public static boolean isResourceNode(EClass eClass)
  {
    return isResourcePackage(eClass.getEPackage())
        && (RESOURCE_NODE_CLASS_NAME.equals(eClass.getName()) || RESOURCE_CLASS_NAME.equals(eClass.getName()) || RESOURCE_FOLDER_CLASS_NAME
            .equals(eClass.getName()));
  }

  /**
   * @since 2.0
   */
  public static boolean isRoot(EClass eClass)
  {
    return isCorePackage(eClass.getEPackage()) && ROOT_CLASS_NAME.equals(eClass.getName());
  }

  public static CDOType getType(int typeID)
  {
    CDOTypeImpl type = CDOTypeImpl.ids.get(typeID);
    if (type == null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOModelUtil.6"), typeID)); //$NON-NLS-1$
    }

    return type;
  }

  /**
   * @since 2.0
   */
  public static CDOType getType(EClassifier classifier)
  {
    if (classifier instanceof EClass)
    {
      return CDOType.OBJECT;
    }

    if (classifier instanceof EEnum)
    {
      return CDOType.ENUM;
    }

    if (isCorePackage(classifier.getEPackage()))
    {
      EDataType eDataType = (EDataType)classifier;
      CDOType type = getCoreType(eDataType);
      if (type != null)
      {
        return type;
      }
    }

    return CDOType.CUSTOM;
  }

  /**
   * Core types includes also complex data like EAnnotation, and EEnum
   * 
   * @since 2.0
   */
  public static CDOType getCoreType(EClassifier eDataType)
  {
    int index = eDataType.getClassifierID();
    if (0 <= index && index < coreTypes.length)
    {
      return coreTypes[index];
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static CDOType getPrimitiveType(Class<? extends Object> primitiveType)
  {
    if (primitiveType == String.class)
    {
      return CDOType.STRING;
    }

    if (primitiveType == Boolean.class)
    {
      return CDOType.BOOLEAN;
    }

    if (primitiveType == Integer.class)
    {
      return CDOType.INT;
    }

    if (primitiveType == Double.class)
    {
      return CDOType.DOUBLE;
    }

    if (primitiveType == Float.class)
    {
      return CDOType.FLOAT;
    }

    if (primitiveType == Long.class)
    {
      return CDOType.LONG;
    }

    if (primitiveType == Date.class)
    {
      return CDOType.DATE;
    }

    if (primitiveType == Byte.class)
    {
      return CDOType.BYTE;
    }

    if (primitiveType == Character.class)
    {
      return CDOType.CHAR;
    }

    throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOModelUtil.7"), primitiveType)); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public static CDOPackageInfo getPackageInfo(Object value, CDOPackageRegistry packageRegistry)
  {
    if (value instanceof EPackage)
    {
      return packageRegistry.getPackageInfo((EPackage)value);
    }

    if (value instanceof CDOPackageInfo)
    {
      CDOPackageInfo packageInfo = (CDOPackageInfo)value;
      if (packageInfo.getPackageUnit().getPackageRegistry() == packageRegistry)
      {
        return packageInfo;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static CDOClassInfo getClassInfo(EClass eClass)
  {
    synchronized (eClass)
    {
      EList<Adapter> adapters = eClass.eAdapters();
      CDOClassInfo classInfo = (CDOClassInfo)EcoreUtil.getAdapter(adapters, CDOClassInfo.class);
      if (classInfo == null)
      {
        classInfo = new CDOClassInfoImpl();
        adapters.add(classInfo);
      }

      return classInfo;
    }
  }

  /**
   * @since 2.0
   */
  public static EStructuralFeature[] getAllPersistentFeatures(EClass eClass)
  {
    CDOClassInfo classInfo = getClassInfo(eClass);
    return classInfo.getAllPersistentFeatures();
  }

  /**
   * @since 2.0
   */
  public static CDOPackageUnit createPackageUnit()
  {
    return new CDOPackageUnitImpl();
  }

  /**
   * @since 2.0
   */
  public static CDOPackageInfo createPackageInfo()
  {
    return new CDOPackageInfoImpl();
  }

  /**
   * @since 2.0
   */
  public static void writePackage(ExtendedDataOutput out, EPackage ePackage, boolean zipped,
      EPackage.Registry packageRegistry) throws IOException
  {
    byte[] bytes = EMFUtil.getEPackageBytes(ePackage, zipped, packageRegistry);
    out.writeString(ePackage.getNsURI());
    out.writeBoolean(zipped);
    out.writeByteArray(bytes);
  }

  /**
   * @since 2.0
   */
  public static EPackage readPackage(ExtendedDataInput in, EPackage.Registry packageRegistry) throws IOException
  {
    String uri = in.readString();
    boolean zipped = in.readBoolean();
    byte[] bytes = in.readByteArray();
    return EMFUtil.createEPackage(uri, bytes, zipped, packageRegistry);
  }
}
