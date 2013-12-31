/**
 */
package org.eclipse.emf.cdo.releng.projectconfig.impl;

import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;
import org.eclipse.emf.cdo.releng.projectconfig.PropertyFilter;
import org.eclipse.emf.cdo.releng.projectconfig.WorkspaceConfiguration;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Property Filter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl#getOmissions <em>Omissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl#getPredicates <em>Predicates</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PropertyFilterImpl#getConfiguration <em>Configuration</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PropertyFilterImpl extends MinimalEObjectImpl.Container implements PropertyFilter
{
  private static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();

  /**
   * The default value of the '{@link #getOmissions() <em>Omissions</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOmissions()
   * @generated
   * @ordered
   */
  protected static final Pattern OMISSIONS_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getOmissions() <em>Omissions</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOmissions()
   * @generated
   * @ordered
   */
  protected Pattern omissions = OMISSIONS_EDEFAULT;

  /**
   * The cached value of the '{@link #getPredicates() <em>Predicates</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPredicates()
   * @generated
   * @ordered
   */
  protected EList<Predicate> predicates;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PropertyFilterImpl()
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
    return ProjectConfigPackage.Literals.PROPERTY_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Pattern getOmissions()
  {
    return omissions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOmissions(Pattern newOmissions)
  {
    Pattern oldOmissions = omissions;
    omissions = newOmissions;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ProjectConfigPackage.PROPERTY_FILTER__OMISSIONS,
          oldOmissions, omissions));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Predicate> getPredicates()
  {
    if (predicates == null)
    {
      predicates = new EObjectContainmentEList<Predicate>(Predicate.class, this,
          ProjectConfigPackage.PROPERTY_FILTER__PREDICATES);
    }
    return predicates;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Property> getProperties()
  {
    List<Property> properties = new ArrayList<Property>();
    WorkspaceConfiguration workspaceConfiguration = getConfiguration();
    if (workspaceConfiguration != null)
    {
      for (Project project : workspaceConfiguration.getProjects())
      {
        PreferenceNode projectPreferenceNode = project.getPreferenceNode();
        if (projectPreferenceNode != null)
        {
          for (PreferenceNode preferenceNode : projectPreferenceNode.getChildren())
          {
            collectProperties(properties, workspaceConfiguration, preferenceNode);
          }
        }
      }
    }

    int size = properties.size();
    return new EcoreEList.UnmodifiableEList<Property>(this, ProjectConfigPackage.Literals.PROPERTY_FILTER__PROPERTIES,
        size, properties.toArray(new Property[size]));
  }

  private void collectProperties(List<Property> properties, WorkspaceConfiguration workspaceConfiguration,
      PreferenceNode preferenceNode)
  {
    for (Property property : preferenceNode.getProperties())
    {
      if (workspaceConfiguration.isOmitted(property))
      {
        properties.add(property);
      }
    }

    for (PreferenceNode childPreferenceNode : preferenceNode.getChildren())
    {
      collectProperties(properties, workspaceConfiguration, childPreferenceNode);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WorkspaceConfiguration getConfiguration()
  {
    if (eContainerFeatureID() != ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION)
    {
      return null;
    }
    return (WorkspaceConfiguration)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConfiguration(WorkspaceConfiguration newConfiguration, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newConfiguration, ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION,
        msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConfiguration(WorkspaceConfiguration newConfiguration)
  {
    if (newConfiguration != eInternalContainer()
        || eContainerFeatureID() != ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION && newConfiguration != null)
    {
      if (EcoreUtil.isAncestor(this, newConfiguration))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newConfiguration != null)
      {
        msgs = ((InternalEObject)newConfiguration).eInverseAdd(this,
            ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROPERTY_FILTERS, WorkspaceConfiguration.class, msgs);
      }
      msgs = basicSetConfiguration(newConfiguration, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION,
          newConfiguration, newConfiguration));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(String value)
  {
    if (value == null)
    {
      return false;
    }

    Pattern omissions = getOmissions();
    if (omissions == null)
    {
      return false;
    }

    Path path = new Path(value);
    int segmentCount = path.segmentCount();
    if (segmentCount < 4)
    {
      return false;
    }

    if (!"project".equals(path.segment(0)))
    {
      return false;
    }

    IProject project = ROOT.getProject(path.segment(1));
    if (!project.isAccessible())
    {
      return false;
    }

    EList<Predicate> predicates = getPredicates();
    if (!predicates.isEmpty())
    {
      boolean matches = false;
      for (Predicate predicate : predicates)
      {
        if (predicate.matches(project))
        {
          matches = true;
          break;
        }
      }

      if (!matches)
      {
        return false;
      }
    }

    String relativePath = path.removeFirstSegments(2).toString();

    return omissions.matcher(relativePath).matches();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetConfiguration((WorkspaceConfiguration)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case ProjectConfigPackage.PROPERTY_FILTER__PREDICATES:
      return ((InternalEList<?>)getPredicates()).basicRemove(otherEnd, msgs);
    case ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION:
      return basicSetConfiguration(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION:
      return eInternalContainer().eInverseRemove(this, ProjectConfigPackage.WORKSPACE_CONFIGURATION__PROPERTY_FILTERS,
          WorkspaceConfiguration.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case ProjectConfigPackage.PROPERTY_FILTER__OMISSIONS:
      return getOmissions();
    case ProjectConfigPackage.PROPERTY_FILTER__PREDICATES:
      return getPredicates();
    case ProjectConfigPackage.PROPERTY_FILTER__PROPERTIES:
      return getProperties();
    case ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION:
      return getConfiguration();
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
    case ProjectConfigPackage.PROPERTY_FILTER__OMISSIONS:
      setOmissions((Pattern)newValue);
      return;
    case ProjectConfigPackage.PROPERTY_FILTER__PREDICATES:
      getPredicates().clear();
      getPredicates().addAll((Collection<? extends Predicate>)newValue);
      return;
    case ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION:
      setConfiguration((WorkspaceConfiguration)newValue);
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
    case ProjectConfigPackage.PROPERTY_FILTER__OMISSIONS:
      setOmissions(OMISSIONS_EDEFAULT);
      return;
    case ProjectConfigPackage.PROPERTY_FILTER__PREDICATES:
      getPredicates().clear();
      return;
    case ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION:
      setConfiguration((WorkspaceConfiguration)null);
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
    case ProjectConfigPackage.PROPERTY_FILTER__OMISSIONS:
      return OMISSIONS_EDEFAULT == null ? omissions != null : !OMISSIONS_EDEFAULT.equals(omissions);
    case ProjectConfigPackage.PROPERTY_FILTER__PREDICATES:
      return predicates != null && !predicates.isEmpty();
    case ProjectConfigPackage.PROPERTY_FILTER__PROPERTIES:
      return !getProperties().isEmpty();
    case ProjectConfigPackage.PROPERTY_FILTER__CONFIGURATION:
      return getConfiguration() != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case ProjectConfigPackage.PROPERTY_FILTER___MATCHES__STRING:
      return matches((String)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
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

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (omissions: ");
    result.append(omissions);
    result.append(')');
    return result.toString();
  }

} // PropertyFilterImpl
