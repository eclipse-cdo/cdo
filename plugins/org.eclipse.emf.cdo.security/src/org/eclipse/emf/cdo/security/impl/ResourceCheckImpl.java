/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.security.ResourceCheck;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Check</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ResourceCheckImpl#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ResourceCheckImpl extends CheckImpl implements ResourceCheck
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ResourceCheckImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SecurityPackage.Literals.RESOURCE_CHECK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPattern()
  {
    return (String)eGet(SecurityPackage.Literals.RESOURCE_CHECK__PATTERN, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPattern(String newPattern)
  {
    eSet(SecurityPackage.Literals.RESOURCE_CHECK__PATTERN, newPattern);
  }

} // ResourceCheckImpl
