/**
 * <copyright>
 * </copyright>
 *
 * $Id: Model2FactoryImpl.java,v 1.4 2008-09-18 12:56:55 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;

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
public class Model2FactoryImpl extends EFactoryImpl implements Model2Factory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static Model2Factory init()
  {
    try
    {
      Model2Factory theModel2Factory = (Model2Factory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/tests/model2/1.0.0");
      if (theModel2Factory != null)
      {
        return theModel2Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Model2FactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model2FactoryImpl()
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
    case Model2Package.SPECIAL_PURCHASE_ORDER:
      return createSpecialPurchaseOrder();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public SpecialPurchaseOrder createSpecialPurchaseOrder()
  {
    SpecialPurchaseOrderImpl specialPurchaseOrder = new SpecialPurchaseOrderImpl();
    return specialPurchaseOrder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model2Package getModel2Package()
  {
    return (Model2Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model2Package getPackage()
  {
    return Model2Package.eINSTANCE;
  }

} // Model2FactoryImpl
