/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionFactory;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.Migration;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.ModelSetChange;
import org.eclipse.emf.cdo.evolution.PropertyChange;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.evolution.util.ElementHandler;
import org.eclipse.emf.cdo.evolution.util.ElementRunnable;
import org.eclipse.emf.cdo.evolution.util.IDAnnotation;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.CollectionUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelSetImpl#getChange <em>Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelSetImpl#getMigrations <em>Migrations</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ModelSetImpl extends CDOObjectImpl implements ModelSet
{
  private static final boolean COMPARE_CONTAINMENT = false;

  private static final boolean DEBUG_IDS = false;

  private static final boolean DEBUG_MATCH = false;

  private static final boolean DEBUG_COMPARE = false;

  private static final boolean DEBUG_CLEANUP = false;

  private boolean changeInvalid;

  private ModelSetImpl emptyModelSet;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ModelSetImpl()
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
    return EvolutionPackage.Literals.MODEL_SET;
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
  public ModelSetChange getChangeGen()
  {
    return (ModelSetChange)eDynamicGet(EvolutionPackage.MODEL_SET__CHANGE, EvolutionPackage.Literals.MODEL_SET__CHANGE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public ModelSetChange getChange()
  {
    ModelSetChangeImpl change = (ModelSetChangeImpl)getChangeGen();
    if (change != null && change.getOldModelSet() != getOldModelSet())
    {
      // Can happen after undoing a release.
      change = null;
    }

    if (change == null || changeInvalid)

    {
      ModelSetChangeImpl result;
      Map<EModelElement, ElementChange> previousElementChanges;

      if (change != null)
      {
        previousElementChanges = change.reset();
        result = change;
      }
      else
      {
        ModelSet oldModelSet = getOldModelSet();
        ModelSet[] modelSetChain = createModelSetChain(oldModelSet, this);

        previousElementChanges = null;
        result = (ModelSetChangeImpl)EvolutionFactory.eINSTANCE.createModelSetChange(modelSetChain);
      }

      compareElements(result, previousElementChanges);

      try
      {
        eSetDeliver(false);
        setChange(change = result);
      }
      finally
      {
        eSetDeliver(true);
        changeInvalid = false;
      }
    }

    return change;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetChange(ModelSetChange newChange, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newChange, EvolutionPackage.MODEL_SET__CHANGE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setChange(ModelSetChange newChange)
  {
    eDynamicSet(EvolutionPackage.MODEL_SET__CHANGE, EvolutionPackage.Literals.MODEL_SET__CHANGE, newChange);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicUnsetChange(NotificationChain msgs)
  {
    return eDynamicInverseRemove((InternalEObject)getChange(), EvolutionPackage.MODEL_SET__CHANGE, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetChange()
  {
    eDynamicUnset(EvolutionPackage.MODEL_SET__CHANGE, EvolutionPackage.Literals.MODEL_SET__CHANGE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetChange()
  {
    return eDynamicIsSet(EvolutionPackage.MODEL_SET__CHANGE, EvolutionPackage.Literals.MODEL_SET__CHANGE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Migration> getMigrations()
  {
    return (EList<Migration>)eDynamicGet(EvolutionPackage.MODEL_SET__MIGRATIONS, EvolutionPackage.Literals.MODEL_SET__MIGRATIONS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract Evolution getEvolution();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract int getVersion();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract Release getPreviousRelease();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract EList<EPackage> getRootPackages();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract EList<EPackage> getAllPackages();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EPackage getPackage(String nsURI)
  {
    for (EPackage ePackage : getAllPackages())
    {
      if (ObjectUtil.equals(ePackage.getNsURI(), nsURI))
      {
        return ePackage;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract boolean containsElement(EModelElement modelElement);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @SuppressWarnings("unchecked")
  public <T extends EModelElement> T getElement(final String id)
  {
    // TODO Design better approach to early return from ElementHandler.execute().
    int xxx;

    class ResultException extends RuntimeException
    {
      private static final long serialVersionUID = 1L;

      EModelElement result;
    }

    try
    {
      ElementHandler.execute(getRootPackages(), new ElementRunnable()
      {
        public void run(EModelElement modelElement)
        {
          if (ObjectUtil.equals(id, getElementID(modelElement)))
          {
            ResultException ex = new ResultException();
            ex.result = modelElement;
            throw ex;
          }
        }
      });
    }
    catch (ResultException ex)
    {
      return (T)ex.result;
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String getElementID(EModelElement modelElement)
  {
    return getElementID(modelElement, false);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String getElementID(EModelElement modelElement, boolean considerOldIDs)
  {
    if (containsElement(modelElement))
    {
      return IDAnnotation.getValue(modelElement, considerOldIDs);
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public ModelSetChange compare(ModelSet other)
  {
    ModelSet[] modelSetChain = createModelSetChain(this, other);
    ModelSetChangeImpl result = (ModelSetChangeImpl)EvolutionFactory.eINSTANCE.createModelSetChange(modelSetChain);

    compareElements(result, null);
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Migration getMigration(String diagnosticID)
  {
    if (diagnosticID != null)
    {
      for (Migration migration : getMigrations())
      {
        if (diagnosticID.equals(migration.getDiagnosticID()))
        {
          return migration;
        }
      }
    }

    return null;
  }

  public EList<ElementChange> getElementChanges(EClass elementType, ChangeKind... changeKinds)
  {
    ModelSetChange modelSetChange = getChange();

    EList<ElementChange> result = new BasicEList<ElementChange>();
    collectElementChanges(result, modelSetChange, elementType, changeKinds);
    return result;
  }

  private void collectElementChanges(EList<ElementChange> result, Change change, EClass elementType, ChangeKind... changeKinds)
  {
    if (change instanceof ElementChange)
    {
      ElementChange elementChange = (ElementChange)change;

      if (elementType == null || elementType.isSuperTypeOf(elementChange.getElement().eClass()))
      {
        if (changeKinds.length == 0 || elementChange.getKind().indexWithin(changeKinds) != -1)
        {
          result.add(elementChange);
        }
      }
    }

    for (Change child : change.getChildren())
    {
      collectElementChanges(result, child, elementType, changeKinds);
    }
  }

  public EList<PropertyChange> getPropertyChanges(EStructuralFeature feature, ChangeKind... changeKinds)
  {
    ModelSetChange modelSetChange = getChange();

    EList<PropertyChange> result = new BasicEList<PropertyChange>();
    collectPropertyChanges(result, modelSetChange, feature, changeKinds);
    return result;
  }

  private void collectPropertyChanges(EList<PropertyChange> result, Change change, EStructuralFeature feature, ChangeKind... changeKinds)
  {
    if (change instanceof PropertyChange)
    {
      PropertyChange propertyChange = (PropertyChange)change;

      if (feature == null || feature == propertyChange.getFeature())
      {
        if (changeKinds.length == 0 || propertyChange.getKind().indexWithin(changeKinds) != -1)
        {
          result.add(propertyChange);
        }
      }
    }

    for (Change child : change.getChildren())
    {
      collectPropertyChanges(result, child, feature, changeKinds);
    }
  }

  public CDOPackageRegistry createPackageRegistry()
  {
    CDOPackageRegistryImpl packageRegistry = new CDOPackageRegistryImpl();
    packageRegistry.setPackageLoader(new PackageLoader()
    {
      public EPackage[] loadPackages(CDOPackageUnit packageUnit)
      {
        throw new ImplementationError("All package units should be fully initialized -- no loading required");
      }
    });

    packageRegistry.activate();

    for (EPackage rootPackage : getRootPackages())
    {
      packageRegistry.putEPackage(rootPackage);
    }

    return packageRegistry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EvolutionPackage.MODEL_SET__MIGRATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getMigrations()).basicAdd(otherEnd, msgs);
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
    case EvolutionPackage.MODEL_SET__CHANGE:
      return basicUnsetChange(msgs);
    case EvolutionPackage.MODEL_SET__MIGRATIONS:
      return ((InternalEList<?>)getMigrations()).basicRemove(otherEnd, msgs);
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
    case EvolutionPackage.MODEL_SET__CHANGE:
      return getChange();
    case EvolutionPackage.MODEL_SET__MIGRATIONS:
      return getMigrations();
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
    case EvolutionPackage.MODEL_SET__CHANGE:
      setChange((ModelSetChange)newValue);
      return;
    case EvolutionPackage.MODEL_SET__MIGRATIONS:
      getMigrations().clear();
      getMigrations().addAll((Collection<? extends Migration>)newValue);
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
    case EvolutionPackage.MODEL_SET__CHANGE:
      unsetChange();
      return;
    case EvolutionPackage.MODEL_SET__MIGRATIONS:
      getMigrations().clear();
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
    case EvolutionPackage.MODEL_SET__CHANGE:
      return isSetChange();
    case EvolutionPackage.MODEL_SET__MIGRATIONS:
      return !getMigrations().isEmpty();
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
    case EvolutionPackage.MODEL_SET___GET_EVOLUTION:
      return getEvolution();
    case EvolutionPackage.MODEL_SET___GET_VERSION:
      return getVersion();
    case EvolutionPackage.MODEL_SET___GET_PREVIOUS_RELEASE:
      return getPreviousRelease();
    case EvolutionPackage.MODEL_SET___GET_ROOT_PACKAGES:
      return getRootPackages();
    case EvolutionPackage.MODEL_SET___GET_ALL_PACKAGES:
      return getAllPackages();
    case EvolutionPackage.MODEL_SET___GET_PACKAGE__STRING:
      return getPackage((String)arguments.get(0));
    case EvolutionPackage.MODEL_SET___CONTAINS_ELEMENT__EMODELELEMENT:
      return containsElement((EModelElement)arguments.get(0));
    case EvolutionPackage.MODEL_SET___GET_ELEMENT__STRING:
      return getElement((String)arguments.get(0));
    case EvolutionPackage.MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT:
      return getElementID((EModelElement)arguments.get(0));
    case EvolutionPackage.MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN:
      return getElementID((EModelElement)arguments.get(0), (Boolean)arguments.get(1));
    case EvolutionPackage.MODEL_SET___COMPARE__MODELSET:
      return compare((ModelSet)arguments.get(0));
    case EvolutionPackage.MODEL_SET___GET_MIGRATION__STRING:
      return getMigration((String)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  public void invalidateChange()
  {
    changeInvalid = true;
  }

  private ModelSet getOldModelSet()
  {
    Release previousRelease = getPreviousRelease();
    return previousRelease != null ? previousRelease : getEmptyModelSet();
  }

  private ModelSet getEmptyModelSet()
  {
    if (emptyModelSet == null)
    {
      emptyModelSet = new ModelSetImpl()
      {
        @Override
        public int getVersion()
        {
          return 0;
        }

        @Override
        public EList<EPackage> getRootPackages()
        {
          return ECollections.emptyEList();
        }

        @Override
        public Release getPreviousRelease()
        {
          return null;
        }

        @Override
        public Evolution getEvolution()
        {
          return ModelSetImpl.this.getEvolution();
        }

        @Override
        public EList<EPackage> getAllPackages()
        {
          return ECollections.emptyEList();
        }

        @Override
        public boolean containsElement(EModelElement modelElement)
        {
          return false;
        }
      };
    }

    return emptyModelSet;
  }

  private static void compareElements(final ModelSetChangeImpl result, final Map<EModelElement, ElementChange> previousElementChanges)
  {
    final ModelSet[] modelSets = result.getModelSetChain();
    final ModelSet oldModelSet = result.getOldModelSet();
    final ModelSet newModelSet = result.getNewModelSet();

    ElementHandler.execute(newModelSet.getRootPackages(), new ElementRunnable()
    {
      public void run(EModelElement newElement)
      {
        EModelElement oldElement = newElement;

        for (int i = modelSets.length - 1; i > 0; --i)
        {
          String id = modelSets[i].getElementID(oldElement, true);
          oldElement = modelSets[i - 1].getElement(id);
          if (oldElement == null)
          {
            break;
          }
        }

        if (oldElement != null)
        {
          String newID = IDAnnotation.getValue(newElement);
          String oldID = IDAnnotation.getValue(oldElement);

          ChangeKind kind = ObjectUtil.equals(newID, oldID) ? ChangeKind.NONE : ChangeKind.COPIED;
          if (kind == ChangeKind.COPIED)
          {
            if (newModelSet.getElement(oldID) == null)
            {
              kind = ChangeKind.MOVED;
            }
          }

          if (DEBUG_MATCH)
          {
            System.out.println(kind + " " + getLabel(oldElement) + " --> " + getLabel(newElement));
          }

          ElementChange elementChange = getElementChange(oldElement, newElement, kind, previousElementChanges);
          getParentChange(elementChange, result).getChildren().add(elementChange);
          result.getElementChanges().put(oldElement, elementChange);
          result.getElementChanges().put(newElement, elementChange);
          result.getNewToOldElements().put(newElement, oldElement);
          CollectionUtil.add(result.getOldToNewElements(), oldElement, newElement);
        }
        else
        {
          if (DEBUG_MATCH)
          {
            System.out.println("ADDED " + getLabel(newElement));
          }

          ElementChange elementChange = getElementChange(null, newElement, ChangeKind.ADDED, previousElementChanges);
          getParentChange(elementChange, result).getChildren().add(elementChange);
          result.getElementChanges().put(newElement, elementChange);
          result.getAddedElements().add(newElement);
        }
      }
    });

    ElementHandler.execute(oldModelSet.getRootPackages(), new ElementRunnable()
    {
      public void run(EModelElement oldElement)
      {
        if (isRemoved(oldElement))
        {
          if (DEBUG_MATCH)
          {
            System.out.println("REMOVED " + getLabel(oldElement));
          }

          ElementChange elementChange = getElementChange(oldElement, null, ChangeKind.REMOVED, previousElementChanges);
          getParentChange(elementChange, result).getChildren().add(elementChange);
          result.getElementChanges().put(oldElement, elementChange);
          result.getRemovedElements().add(oldElement);
        }
      }

      private boolean isRemoved(EModelElement oldElement)
      {
        Set<EModelElement> newElements = result.getOldToNewElements().get(oldElement);
        if (newElements == null || newElements.isEmpty())
        {
          return true;
        }

        String oldID = oldModelSet.getElementID(oldElement);
        for (EModelElement newElement : newElements)
        {
          String newID = newModelSet.getElementID(newElement);
          if (ObjectUtil.equals(newID, oldID))
          {
            return false;
          }
        }

        return true;
      }
    });

    for (Map.Entry<EModelElement, EModelElement> entry : result.getNewToOldElements().entrySet())
    {
      EModelElement newElement = entry.getKey();
      EModelElement oldElement = entry.getValue();

      compareProperties(result, newElement, oldElement);
    }

    for (EModelElement newElement : result.getAddedElements())
    {
      compareProperties(result, newElement, null);
    }

    cleanupChanges(result);
  }

  private static void compareProperties(ModelSetChange result, EModelElement newElement, EModelElement oldElement)
  {
    EClass eClass = newElement.eClass();

    if (oldElement == null)
    {
      oldElement = (EModelElement)EcoreUtil.create(eClass);
    }
    else if (oldElement.eClass() != eClass)
    {
      int xxx;
      throw new IllegalArgumentException();
    }

    Map<EModelElement, EModelElement> newToOldElements = result.getNewToOldElements();

    for (EStructuralFeature feature : eClass.getEAllStructuralFeatures())
    {
      if (feature.isDerived())
      {
        continue;
      }

      if (feature == EcorePackage.Literals.EPACKAGE__EFACTORY_INSTANCE)
      {
        continue;
      }

      if (feature == EcorePackage.Literals.EENUM_LITERAL__INSTANCE)
      {
        continue;
      }

      EReference eReference = feature instanceof EReference ? (EReference)feature : null;
      if (COMPARE_CONTAINMENT)
      {
        if (feature == EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS)
        {
          continue;
        }

        if (feature == EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE)
        {
          continue;
        }

        if (feature == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES)
        {
          continue;
        }
      }
      else
      {
        if (eReference != null && eReference.isContainment())
        {
          continue;
        }
      }

      Object newValue = newElement.eGet(feature);
      if (eReference != null)
      {
        if (eReference.isMany())
        {
          @SuppressWarnings("unchecked")
          EList<EObject> list = (EList<EObject>)newValue;

          // TODO As an optimization, defer list creation.
          EList<EObject> newList = new BasicEList<EObject>(list.size());

          for (EObject eObject : list)
          {
            if (eObject instanceof EModelElement)
            {
              EModelElement newListElement = (EModelElement)eObject;
              EModelElement oldListElement = newToOldElements.get(newListElement);
              if (oldListElement != null && oldListElement != newListElement)
              {
                eObject = oldListElement;
              }
            }

            newList.add(eObject);
          }

          newValue = newList;
        }
        else
        {
          if (newValue instanceof EModelElement)
          {
            EModelElement oldValue = newToOldElements.get(newValue);
            if (oldValue != null && oldValue != newValue)
            {
              newValue = oldValue;
            }
          }
        }
      }

      Object oldValue = oldElement.eGet(feature);
      if (!ObjectUtil.equals(newValue, oldValue))
      {
        if (DEBUG_COMPARE)
        {
          System.out.println("PROPERTY CHANGE " + getLabel(newElement) + " --> " + feature.getName());
        }

        PropertyChange propertyChange = EvolutionFactory.eINSTANCE.createPropertyChange(feature, oldValue, newValue);
        ElementChange elementChange = result.getElementChanges().get(newElement);
        elementChange.getChildren().add(0, propertyChange);
      }
    }
  }

  private static void cleanupChanges(Change change)
  {
    for (Iterator<Change> it = change.getChildren().iterator(); it.hasNext();)
    {
      Change child = it.next();
      if (child instanceof ElementChange)
      {
        ElementChange elementChange = (ElementChange)child;
        cleanupChanges(elementChange);

        if (elementChange.getKind() == ChangeKind.NONE && elementChange.getChildren().isEmpty())
        {
          if (DEBUG_CLEANUP)
          {
            System.out.println("CLEANUP " + getLabel(elementChange.getElement()));
          }

          it.remove();
        }
      }
    }
  }

  private static Change getParentChange(ElementChange elementChange, ModelSetChange modelSetChange)
  {
    EModelElement element = elementChange.getElement();
    EObject eContainer = element.eContainer();
    if (eContainer instanceof EModelElement)
    {
      EModelElement parentElement = (EModelElement)eContainer;
      ElementChange parentChange = modelSetChange.getElementChanges().get(parentElement);
      if (parentChange != null)
      {
        return parentChange;
      }
    }

    return modelSetChange;
  }

  private static ElementChange getElementChange(EModelElement oldElement, EModelElement newElement, ChangeKind kind,
      Map<EModelElement, ElementChange> previousElementChanges)
  {
    if (previousElementChanges != null)
    {
      ElementChangeImpl elementChange = (ElementChangeImpl)previousElementChanges.remove(newElement);
      if (elementChange == null)
      {
        elementChange = (ElementChangeImpl)previousElementChanges.remove(oldElement);
      }

      if (elementChange != null)
      {
        // Make sure that the ElementChange can't be used later through any other lookup.
        for (Iterator<Map.Entry<EModelElement, ElementChange>> it = previousElementChanges.entrySet().iterator(); it.hasNext();)
        {
          Map.Entry<EModelElement, ElementChange> entry = it.next();
          if (entry.getValue() == elementChange)
          {
            it.remove();
          }
        }

        elementChange.setOldElement(oldElement);
        elementChange.setNewElement(newElement);
        elementChange.setKind(kind);
        return elementChange;
      }
    }

    return EvolutionFactory.eINSTANCE.createElementChange(oldElement, newElement, kind);
  }

  private static String getLabel(EModelElement element)
  {
    String label = ElementHandler.getLabel(element);

    if (DEBUG_IDS)
    {
      String id = IDAnnotation.getValue(element);
      if (id != null)
      {
        label += "[" + id + "]";
      }
    }

    return label;
  }

  private static ModelSet[] createModelSetChain(ModelSet modelSetA, ModelSet modelSetB)
  {
    int versionA = modelSetA.getVersion();
    int versionB = modelSetB.getVersion();

    EList<ModelSet> result = new BasicEList<ModelSet>();
    if (versionA < versionB)
    {
      result.add(modelSetA);
      result.add(modelSetB);
    }
    else
    {
      result.add(modelSetB);
      result.add(modelSetA);
    }

    ModelSet first = result.get(0);
    ModelSet second;
    while ((second = result.get(1).getPreviousRelease()) != first)
    {
      if (second == null)
      {
        break;
      }

      result.add(1, second);
    }

    return result.toArray(new ModelSet[result.size()]);
  }

} // ModelSetImpl
