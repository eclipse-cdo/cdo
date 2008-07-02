/**
 * <copyright>
 * </copyright>
 *
 * $Id: DerivedFactoryImpl.java,v 1.1.2.2 2008-07-02 14:10:02 estepper Exp $
 */
package derived.impl;

import derived.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DerivedFactoryImpl extends EFactoryImpl implements DerivedFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static DerivedFactory init()
  {
    try
    {
      DerivedFactory theDerivedFactory = (DerivedFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.fernuni-hagen.de/ST/dummy/derived.ecore");
      if (theDerivedFactory != null)
      {
        return theDerivedFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new DerivedFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DerivedFactoryImpl()
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
    case DerivedPackage.DERIVED_CLASS:
      return (EObject)createDerivedClass();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DerivedClass createDerivedClass()
  {
    DerivedClassImpl derivedClass = new DerivedClassImpl();
    return derivedClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DerivedPackage getDerivedPackage()
  {
    return (DerivedPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static DerivedPackage getPackage()
  {
    return DerivedPackage.eINSTANCE;
  }

} //DerivedFactoryImpl
