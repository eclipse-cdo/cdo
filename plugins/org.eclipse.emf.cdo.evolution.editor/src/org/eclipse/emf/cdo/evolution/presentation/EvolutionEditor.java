/**
 */
package org.eclipse.emf.cdo.evolution.presentation;

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.evolution.impl.EvolutionImpl;
import org.eclipse.emf.cdo.evolution.presentation.quickfix.DiagnosticResolution;
import org.eclipse.emf.cdo.evolution.presentation.quickfix.QuickFixWizard;
import org.eclipse.emf.cdo.evolution.provider.EvolutionItemProviderAdapterFactory;
import org.eclipse.emf.cdo.evolution.util.DiagnosticID;
import org.eclipse.emf.cdo.evolution.util.ElementHandler;
import org.eclipse.emf.cdo.evolution.util.ValidationContext;
import org.eclipse.emf.cdo.evolution.util.ValidationPhase;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.ImageURIRegistry;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.ui.editor.ProblemEditorPart;
import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.EMFEditUIPlugin;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DecoratingColumLabelProvider;
import org.eclipse.emf.edit.ui.provider.DelegatingStyledCellLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator.DiagnosticAdapter;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator.LiveValidator;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.emf.edit.ui.provider.UnwrappingSelectionProvider;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.emf.edit.ui.util.FindAndReplaceTarget;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is an example of a Evolution model editor.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class EvolutionEditor extends MultiPageEditorPart implements IEditingDomainProvider, ISelectionProvider, IMenuListener, IViewerProvider, IGotoMarker
{
  /**
   * This keeps track of the editing domain that is used to track all changes to the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AdapterFactoryEditingDomain editingDomain;

  /**
   * This is the one adapter factory used for providing views of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ComposedAdapterFactory adapterFactory;

  /**
   * This is the content outline page.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IContentOutlinePage contentOutlinePage;

  /**
   * This is a kludge...
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IStatusLineManager contentOutlineStatusLineManager;

  /**
   * This is the content outline page's viewer.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TreeViewer contentOutlineViewer;

  /**
   * This is the property sheet page.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected List<PropertySheetPage> propertySheetPages = new ArrayList<PropertySheetPage>();

  /**
   * This is the viewer that shadows the selection in the content outline.
   * The parent relation must be correctly defined for this to work.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TreeViewer selectionViewer;

  /**
   * This keeps track of the active content viewer, which may be either one of the viewers in the pages or the content outline viewer.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Viewer currentViewer;

  /**
   * This listens to which ever viewer is active.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ISelectionChangedListener selectionChangedListener;

  /**
   * This keeps track of all the {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are listening to this editor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

  /**
   * This keeps track of the selection of the editor as a whole.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ISelection editorSelection = StructuredSelection.EMPTY;

  /**
   * The MarkerHelper is responsible for creating workspace resource markers presented
   * in Eclipse's Problems View.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MarkerHelper markerHelper = new EditUIMarkerHelper();

  /**
   * This listens for when the outline becomes active
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IPartListener partListener = new IPartListener()
  {
    public void partActivated(IWorkbenchPart p)
    {
      if (p instanceof ContentOutline)
      {
        if (((ContentOutline)p).getCurrentPage() == contentOutlinePage)
        {
          getActionBarContributor().setActiveEditor(EvolutionEditor.this);

          setCurrentViewer(contentOutlineViewer);
        }
      }
      else if (p instanceof PropertySheet)
      {
        if (propertySheetPages.contains(((PropertySheet)p).getCurrentPage()))
        {
          getActionBarContributor().setActiveEditor(EvolutionEditor.this);
          handleActivate();
        }
      }
      else if (p == EvolutionEditor.this)
      {
        handleActivate();
      }
    }

    public void partBroughtToTop(IWorkbenchPart p)
    {
      // Ignore.
    }

    public void partClosed(IWorkbenchPart p)
    {
      // Ignore.
    }

    public void partDeactivated(IWorkbenchPart p)
    {
      // Ignore.
    }

    public void partOpened(IWorkbenchPart p)
    {
      // Ignore.
    }
  };

  /**
   * Resources that have been removed since last activation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Resource> removedResources = new ArrayList<Resource>();

  /**
   * Resources that have been changed since last activation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Resource> changedResources = new ArrayList<Resource>();

  /**
   * Resources that have been saved.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Resource> savedResources = new ArrayList<Resource>();

  /**
   * Map to store the diagnostic associated with a resource.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<Resource, Diagnostic>();

  /**
   * Controls whether the problem indication should be updated.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected boolean updateProblemIndication = true;

  /**
   * Adapter used to update the problem indication when resources are demanded loaded.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EContentAdapter problemIndicationAdapter = new EContentAdapter()
  {
    protected boolean dispatching;

    @Override
    public void notifyChanged(Notification notification)
    {
      if (notification.getNotifier() instanceof Resource)
      {
        switch (notification.getFeatureID(Resource.class))
        {
        case Resource.RESOURCE__IS_LOADED:
        case Resource.RESOURCE__ERRORS:
        case Resource.RESOURCE__WARNINGS:
        {
          Resource resource = (Resource)notification.getNotifier();
          Diagnostic diagnostic = analyzeResourceProblems(resource, null);
          if (diagnostic.getSeverity() != Diagnostic.OK)
          {
            resourceToDiagnosticMap.put(resource, diagnostic);
          }
          else
          {
            resourceToDiagnosticMap.remove(resource);
          }
          dispatchUpdateProblemIndication();
          break;
        }
        }
      }
      else
      {
        super.notifyChanged(notification);
      }
    }

    protected void dispatchUpdateProblemIndication()
    {
      if (updateProblemIndication && !dispatching)
      {
        dispatching = true;
        getSite().getShell().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            dispatching = false;
            updateProblemIndication();
          }
        });
      }
    }

    @Override
    protected void setTarget(Resource target)
    {
      basicSetTarget(target);
    }

    @Override
    protected void unsetTarget(Resource target)
    {
      basicUnsetTarget(target);
      resourceToDiagnosticMap.remove(target);
      dispatchUpdateProblemIndication();
    }
  };

  /**
   * This listens for workspace changes.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IResourceChangeListener resourceChangeListener = new IResourceChangeListener()
  {
    public void resourceChanged(IResourceChangeEvent event)
    {
      IResourceDelta delta = event.getDelta();
      try
      {
        class ResourceDeltaVisitor implements IResourceDeltaVisitor
        {
          protected ResourceSet resourceSet = editingDomain.getResourceSet();

          protected Collection<Resource> changedResources = new ArrayList<Resource>();

          protected Collection<Resource> removedResources = new ArrayList<Resource>();

          public boolean visit(final IResourceDelta delta)
          {
            if (delta.getResource().getType() == IResource.FILE)
            {
              if (delta.getKind() == IResourceDelta.REMOVED || delta.getKind() == IResourceDelta.CHANGED)
              {
                final Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(delta.getFullPath().toString(), true), false);
                if (resource != null)
                {
                  if (delta.getKind() == IResourceDelta.REMOVED)
                  {
                    removedResources.add(resource);
                  }
                  else
                  {
                    if ((delta.getFlags() & IResourceDelta.MARKERS) != 0)
                    {
                      DiagnosticDecorator.Styled.DiagnosticAdapter.update(resource,
                          markerHelper.getMarkerDiagnostics(resource, (IFile)delta.getResource(), false));
                    }
                    if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
                    {
                      if (!savedResources.remove(resource))
                      {
                        changedResources.add(resource);
                      }
                    }
                  }
                }
              }
              return false;
            }

            return true;
          }

          public Collection<Resource> getChangedResources()
          {
            return changedResources;
          }

          public Collection<Resource> getRemovedResources()
          {
            return removedResources;
          }
        }

        final ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
        delta.accept(visitor);

        if (!visitor.getRemovedResources().isEmpty())
        {
          getSite().getShell().getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              removedResources.addAll(visitor.getRemovedResources());
              if (!isDirty())
              {
                getSite().getPage().closeEditor(EvolutionEditor.this, false);
              }
            }
          });
        }

        if (!visitor.getChangedResources().isEmpty())
        {
          getSite().getShell().getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              changedResources.addAll(visitor.getChangedResources());
              if (getSite().getPage().getActiveEditor() == EvolutionEditor.this)
              {
                handleActivate();
              }
            }
          });
        }
      }
      catch (CoreException exception)
      {
        EvolutionEditorPlugin.INSTANCE.log(exception);
      }
    }
  };

  protected EcoreItemProviderAdapterFactory ecoreItemProviderAdapterFactory;

  private TableViewer problemViewer;

  private final Set<Object> readOnlyObjects = new HashSet<Object>();

  private Evolution evolution;

  private Diagnostic[] allDiagnostics;

  /**
   * Handles activation of the editor or it's associated views.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void handleActivate()
  {
    // Recompute the read only state.
    //
    if (editingDomain.getResourceToReadOnlyMap() != null)
    {
      editingDomain.getResourceToReadOnlyMap().clear();

      // Refresh any actions that may become enabled or disabled.
      //
      setSelection(getSelection());
    }

    if (!removedResources.isEmpty())
    {
      if (handleDirtyConflict())
      {
        getSite().getPage().closeEditor(EvolutionEditor.this, false);
      }
      else
      {
        removedResources.clear();
        changedResources.clear();
        savedResources.clear();
      }
    }
    else if (!changedResources.isEmpty())
    {
      changedResources.removeAll(savedResources);
      handleChangedResources();
      changedResources.clear();
      savedResources.clear();
    }
  }

  /**
   * Handles what to do with changed resources on activation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void handleChangedResources()
  {
    if (!changedResources.isEmpty() && (!isDirty() || handleDirtyConflict()))
    {
      ResourceSet resourceSet = editingDomain.getResourceSet();
      if (isDirty())
      {
        changedResources.addAll(resourceSet.getResources());
      }
      editingDomain.getCommandStack().flush();

      updateProblemIndication = false;
      for (Resource resource : changedResources)
      {
        if (resource.isLoaded())
        {
          resource.unload();
          try
          {
            resource.load(resourceSet.getLoadOptions());
          }
          catch (IOException exception)
          {
            if (!resourceToDiagnosticMap.containsKey(resource))
            {
              resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
            }
          }
        }
      }

      if (AdapterFactoryEditingDomain.isStale(editorSelection))
      {
        setSelection(StructuredSelection.EMPTY);
      }

      updateProblemIndication = true;
      updateProblemIndication();
    }
  }

  /**
   * Updates the problems indication with the information described in the specified diagnostic.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void updateProblemIndication()
  {
    if (updateProblemIndication)
    {
      BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK, "org.eclipse.emf.cdo.evolution.editor", 0, null,
          new Object[] { editingDomain.getResourceSet() });
      for (Diagnostic childDiagnostic : resourceToDiagnosticMap.values())
      {
        if (childDiagnostic.getSeverity() != Diagnostic.OK)
        {
          diagnostic.add(childDiagnostic);
        }
      }

      int lastEditorPage = getPageCount() - 1;
      if (lastEditorPage >= 0 && getEditor(lastEditorPage) instanceof ProblemEditorPart)
      {
        ((ProblemEditorPart)getEditor(lastEditorPage)).setDiagnostic(diagnostic);
        if (diagnostic.getSeverity() != Diagnostic.OK)
        {
          setActivePage(lastEditorPage);
        }
      }
      else if (diagnostic.getSeverity() != Diagnostic.OK)
      {
        ProblemEditorPart problemEditorPart = new ProblemEditorPart();
        problemEditorPart.setDiagnostic(diagnostic);
        problemEditorPart.setMarkerHelper(markerHelper);
        try
        {
          addPage(++lastEditorPage, problemEditorPart, getEditorInput());
          setPageText(lastEditorPage, problemEditorPart.getPartName());
          setActivePage(lastEditorPage);
          showTabs();
        }
        catch (PartInitException exception)
        {
          EvolutionEditorPlugin.INSTANCE.log(exception);
        }
      }

      if (markerHelper.hasMarkers(editingDomain.getResourceSet()))
      {
        try
        {
          markerHelper.updateMarkers(diagnostic);
        }
        catch (CoreException exception)
        {
          EvolutionEditorPlugin.INSTANCE.log(exception);
        }
      }
    }
  }

  /**
   * Shows a dialog that asks if conflicting changes should be discarded.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected boolean handleDirtyConflict()
  {
    return MessageDialog.openQuestion(getSite().getShell(), getString("_UI_FileConflict_label"), getString("_WARN_FileConflict"));
  }

  /**
   * This creates a model editor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EvolutionEditor()
  {
    super();
    initializeEditingDomain();
  }

  /**
   * This sets up the editing domain for the model editor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void initializeEditingDomain()
  {
    // Create an adapter factory that yields item providers.
    //
    adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new EvolutionItemProviderAdapterFactory());
    ecoreItemProviderAdapterFactory = new EcoreItemProviderAdapterFactory();
    adapterFactory.addAdapterFactory(ecoreItemProviderAdapterFactory);
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

    // Create the command stack that will notify this editor as commands are executed.
    //
    BasicCommandStack commandStack = new BasicCommandStack()
    {
      @Override
      public void execute(Command command)
      {
        // Cancel live validation before executing a command that will trigger a new round of validation.
        //
        if (!(command instanceof AbstractCommand.NonDirtying))
        {
          DiagnosticDecorator.Styled.cancel(editingDomain);
        }
        super.execute(command);
      }
    };

    // Add a listener to set the most recent command's affected objects to be the selection of the viewer with focus.
    //
    commandStack.addCommandStackListener(new CommandStackListener()
    {
      public void commandStackChanged(final EventObject event)
      {
        ((EvolutionImpl)evolution).invalidateChange();

        getContainer().getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            firePropertyChange(IEditorPart.PROP_DIRTY);

            // Try to select the affected objects.
            //
            Command mostRecentCommand = ((CommandStack)event.getSource()).getMostRecentCommand();
            if (mostRecentCommand != null)
            {
              setSelectionToViewer(mostRecentCommand.getAffectedObjects());
            }
            for (Iterator<PropertySheetPage> i = propertySheetPages.iterator(); i.hasNext();)
            {
              PropertySheetPage propertySheetPage = i.next();
              if (propertySheetPage.getControl().isDisposed())
              {
                i.remove();
              }
              else
              {
                propertySheetPage.refresh();
              }
            }
          }
        });
      }
    });

    // Create the editing domain with a special command stack.
    //
    editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>())
    {
      @Override
      public Command createCommand(Class<? extends Command> commandClass, CommandParameter commandParameter)
      {
        Collection<?> collection = commandParameter.getCollection();
        if (collection != null && !collection.isEmpty())
        {
          Object value = collection.iterator().next();
          if (isReadOnlyObject(value))
          {
            return UnexecutableCommand.INSTANCE;
          }
        }

        return super.createCommand(commandClass, commandParameter);
      }
    };
  }

  protected boolean isReadOnlyObject(Object object)
  {
    if (object instanceof Release)
    {
      return true;
    }

    if (object instanceof Change)
    {
      return true;
    }

    if (readOnlyObjects.contains(object))
    {
      return true;
    }

    if (object instanceof EModelElement)
    {
      EModelElement modelElement = (EModelElement)object;

      if (evolution.containsElement(modelElement))
      {
        return false;
      }

      for (Release release : evolution.getReleases())
      {
        if (release.containsElement(modelElement))
        {
          readOnlyObjects.add(modelElement);
          return true;
        }
      }
    }

    return false;
  }

  /**
   * This is here for the listener to be able to call it.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void firePropertyChange(int action)
  {
    super.firePropertyChange(action);
  }

  /**
   * This sets the selection into whichever viewer is active.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSelectionToViewer(Collection<?> collection)
  {
    final Collection<?> theSelection = collection;
    // Make sure it's okay.
    //
    if (theSelection != null && !theSelection.isEmpty())
    {
      Runnable runnable = new Runnable()
      {
        public void run()
        {
          // Try to select the items in the current content viewer of the editor.
          //
          if (currentViewer != null)
          {
            currentViewer.setSelection(new StructuredSelection(theSelection.toArray()), true);
          }
        }
      };
      getSite().getShell().getDisplay().asyncExec(runnable);
    }
  }

  /**
   * This returns the editing domain as required by the {@link IEditingDomainProvider} interface.
   * This is important for implementing the static methods of {@link AdapterFactoryEditingDomain}
   * and for supporting {@link org.eclipse.emf.edit.ui.action.CommandAction}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EditingDomain getEditingDomain()
  {
    return editingDomain;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public class ReverseAdapterFactoryContentProvider extends AdapterFactoryContentProvider
  {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ReverseAdapterFactoryContentProvider(AdapterFactory adapterFactory)
    {
      super(adapterFactory);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object[] getElements(Object object)
    {
      Object parent = super.getParent(object);
      return (parent == null ? Collections.EMPTY_SET : Collections.singleton(parent)).toArray();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object[] getChildren(Object object)
    {
      Object parent = super.getParent(object);
      return (parent == null ? Collections.EMPTY_SET : Collections.singleton(parent)).toArray();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean hasChildren(Object object)
    {
      Object parent = super.getParent(object);
      return parent != null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getParent(Object object)
    {
      return null;
    }
  }

  /**
   * This makes sure that one content viewer, either for the current page or the outline view, if it has focus,
   * is the current one.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCurrentViewer(Viewer viewer)
  {
    // If it is changing...
    //
    if (currentViewer != viewer)
    {
      if (selectionChangedListener == null)
      {
        // Create the listener on demand.
        //
        selectionChangedListener = new ISelectionChangedListener()
        {
          // This just notifies those things that are affected by the section.
          //
          public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
          {
            setSelection(selectionChangedEvent.getSelection());
          }
        };
      }

      // Stop listening to the old one.
      //
      if (currentViewer != null)
      {
        currentViewer.removeSelectionChangedListener(selectionChangedListener);
      }

      // Start listening to the new one.
      //
      if (viewer != null)
      {
        viewer.addSelectionChangedListener(selectionChangedListener);
      }

      // Remember it.
      //
      currentViewer = viewer;

      // Set the editors selection based on the current viewer's selection.
      //
      setSelection(currentViewer == null ? StructuredSelection.EMPTY : currentViewer.getSelection());
    }
  }

  /**
   * This returns the viewer as required by the {@link IViewerProvider} interface.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Viewer getViewer()
  {
    return currentViewer;
  }

  /**
   * This creates a context menu for the viewer and adds a listener as well registering the menu for extension.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void createContextMenuFor(StructuredViewer viewer)
  {
    MenuManager contextMenu = new MenuManager("#PopUp");
    contextMenu.add(new Separator("additions"));
    contextMenu.setRemoveAllWhenShown(true);
    contextMenu.addMenuListener(this);
    Menu menu = contextMenu.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(contextMenu, new UnwrappingSelectionProvider(viewer));

    int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
    Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance(), LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() };
    viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer));
    viewer.addDropSupport(dndOperations, transfers, new EditingDomainViewerDropAdapter(editingDomain, viewer)
    {
      @Override
      public void dropAccept(DropTargetEvent event)
      {
        super.dropAccept(event);
      }

      @Override
      public void drop(DropTargetEvent event)
      {
        super.drop(event);
      }
    });
  }

  /**
   * This is the method called to load a resource into the editing domain's resource set based on the editor's input.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createModelGen()
  {
    URI resourceURI = EditUIUtil.getURI(getEditorInput(), editingDomain.getResourceSet().getURIConverter());
    Exception exception = null;
    Resource resource = null;
    try
    {
      // Load the resource through the editing domain.
      //
      resource = editingDomain.getResourceSet().getResource(resourceURI, true);
    }
    catch (Exception e)
    {
      exception = e;
      resource = editingDomain.getResourceSet().getResource(resourceURI, false);
    }

    Diagnostic diagnostic = analyzeResourceProblems(resource, exception);
    if (diagnostic.getSeverity() != Diagnostic.OK)
    {
      resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
    }
    editingDomain.getResourceSet().eAdapters().add(problemIndicationAdapter);
  }

  /**
   * This is the method called to load a resource into the editing domain's resource set based on the editor's input.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public void createModel()
  {
    createModelGen();

    ResourceSet resourceSet = editingDomain.getResourceSet();
    evolution = EvolutionImpl.get(resourceSet);
    if (evolution != null)
    {
      if (hasGenerics(evolution))
      {
        ((EvolutionActionBarContributor)getActionBarContributor()).showGenerics(true);
      }
    }
  }

  private boolean hasGenerics(Evolution evolution)
  {
    if (hasGenerics(evolution.getRootPackages()))
    {
      return true;
    }

    for (Release release : evolution.getReleases())
    {
      if (hasGenerics(release.getRootPackages()))
      {
        return true;
      }
    }

    return false;
  }

  private boolean hasGenerics(EList<EPackage> rootPackages)
  {
    for (EPackage rootPackage : rootPackages)
    {
      for (Iterator<EObject> i = rootPackage.eAllContents(); i.hasNext();)
      {
        EObject eObject = i.next();
        if (eObject instanceof ETypeParameter || eObject instanceof EGenericType && !((EGenericType)eObject).getETypeArguments().isEmpty())
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Returns a diagnostic describing the errors and warnings listed in the resource
   * and the specified exception (if any).
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Diagnostic analyzeResourceProblems(Resource resource, Exception exception)
  {
    boolean hasErrors = !resource.getErrors().isEmpty();
    if (hasErrors || !resource.getWarnings().isEmpty())
    {
      BasicDiagnostic basicDiagnostic = new BasicDiagnostic(hasErrors ? Diagnostic.ERROR : Diagnostic.WARNING, "org.eclipse.emf.cdo.evolution.editor", 0,
          getString("_UI_CreateModelError_message", resource.getURI()), new Object[] { exception == null ? (Object)resource : exception });
      basicDiagnostic.merge(EcoreUtil.computeDiagnostic(resource, true));
      return basicDiagnostic;
    }
    else if (exception != null)
    {
      return new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.evolution.editor", 0, getString("_UI_CreateModelError_message", resource.getURI()),
          new Object[] { exception });
    }
    else
    {
      return Diagnostic.OK_INSTANCE;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class PhasedLiveValidator extends LiveValidator
  {
    public PhasedLiveValidator(EditingDomain editingDomain, IDialogSettings dialogSettings)
    {
      super(editingDomain, dialogSettings);
    }

    @Override
    public void scheduleValidation()
    {
      if (validationJob == null && (dialogSettings == null || dialogSettings.getBoolean(LiveValidationAction.LIVE_VALIDATOR_DIALOG_SETTINGS_KEY)))
      {
        validationJob = new Job("Validation Job")
        {
          @Override
          protected IStatus run(final IProgressMonitor monitor)
          {
            try
            {
              Diagnostician diagnostician = new Diagnostician()
              {
                @Override
                public String getObjectLabel(EObject eObject)
                {
                  String text = labelProvider != null && eObject.eIsProxy() ? ((InternalEObject)eObject).eProxyURI().toString()
                      : labelProvider.getText(eObject);
                  if (text == null || text.length() == 0)
                  {
                    text = "<i>null</i>";
                  }
                  else
                  {
                    text = DiagnosticDecorator.escapeContent(text);
                  }
                  Image image = labelProvider != null ? labelProvider.getImage(eObject) : null;
                  if (image != null)
                  {
                    URI imageURI = ImageURIRegistry.INSTANCE.getImageURI(image);
                    return DiagnosticDecorator.enquote("<img src='" + imageURI + "'/> " + text);
                  }
                  else
                  {
                    return text;
                  }
                }

                @Override
                public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
                {
                  if (monitor.isCanceled())
                  {
                    throw new RuntimeException();
                  }

                  monitor.worked(1);
                  return super.validate(eClass, eObject, diagnostics, context);
                }

                @Override
                protected boolean doValidateContents(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
                {
                  Evolution evolution;
                  List<? extends EObject> eContents;

                  ValidationContext validationContext = ValidationContext.getFrom(context);
                  if (validationContext != null)
                  {
                    ValidationPhase phase = validationContext.getPhase();
                    evolution = validationContext.getEvolution();
                    eContents = phase.getContentsToValidate(this, evolution, eObject, context);
                  }
                  else
                  {
                    evolution = null;
                    eContents = eObject.eContents();
                  }

                  boolean result = true;
                  for (EObject child : eContents)
                  {
                    Resource resource = child.eResource();
                    if (resource == null && evolution != null)
                    {
                      // Change objects are not contained in a resource. Use the evolution's resource.
                      resource = evolution.eResource();
                    }

                    DiagnosticChain resourceDiagnostic = resource != null && validationContext != null
                        ? validationContext.getResourceDiagnostics().get(resource)
                        : null;

                    result &= validate(child, resourceDiagnostic, context);
                  }

                  return result;
                }
              };

              final ResourceSet resourceSet = editingDomain.getResourceSet();

              Map<Object, Object> context = diagnostician.createDefaultContext();
              context.put(EObjectValidator.ROOT_OBJECT, new Object()); // Disable circular containment validation.

              { ///////////////////////////////////////////////////////////////////////////////

                Evolution evolution = EvolutionImpl.get(resourceSet);
                ValidationContext validationContext = new ValidationContext(evolution);
                validationContext.putInto(context);

                int count = 0;
                for (ValidationPhase phase : ValidationPhase.values())
                {
                  validationContext.setPhase(phase);

                  // int xxx;
                  // System.out.println("Count " + phase);

                  count += countObjects(diagnostician, evolution, validationContext, context, monitor);
                }

                scheduledResources.clear();
                monitor.beginTask("", count);

                Map<Resource, BasicDiagnostic> resourceDiagnostics = validationContext.getResourceDiagnostics();
                final BasicDiagnostic rootDiagnostic = new BasicDiagnostic(EObjectValidator.DIAGNOSTIC_SOURCE, 0,
                    EMFEditUIPlugin.INSTANCE.getString("_UI_DiagnosisOfNObjects_message", new String[] { "" + resourceDiagnostics.size() }),
                    new Object[] { resourceSet });

                for (BasicDiagnostic resourceDiagnostic : resourceDiagnostics.values())
                {
                  rootDiagnostic.add(resourceDiagnostic);
                }

                BasicDiagnostic modelSetResourceDiagnostic = resourceDiagnostics.get(evolution.eResource());

                for (ValidationPhase phase : ValidationPhase.values())
                {
                  validationContext.setPhase(phase);

                  // int xxx;
                  // System.out.println("Validate " + phase);

                  monitor
                      .setTaskName(EvolutionEditorPlugin.INSTANCE.getString("_UI_ValidatingPhase_message", new Object[] { Integer.toString(phase.ordinal()) }));

                  boolean valid = diagnostician.validate(evolution, modelSetResourceDiagnostic, context);
                  if (!valid)
                  {
                    break;
                  }
                }

                if (monitor.isCanceled())
                {
                  throw new RuntimeException();
                }

                monitor.worked(1);

                DiagnosticAdapter.update(resourceSet, rootDiagnostic);

              } ///////////////////////////////////////////////////////////////////////////////

              Display.getDefault().asyncExec(new Runnable()
              {
                public void run()
                {
                  validationJob = null;
                  if (!monitor.isCanceled() && !scheduledResources.isEmpty())
                  {
                    PhasedLiveValidator.this.scheduleValidation();
                  }
                }
              });

              return Status.OK_STATUS;
            }
            catch (RuntimeException exception)
            {
              int xxx;
              exception.printStackTrace();

              validationJob = null;
              return Status.CANCEL_STATUS;
            }
          }

          private int countObjects(Diagnostician diagnostician, EObject eObject, ValidationContext validationContext, Map<Object, Object> context,
              IProgressMonitor monitor)
          {
            if (monitor.isCanceled())
            {
              throw new RuntimeException();
            }

            for (EReference eReference : eObject.eClass().getEAllReferences())
            {
              if (monitor.isCanceled())
              {
                throw new RuntimeException();
              }

              if (eReference == EvolutionPackage.Literals.MODEL_SET__CHANGE)
              {
                continue;
              }

              if (eReference.isContainment() || eReference.isContainer())
              {
                continue;
              }

              // Resolve all proxies.
              if (eReference.isResolveProxies())
              {
                Object value = eObject.eGet(eReference, true);
                if (eReference.isMany())
                {
                  @SuppressWarnings("unchecked")
                  List<EObject> list = (List<EObject>)value;

                  for (@SuppressWarnings("unused")
                  EObject element : list)
                  {
                    if (monitor.isCanceled())
                    {
                      throw new RuntimeException();
                    }
                  }
                }
              }
            }

            Resource resource = eObject.eResource();
            if (resource != null)
            {
              Map<Resource, BasicDiagnostic> resourceDiagnostics = validationContext.getResourceDiagnostics();
              if (!resourceDiagnostics.containsKey(resource))
              {
                BasicDiagnostic diagnostic = new BasicDiagnostic(EObjectValidator.DIAGNOSTIC_SOURCE, 0,
                    EMFEditUIPlugin.INSTANCE.getString("_UI_DiagnosisOfNObjects_message", new String[] { "1" }), new Object[] { resource });
                resourceDiagnostics.put(resource, diagnostic);
              }
            }

            Evolution evolution = validationContext.getEvolution();
            ValidationPhase phase = validationContext.getPhase();
            int count = 1;

            for (EObject child : phase.getContentsToValidate(diagnostician, evolution, eObject, context))
            {
              count += countObjects(diagnostician, child, validationContext, context, monitor);
            }

            return count;
          }
        };

        validationJob.setPriority(Job.DECORATE);
        validationJob.schedule(500);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class DiagnosticContentProvider implements IStructuredContentProvider
  {
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public Object[] getElements(Object inputElement)
    {
      List<Diagnostic> result = new ArrayList<Diagnostic>();

      @SuppressWarnings("unchecked")
      List<Diagnostic> diagnostics = (List<Diagnostic>)inputElement;
      for (Diagnostic diagnostic : diagnostics)
      {
        collectDiagnostics(diagnostic, result);
      }

      return result.toArray(new Diagnostic[result.size()]);
    }

    private void collectDiagnostics(Diagnostic diagnostic, List<Diagnostic> result)
    {
      if (isRelevant(diagnostic))
      {
        result.add(diagnostic);
      }

      for (Diagnostic child : diagnostic.getChildren())
      {
        collectDiagnostics(child, result);
      }
    }

    private boolean isRelevant(Diagnostic diagnostic)
    {
      // if (diagnostic.getSeverity() != Diagnostic.ERROR)
      // {
      // return false;
      // }

      List<?> data = diagnostic.getData();
      if (data.isEmpty())
      {
        return false;
      }

      if (data.get(0) instanceof Resource)
      {
        return false;
      }

      return true;
    }

    public void dispose()
    {
    }
  }

  /**
   * This is the method used by the framework to install your own controls.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void createPages()
  {
    // Creates the model from the editor input
    //
    createModel();

    // Only creates the other pages if there is something that can be edited
    //
    if (!getEditingDomain().getResourceSet().getResources().isEmpty())
    {
      // Create a page for the selection tree view.
      //

      SashForm mainSash = new SashForm(getContainer(), SWT.VERTICAL);
      mainSash.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL));

      selectionViewer = createSelectionViewer(mainSash);
      TableViewer problemViewer = createProblemViewer(mainSash);

      mainSash.setWeights(new int[] { 70, 30 });

      int pageIndex = addPage(mainSash);
      setPageText(pageIndex, getString("_UI_SelectionPage_label"));

      getSite().getShell().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          if (!getContainer().isDisposed())
          {
            setActivePage(0);
          }
        }
      });

      this.problemViewer = problemViewer;
    }

    // Ensures that this editor will only display the page's tab
    // area if there are more than one page
    //
    getContainer().addControlListener(new ControlAdapter()
    {
      boolean guard = false;

      @Override
      public void controlResized(ControlEvent event)
      {
        if (!guard)
        {
          guard = true;
          hideTabs();
          guard = false;
        }
      }
    });

    getSite().getShell().getDisplay().asyncExec(new Runnable()
    {
      public void run()
      {
        updateProblemIndication();
      }
    });
  }

  private TreeViewer createSelectionViewer(Composite parent)
  {
    Tree tree = new Tree(parent, SWT.MULTI);
    TreeViewer selectionViewer = new TreeViewer(tree);
    setCurrentViewer(selectionViewer);

    DiagnosticDecorator diagnosticDecorator = new DiagnosticDecorator.Styled(editingDomain, selectionViewer,
        EvolutionEditorPlugin.getPlugin().getDialogSettings())
    {
      @Override
      protected void redecorate()
      {
        super.redecorate();
        handleDiagnostics(diagnostics);
      }

      @Override
      protected LiveValidator getLiveValidator()
      {
        if (liveValidator == null && editingDomain != null)
        {
          Field field = ReflectUtil.getField(DiagnosticDecorator.class, "LIVE_VALIDATORS");

          @SuppressWarnings("unchecked")
          Map<EditingDomain, LiveValidator> LIVE_VALIDATORS = (Map<EditingDomain, LiveValidator>)ReflectUtil.getValue(field, null);

          liveValidator = LIVE_VALIDATORS.get(editingDomain);
          if (liveValidator == null)
          {
            liveValidator = new PhasedLiveValidator(editingDomain, dialogSettings);

            LIVE_VALIDATORS.put(editingDomain, liveValidator);
          }

          liveValidator.register(this);
        }

        return liveValidator;
      }
    };

    selectionViewer.setUseHashlookup(true);
    selectionViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
    selectionViewer.setLabelProvider(new DelegatingStyledCellLabelProvider.FontAndColorProvider(new DecoratingColumLabelProvider.StyledLabelProvider(
        new AdapterFactoryLabelProvider.StyledLabelProvider(adapterFactory, selectionViewer), diagnosticDecorator)));
    selectionViewer.setInput(editingDomain.getResourceSet().getResources().get(0));
    selectionViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)), true);

    new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);
    new ColumnViewerInformationControlToolTipSupport(selectionViewer,
        new DiagnosticDecorator.Styled.EditingDomainLocationListener(editingDomain, selectionViewer));

    createContextMenuFor(selectionViewer);
    return selectionViewer;
  }

  private TableViewer createProblemViewer(Composite parent)
  {
    final TableViewer problemViewer = new TableViewer(parent, SWT.MULTI);
    problemViewer.setContentProvider(new DiagnosticContentProvider());
    problemViewer.setInput(Collections.emptyList());

    Table problemTable = problemViewer.getTable();
    problemTable.setHeaderVisible(true);
    problemTable.setLinesVisible(true);

    createDiagnosticMessageColumn(problemViewer);
    createDiagnosticElementColumn(problemViewer, adapterFactory);
    createDiagnosticResourceColumn(problemViewer, adapterFactory);
    // createDiagnosticIDColumn(problemViewer);

    // {
    // TableViewerColumn tableViewerColumn = new TableViewerColumn(problemViewer, SWT.NONE);
    // tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    // {
    // @Override
    // public String getText(Object element)
    // {
    // return ((Diagnostic)element).getSource();
    // }
    // });
    //
    // TableColumn tableColumn = tableViewerColumn.getColumn();
    // tableColumn.setResizable(true);
    // tableColumn.setAlignment(SWT.LEFT);
    // tableColumn.setWidth(200);
    // tableColumn.setText("Source");
    // }
    //
    // {
    // TableViewerColumn tableViewerColumn = new TableViewerColumn(problemViewer, SWT.NONE);
    // tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    // {
    //
    // @Override
    // public String getText(Object element)
    // {
    // return Integer.toString(((Diagnostic)element).getCode());
    // }
    // });
    //
    // TableColumn tableColumn = tableViewerColumn.getColumn();
    // tableColumn.setResizable(true);
    // tableColumn.setAlignment(SWT.LEFT);
    // tableColumn.setWidth(70);
    // tableColumn.setText("Code");
    // }
    //
    // {
    // TableViewerColumn tableViewerColumn = new TableViewerColumn(problemViewer, SWT.NONE);
    // tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    // {
    // @Override
    // public String getText(Object element)
    // {
    // StringBuilder builder = new StringBuilder();
    //
    // List<?> data = ((Diagnostic)element).getData();
    // for (Object object : data)
    // {
    // if (builder.length() != 0)
    // {
    // builder.append(", ");
    // }
    //
    // if (object instanceof Resource)
    // {
    // Resource resource = (Resource)object;
    // builder.append("Resource[");
    // builder.append(resource.getURI());
    // builder.append("]");
    // }
    // else if (object instanceof ENamedElement)
    // {
    // ENamedElement namedElement = (ENamedElement)object;
    // builder.append(namedElement.getName());
    // }
    // else if (object instanceof EObject)
    // {
    // EObject eObject = (EObject)object;
    // builder.append(eObject.eClass().getName());
    // }
    // else
    // {
    // builder.append(String.valueOf(object));
    // }
    // }
    //
    // return builder.toString();
    // }
    // });
    //
    // TableColumn tableColumn = tableViewerColumn.getColumn();
    // tableColumn.setResizable(true);
    // tableColumn.setAlignment(SWT.LEFT);
    // tableColumn.setWidth(400);
    // tableColumn.setText("Data");
    // }

    problemTable.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (e.stateMask == SWT.CTRL && e.character == '1')
        {
          showQuickFixes();
        }
      }
    });

    problemViewer.addOpenListener(new IOpenListener()
    {
      public void open(OpenEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        Object element = selection.getFirstElement();
        if (element instanceof Diagnostic)
        {
          List<?> data = ((Diagnostic)element).getData();
          int size = data.size();
          if (size != 0)
          {
            List<Object> list = new ArrayList<Object>(size);
            for (Object object : data)
            {
              Object wrapper = editingDomain.getWrapper(object);
              list.add(wrapper);
            }

            setSelectionToViewer(list);
          }
        }
      }
    });

    return problemViewer;
  }

  public static TableViewerColumn createDiagnosticMessageColumn(final TableViewer problemViewer)
  {
    TableViewerColumn tableViewerColumn = new TableViewerColumn(problemViewer, SWT.NONE);
    tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @Override
      public Image getImage(Object element)
      {
        switch (((Diagnostic)element).getSeverity())
        {
        case Diagnostic.INFO:
          return SharedIcons.getImage(SharedIcons.OBJ_INFO);

        default:
          return SharedIcons.getImage(SharedIcons.OBJ_ERROR);
        }
      }

      @Override
      public String getText(Object element)
      {
        return ((Diagnostic)element).getMessage();
      }
    });

    TableColumn tableColumn = tableViewerColumn.getColumn();
    tableColumn.setResizable(true);
    tableColumn.setAlignment(SWT.LEFT);
    tableColumn.setWidth(400);
    tableColumn.setText("Message");
    return tableViewerColumn;
  }

  public static TableViewerColumn createDiagnosticResourceColumn(TableViewer problemViewer, AdapterFactory adapterFactory)
  {
    TableViewerColumn tableViewerColumn = new TableViewerColumn(problemViewer, SWT.NONE);
    tableViewerColumn.setLabelProvider(new ResourceColumnLabelProvider(adapterFactory));

    TableColumn tableColumn = tableViewerColumn.getColumn();
    tableColumn.setResizable(true);
    tableColumn.setAlignment(SWT.LEFT);
    tableColumn.setWidth(450);
    tableColumn.setText("Resource");
    return tableViewerColumn;
  }

  public static TableViewerColumn createDiagnosticElementColumn(TableViewer problemViewer, AdapterFactory adapterFactory)
  {
    TableViewerColumn tableViewerColumn = new TableViewerColumn(problemViewer, SWT.NONE);
    tableViewerColumn.setLabelProvider(new ElementColumnLabelProvider(adapterFactory));

    TableColumn tableColumn = tableViewerColumn.getColumn();
    tableColumn.setResizable(true);
    tableColumn.setAlignment(SWT.LEFT);
    tableColumn.setWidth(200);
    tableColumn.setText("Element");
    return tableViewerColumn;
  }

  public static TableViewerColumn createDiagnosticIDColumn(final TableViewer problemViewer)
  {
    TableViewerColumn tableViewerColumn = new TableViewerColumn(problemViewer, SWT.NONE);
    tableViewerColumn.setLabelProvider(new ColumnLabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        DiagnosticID diagnosticID = DiagnosticID.get((Diagnostic)element);
        if (diagnosticID != null)
        {
          return diagnosticID.getValue();
        }

        return "";
      }
    });

    TableColumn tableColumn = tableViewerColumn.getColumn();
    tableColumn.setResizable(true);
    tableColumn.setAlignment(SWT.LEFT);
    tableColumn.setWidth(800);
    tableColumn.setText("ID");
    return tableViewerColumn;
  }

  /**
   * @author Eike Stepper
   */
  public static class ResourceColumnLabelProvider extends ColumnLabelProvider
  {
    protected final AdapterFactory adapterFactory;

    public ResourceColumnLabelProvider(AdapterFactory adapterFactory)
    {
      this.adapterFactory = adapterFactory;
    }

    @Override
    public Image getImage(Object element)
    {
      List<?> data = ((Diagnostic)element).getData();
      if (!data.isEmpty())
      {
        Object object = data.get(0);
        if (object instanceof EObject)
        {
          EObject eObject = (EObject)object;
          Resource resource = eObject.eResource();
          if (resource != null)
          {
            IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(resource, IItemLabelProvider.class);
            if (labelProvider != null)
            {
              return ExtendedImageRegistry.getInstance().getImage(labelProvider.getImage(resource));
            }
          }
        }
      }

      return null;
    }

    @Override
    public String getText(Object element)
    {
      List<?> data = ((Diagnostic)element).getData();
      if (!data.isEmpty())
      {
        Object object = data.get(0);
        if (object instanceof EObject)
        {
          EObject eObject = (EObject)object;
          Resource resource = eObject.eResource();
          if (resource != null)
          {
            IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(resource, IItemLabelProvider.class);
            if (labelProvider != null)
            {
              return labelProvider.getText(resource);
            }
          }
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ElementColumnLabelProvider extends ColumnLabelProvider
  {
    protected final AdapterFactory adapterFactory;

    public ElementColumnLabelProvider(AdapterFactory adapterFactory)
    {
      this.adapterFactory = adapterFactory;
    }

    @Override
    public Image getImage(Object element)
    {
      List<?> data = ((Diagnostic)element).getData();
      if (!data.isEmpty())
      {
        Object object = data.get(0);
        if (object instanceof EObject)
        {
          IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
          if (labelProvider != null)
          {
            return ExtendedImageRegistry.getInstance().getImage(labelProvider.getImage(object));
          }
        }
      }

      return null;
    }

    @Override
    public String getText(Object element)
    {
      List<?> data = ((Diagnostic)element).getData();
      if (!data.isEmpty())
      {
        Object object = data.get(0);
        if (object instanceof EModelElement)
        {
          EModelElement modelElement = (EModelElement)object;
          return ElementHandler.getLabel(modelElement);
        }

        if (object instanceof EObject)
        {

          IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
          if (labelProvider != null)
          {
            return labelProvider.getText(object);
          }
        }
      }

      return null;
    }
  }

  private Diagnostic[] getSelectedDiagnostics()
  {
    IStructuredSelection selection = problemViewer.getStructuredSelection();
    if (selection.isEmpty())
    {
      return DiagnosticResolution.NO_DIAGNOSTICS;
    }

    List<Diagnostic> result = new ArrayList<Diagnostic>();
    for (Iterator<?> it = selection.iterator(); it.hasNext();)
    {
      result.add((Diagnostic)it.next());
    }

    return result.toArray(new Diagnostic[result.size()]);
  }

  private void showQuickFixes()
  {
    final Map<DiagnosticResolution, Collection<Diagnostic>> resolutionsMap = new LinkedHashMap<DiagnosticResolution, Collection<Diagnostic>>();
    final Diagnostic[] selectedDiagnostics = getSelectedDiagnostics();
    if (selectedDiagnostics == null || selectedDiagnostics.length == 0)
    {
      return;
    }

    final Diagnostic firstSelectedDiagnostic = selectedDiagnostics[0];

    IRunnableWithProgress runnable = new IRunnableWithProgress()
    {
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
        monitor.beginTask("Finding fixes", 100);
        monitor.worked(20);

        DiagnosticResolution[] resolutions = DiagnosticResolution.getResolutions(firstSelectedDiagnostic, EvolutionEditor.this);
        int progressCount = 80;
        if (resolutions.length > 1)
        {
          progressCount = progressCount / resolutions.length;
        }

        for (DiagnosticResolution resolution : resolutions)
        {
          Diagnostic[] other = resolution.findOtherDiagnostics(allDiagnostics);
          if (containsAllButFirst(other, selectedDiagnostics))
          {
            Collection<Diagnostic> diagnostics = new LinkedHashSet<Diagnostic>(other.length + 1);
            diagnostics.add(firstSelectedDiagnostic);
            diagnostics.addAll(Arrays.asList(other));
            resolutionsMap.put(resolution, diagnostics);
          }

          monitor.worked(progressCount);
        }

        monitor.done();
      }
    };

    IWorkbenchPartSite site = getSite();
    IWorkbenchSiteProgressService service = Adapters.adapt(site, IWorkbenchSiteProgressService.class);

    Shell shell = site.getShell();
    IRunnableContext context = new ProgressMonitorDialog(shell);

    try
    {
      if (service == null)
      {
        PlatformUI.getWorkbench().getProgressService().runInUI(context, runnable, null);
      }
      else
      {
        service.runInUI(context, runnable, null);
      }
    }
    catch (InvocationTargetException ex)
    {
      throw WrappedException.wrap(ex);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }

    String description = StringUtil.safe(firstSelectedDiagnostic.getMessage());
    if (resolutionsMap.isEmpty())
    {
      if (selectedDiagnostics.length == 1)
      {
        MessageDialog.openInformation(shell, "Quick Fix", NLS.bind("No fixes available for:\n\n {0}.", new Object[] { description }));
      }
      else
      {
        MessageDialog.openInformation(shell, "Quick Fix", "The selected problems do not have a common applicable quick fix.");
      }
    }
    else
    {
      String problemDescription = NLS.bind("Select the fix for ''{0}''.", description);

      Wizard wizard = new QuickFixWizard(problemDescription, selectedDiagnostics, resolutionsMap, editingDomain);
      wizard.setWindowTitle("Quick Fix");

      WizardDialog dialog = new QuickFixWizardDialog(shell, wizard);
      dialog.open();
    }
  }

  /**
   * Checks whether the given extent contains all but the first element from the given members array.
   *
   * @param extent the array which should contain the elements
   * @param members the elements to check
   * @return <code>true</code> if all but the first element are inside the extent
   */
  private static boolean containsAllButFirst(Object[] extent, Object[] members)
  {
    outer: for (int i = 1; i < members.length; i++)
    {
      for (int j = 0; j < extent.length; j++)
      {
        if (members[i] == extent[j])
        {
          continue outer;
        }
      }

      return false;
    }

    return true;
  }

  /**
   * @author Eike Stepper
   */
  private static final class QuickFixWizardDialog extends WizardDialog
  {
    public QuickFixWizardDialog(Shell parentShell, IWizard newWizard)
    {
      super(parentShell, newWizard);
      setShellStyle(SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.MODELESS | SWT.RESIZE | getDefaultOrientation());
    }
  }

  protected void handleDiagnostics(List<Diagnostic> diagnostics)
  {
    List<Diagnostic> result = new ArrayList<Diagnostic>();
    flattenDiagnostics(diagnostics, result);
    allDiagnostics = result.toArray(new Diagnostic[result.size()]);

    if (problemViewer != null && !problemViewer.getControl().isDisposed())
    {
      problemViewer.setInput(diagnostics);
    }
  }

  private void flattenDiagnostics(List<Diagnostic> diagnostics, List<Diagnostic> result)
  {
    for (Diagnostic diagnostic : diagnostics)
    {
      result.add(diagnostic);
      flattenDiagnostics(diagnostic.getChildren(), result);
    }
  }

  /**
   * If there is just one page in the multi-page editor part,
   * this hides the single tab at the bottom.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void hideTabs()
  {
    if (getPageCount() <= 1)
    {
      setPageText(0, "");
      if (getContainer() instanceof CTabFolder)
      {
        Point point = getContainer().getSize();
        Rectangle clientArea = getContainer().getClientArea();
        getContainer().setSize(point.x, 2 * point.y - clientArea.height - clientArea.y);
      }
    }
  }

  /**
   * If there is more than one page in the multi-page editor part,
   * this shows the tabs at the bottom.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void showTabs()
  {
    if (getPageCount() > 1)
    {
      setPageText(0, getString("_UI_SelectionPage_label"));
      if (getContainer() instanceof CTabFolder)
      {
        Point point = getContainer().getSize();
        Rectangle clientArea = getContainer().getClientArea();
        getContainer().setSize(point.x, clientArea.height + clientArea.y);
      }
    }
  }

  /**
   * This is used to track the active viewer.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void pageChange(int pageIndex)
  {
    super.pageChange(pageIndex);

    if (contentOutlinePage != null)
    {
      handleContentOutlineSelection(contentOutlinePage.getSelection());
    }
  }

  /**
   * This is how the framework determines which interfaces we implement.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public <T> T getAdapter(Class<T> key)
  {
    if (key.equals(IContentOutlinePage.class))
    {
      return showOutlineView() ? key.cast(getContentOutlinePage()) : null;
    }
    else if (key.equals(IPropertySheetPage.class))
    {
      return key.cast(getPropertySheetPage());
    }
    else if (key.equals(IGotoMarker.class))
    {
      return key.cast(this);
    }
    else if (key.equals(IFindReplaceTarget.class))
    {
      return FindAndReplaceTarget.getAdapter(key, this, EvolutionEditorPlugin.getPlugin());
    }
    else
    {
      return super.getAdapter(key);
    }
  }

  /**
   * This accesses a cached version of the content outliner.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IContentOutlinePage getContentOutlinePage()
  {
    if (contentOutlinePage == null)
    {
      // The content outline is just a tree.
      //
      class MyContentOutlinePage extends ContentOutlinePage
      {
        @Override
        public void createControl(Composite parent)
        {
          super.createControl(parent);
          contentOutlineViewer = getTreeViewer();
          contentOutlineViewer.addSelectionChangedListener(this);

          // Set up the tree viewer.
          //
          contentOutlineViewer.setUseHashlookup(true);
          contentOutlineViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
          contentOutlineViewer.setLabelProvider(new DelegatingStyledCellLabelProvider.FontAndColorProvider(
              new DecoratingColumLabelProvider.StyledLabelProvider(new AdapterFactoryLabelProvider.StyledLabelProvider(adapterFactory, contentOutlineViewer),
                  new DiagnosticDecorator.Styled(editingDomain, contentOutlineViewer, EvolutionEditorPlugin.getPlugin().getDialogSettings()))));
          contentOutlineViewer.setInput(editingDomain.getResourceSet());

          new ColumnViewerInformationControlToolTipSupport(contentOutlineViewer,
              new DiagnosticDecorator.Styled.EditingDomainLocationListener(editingDomain, contentOutlineViewer));

          // Make sure our popups work.
          //
          createContextMenuFor(contentOutlineViewer);

          if (!editingDomain.getResourceSet().getResources().isEmpty())
          {
            // Select the root object in the view.
            //
            contentOutlineViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)), true);
          }
        }

        @Override
        public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager)
        {
          super.makeContributions(menuManager, toolBarManager, statusLineManager);
          contentOutlineStatusLineManager = statusLineManager;
        }

        @Override
        public void setActionBars(IActionBars actionBars)
        {
          super.setActionBars(actionBars);
          getActionBarContributor().shareGlobalActions(this, actionBars);
        }
      }

      contentOutlinePage = new MyContentOutlinePage();

      // Listen to selection so that we can handle it is a special way.
      //
      contentOutlinePage.addSelectionChangedListener(new ISelectionChangedListener()
      {
        // This ensures that we handle selections correctly.
        //
        public void selectionChanged(SelectionChangedEvent event)
        {
          handleContentOutlineSelection(event.getSelection());
        }
      });
    }

    return contentOutlinePage;
  }

  /**
   * @author Eike Stepper
   */
  private static class DelegatingItemPropertyDescriptor implements IItemPropertyDescriptor
  {
    private final IItemPropertyDescriptor itemPropertyDescriptor;

    private DelegatingItemPropertyDescriptor(IItemPropertyDescriptor itemPropertyDescriptor)
    {
      this.itemPropertyDescriptor = itemPropertyDescriptor;
    }

    public Object getPropertyValue(Object object)
    {
      return itemPropertyDescriptor.getPropertyValue(object);
    }

    public boolean isPropertySet(Object object)
    {
      return itemPropertyDescriptor.isPropertySet(object);
    }

    public boolean canSetProperty(Object object)
    {
      return itemPropertyDescriptor.canSetProperty(object);
    }

    public void resetPropertyValue(Object object)
    {
      itemPropertyDescriptor.resetPropertyValue(object);
    }

    public void setPropertyValue(Object object, Object value)
    {
      itemPropertyDescriptor.setPropertyValue(object, value);
    }

    public String getCategory(Object object)
    {
      return itemPropertyDescriptor.getCategory(object);
    }

    public String getDescription(Object object)
    {
      return itemPropertyDescriptor.getDescription(object);
    }

    public String getDisplayName(Object object)
    {
      return itemPropertyDescriptor.getDisplayName(object);
    }

    public String[] getFilterFlags(Object object)
    {
      return itemPropertyDescriptor.getFilterFlags(object);
    }

    public Object getHelpContextIds(Object object)
    {
      return itemPropertyDescriptor.getHelpContextIds(object);
    }

    public String getId(Object object)
    {
      return itemPropertyDescriptor.getId(object);
    }

    public IItemLabelProvider getLabelProvider(Object object)
    {
      return itemPropertyDescriptor.getLabelProvider(object);
    }

    public boolean isCompatibleWith(Object object, Object anotherObject, IItemPropertyDescriptor anotherPropertyDescriptor)
    {
      return itemPropertyDescriptor.isCompatibleWith(object, anotherObject, anotherPropertyDescriptor);
    }

    public Object getFeature(Object object)
    {
      return itemPropertyDescriptor.getFeature(object);
    }

    public boolean isMany(Object object)
    {
      return itemPropertyDescriptor.isMany(object);
    }

    public Collection<?> getChoiceOfValues(Object object)
    {
      return itemPropertyDescriptor.getChoiceOfValues(object);
    }

    public boolean isMultiLine(Object object)
    {
      return itemPropertyDescriptor.isMultiLine(object);
    }

    public boolean isSortChoices(Object object)
    {
      return itemPropertyDescriptor.isSortChoices(object);
    }
  }

  /**
   * This accesses a cached version of the property sheet.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public IPropertySheetPage getPropertySheetPage()
  {
    PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage(editingDomain, ExtendedPropertySheetPage.Decoration.LIVE,
        EvolutionEditorPlugin.getPlugin().getDialogSettings(), 0, true)
    {
      {
        setSorter(new PropertySheetSorter()
        {
          @Override
          public void sort(IPropertySheetEntry[] entries)
          {
            // Intentionally left empty
          }
        });
      }

      @Override
      public void setSelectionToViewer(List<?> selection)
      {
        EvolutionEditor.this.setSelectionToViewer(selection);
        EvolutionEditor.this.setFocus();
      }

      @Override
      public void setActionBars(IActionBars actionBars)
      {
        super.setActionBars(actionBars);
        getActionBarContributor().shareGlobalActions(this, actionBars);
      }
    };

    propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory)
    {
      @Override
      protected IPropertySource createPropertySource(Object object, IItemPropertySource itemPropertySource)
      {
        if (isReadOnlyObject(object))
        {
          /**
           * Create no cell editor for read-only objects.
           */
          return new PropertySource(object, itemPropertySource)
          {
            @Override
            protected IPropertyDescriptor createPropertyDescriptor(final IItemPropertyDescriptor itemPropertyDescriptor)
            {
              return super.createPropertyDescriptor(new DelegatingItemPropertyDescriptor(itemPropertyDescriptor)
              {
                @Override
                public boolean canSetProperty(Object object)
                {
                  return false;
                }
              });
            }
          };
        }
        else if (object instanceof EModelElement && evolution.containsElement((EModelElement)object))
        {
          /**
           * Eliminate released elements from selection choices.
           */
          return new PropertySource(object, itemPropertySource)
          {
            @Override
            protected IPropertyDescriptor createPropertyDescriptor(final IItemPropertyDescriptor itemPropertyDescriptor)
            {
              return super.createPropertyDescriptor(new DelegatingItemPropertyDescriptor(itemPropertyDescriptor)
              {
                @Override
                public Collection<?> getChoiceOfValues(Object object)
                {
                  Collection<?> values = super.getChoiceOfValues(object);
                  if (values != null)
                  {
                    for (Iterator<?> it = values.iterator(); it.hasNext();)
                    {
                      Object value = it.next();
                      if (value instanceof EModelElement)
                      {
                        EModelElement modelElement = (EModelElement)value;
                        if (!evolution.containsElement(modelElement))
                        {
                          it.remove();
                        }
                      }
                    }
                  }

                  return values;
                }
              });
            }
          };
        }

        return super.createPropertySource(object, itemPropertySource);
      }
    });

    propertySheetPages.add(propertySheetPage);

    return propertySheetPage;
  }

  /**
   * This deals with how we want selection in the outliner to affect the other views.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void handleContentOutlineSelection(ISelection selection)
  {
    if (selectionViewer != null && !selection.isEmpty() && selection instanceof IStructuredSelection)
    {
      Iterator<?> selectedElements = ((IStructuredSelection)selection).iterator();
      if (selectedElements.hasNext())
      {
        // Get the first selected element.
        //
        Object selectedElement = selectedElements.next();

        ArrayList<Object> selectionList = new ArrayList<Object>();
        selectionList.add(selectedElement);
        while (selectedElements.hasNext())
        {
          selectionList.add(selectedElements.next());
        }

        // Set the selection to the widget.
        //
        selectionViewer.setSelection(new StructuredSelection(selectionList));
      }
    }
  }

  /**
   * This is for implementing {@link IEditorPart} and simply tests the command stack.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isDirty()
  {
    return ((BasicCommandStack)editingDomain.getCommandStack()).isSaveNeeded();
  }

  /**
   * This is for implementing {@link IEditorPart} and simply saves the model file.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void doSave(IProgressMonitor progressMonitor)
  {
    // Save only resources that have actually changed.
    //
    final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
    saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
    saveOptions.put(Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);

    // Do the work within an operation because this is a long running activity that modifies the workbench.
    //
    WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
    {
      // This is the method that gets invoked when the operation runs.
      //
      @Override
      public void execute(IProgressMonitor monitor)
      {
        // Save the resources to the file system.
        //
        boolean first = true;
        List<Resource> resources = editingDomain.getResourceSet().getResources();
        for (int i = 0; i < resources.size(); ++i)
        {
          Resource resource = resources.get(i);
          if ((first || !resource.getContents().isEmpty() || isPersisted(resource)) && !editingDomain.isReadOnly(resource))
          {
            try
            {
              long timeStamp = resource.getTimeStamp();
              resource.save(saveOptions);
              if (resource.getTimeStamp() != timeStamp)
              {
                savedResources.add(resource);
              }
            }
            catch (Exception exception)
            {
              resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
            }
            first = false;
          }
        }
      }
    };

    updateProblemIndication = false;
    try
    {
      // This runs the options, and shows progress.
      //
      new ProgressMonitorDialog(getSite().getShell()).run(true, false, operation);

      // Refresh the necessary state.
      //
      ((BasicCommandStack)editingDomain.getCommandStack()).saveIsDone();
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
    catch (Exception exception)
    {
      // Something went wrong that shouldn't.
      //
      EvolutionEditorPlugin.INSTANCE.log(exception);
    }
    updateProblemIndication = true;
    updateProblemIndication();
  }

  /**
   * This returns whether something has been persisted to the URI of the specified resource.
   * The implementation uses the URI converter from the editor's resource set to try to open an input stream.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected boolean isPersisted(Resource resource)
  {
    boolean result = false;
    try
    {
      InputStream stream = editingDomain.getResourceSet().getURIConverter().createInputStream(resource.getURI());
      if (stream != null)
      {
        result = true;
        stream.close();
      }
    }
    catch (IOException e)
    {
      // Ignore
    }
    return result;
  }

  /**
   * This always returns true because it is not currently supported.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSaveAsAllowed()
  {
    return true;
  }

  /**
   * This also changes the editor's input.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void doSaveAs()
  {
    SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
    saveAsDialog.open();
    IPath path = saveAsDialog.getResult();
    if (path != null)
    {
      IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
      if (file != null)
      {
        doSaveAs(URI.createPlatformResourceURI(file.getFullPath().toString(), true), new FileEditorInput(file));
      }
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void doSaveAs(URI uri, IEditorInput editorInput)
  {
    editingDomain.getResourceSet().getResources().get(0).setURI(uri);
    setInputWithNotify(editorInput);
    setPartName(editorInput.getName());
    IProgressMonitor progressMonitor = getActionBars().getStatusLineManager() != null ? getActionBars().getStatusLineManager().getProgressMonitor()
        : new NullProgressMonitor();
    doSave(progressMonitor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void gotoMarker(IMarker marker)
  {
    List<?> targetObjects = markerHelper.getTargetObjects(editingDomain, marker);
    if (!targetObjects.isEmpty())
    {
      setSelectionToViewer(targetObjects);
    }
  }

  /**
   * This is called during startup.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void init(IEditorSite site, IEditorInput editorInput)
  {
    setSite(site);
    setInputWithNotify(editorInput);
    setPartName(editorInput.getName());
    site.setSelectionProvider(this);
    site.getPage().addPartListener(partListener);
    ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFocus()
  {
    getControl(getActivePage()).setFocus();
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.add(listener);
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.remove(listener);
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ISelection getSelection()
  {
    return editorSelection;
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to set this editor's overall selection.
   * Calling this result will notify the listeners.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSelection(ISelection selection)
  {
    editorSelection = selection;

    for (ISelectionChangedListener listener : selectionChangedListeners)
    {
      listener.selectionChanged(new SelectionChangedEvent(this, selection));
    }
    setStatusLineManager(selection);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStatusLineManager(ISelection selection)
  {
    IStatusLineManager statusLineManager = currentViewer != null && currentViewer == contentOutlineViewer ? contentOutlineStatusLineManager
        : getActionBars().getStatusLineManager();

    if (statusLineManager != null)
    {
      if (selection instanceof IStructuredSelection)
      {
        Collection<?> collection = ((IStructuredSelection)selection).toList();
        switch (collection.size())
        {
        case 0:
        {
          statusLineManager.setMessage(getString("_UI_NoObjectSelected"));
          break;
        }
        case 1:
        {
          String text = new AdapterFactoryItemDelegator(adapterFactory).getText(collection.iterator().next());
          statusLineManager.setMessage(getString("_UI_SingleObjectSelected", text));
          break;
        }
        default:
        {
          statusLineManager.setMessage(getString("_UI_MultiObjectSelected", Integer.toString(collection.size())));
          break;
        }
        }
      }
      else
      {
        statusLineManager.setMessage("");
      }
    }
  }

  /**
   * This looks up a string in the plugin's plugin.properties file.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static String getString(String key)
  {
    return EvolutionEditorPlugin.INSTANCE.getString(key);
  }

  /**
   * This looks up a string in plugin.properties, making a substitution.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static String getString(String key, Object s1)
  {
    return EvolutionEditorPlugin.INSTANCE.getString(key, new Object[] { s1 });
  }

  /**
   * This implements {@link org.eclipse.jface.action.IMenuListener} to help fill the context menus with contributions from the Edit menu.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void menuAboutToShow(IMenuManager menuManager)
  {
    ((IMenuListener)getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EditingDomainActionBarContributor getActionBarContributor()
  {
    return (EditingDomainActionBarContributor)getEditorSite().getActionBarContributor();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IActionBars getActionBars()
  {
    return getActionBarContributor().getActionBars();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void dispose()
  {
    updateProblemIndication = false;

    ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);

    getSite().getPage().removePartListener(partListener);

    adapterFactory.dispose();

    if (getActionBarContributor().getActiveEditor() == this)
    {
      getActionBarContributor().setActiveEditor(null);
    }

    for (PropertySheetPage propertySheetPage : propertySheetPages)
    {
      propertySheetPage.dispose();
    }

    if (contentOutlinePage != null)
    {
      contentOutlinePage.dispose();
    }

    super.dispose();
  }

  /**
   * Returns whether the outline view should be presented to the user.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected boolean showOutlineView()
  {
    return false;
  }
}
