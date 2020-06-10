/*
 * Copyright (c) 2010-2012, 2014, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 *
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class EtypesFactoryImpl extends EFactoryImpl implements EtypesFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static EtypesFactory init()
  {
    try
    {
      EtypesFactory theEtypesFactory = (EtypesFactory)EPackage.Registry.INSTANCE.getEFactory(EtypesPackage.eNS_URI);
      if (theEtypesFactory != null)
      {
        return theEtypesFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EtypesFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EtypesFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case EtypesPackage.ANNOTATION:
      return createAnnotation();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case EtypesPackage.BLOB:
      return createBlobFromString(eDataType, initialValue);
    case EtypesPackage.CLOB:
      return createClobFromString(eDataType, initialValue);
    case EtypesPackage.CLASSIFIER_REF:
      return createClassifierRefFromString(eDataType, initialValue);
    case EtypesPackage.BRANCH_REF:
      return createBranchRefFromString(eDataType, initialValue);
    case EtypesPackage.BRANCH_POINT_REF:
      return createBranchPointRefFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case EtypesPackage.BLOB:
      return convertBlobToString(eDataType, instanceValue);
    case EtypesPackage.CLOB:
      return convertClobToString(eDataType, instanceValue);
    case EtypesPackage.CLASSIFIER_REF:
      return convertClassifierRefToString(eDataType, instanceValue);
    case EtypesPackage.BRANCH_REF:
      return convertBranchRefToString(eDataType, instanceValue);
    case EtypesPackage.BRANCH_POINT_REF:
      return convertBranchPointRefToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Annotation createAnnotation()
  {
    AnnotationImpl annotation = new AnnotationImpl();
    return annotation;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.6
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOBlob createBlobFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      HexUtil.hexToBytes(new StringReader(initialValue), baos);
      return new CDOBlob(new ByteArrayInputStream(baos.toByteArray()));
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.6
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBlobToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }

    CDOBlob blob = (CDOBlob)instanceValue;

    try
    {
      InputStream inputStream = blob.getContents();
      StringWriter writer = new StringWriter();
      HexUtil.bytesToHex(inputStream, writer);
      return writer.toString();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.6
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOClob createClobFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    try
    {
      return new CDOClob(new StringReader(initialValue));
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.6
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertClobToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }

    CDOClob clob = (CDOClob)instanceValue;

    try
    {
      Reader reader = clob.getContents();
      StringWriter writer = new StringWriter();
      IOUtil.copyCharacter(reader, writer);
      return writer.toString();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.10
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOClassifierRef createClassifierRefFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : new CDOClassifierRef(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.10
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertClassifierRefToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : ((CDOClassifierRef)instanceValue).getURI();
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.10
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOBranchRef createBranchRefFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : new CDOBranchRef(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.10
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBranchRefToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : ((CDOBranchRef)instanceValue).getBranchPath();
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.10
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOBranchPointRef createBranchPointRefFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : new CDOBranchPointRef(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.10
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertBranchPointRefToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : ((CDOBranchPointRef)instanceValue).getURI();
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.1
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public CDOLob<?> createLobFromString(EDataType eDataType, String initialValue)
  {
    return (CDOLob<?>)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EtypesPackage getEtypesPackage()
  {
    return (EtypesPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EtypesPackage getPackage()
  {
    return EtypesPackage.eINSTANCE;
  }

} // EtypesFactoryImpl
