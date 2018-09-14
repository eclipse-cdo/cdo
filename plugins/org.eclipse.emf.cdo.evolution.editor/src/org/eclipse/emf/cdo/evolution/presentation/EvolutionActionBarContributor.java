/**
 */
package org.eclipse.emf.cdo.evolution.presentation;

import org.eclipse.emf.cdo.evolution.util.ElementHandler;
import org.eclipse.emf.cdo.evolution.util.ElementRunnable;
import org.eclipse.emf.cdo.evolution.util.IDAnnotation;

import org.eclipse.emf.common.ui.action.ViewerFilterAction;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.presentation.EcoreEditorPlugin;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.CollapseAllAction;
import org.eclipse.emf.edit.ui.action.ControlAction;
import org.eclipse.emf.edit.ui.action.CreateChildAction;
import org.eclipse.emf.edit.ui.action.CreateSiblingAction;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.action.FindAction;
import org.eclipse.emf.edit.ui.action.LoadResourceAction;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This is the action bar contributor for the Evolution model editor.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class EvolutionActionBarContributor extends EditingDomainActionBarContributor implements ISelectionChangedListener
{
  /**
   * This keeps track of the active editor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IEditorPart activeEditorPart;

  /**
   * This keeps track of the current selection provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ISelectionProvider selectionProvider;

  /**
   * This action opens the Properties view.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IAction showPropertiesViewAction = new Action(EvolutionEditorPlugin.INSTANCE.getString("_UI_ShowPropertiesView_menu_item"))
  {
    @Override
    public void run()
    {
      try
      {
        getPage().showView("org.eclipse.ui.views.PropertySheet");
      }
      catch (PartInitException exception)
      {
        EvolutionEditorPlugin.INSTANCE.log(exception);
      }
    }
  };

  /**
   * This action refreshes the viewer of the current editor if the editor
   * implements {@link org.eclipse.emf.common.ui.viewer.IViewerProvider}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IAction refreshViewerAction = new Action(EvolutionEditorPlugin.INSTANCE.getString("_UI_RefreshViewer_menu_item"))
  {
    @Override
    public boolean isEnabled()
    {
      return activeEditorPart instanceof IViewerProvider;
    }

    @Override
    public void run()
    {
      if (activeEditorPart instanceof IViewerProvider)
      {
        Viewer viewer = ((IViewerProvider)activeEditorPart).getViewer();
        if (viewer != null)
        {
          viewer.refresh();
        }
      }
    }
  };

  protected IAction assignIDsAction = new IDAction("Assign IDs")
  {
    @Override
    protected void handleID(EModelElement modelElement)
    {
      IDAnnotation.ensureValue(modelElement);
    }
  };

  protected IAction removeIDsAction = new IDAction("Remove IDs")
  {
    @Override
    protected void handleID(EModelElement modelElement)
    {
      IDAnnotation.removeFrom(modelElement);
    }
  };

  /**
   * This will contain one {@link org.eclipse.emf.edit.ui.action.CreateChildAction} corresponding to each descriptor
   * generated for the current selection by the item provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<IAction> createChildActions;

  /**
   * This is the menu manager into which menu contribution items should be added for CreateChild actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IMenuManager createChildMenuManager;

  /**
   * This will contain one {@link org.eclipse.emf.edit.ui.action.CreateSiblingAction} corresponding to each descriptor
   * generated for the current selection by the item provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<IAction> createSiblingActions;

  /**
   * This is the menu manager into which menu contribution items should be added for CreateSibling actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IMenuManager createSiblingMenuManager;

  protected SelectionChangedEvent lastSelectionChangedEvent;

  protected ViewerFilterAction showGenericsAction = new ViewerFilterAction(EcoreEditorPlugin.INSTANCE.getString("_UI_ShowGenerics_menu_item"),
      IAction.AS_CHECK_BOX)
  {
    @Override
    protected void refreshViewers()
    {
      if (activeEditorPart instanceof EvolutionEditor)
      {
        ((EvolutionEditor)activeEditorPart).ecoreItemProviderAdapterFactory.setShowGenerics(isChecked());
      }

      super.refreshViewers();

      if (lastSelectionChangedEvent != null && activeEditorPart instanceof EvolutionEditor)
      {
        selectionChanged(lastSelectionChangedEvent);
      }
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
      return isChecked() || !(element instanceof ETypeParameter || element instanceof EGenericType);
    }
  };

  /**
   * This creates an instance of the contributor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EvolutionActionBarContributor()
  {
    super(ADDITIONS_LAST_STYLE);
    loadResourceAction = new LoadResourceAction();
    // validateAction = new ValidateAction();
    liveValidationAction = new DiagnosticDecorator.LiveValidator.LiveValidationAction(EvolutionEditorPlugin.getPlugin().getDialogSettings());
    controlAction = new ControlAction();
    findAction = FindAction.create();
    collapseAllAction = new CollapseAllAction();

    showGenericsAction.setChecked(Boolean.parseBoolean(EcoreEditorPlugin.getPlugin().getDialogSettings().get("showGenericsAction")));
  }

  public void showGenerics(boolean isChecked)
  {
    if (showGenericsAction != null)
    {
      showGenericsAction.setChecked(isChecked);
    }
  }

  @Override
  public void dispose()
  {
    EcoreEditorPlugin.getPlugin().getDialogSettings().put("showGenericsAction", Boolean.toString(showGenericsAction.isChecked()));

    super.dispose();
  }

  /**
   * This adds Separators for editor additions to the tool bar.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void contributeToToolBar(IToolBarManager toolBarManager)
  {
    super.contributeToToolBar(toolBarManager);
    toolBarManager.add(new Separator("evolution-settings"));
    toolBarManager.add(new Separator("evolution-additions"));
    toolBarManager.add(assignIDsAction);
    toolBarManager.add(removeIDsAction);
  }

  /**
   * This adds to the menu bar a menu and some separators for editor additions,
   * as well as the sub-menus for object creation items.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void contributeToMenu(IMenuManager menuManager)
  {
    super.contributeToMenu(menuManager);

    IMenuManager submenuManager = new MenuManager(EvolutionEditorPlugin.INSTANCE.getString("_UI_EvolutionEditor_menu"), "org.eclipse.emf.cdo.evolutionMenuID");
    menuManager.insertAfter("additions", submenuManager);
    submenuManager.add(new Separator("settings"));
    submenuManager.add(new Separator("actions"));
    submenuManager.add(new Separator("additions"));
    submenuManager.add(new Separator("additions-end"));

    // Prepare for CreateChild item addition or removal.
    //
    createChildMenuManager = new MenuManager(EvolutionEditorPlugin.INSTANCE.getString("_UI_CreateChild_menu_item"));
    submenuManager.insertBefore("additions", createChildMenuManager);

    // Prepare for CreateSibling item addition or removal.
    //
    createSiblingMenuManager = new MenuManager(EvolutionEditorPlugin.INSTANCE.getString("_UI_CreateSibling_menu_item"));
    submenuManager.insertBefore("additions", createSiblingMenuManager);

    // Force an update because Eclipse hides empty menus now.
    //
    submenuManager.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager menuManager)
      {
        menuManager.updateAll(true);
      }
    });

    addGlobalActions(submenuManager);
    submenuManager.insertBefore("additions-end", showGenericsAction);
  }

  /**
   * When the active editor changes, this remembers the change and registers with it as a selection provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setActiveEditorGen(IEditorPart part)
  {
    super.setActiveEditor(part);
    activeEditorPart = part;

    // Switch to the new selection provider.
    //
    if (selectionProvider != null)
    {
      selectionProvider.removeSelectionChangedListener(this);
    }
    if (part == null)
    {
      selectionProvider = null;
    }
    else
    {
      selectionProvider = part.getSite().getSelectionProvider();
      selectionProvider.addSelectionChangedListener(this);

      // Fake a selection changed event to update the menus.
      //
      if (selectionProvider.getSelection() != null)
      {
        selectionChanged(new SelectionChangedEvent(selectionProvider, selectionProvider.getSelection()));
      }
    }
  }

  /**
   * When the active editor changes, this remembers the change and registers with it as a selection provider.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setActiveEditor(IEditorPart part)
  {
    setActiveEditorGen(part);

    if (part instanceof EvolutionEditor)
    {
      showGenericsAction.addViewer(((EvolutionEditor)part).getViewer());
      showGenericsAction.setEnabled(true);
    }
    else
    {
      showGenericsAction.setEnabled(false);
    }
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionChangedListener},
   * handling {@link org.eclipse.jface.viewers.SelectionChangedEvent}s by querying for the children and siblings
   * that can be added to the selected object and updating the menus accordingly.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void selectionChanged(SelectionChangedEvent event)
  {
    // Remove any menu items for old selection.
    //
    if (createChildMenuManager != null)
    {
      depopulateManager(createChildMenuManager, createChildActions);
    }
    if (createSiblingMenuManager != null)
    {
      depopulateManager(createSiblingMenuManager, createSiblingActions);
    }

    // Query the new selection for appropriate new child/sibling descriptors
    //
    Collection<?> newChildDescriptors = null;
    Collection<?> newSiblingDescriptors = null;

    ISelection selection = event.getSelection();
    if (selection instanceof IStructuredSelection && ((IStructuredSelection)selection).size() == 1)
    {
      Object object = ((IStructuredSelection)selection).getFirstElement();

      EditingDomain domain = ((IEditingDomainProvider)activeEditorPart).getEditingDomain();

      newChildDescriptors = domain.getNewChildDescriptors(object, null);
      newSiblingDescriptors = domain.getNewChildDescriptors(null, object);
    }

    // Generate actions for selection; populate and redraw the menus.
    //
    createChildActions = generateCreateChildActions(newChildDescriptors, selection);
    createSiblingActions = generateCreateSiblingActions(newSiblingDescriptors, selection);

    if (createChildMenuManager != null)
    {
      populateManager(createChildMenuManager, createChildActions, null);
      createChildMenuManager.update(true);
    }
    if (createSiblingMenuManager != null)
    {
      populateManager(createSiblingMenuManager, createSiblingActions, null);
      createSiblingMenuManager.update(true);
    }
  }

  /**
   * This generates a {@link org.eclipse.emf.edit.ui.action.CreateChildAction} for each object in <code>descriptors</code>,
   * and returns the collection of these actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected Collection<IAction> generateCreateChildActions(Collection<?> descriptors, ISelection selection)
  {
    Collection<IAction> actions = new ArrayList<IAction>();
    if (descriptors != null)
    {
      for (Object descriptor : descriptors)
      {
        if (!showGenericsAction.isChecked() && descriptor instanceof CommandParameter)
        {
          Object feature = ((CommandParameter)descriptor).getFeature();
          if (isGenericFeature(feature))
          {
            continue;
          }
        }
        actions.add(new EcoreCreateChildAction(activeEditorPart, selection, descriptor));
      }
    }
    return actions;
  }

  /**
   * This generates a {@link org.eclipse.emf.edit.ui.action.CreateSiblingAction} for each object in <code>descriptors</code>,
   * and returns the collection of these actions.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected Collection<IAction> generateCreateSiblingActions(Collection<?> descriptors, ISelection selection)
  {
    Collection<IAction> actions = new ArrayList<IAction>();
    if (descriptors != null)
    {
      for (Object descriptor : descriptors)
      {
        if (!showGenericsAction.isChecked() && descriptor instanceof CommandParameter)
        {
          Object feature = ((CommandParameter)descriptor).getFeature();
          if (isGenericFeature(feature))
          {
            continue;
          }
        }
        actions.add(new EcoreCreateSiblingAction(activeEditorPart, selection, descriptor));
      }
    }
    return actions;
  }

  protected boolean isGenericFeature(Object feature)
  {
    return feature == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES || feature == EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS
        || feature == EcorePackage.Literals.EOPERATION__EGENERIC_EXCEPTIONS || feature == EcorePackage.Literals.EOPERATION__ETYPE_PARAMETERS
        || feature == EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE;
  }

  /**
   * This populates the specified <code>manager</code> with {@link org.eclipse.jface.action.ActionContributionItem}s
   * based on the {@link org.eclipse.jface.action.IAction}s contained in the <code>actions</code> collection,
   * by inserting them before the specified contribution item <code>contributionID</code>.
   * If <code>contributionID</code> is <code>null</code>, they are simply added.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void populateManager(IContributionManager manager, Collection<? extends IAction> actions, String contributionID)
  {
    if (actions != null)
    {
      // Look for actions that create EAnnotations.
      //
      Set<IAction> ignoredActions = new HashSet<IAction>();
      Set<IAction> annotationActions = new LinkedHashSet<IAction>();
      for (IAction action : actions)
      {
        if (action instanceof EObjectProvider)
        {
          EObjectProvider eObjectProvider = (EObjectProvider)action;
          EObject eObject = eObjectProvider.getEObject();
          if (eObject instanceof EAnnotation)
          {
            annotationActions.add(action);
            ignoredActions.add(action);
          }
        }
      }

      // If there is more than one action that creates an annotation...
      //
      if (annotationActions.size() > 1)
      {
        // Create a menu manager to group them.
        // This assumes the first action is one for the null source.
        //
        IAction action = annotationActions.iterator().next();
        String actionText = action.getText();
        MenuManager annotationMenuManager = new MenuManager(actionText, action.getImageDescriptor(), "annotations");

        // Add that menu manager instead of the individual actions.
        if (contributionID != null)
        {
          manager.insertBefore(contributionID, annotationMenuManager);
        }
        else
        {
          manager.add(annotationMenuManager);
        }

        // Add an item for each annotation action.
        //
        for (IAction annotationAction : annotationActions)
        {
          annotationMenuManager.add(annotationAction);
          String source = ((EAnnotation)((EObjectProvider)annotationAction).getEObject()).getSource();
          if (source != null)
          {
            // Set the label to include the source.
            //
            annotationAction.setText(actionText + " - " + source);
          }
        }
      }
      else
      {
        ignoredActions.clear();
      }

      for (IAction action : actions)
      {
        if (!ignoredActions.contains(action))
        {
          if (contributionID != null)
          {
            manager.insertBefore(contributionID, action);
          }
          else
          {
            manager.add(action);
          }
        }
      }
    }
  }

  /**
   * This removes from the specified <code>manager</code> all {@link org.eclipse.jface.action.ActionContributionItem}s
   * based on the {@link org.eclipse.jface.action.IAction}s contained in the <code>actions</code> collection.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void depopulateManager(IContributionManager manager, Collection<? extends IAction> actions)
  {
    if (actions != null)
    {
      IContributionItem[] items = manager.getItems();
      for (int i = 0; i < items.length; i++)
      {
        // Look into SubContributionItems
        //
        IContributionItem contributionItem = items[i];
        while (contributionItem instanceof SubContributionItem)
        {
          contributionItem = ((SubContributionItem)contributionItem).getInnerItem();
        }

        // Delete the ActionContributionItems with matching action.
        //
        if (contributionItem instanceof ActionContributionItem)
        {
          IAction action = ((ActionContributionItem)contributionItem).getAction();
          if (actions.contains(action))
          {
            manager.remove(contributionItem);
          }
        }
      }
    }
  }

  /**
   * This populates the pop-up menu before it appears.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void menuAboutToShow(IMenuManager menuManager)
  {
    super.menuAboutToShow(menuManager);
    MenuManager submenuManager = null;

    submenuManager = new MenuManager(EvolutionEditorPlugin.INSTANCE.getString("_UI_CreateChild_menu_item"));
    populateManager(submenuManager, createChildActions, null);
    menuManager.insertBefore("edit", submenuManager);

    submenuManager = new MenuManager(EvolutionEditorPlugin.INSTANCE.getString("_UI_CreateSibling_menu_item"));
    populateManager(submenuManager, createSiblingActions, null);
    menuManager.insertBefore("edit", submenuManager);
  }

  /**
   * This inserts global actions before the "additions-end" separator.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void addGlobalActions(IMenuManager menuManager)
  {
    menuManager.insertAfter("additions-end", new Separator("ui-actions"));
    menuManager.insertAfter("ui-actions", showPropertiesViewAction);

    refreshViewerAction.setEnabled(refreshViewerAction.isEnabled());
    menuManager.insertAfter("ui-actions", refreshViewerAction);

    super.addGlobalActions(menuManager);
  }

  /**
   * This ensures that a delete action will clean up all references to deleted objects.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean removeAllReferencesOnDelete()
  {
    return true;
  }

  /**
   * An interface implemented by {@link EcoreCreateChildAction} and {@link EcoreCreateSiblingAction} to provide access to the data in the descriptor.
   */
  public interface EObjectProvider
  {
    public EObject getEObject();
  }

  /**
   * A create child action subclass that provides access to the {@link #descriptor} and specializes {@link #run()} to show the properties view.
   */
  public class EcoreCreateChildAction extends CreateChildAction implements EObjectProvider
  {
    public EcoreCreateChildAction(IWorkbenchPart workbenchPart, ISelection selection, Object descriptor)
    {
      super(workbenchPart, selection, descriptor);
    }

    public EObject getEObject()
    {
      if (descriptor instanceof CommandParameter)
      {
        CommandParameter commandParameter = (CommandParameter)descriptor;
        return commandParameter.getEValue();
      }
      else
      {
        return null;
      }
    }

    @Override
    public void run()
    {
      super.run();

      // This is dispatched twice because the command stack listener dispatches once and then the viewer selection is
      // also dispatches once,
      // and we need to delay until the selection is established.
      //
      final Display display = getPage().getWorkbenchWindow().getShell().getDisplay();
      display.asyncExec(new Runnable()
      {
        public void run()
        {
          display.asyncExec(new Runnable()
          {
            public void run()
            {
              showPropertiesViewAction.run();
            }
          });
        }
      });
    }
  }

  /**
   * A create sibling action subclass that provides access to the {@link #descriptor} and specializes {@link #run()} to show the properties view.
   */
  public class EcoreCreateSiblingAction extends CreateSiblingAction implements EObjectProvider
  {
    public EcoreCreateSiblingAction(IWorkbenchPart workbenchPart, ISelection selection, Object descriptor)
    {
      super(workbenchPart, selection, descriptor);
    }

    public EObject getEObject()
    {
      if (descriptor instanceof CommandParameter)
      {
        CommandParameter commandParameter = (CommandParameter)descriptor;
        return commandParameter.getEValue();
      }
      else
      {
        return null;
      }
    }

    @Override
    public void run()
    {
      super.run();

      // This is dispatched twice because the command stack listener dispatches once and then the viewer selection is
      // also dispatches once,
      // and we need to delay until the selection is established.
      //
      final Display display = getPage().getWorkbenchWindow().getShell().getDisplay();
      display.asyncExec(new Runnable()
      {
        public void run()
        {
          display.asyncExec(new Runnable()
          {
            public void run()
            {
              showPropertiesViewAction.run();
            }
          });
        }
      });
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class IDAction extends Action
  {
    public IDAction(String text)
    {
      super(text);
    }

    @Override
    public void run()
    {
      if (selectionProvider != null)
      {
        ISelection selection = selectionProvider.getSelection();
        if (selection instanceof IStructuredSelection)
        {
          IStructuredSelection ssel = (IStructuredSelection)selection;
          final Object element = ssel.getFirstElement();
          if (element instanceof EModelElement)
          {
            final EModelElement rootElement = (EModelElement)element;

            EditingDomain domain = ((IEditingDomainProvider)activeEditorPart).getEditingDomain();
            ChangeCommand command = new ChangeCommand(rootElement)
            {
              @Override
              protected void doExecute()
              {
                ElementHandler.execute(rootElement, new ElementRunnable()
                {
                  public void run(EModelElement modelElement)
                  {
                    handleID(modelElement);
                  }
                });
              }
            };

            domain.getCommandStack().execute(command);
          }
        }
      }
    }

    protected abstract void handleID(EModelElement modelElement);
  }

}
