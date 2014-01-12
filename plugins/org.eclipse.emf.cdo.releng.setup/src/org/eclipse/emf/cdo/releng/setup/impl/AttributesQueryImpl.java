/**
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.AttributesQuery;
import org.eclipse.emf.cdo.releng.setup.MylynQuery;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attributes Query</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.AttributesQueryImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AttributesQueryImpl extends MylynQueryImpl implements AttributesQuery
{
  /**
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected EMap<String, String> attributes;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AttributesQueryImpl()
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
    return SetupPackage.Literals.ATTRIBUTES_QUERY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EMap<String, String> getAttributes()
  {
    if (attributes == null)
    {
      attributes = new EcoreEMap<String, String>(SetupPackage.Literals.QUERY_ATTRIBUTE, QueryAttributeImpl.class, this,
          SetupPackage.ATTRIBUTES_QUERY__ATTRIBUTES);
    }
    return attributes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.ATTRIBUTES_QUERY__ATTRIBUTES:
      return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case SetupPackage.ATTRIBUTES_QUERY__ATTRIBUTES:
      if (coreType)
      {
        return getAttributes();
      }
      else
      {
        return getAttributes().map();
      }
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.ATTRIBUTES_QUERY__ATTRIBUTES:
      ((EStructuralFeature.Setting)getAttributes()).set(newValue);
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
    case SetupPackage.ATTRIBUTES_QUERY__ATTRIBUTES:
      getAttributes().clear();
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
    case SetupPackage.ATTRIBUTES_QUERY__ATTRIBUTES:
      return attributes != null && !attributes.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  protected MylynHelper createMylynHelper()
  {
    return AttributesMylynHelper.create();
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  private static class AttributesMylynHelper extends AbstractMylynHelper
  {
    private Map<String, String> attributes;

    @Override
    protected boolean isQueryDifferent(SetupTaskContext context, MylynQuery mylynQuery,
        org.eclipse.mylyn.internal.tasks.core.RepositoryQuery repositoryQuery)
    {
      attributes = ((AttributesQuery)mylynQuery).getAttributes().map();
      return !ObjectUtil.equals(repositoryQuery.getAttributes(), attributes);
    }

    @Override
    protected void configureQuery(SetupTaskContext context, MylynQuery mylynQuery,
        org.eclipse.mylyn.internal.tasks.core.RepositoryQuery repositoryQuery)
    {
      Map<String, String> repositoryAttributes = repositoryQuery.getAttributes();

      for (Entry<String, String> entry : attributes.entrySet())
      {
        String key = entry.getKey();
        String value = entry.getValue();

        String repositoryValue = repositoryAttributes.get(key);
        if (!ObjectUtil.equals(value, repositoryValue))
        {
          context.log("Setting attribute " + key + " = " + value);
          repositoryQuery.setAttribute(key, value);
        }
      }

      for (String key : new ArrayList<String>(repositoryAttributes.keySet()))
      {
        if (!attributes.containsKey(key))
        {
          context.log("Removing attribute " + key);
          repositoryQuery.setAttribute(key, null);
        }
      }
    }

    public static MylynHelper create()
    {
      return new AttributesMylynHelper();
    }
  }

} // AttributesQueryImpl
