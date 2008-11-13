/**
 * <copyright>
 * </copyright>
 *
 * $Id: Model5FactoryImpl.java,v 1.2 2008-11-13 14:48:46 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model5.impl;

import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.GenListOfBoolean;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.GenListOfInteger;
import org.eclipse.emf.cdo.tests.model5.GenListOfLong;
import org.eclipse.emf.cdo.tests.model5.GenListOfString;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.Model5Factory;
import org.eclipse.emf.cdo.tests.model5.Model5Package;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;

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
public class Model5FactoryImpl extends EFactoryImpl implements Model5Factory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static Model5Factory init()
  {
    try
    {
      Model5Factory theModel5Factory = (Model5Factory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/tests/model5/1.0.0");
      if (theModel5Factory != null)
      {
        return theModel5Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Model5FactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model5FactoryImpl()
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
    case Model5Package.TEST_FEATURE_MAP:
      return createTestFeatureMap();
    case Model5Package.MANAGER:
      return createManager();
    case Model5Package.DOCTOR:
      return createDoctor();
    case Model5Package.GEN_LIST_OF_STRING:
      return createGenListOfString();
    case Model5Package.GEN_LIST_OF_INT:
      return createGenListOfInt();
    case Model5Package.GEN_LIST_OF_INTEGER:
      return createGenListOfInteger();
    case Model5Package.GEN_LIST_OF_LONG:
      return createGenListOfLong();
    case Model5Package.GEN_LIST_OF_BOOLEAN:
      return createGenListOfBoolean();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TestFeatureMap createTestFeatureMap()
  {
    TestFeatureMapImpl testFeatureMap = new TestFeatureMapImpl();
    return testFeatureMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Manager createManager()
  {
    ManagerImpl manager = new ManagerImpl();
    return manager;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Doctor createDoctor()
  {
    DoctorImpl doctor = new DoctorImpl();
    return doctor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public GenListOfInt createGenListOfInt()
  {
    GenListOfIntImpl genListOfInt = new GenListOfIntImpl();
    return genListOfInt;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public GenListOfInteger createGenListOfInteger()
  {
    GenListOfIntegerImpl genListOfInteger = new GenListOfIntegerImpl();
    return genListOfInteger;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public GenListOfLong createGenListOfLong()
  {
    GenListOfLongImpl genListOfLong = new GenListOfLongImpl();
    return genListOfLong;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public GenListOfBoolean createGenListOfBoolean()
  {
    GenListOfBooleanImpl genListOfBoolean = new GenListOfBooleanImpl();
    return genListOfBoolean;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public GenListOfString createGenListOfString()
  {
    GenListOfStringImpl genListOfString = new GenListOfStringImpl();
    return genListOfString;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model5Package getModel5Package()
  {
    return (Model5Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model5Package getPackage()
  {
    return Model5Package.eINSTANCE;
  }

} // Model5FactoryImpl
