/**
 * <copyright>
 * </copyright>
 *
 * $Id: TestDefImpl.java,v 1.1 2008-12-30 08:43:08 estepper Exp $
 */
package org.eclipse.net4j.util.tests.defs.impl;

import org.eclipse.net4j.util.net4jutildefs.Def;
import org.eclipse.net4j.util.net4jutildefs.impl.DefImpl;
import org.eclipse.net4j.util.tests.defs.DefsPackage;
import org.eclipse.net4j.util.tests.defs.TestDef;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.tests.defs.impl.TestDefImpl#getReferences <em>References</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.tests.defs.impl.TestDefImpl#getAttribute <em>Attribute</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestDefImpl extends DefImpl implements TestDef {
	/**
   * The cached value of the '{@link #getReferences() <em>References</em>}' reference list.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @see #getReferences()
   * @generated
   * @ordered
   */
	protected EList<Def> references;

	/**
   * The default value of the '{@link #getAttribute() <em>Attribute</em>}' attribute.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @see #getAttribute()
   * @generated
   * @ordered
   */
	protected static final String ATTRIBUTE_EDEFAULT = null;
	/**
   * The cached value of the '{@link #getAttribute() <em>Attribute</em>}' attribute.
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @see #getAttribute()
   * @generated
   * @ordered
   */
	protected String attribute = ATTRIBUTE_EDEFAULT;

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	protected TestDefImpl() {
    super();
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	@Override
	protected EClass eStaticClass() {
    return DefsPackage.Literals.TEST_DEF;
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public EList<Def> getReferences() {
    if (references == null)
    {
      references = new EObjectResolvingEList<Def>(Def.class, this, DefsPackage.TEST_DEF__REFERENCES);
    }
    return references;
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public String getAttribute() {
    return attribute;
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	public void setAttribute(String newAttribute) {
    String oldAttribute = attribute;
    attribute = newAttribute;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DefsPackage.TEST_DEF__ATTRIBUTE, oldAttribute, attribute));
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID)
    {
      case DefsPackage.TEST_DEF__REFERENCES:
        return getReferences();
      case DefsPackage.TEST_DEF__ATTRIBUTE:
        return getAttribute();
    }
    return super.eGet(featureID, resolve, coreType);
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
    switch (featureID)
    {
      case DefsPackage.TEST_DEF__REFERENCES:
        getReferences().clear();
        getReferences().addAll((Collection<? extends Def>)newValue);
        return;
      case DefsPackage.TEST_DEF__ATTRIBUTE:
        setAttribute((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	@Override
	public void eUnset(int featureID) {
    switch (featureID)
    {
      case DefsPackage.TEST_DEF__REFERENCES:
        getReferences().clear();
        return;
      case DefsPackage.TEST_DEF__ATTRIBUTE:
        setAttribute(ATTRIBUTE_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	@Override
	public boolean eIsSet(int featureID) {
    switch (featureID)
    {
      case DefsPackage.TEST_DEF__REFERENCES:
        return references != null && !references.isEmpty();
      case DefsPackage.TEST_DEF__ATTRIBUTE:
        return ATTRIBUTE_EDEFAULT == null ? attribute != null : !ATTRIBUTE_EDEFAULT.equals(attribute);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @generated
   */
	@Override
	public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (attribute: ");
    result.append(attribute);
    result.append(')');
    return result.toString();
  }

		@Override
  protected Object createInstance()
  {
    return new String("TestDefStringInstance");
  }

} //TestDefImpl
