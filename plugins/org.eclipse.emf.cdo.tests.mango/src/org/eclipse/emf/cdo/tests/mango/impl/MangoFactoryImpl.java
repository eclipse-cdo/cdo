/**
 * <copyright>
 * </copyright>
 *
 * $Id: MangoFactoryImpl.java,v 1.2 2008-02-23 10:00:34 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.mango.impl;

import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.mango.Value;
import org.eclipse.emf.cdo.tests.mango.ValueList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class MangoFactoryImpl extends EFactoryImpl implements MangoFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static MangoFactory init()
  {
    try
    {
      MangoFactory theMangoFactory = (MangoFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eiswind.de/mango");
      if (theMangoFactory != null)
      {
        return theMangoFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new MangoFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public MangoFactoryImpl()
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
    case MangoPackage.VALUE_LIST:
      return (EObject)createValueList();
    case MangoPackage.VALUE:
      return (EObject)createValue();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ValueList createValueList()
  {
    ValueListImpl valueList = new ValueListImpl();
    return valueList;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Value createValue()
  {
    ValueImpl value = new ValueImpl();
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public MangoPackage getMangoPackage()
  {
    return (MangoPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static MangoPackage getPackage()
  {
    return MangoPackage.eINSTANCE;
  }

} // MangoFactoryImpl
