/**
 * <copyright>
 * </copyright>
 *
 * $Id: InterfaceFactoryImpl.java,v 1.1.2.2 2008-07-02 14:10:02 estepper Exp $
 */
package interface_.impl;

import interface_.*;

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
public class InterfaceFactoryImpl extends EFactoryImpl implements InterfaceFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static InterfaceFactory init()
  {
    try
    {
      InterfaceFactory theInterfaceFactory = (InterfaceFactory)EPackage.Registry.INSTANCE
          .getEFactory("uuid://interface");
      if (theInterfaceFactory != null)
      {
        return theInterfaceFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new InterfaceFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InterfaceFactoryImpl()
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
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InterfacePackage getInterfacePackage()
  {
    return (InterfacePackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static InterfacePackage getPackage()
  {
    return InterfacePackage.eINSTANCE;
  }

} //InterfaceFactoryImpl
