/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.security.CombinedFilter;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.util.Iterator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Combined Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.CombinedFilterImpl#getOperands <em>Operands</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class CombinedFilterImpl extends PermissionFilterImpl implements CombinedFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CombinedFilterImpl()
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
    return SecurityPackage.Literals.COMBINED_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<PermissionFilter> getOperands()
  {
    return (EList<PermissionFilter>)eGet(SecurityPackage.Literals.COMBINED_FILTER__OPERANDS, true);
  }

  public String format()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(formatOperator());

    EList<PermissionFilter> operands = getOperands();
    if (!operands.isEmpty())
    {
      builder.append("(");
      Iterator<PermissionFilter> iterator = operands.iterator();
      builder.append(iterator.next().format());

      while (iterator.hasNext())
      {
        builder.append(", ");
        builder.append(iterator.next().format());
      }

      builder.append(")");
    }

    return builder.toString();
  }

  protected abstract String formatOperator();

} // CombinedFilterImpl
