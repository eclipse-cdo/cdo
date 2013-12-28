/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.projectconfig.provider;

import org.eclipse.emf.cdo.releng.predicates.AndPredicate;
import org.eclipse.emf.cdo.releng.predicates.NaturePredicate;
import org.eclipse.emf.cdo.releng.predicates.PredicatesFactory;
import org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigFactory;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;
import org.eclipse.emf.cdo.releng.projectconfig.util.ProjectConfigUtil;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.IdentityCommand;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.DragAndDropCommand;
import org.eclipse.emf.edit.command.DragAndDropCommand.Detail;
import org.eclipse.emf.edit.command.DragAndDropFeedback;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.IViewerNotification;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.releng.projectconfig.Project} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ProjectItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
  private static final Class<?> IWORKBENCH_ADAPTER_CLASS;

  private static final Method GET_IMAGE_DESCRIPTOR_METHOD;

  static
  {
    Class<?> workbenchAdapterClass = null;
    Method method = null;

    try
    {
      workbenchAdapterClass = CommonPlugin.loadClass("org.eclipse.ui.ide", "org.eclipse.ui.model.IWorkbenchAdapter");
      method = workbenchAdapterClass.getMethod("getImageDescriptor", Object.class);
    }
    catch (Throwable throwable)
    {
      // Ignore
    }

    IWORKBENCH_ADAPTER_CLASS = workbenchAdapterClass;
    GET_IMAGE_DESCRIPTOR_METHOD = method;
  }

  // private static final Pattern PROJECT_ENCODING_PROPERTY_PATTERN = Pattern.compile("<project>");

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ProjectItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addPreferenceNodePropertyDescriptor(object);
      addPreferenceProfileReferencesPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Preference Node feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addPreferenceNodePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Project_preferenceNode_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Project_preferenceNode_feature", "_UI_Project_type"),
        ProjectConfigPackage.Literals.PROJECT__PREFERENCE_NODE, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Preference Profile References feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addPreferenceProfileReferencesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory)
        .getRootAdapterFactory(), getResourceLocator(), getString("_UI_Project_preferenceProfileReferences_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Project_preferenceProfileReferences_feature",
            "_UI_Project_type"), ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILE_REFERENCES, true, false,
        true, null, null, null)
    {
      IItemLabelProvider labelProvider = new IItemLabelProvider()
      {
        public String getText(Object object)
        {
          String result = itemDelegator.getText(object);
          if (object instanceof PreferenceProfile)
          {
            PreferenceProfile preferenceProfile = (PreferenceProfile)object;
            Project project = preferenceProfile.getProject();
            if (project != null)
            {
              PreferenceNode preferenceNode = project.getPreferenceNode();
              if (preferenceNode != null)
              {
                String name = preferenceNode.getName();
                if (name != null)
                {
                  result += " - " + name;
                }
              }
            }
          }
          return result;
        }

        public Object getImage(Object object)
        {
          return itemDelegator.getImage(object);
        }
      };

      @Override
      public IItemLabelProvider getLabelProvider(Object object)
      {
        return labelProvider;
      }

    });
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILES);
    }
    return childrenFeatures;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EStructuralFeature getChildFeature(Object object, Object child)
  {
    // Check the type of the specified child object and return the proper feature to use for
    // adding (see {@link AddCommand}) it as a child.

    return super.getChildFeature(object, child);
  }

  /**
   * This returns Project.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getImage(Object object)
  {
    Project project = (Project)object;
    PreferenceNode preferenceNode = project.getPreferenceNode();
    if (preferenceNode != null)
    {
      String name = preferenceNode.getName();
      if (name != null)
      {
        IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        try
        {
          Object adapter = iProject.getAdapter(IWORKBENCH_ADAPTER_CLASS);
          if (adapter != null)
          {
            return GET_IMAGE_DESCRIPTOR_METHOD.invoke(adapter, iProject);
          }
        }
        catch (Throwable throwable)
        {
          // Ignore
        }
      }
    }

    return overlayImage(object, getResourceLocator().getImage("full/obj16/Project"));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean shouldComposeCreationImage()
  {
    return true;
  }

  /**
   * This returns the label text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String getText(Object object)
  {
    Project project = (Project)object;
    PreferenceNode preferenceNode = project.getPreferenceNode();
    String label = "<invalid>";
    if (preferenceNode != null)
    {
      String name = preferenceNode.getName();
      if (name != null)
      {
        label = name;
      }
    }
    return label;
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(Project.class))
    {
    case ProjectConfigPackage.PROJECT__PREFERENCE_PROFILES:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
      return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void notifyChangedFoo(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(Project.class))
    {
    case ProjectConfigPackage.PROJECT__PREFERENCE_PROFILE_REFERENCES:
    case ProjectConfigPackage.PROJECT__PREFERENCE_PROFILES:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, true));
      return;
    }
    super.notifyChanged(notification);
  }

  private class References extends ItemProvider implements IEditingDomainItemProvider
  {
    Project project;

    public References(AdapterFactory adapterFactory, String text, Object image, Project project)
    {
      super(adapterFactory, text, image, project);
      this.project = project;
    }

    Map<PreferenceProfile, IWrapperItemProvider> wrappers = new HashMap<PreferenceProfile, IWrapperItemProvider>();

    public void update()
    {
      /*
       * for (Object child : getChildren()) { ((IDisposable)child).dispose(); }
       */
      List<Object> children = new ArrayList<Object>();
      EList<PreferenceProfile> referentProjects = project.getPreferenceProfileReferences();
      for (int i = 0, size = referentProjects.size(); i < size; ++i)
      {
        PreferenceProfile project = referentProjects.get(i);
        IWrapperItemProvider wrapper = wrap(i, project);
        children.add(wrapper);
      }
      ECollections.setEList(getChildren(), children);
    }

    private IWrapperItemProvider wrap(int i, PreferenceProfile preferenceProfile)
    {
      IWrapperItemProvider wrapper = wrappers.get(preferenceProfile);
      if (wrapper == null)
      {
        wrapper = new DelegatingWrapperItemProvider(preferenceProfile, project,
            ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILE_REFERENCES, i, adapterFactory)
        {
          @Override
          public Object getParent(Object object)
          {
            return References.this;
          }

          @Override
          public String getText(Object object)
          {
            String result = super.getText(object);
            PreferenceProfile preferenceProfile = (PreferenceProfile)value;
            Project project = preferenceProfile.getProject();
            if (project != null)
            {
              PreferenceNode preferenceNode = project.getPreferenceNode();
              if (preferenceNode != null)
              {
                String name = preferenceNode.getName();
                if (name != null)
                {
                  result += " - " + name;
                }
              }
            }
            return result;
          }

          @Override
          public Object getImage(Object object)
          {
            Object image = super.getImage(object);
            List<Object> images = new ArrayList<Object>(2);
            images.add(image);
            images.add(EMFEditPlugin.INSTANCE.getImage("full/ovr16/ControlledObject"));
            return image = new ComposedImage(images);
          }

          @Override
          public boolean hasChildren(Object object)
          {
            return false;
          }

          @Override
          public Collection<?> getChildren(Object object)
          {
            return Collections.emptyList();
          }

          @Override
          public void notifyChanged(Notification notification)
          {
            if (notification instanceof IViewerNotification && ((IViewerNotification)notification).isLabelUpdate())
            {
              super.notifyChanged(notification);
            }
          }
        };
        wrappers.put(preferenceProfile, wrapper);
      }
      else
      {
        wrapper.setIndex(i);
      }
      return wrapper;
    }

    @Override
    public Command createCommand(Object object, EditingDomain editingDomain, Class<? extends Command> commandClass,
        CommandParameter commandParameter)
    {
      final Collection<?> originalCollection = commandParameter.getCollection();
      commandParameter = unwrapCommandValues(commandParameter, commandClass);
      Collection<?> collection = commandParameter.getCollection();
      if (commandClass == RemoveCommand.class)
      {
        if (project.getPreferenceProfileReferences().containsAll(collection))
        {
          return new RemoveCommand(editingDomain, project,
              ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILE_REFERENCES, collection)
          {
            @Override
            public void doExecute()
            {
              super.doExecute();
              update();
              affectedObjects = Collections.singleton(References.this);
            }

            @Override
            public void doUndo()
            {
              super.doUndo();
              update();
              affectedObjects = Collections.singleton(References.this);
            }

            @Override
            public void doRedo()
            {
              super.doRedo();
              update();
              affectedObjects = Collections.singleton(References.this);
            }
          };
        }
      }
      else if (commandClass == AddCommand.class && collection != null)
      {
        final Collection<Object> wrappers = new ArrayList<Object>();
        for (Object value : collection)
        {
          if (value instanceof PreferenceProfile)
          {
            wrappers.add(wrap(-1, (PreferenceProfile)value));
          }
          else
          {
            return UnexecutableCommand.INSTANCE;
          }
        }
        return new AddCommand(editingDomain, project,
            ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILE_REFERENCES, collection)
        {
          @Override
          public void doExecute()
          {
            super.doExecute();
            update();
            affectedObjects = wrappers;
          }

          @Override
          public void doUndo()
          {
            super.doUndo();
            update();
            affectedObjects = Collections.singleton(References.this);
          }

          @Override
          public void doRedo()
          {
            super.doRedo();
            update();
            affectedObjects = wrappers;
          }
        };
      }
      else if (commandClass == DragAndDropCommand.class)
      {
        DragAndDropCommand.Detail detail = (DragAndDropCommand.Detail)commandParameter.getFeature();
        return new DragAndDropCommand(editingDomain, this, detail.location, detail.operations, detail.operation,
            commandParameter.getCollection())
        {
          @Override
          protected boolean prepareDropLinkOn()
          {
            dragCommand = IdentityCommand.INSTANCE;
            dropCommand = AddCommand.create(domain, References.this,
                ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILE_REFERENCES, originalCollection);
            return dropCommand.canExecute();
          }
        };
      }
      return super.createCommand(object, editingDomain, commandClass, commandParameter);
    }
  }

  /*
   * @Override public Command createCommand(Object object, EditingDomain domain, Class<? extends Command> commandClass,
   * CommandParameter commandParameter) { Collection<?> collection = commandParameter.getCollection(); if (collection !=
   * null) { for (Object value : collection) { if (value instanceof PreferenceProfile) { return
   * referents.createCommand(referents, domain, commandClass, commandParameter); } } } return
   * super.createCommand(object, domain, commandClass, commandParameter); }
   */

  private References references;

  @Override
  public Collection<?> getChildren(Object object)
  {
    Collection<Object> result = new ArrayList<Object>(super.getChildren(object));
    Project project = (Project)object;
    if (references == null)
    {
      references = new References(adapterFactory, "References", getResourceLocator().getImage(
          "full/obj16/OutgoingLinks"), project);
    }
    references.update();
    result.add(references);
    return result;
  }

  @Override
  protected Command createDragAndDropCommand(EditingDomain domain, Object owner, float location, int operations,
      int operation, Collection<?> collection)
  {
    if (operation == DragAndDropFeedback.DROP_LINK)
    {
      return references.createCommand(references, domain, DragAndDropCommand.class, new CommandParameter(references,
          new Detail(location, operations, operation), collection));
    }
    return super.createDragAndDropCommand(domain, owner, location, operations, operation, collection);
  }

  private static final Set<String> IGNORE_NAME_COMPONENTS = new HashSet<String>(Arrays.asList(new String[] { "org",
      "eclipse", "com" }));

  private static final Map<String, String> ACRYONYMS = new HashMap<String, String>();
  static
  {
    ACRYONYMS.put("jdt", "JDT");
    ACRYONYMS.put("pde", "PDE");
    ACRYONYMS.put("ui", "UI");
    ACRYONYMS.put("api", "API");
    ACRYONYMS.put("ltk", "LTK");
    ACRYONYMS.put("gmf", "GMF");
  }

  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    Project project = (Project)object;
    Map<PreferenceNode, Set<Property>> unmanagedPreferenceNodes = ProjectConfigUtil
        .getUnmanagedPreferenceNodes(project);

    for (Map.Entry<PreferenceNode, Set<Property>> entry : unmanagedPreferenceNodes.entrySet())
    {
      PreferenceNode preferenceNode = entry.getKey();
      Set<Property> properties = entry.getValue();

      String[] rootNameComponents = null;
      StringBuilder qualifiedPreferenceNodeName = new StringBuilder();
      List<PreferenceNode> path = PreferencesUtil.getPath(preferenceNode);
      int pathSize = path.size();
      for (int i = 3; i < pathSize; ++i)
      {
        String preferenceNodeName = path.get(i).getName();
        String[] nameComponents = preferenceNodeName.split("\\.");

        int start = 0;
        if (rootNameComponents == null)
        {
          rootNameComponents = nameComponents;

          while (start < nameComponents.length)
          {
            if (!IGNORE_NAME_COMPONENTS.contains(nameComponents[start]))
            {
              break;
            }
            ++start;
          }
        }

        for (int j = start; j < nameComponents.length; ++j)
        {
          if (qualifiedPreferenceNodeName.length() > 0)
          {
            qualifiedPreferenceNodeName.append(' ');
          }

          String nameComponent = nameComponents[j];
          String acronym = ACRYONYMS.get(nameComponent);
          if (acronym != null)
          {
            qualifiedPreferenceNodeName.append(acronym);
          }
          else
          {
            int length = nameComponent.length();
            if (length >= 1)
            {
              qualifiedPreferenceNodeName.append(Character.toUpperCase(nameComponent.charAt(0)));
              qualifiedPreferenceNodeName.append(nameComponent, 1, length);
            }
          }
        }
      }

      PreferenceProfile preferenceProfile = ProjectConfigFactory.eINSTANCE.createPreferenceProfile();
      if (qualifiedPreferenceNodeName.length() > 0)
      {
        preferenceProfile.setName(qualifiedPreferenceNodeName.toString());
      }

      PreferenceFilter preferenceFilter = ProjectConfigFactory.eINSTANCE.createPreferenceFilter();
      preferenceFilter.setPreferenceNode(preferenceNode);
      preferenceProfile.getPreferenceFilters().add(preferenceFilter);

      EList<Property> realProperties = preferenceNode.getProperties();
      int realSize = realProperties.size();
      int targetSize = properties.size();
      if (realSize != targetSize)
      {
        if (realSize - targetSize > realSize / 2)
        {
          StringBuilder pattern = new StringBuilder();
          for (Property property : properties)
          {
            String name = property.getName();
            name = name.replace(".", "\\.");
            if (pattern.length() != 0)
            {
              pattern.append('|');
            }
            pattern.append(name);
          }

          preferenceFilter.setInclusions(Pattern.compile(pattern.toString()));
        }
        else
        {
          StringBuilder pattern = new StringBuilder();
          for (Property property : realProperties)
          {
            if (!properties.contains(property))
            {
              String name = property.getName();
              name = name.replace(".", "\\.");
              if (pattern.length() != 0)
              {
                pattern.append('|');
              }
              pattern.append(name);
            }
          }

          preferenceFilter.setExclusions(Pattern.compile(pattern.toString()));
        }
      }

      AndPredicate andPredicate = PredicatesFactory.eINSTANCE.createAndPredicate();
      preferenceProfile.getPredicates().add(andPredicate);

      RepositoryPredicate repositoryPredicate = PredicatesFactory.eINSTANCE.createRepositoryPredicate();
      IProject iProject = ProjectConfigUtil.getProject(project);
      repositoryPredicate.setProject(iProject);
      andPredicate.getOperands().add(repositoryPredicate);

      try
      {
        String[] natureIds = iProject.getDescription().getNatureIds();
        int bestMatchLength = 0;
        String bestNatureId = null;
        for (String natureId : natureIds)
        {
          String[] natureComponents = natureId.split("\\.");
          int index = 0;
          while (index < natureComponents.length && index < rootNameComponents.length)
          {
            if (!rootNameComponents[index].equals(natureComponents[index]))
            {
              break;
            }
            ++index;
          }

          if (bestMatchLength < index)
          {
            bestMatchLength = index;
            bestNatureId = natureId;
          }
        }

        if (bestMatchLength >= 3)
        {
          NaturePredicate naturePredicate = PredicatesFactory.eINSTANCE.createNaturePredicate();
          naturePredicate.setNature(bestNatureId);
          andPredicate.getOperands().add(naturePredicate);
        }
      }
      catch (CoreException ex)
      {
        // Ignore
      }

      newChildDescriptors.add(createChildParameter(ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILES,
          preferenceProfile));
    }

    collectNewChildDescriptorsGen(newChildDescriptors, object);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
   * that can be created under this object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void collectNewChildDescriptorsGen(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);

    newChildDescriptors.add(createChildParameter(ProjectConfigPackage.Literals.PROJECT__PREFERENCE_PROFILES,
        ProjectConfigFactory.eINSTANCE.createPreferenceProfile()));
  }

  @Override
  public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection)
  {
    String text = super.getCreateChildText(owner, feature, child, selection);
    if (child instanceof PreferenceProfile)
    {
      PreferenceProfile preferenceProfile = (PreferenceProfile)child;
      EList<PreferenceFilter> preferenceFilters = preferenceProfile.getPreferenceFilters();
      if (!preferenceFilters.isEmpty())
      {
        PreferenceFilter preferenceFilter = preferenceFilters.get(0);
        PreferenceNode preferenceNode = preferenceFilter.getPreferenceNode();
        if (preferenceNode != null)
        {
          List<PreferenceNode> path = PreferencesUtil.getPath(preferenceNode);
          StringBuilder qualifiedName = new StringBuilder();
          for (int i = 3, size = path.size(); i < size; ++i)
          {
            if (qualifiedName.length() != 0)
            {
              qualifiedName.append('/');
            }
            qualifiedName.append(path.get(i).getName());
          }
          if (qualifiedName.length() != 0)
          {
            text += " for " + qualifiedName;
          }
        }
      }
    }
    return text;
  }

  @Override
  protected Command createCreateChildCommand(EditingDomain domain, EObject owner, EStructuralFeature feature,
      Object value, int index, Collection<?> collection)
  {
    return new CreateChildCommand(domain, owner, feature, value, index, collection, this);
  }

  /**
   * Return the resource locator for this item provider's resources.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return ProjectConfigEditPlugin.INSTANCE;
  }

}
