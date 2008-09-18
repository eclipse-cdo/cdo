/**
 * <copyright>
 * </copyright>
 *
 * $Id: Model3FactoryImpl.java,v 1.4 2008-09-18 12:57:20 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;

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
public class Model3FactoryImpl extends EFactoryImpl implements Model3Factory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static Model3Factory init()
  {
    try
    {
      Model3Factory theModel3Factory = (Model3Factory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/tests/model3/1.0.0");
      if (theModel3Factory != null)
      {
        return theModel3Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Model3FactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model3FactoryImpl()
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
    case Model3Package.CLASS1:
      return createClass1();
    case Model3Package.META_REF:
      return createMetaRef();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Class1 createClass1()
  {
    Class1Impl class1 = new Class1Impl();
    return class1;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public MetaRef createMetaRef()
  {
    MetaRefImpl metaRef = new MetaRefImpl();
    return metaRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model3Package getModel3Package()
  {
    return (Model3Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model3Package getPackage()
  {
    return Model3Package.eINSTANCE;
  }

} // Model3FactoryImpl
