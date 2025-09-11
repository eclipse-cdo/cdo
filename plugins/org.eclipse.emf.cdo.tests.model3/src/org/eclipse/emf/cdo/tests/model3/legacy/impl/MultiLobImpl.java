/**
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.tests.model3.MultiLob;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Multi Lob</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MultiLobImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MultiLobImpl#getBlobs <em>Blobs</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MultiLobImpl#getClobs <em>Clobs</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MultiLobImpl extends EObjectImpl implements MultiLob
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getBlobs() <em>Blobs</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBlobs()
   * @generated
   * @ordered
   */
  protected EList<CDOBlob> blobs;

  /**
   * The cached value of the '{@link #getClobs() <em>Clobs</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getClobs()
   * @generated
   * @ordered
   */
  protected EList<CDOClob> clobs;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MultiLobImpl()
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
    return Model3Package.eINSTANCE.getMultiLob();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.MULTI_LOB__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<CDOBlob> getBlobs()
  {
    if (blobs == null)
    {
      blobs = new EDataTypeUniqueEList<>(CDOBlob.class, this, Model3Package.MULTI_LOB__BLOBS);
    }
    return blobs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<CDOClob> getClobs()
  {
    if (clobs == null)
    {
      clobs = new EDataTypeUniqueEList<>(CDOClob.class, this, Model3Package.MULTI_LOB__CLOBS);
    }
    return clobs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model3Package.MULTI_LOB__NAME:
      return getName();
    case Model3Package.MULTI_LOB__BLOBS:
      return getBlobs();
    case Model3Package.MULTI_LOB__CLOBS:
      return getClobs();
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
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model3Package.MULTI_LOB__NAME:
      setName((String)newValue);
      return;
    case Model3Package.MULTI_LOB__BLOBS:
      getBlobs().clear();
      getBlobs().addAll((Collection<? extends CDOBlob>)newValue);
      return;
    case Model3Package.MULTI_LOB__CLOBS:
      getClobs().clear();
      getClobs().addAll((Collection<? extends CDOClob>)newValue);
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
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.MULTI_LOB__NAME:
      setName(NAME_EDEFAULT);
      return;
    case Model3Package.MULTI_LOB__BLOBS:
      getBlobs().clear();
      return;
    case Model3Package.MULTI_LOB__CLOBS:
      getClobs().clear();
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
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.MULTI_LOB__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case Model3Package.MULTI_LOB__BLOBS:
      return blobs != null && !blobs.isEmpty();
    case Model3Package.MULTI_LOB__CLOBS:
      return clobs != null && !clobs.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", blobs: ");
    result.append(blobs);
    result.append(", clobs: ");
    result.append(clobs);
    result.append(')');
    return result.toString();
  }

} // MultiLobImpl
