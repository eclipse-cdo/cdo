/**
 * <copyright>
 * </copyright>
 *
 * $Id: EtypesFactoryImpl.java,v 1.3 2010-11-12 17:37:25 estepper Exp $
 */
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.common.model.lob.CDOBlob;
import org.eclipse.emf.cdo.common.model.lob.CDOClob;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>.
 * 
 * @since 4.0 <!-- end-user-doc -->
 * @generated
 */
public class EtypesFactoryImpl extends EFactoryImpl implements EtypesFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static EtypesFactory init()
  {
    try
    {
      EtypesFactory theEtypesFactory = (EtypesFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/Etypes/4.0.0"); //$NON-NLS-1$ 
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
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EtypesFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOBlob createBlobFromString(EDataType eDataType, String initialValue)
  {
    return (CDOBlob)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String convertBlobToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOClob createClobFromString(EDataType eDataType, String initialValue)
  {
    return (CDOClob)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String convertClobToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EtypesPackage getEtypesPackage()
  {
    return (EtypesPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EtypesPackage getPackage()
  {
    return EtypesPackage.eINSTANCE;
  }

} // EtypesFactoryImpl
