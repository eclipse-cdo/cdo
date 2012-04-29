/**
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.G;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>G</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl#getDummy <em>Dummy</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl#getList <em>List</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GImpl extends CDOObjectImpl implements G
{
  private boolean listModified = false;

  private boolean referenceModified = false;

  private boolean attributeModified = false;

  /**
   * @ADDED
   */
  {
    eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        String feature = ((EStructuralFeature)msg.getFeature()).getName();
        if (feature.equals("dummy"))
        {
          attributeModified = true;
        }
        else if (feature.equals("reference"))
        {
          referenceModified = true;
        }
        else if (feature.equals("list"))
        {
          listModified = true;
        }
      }
    });
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GImpl()
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
    return Model6Package.Literals.G;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDummy()
  {
    return (String)eGet(Model6Package.Literals.G__DUMMY, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDummy(String newDummy)
  {
    eSet(Model6Package.Literals.G__DUMMY, newDummy);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BaseObject getReference()
  {
    return (BaseObject)eGet(Model6Package.Literals.G__REFERENCE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setReference(BaseObject newReference)
  {
    eSet(Model6Package.Literals.G__REFERENCE, newReference);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<BaseObject> getList()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.G__LIST, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isAttributeModified()
  {
    return attributeModified;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isReferenceModified()
  {
    return referenceModified;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean isListModified()
  {
    return listModified;
  }

} // GImpl
