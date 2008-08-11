/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.BulkAddDialog;
import org.eclipse.emf.cdo.internal.ui.dialogs.RollbackTransactionDialog;
import org.eclipse.emf.cdo.ui.CDOEventHandler;
import org.eclipse.emf.cdo.ui.CDOLabelProvider;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOLegacyImpl;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.transaction.TransactionException;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.ui.editor.ProblemEditorPart;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.UnwrappingSelectionProvider;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @generated
 */
public class CDOEditor extends MultiPageEditorPart implements IEditingDomainProvider, ISelectionProvider,
    IMenuListener, IViewerProvider, IGotoMarker
{
  /**
   * @ADDED
   */
  public static final String EDITOR_ID = "org.eclipse.emf.cdo.ui.CDOEditor";

  /**
   * @ADDED
   */
  private CDOView view;

  /**
   * @ADDED
   */
  private Object viewerInput;

  /**
   * @ADDED
   */
  private CDOEventHandler eventHandler;

  /**
   * This keeps track of the editing domain that is used to track all changes to the model. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  protected AdapterFactoryEditingDomain editingDomain;

  /**
   * This is the one adapter factory used for providing views of the model. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   */
  protected ComposedAdapterFactory adapterFactory;

  /**
   * This is the content outline page. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected IContentOutlinePage contentOutlinePage;

  /**
   * This is a kludge... <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected IStatusLineManager contentOutlineStatusLineManager;

  /**
   * This is the content outline page's viewer. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TreeViewer contentOutlineViewer;

  /**
   * This is the property sheet page. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected PropertySheetPage propertySheetPage;

  /**
   * This is the viewer that shadows the selection in the content outline. The parent relation must be correctly defined
   * for this to work. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TreeViewer selectionViewer;

  /**
   * This keeps track of the active content viewer, which may be either one of the viewers in the pages or the content
   * outline viewer. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Viewer currentViewer;

  /**
   * This listens to which ever viewer is active. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ISelectionChangedListener selectionChangedListener;

  /**
   * This keeps track of all the {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are listening to this
   * editor. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Collection<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

  /**
   * This keeps track of the selection of the editor as a whole. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ISelection editorSelection = StructuredSelection.EMPTY;

  /**
   * The MarkerHelper is responsible for creating workspace resource markers presented in Eclipse's Problems View. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected MarkerHelper markerHelper = new EditUIMarkerHelper();

  /**
   * This listens for when the outline becomes active <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
          getActionBarContributor().setActiveEditor(CDOEditor.this);

          setCurrentViewer(contentOutlineViewer);
        }
      }
      else if (p instanceof PropertySheet)
      {
        if (((PropertySheet)p).getCurrentPage() == propertySheetPage)
        {
          getActionBarContributor().setActiveEditor(CDOEditor.this);
          handleActivate();
        }
      }
      else if (p == CDOEditor.this)
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
   * Resources that have been removed since last activation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Collection<Resource> removedResources = new ArrayList<Resource>();

  /**
   * Resources that have been changed since last activation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Collection<Resource> changedResources = new ArrayList<Resource>();

  /**
   * Resources that have been saved. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Collection<Resource> savedResources = new ArrayList<Resource>();

  /**
   * Map to store the diagnostic associated with a resource. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<Resource, Diagnostic>();

  /**
   * Controls whether the problem indication should be updated. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected boolean updateProblemIndication = true;

  /**
   * Adapter used to update the problem indication when resources are demanded loaded. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  protected EContentAdapter problemIndicationAdapter = new EContentAdapter()
  {
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

          if (updateProblemIndication)
          {
            getSite().getShell().getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                updateProblemIndication();
              }
            });
          }
          break;
        }
        }
      }
      else
      {
        super.notifyChanged(notification);
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
    }
  };

  /**
   * This listens for workspace changes. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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

          public boolean visit(IResourceDelta delta)
          {
            if (delta.getResource().getType() == IResource.FILE)
            {
              if (delta.getKind() == IResourceDelta.REMOVED || delta.getKind() == IResourceDelta.CHANGED
                  && delta.getFlags() != IResourceDelta.MARKERS)
              {
                Resource resource = resourceSet.getResource(URI.createURI(delta.getFullPath().toString()), false);
                if (resource != null)
                {
                  if (delta.getKind() == IResourceDelta.REMOVED)
                  {
                    removedResources.add(resource);
                  }
                  else if (!savedResources.remove(resource))
                  {
                    changedResources.add(resource);
                  }
                }
              }
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

        ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
        delta.accept(visitor);

        if (!visitor.getRemovedResources().isEmpty())
        {
          removedResources.addAll(visitor.getRemovedResources());
          if (!isDirty())
          {
            getSite().getShell().getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                getSite().getPage().closeEditor(CDOEditor.this, false);
              }
            });
          }
        }

        if (!visitor.getChangedResources().isEmpty())
        {
          changedResources.addAll(visitor.getChangedResources());
          if (getSite().getPage().getActiveEditor() == CDOEditor.this)
          {
            getSite().getShell().getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                handleActivate();
              }
            });
          }
        }
      }
      catch (CoreException exception)
      {
        PluginDelegator.INSTANCE.log(exception);
      }
    }
  };

  /**
   * Handles activation of the editor or it's associated views. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
        getSite().getPage().closeEditor(CDOEditor.this, false);
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
   * Handles what to do with changed resources on activation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void handleChangedResources()
  {
    if (!changedResources.isEmpty() && (!isDirty() || handleDirtyConflict()))
    {
      if (isDirty())
      {
        changedResources.addAll(editingDomain.getResourceSet().getResources());
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
            resource.load(Collections.EMPTY_MAP);
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
   * Updates the problems indication with the information described in the specified diagnostic. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void updateProblemIndication()
  {
    if (updateProblemIndication)
    {
      BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK, "org.eclipse.emf.cdo.ui.editor", 0, null,
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
          PluginDelegator.INSTANCE.log(exception);
        }
      }

      if (markerHelper.hasMarkers(editingDomain.getResourceSet()))
      {
        markerHelper.deleteMarkers(editingDomain.getResourceSet());
        if (diagnostic.getSeverity() != Diagnostic.OK)
        {
          try
          {
            markerHelper.createMarkers(diagnostic);
          }
          catch (CoreException exception)
          {
            PluginDelegator.INSTANCE.log(exception);
          }
        }
      }
    }
  }

  /**
   * Shows a dialog that asks if conflicting changes should be discarded. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected boolean handleDirtyConflict()
  {
    return MessageDialog.openQuestion(getSite().getShell(), getString("_UI_FileConflict_label"),
        getString("_WARN_FileConflict"));
  }

  /**
   * This creates a model editor. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CDOEditor()
  {
    super();
    initializeEditingDomain();
  }

  /**
   * This sets up the editing domain for the model editor. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void initializeEditingDomainGen()
  {
    // Create an adapter factory that yields item providers.
    //
    adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

    // Create the command stack that will notify this editor as commands are executed.
    //
    BasicCommandStack commandStack = new BasicCommandStack();

    // Add a listener to set the most recent command's affected objects to be the selection of the viewer with focus.
    //
    commandStack.addCommandStackListener(new CommandStackListener()
    {
      public void commandStackChanged(final EventObject event)
      {
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
            if (propertySheetPage != null && !propertySheetPage.getControl().isDisposed())
            {
              propertySheetPage.refresh();
            }
          }
        });
      }
    });

    // Create the editing domain with a special command stack.
    //
    editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<Resource, Boolean>());
  }

  /**
   * @ADDED
   */
  protected void initializeEditingDomain()
  {
    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
  }

  /**
   * This is here for the listener to be able to call it. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected void firePropertyChange(int action)
  {
    super.firePropertyChange(action);
  }

  /**
   * This sets the selection into whichever viewer is active. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setSelectionToViewer(Collection<?> collection)
  {
    final Collection<?> theSelection = collection;
    // Make sure it's okay.
    //
    if (theSelection != null && !theSelection.isEmpty())
    {
      // I don't know if this should be run this deferred
      // because we might have to give the editor a chance to process the viewer update events
      // and hence to update the views first.
      //
      //
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
      runnable.run();
    }
  }

  /**
   * This returns the editing domain as required by the {@link IEditingDomainProvider} interface. This is important for
   * implementing the static methods of {@link AdapterFactoryEditingDomain} and for supporting
   * {@link org.eclipse.emf.edit.ui.action.CommandAction}. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EditingDomain getEditingDomain()
  {
    return editingDomain;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public class ReverseAdapterFactoryContentProvider extends AdapterFactoryContentProvider
  {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ReverseAdapterFactoryContentProvider(AdapterFactory adapterFactory)
    {
      super(adapterFactory);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object[] getElements(Object object)
    {
      Object parent = super.getParent(object);
      return (parent == null ? Collections.EMPTY_SET : Collections.singleton(parent)).toArray();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object[] getChildren(Object object)
    {
      Object parent = super.getParent(object);
      return (parent == null ? Collections.EMPTY_SET : Collections.singleton(parent)).toArray();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean hasChildren(Object object)
    {
      Object parent = super.getParent(object);
      return parent != null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getParent(Object object)
    {
      return null;
    }
  }

  /**
   * This makes sure that one content viewer, either for the current page or the outline view, if it has focus, is the
   * current one. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
   * This returns the viewer as required by the {@link IViewerProvider} interface. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  public Viewer getViewer()
  {
    return currentViewer;
  }

  /**
   * This creates a context menu for the viewer and adds a listener as well registering the menu for extension. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
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
    Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance() };
    viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer));
    viewer.addDropSupport(dndOperations, transfers, new EditingDomainViewerDropAdapter(editingDomain, viewer));
  }

  /**
   * @ADDED
   */
  public CDOView getView()
  {
    return view;
  }

  /**
   * This is the method called to load a resource into the editing domain's resource set based on the editor's input.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createModelGen()
  {
    URI resourceURI = EditUIUtil.getURI(getEditorInput());
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
   * @ADDED
   */
  public void createModel()
  {
    try
    {
      CDOEditorInput editorInput = (CDOEditorInput)getEditorInput();
      view = editorInput.getView();

      // TODO Check if a CommandStack is needed
      BasicCommandStack commandStack = new BasicCommandStack();
      commandStack.addCommandStackListener(new CommandStackListener()
      {
        public void commandStackChanged(final EventObject event)
        {
          getContainer().getDisplay().asyncExec(new Runnable()
          {
            public void run()
            {
              Command mostRecentCommand = ((CommandStack)event.getSource()).getMostRecentCommand();
              if (mostRecentCommand != null)
              {
                setSelectionToViewer(mostRecentCommand.getAffectedObjects());
              }
              if (propertySheetPage != null && !propertySheetPage.getControl().isDisposed())
              {
                propertySheetPage.refresh();
              }
            }
          });
        }
      });

      ResourceSet resourceSet = view.getResourceSet();
      editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, resourceSet);

      String resourcePath = editorInput.getResourcePath();
      if (resourcePath == null)
      {
        viewerInput = resourceSet;
      }
      else
      {
        URI resourceURI = CDOUtil.createResourceURI(resourcePath);
        viewerInput = resourceSet.getResource(resourceURI, true);
      }

      // resourceSet.eAdapters().add(problemIndicationAdapter);
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
      throw ex;
    }
  }

  /**
   * Returns a diagnostic describing the errors and warnings listed in the resource and the specified exception (if
   * any). <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Diagnostic analyzeResourceProblems(Resource resource, Exception exception)
  {
    if (!resource.getErrors().isEmpty() || !resource.getWarnings().isEmpty())
    {
      BasicDiagnostic basicDiagnostic = new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.ui.editor", 0,
          getString("_UI_CreateModelError_message", resource.getURI()),
          new Object[] { exception == null ? (Object)resource : exception });
      basicDiagnostic.merge(EcoreUtil.computeDiagnostic(resource, true));
      return basicDiagnostic;
    }
    else if (exception != null)
    {
      return new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.ui.editor", 0, getString(
          "_UI_CreateModelError_message", resource.getURI()), new Object[] { exception });
    }
    else
    {
      return Diagnostic.OK_INSTANCE;
    }
  }

  /**
   * This is the method used by the framework to install your own controls. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   */
  public void createPagesGen()
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
      Tree tree = new Tree(getContainer(), SWT.MULTI);
      selectionViewer = new TreeViewer(tree);
      setCurrentViewer(selectionViewer);

      selectionViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
      selectionViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
      selectionViewer.setInput(editingDomain.getResourceSet());
      selectionViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources().get(0)), true);

      new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);

      createContextMenuFor(selectionViewer);
      int pageIndex = addPage(tree);
      setPageText(pageIndex, getString("_UI_SelectionPage_label"));

      getSite().getShell().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          setActivePage(0);
        }
      });
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

  /**
   * @ADDED
   */
  @Override
  public void createPages()
  {
    try
    {
      // Creates the model from the editor input
      //
      createModel();

      // Create a page for the selection tree view.
      //
      Tree tree = new Tree(getContainer(), SWT.MULTI);
      selectionViewer = new TreeViewer(tree);
      setCurrentViewer(selectionViewer);

      selectionViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
      selectionViewer.setLabelProvider(new CDOLabelProvider(adapterFactory, view, selectionViewer));
      selectionViewer.setInput(viewerInput);
      // selectionViewer.setSelection(new StructuredSelection(viewerInput),
      // true);

      new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);

      createContextMenuFor(selectionViewer);
      int pageIndex = addPage(tree);
      setPageText(pageIndex, getString("_UI_SelectionPage_label"));

      setActivePage(0);

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

      updateProblemIndication();
      eventHandler = new CDOEventHandler(view, selectionViewer)
      {
        @Override
        protected void objectInvalidated(InternalCDOObject cdoObject)
        {
          if (cdoObject instanceof CDOLegacyImpl)
          {
            CDOStateMachine.INSTANCE.read(cdoObject);
          }
        }

        @Override
        protected void viewConflict(final CDOObject conflictingObject, boolean firstConflict)
        {
          refreshViewer(conflictingObject);
        }

        @Override
        protected void viewClosed()
        {
          closeEditor();
        }

        @Override
        protected void viewDirtyStateChanged()
        {
          if (viewerInput instanceof CDOResource)
          {
            CDOResource resource = (CDOResource)viewerInput;
            if (!view.isObjectRegistered(resource.cdoID()))
            {
              closeEditor();
              return;
            }
          }

          fireDirtyPropertyChange();
        }
      };
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
  }

  /**
   * If there is just one page in the multi-page editor part, this hides the single tab at the bottom. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void hideTabs()
  {
    if (getPageCount() <= 1)
    {
      setPageText(0, "");
      if (getContainer() instanceof CTabFolder)
      {
        ((CTabFolder)getContainer()).setTabHeight(1);
        Point point = getContainer().getSize();
        getContainer().setSize(point.x, point.y + 6);
      }
    }
  }

  /**
   * If there is more than one page in the multi-page editor part, this shows the tabs at the bottom. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void showTabs()
  {
    if (getPageCount() > 1)
    {
      setPageText(0, getString("_UI_SelectionPage_label"));
      if (getContainer() instanceof CTabFolder)
      {
        ((CTabFolder)getContainer()).setTabHeight(SWT.DEFAULT);
        Point point = getContainer().getSize();
        getContainer().setSize(point.x, point.y - 6);
      }
    }
  }

  /**
   * This is used to track the active viewer. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
   * This is how the framework determines which interfaces we implement. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public Object getAdapter(Class key)
  {
    if (key.equals(IContentOutlinePage.class))
    {
      return showOutlineView() ? getContentOutlinePage() : null;
    }
    else if (key.equals(IPropertySheetPage.class))
    {
      return getPropertySheetPage();
    }
    else if (key.equals(IGotoMarker.class))
    {
      return this;
    }
    else
    {
      return super.getAdapter(key);
    }
  }

  /**
   * This accesses a cached version of the content outliner. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
          contentOutlineViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
          contentOutlineViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
          contentOutlineViewer.setInput(editingDomain.getResourceSet());

          // Make sure our popups work.
          //
          createContextMenuFor(contentOutlineViewer);

          if (!editingDomain.getResourceSet().getResources().isEmpty())
          {
            // Select the root object in the view.
            //
            contentOutlineViewer.setSelection(new StructuredSelection(editingDomain.getResourceSet().getResources()
                .get(0)), true);
          }
        }

        @Override
        public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager,
            IStatusLineManager statusLineManager)
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
   * This accesses a cached version of the property sheet. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public IPropertySheetPage getPropertySheetPage()
  {
    if (propertySheetPage == null)
    {
      propertySheetPage = new ExtendedPropertySheetPage(editingDomain)
      {
        @Override
        public void setSelectionToViewer(List<?> selection)
        {
          CDOEditor.this.setSelectionToViewer(selection);
          CDOEditor.this.setFocus();
        }

        @Override
        public void setActionBars(IActionBars actionBars)
        {
          super.setActionBars(actionBars);
          getActionBarContributor().shareGlobalActions(this, actionBars);
        }
      };
      propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
    }

    return propertySheetPage;
  }

  /**
   * This deals with how we want selection in the outliner to affect the other views. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
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
   * This is for implementing {@link IEditorPart} and simply tests the command stack. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public boolean isDirty()
  {
    return view.isDirty();
  }

  /**
   * This is for implementing {@link IEditorPart} and simply saves the model file. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  public void doSaveGen(IProgressMonitor progressMonitor)
  {
    // Save only resources that have actually changed.
    //
    final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
    saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

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
        for (Resource resource : editingDomain.getResourceSet().getResources())
        {
          if ((first || !resource.getContents().isEmpty() || isPersisted(resource))
              && !editingDomain.isReadOnly(resource))
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
      PluginDelegator.INSTANCE.log(exception);
    }
    updateProblemIndication = true;
    updateProblemIndication();
  }

  /**
   * @ADDED
   */
  @Override
  public void doSave(IProgressMonitor progressMonitor)
  {
    // Save only resources that have actually changed.
    //
    final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
    saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

    // Do the work within an operation because this is a long running activity
    // that modifies the workbench.
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
        for (Resource resource : editingDomain.getResourceSet().getResources())
        {
          if ((first || !resource.getContents().isEmpty() || isPersisted(resource))
              && !editingDomain.isReadOnly(resource))
          {
            try
            {
              savedResources.add(resource);
              resource.save(saveOptions);
            }
            catch (final TransactionException exception)
            {
              final Shell shell = getSite().getShell();
              shell.getDisplay().syncExec(new Runnable()
              {
                public void run()
                {
                  CDOTransaction transaction = (CDOTransaction)view;
                  Dialog dialog = new RollbackTransactionDialog(getEditorSite().getPage(), "Transaction Error",
                      exception.getMessage(), transaction);
                  switch (dialog.open())
                  {
                  case RollbackTransactionDialog.REMOTE:
                    transaction.rollback(true);
                    break;
                  case RollbackTransactionDialog.LOCAL:
                    transaction.rollback(false);
                    break;
                  }
                }
              });
            }
            catch (Exception exception)
            {
              OM.LOG.error(exception);
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
    }
    catch (Exception exception)
    {
      // Something went wrong that shouldn't.
      //
      PluginDelegator.INSTANCE.log(exception);
    }
    updateProblemIndication = true;
    updateProblemIndication();
  }

  /**
   * This returns whether something has been persisted to the URI of the specified resource. The implementation uses the
   * URI converter from the editor's resource set to try to open an input stream. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
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
   * This always returns true because it is not currently supported. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSaveAsAllowedGen()
  {
    return true;
  }

  /**
   * @ADDED
   */
  @Override
  public boolean isSaveAsAllowed()
  {
    return false;
  }

  /**
   * This also changes the editor's input. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void doSaveAs(URI uri, IEditorInput editorInput)
  {
    editingDomain.getResourceSet().getResources().get(0).setURI(uri);
    setInputWithNotify(editorInput);
    setPartName(editorInput.getName());
    IProgressMonitor progressMonitor = getActionBars().getStatusLineManager() != null ? getActionBars()
        .getStatusLineManager().getProgressMonitor() : new NullProgressMonitor();
    doSave(progressMonitor);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void gotoMarker(IMarker marker)
  {
    try
    {
      if (marker.getType().equals(EValidator.MARKER))
      {
        String uriAttribute = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
        if (uriAttribute != null)
        {
          URI uri = URI.createURI(uriAttribute);
          EObject eObject = editingDomain.getResourceSet().getEObject(uri, true);
          if (eObject != null)
          {
            setSelectionToViewer(Collections.singleton(editingDomain.getWrapper(eObject)));
          }
        }
      }
    }
    catch (CoreException exception)
    {
      PluginDelegator.INSTANCE.log(exception);
    }
  }

  /**
   * This is called during startup. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initGen(IEditorSite site, IEditorInput editorInput)
  {
    setSite(site);
    setInputWithNotify(editorInput);
    setPartName(editorInput.getName());
    site.setSelectionProvider(this);
    site.getPage().addPartListener(partListener);
    ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
  }

  /**
   * @ADDED
   */
  @Override
  public void init(IEditorSite site, IEditorInput editorInput)
  {
    setSite(site);
    setPartName(editorInput.getName());
    site.setSelectionProvider(this);
    site.getPage().addPartListener(partListener);
    setInputWithNotify(editorInput);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void setFocus()
  {
    getControl(getActivePage()).setFocus();
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.add(listener);
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.remove(listener);
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public ISelection getSelection()
  {
    return editorSelection;
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to set this editor's overall selection.
   * Calling this result will notify the listeners. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
   * This looks up a string in the plugin's plugin.properties file. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static String getString(String key)
  {
    return PluginDelegator.INSTANCE.getString(key);
  }

  /**
   * This looks up a string in plugin.properties, making a substitution. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static String getString(String key, Object s1)
  {
    return PluginDelegator.INSTANCE.getString(key, new Object[] { s1 });
  }

  /**
   * This implements {@link org.eclipse.jface.action.IMenuListener} to help fill the context menus with contributions
   * from the Edit menu. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void menuAboutToShowGen(IMenuManager menuManager)
  {
    ((IMenuListener)getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
  }

  /**
   * @ADDED
   */
  public void menuAboutToShow(IMenuManager menuManager)
  {
    menuAboutToShowGen(menuManager);
    MenuManager submenuManager = new MenuManager("New Root");
    if (populateNewRoot(submenuManager))
    {
      menuManager.insertBefore("edit", submenuManager);
    }

    if (OM.TEST_BULK_ADD.isEnabled())
    {
      IStructuredSelection sel = (IStructuredSelection)editorSelection;
      if (sel.size() == 1)
      {
        Object element = sel.getFirstElement();
        if (element instanceof EObject)
        {
          final EObject object = (EObject)element;
          final List<EReference> features = new ArrayList<EReference>();
          for (EReference containment : object.eClass().getEAllContainments())
          {
            if (containment.isMany())
            {
              features.add(containment);
            }
          }

          if (!features.isEmpty())
          {
            final IWorkbenchPage page = getSite().getPage();
            menuManager.insertBefore("edit", new LongRunningAction(page, "Bulk Add" + SafeAction.INTERACTIVE)
            {
              private EReference feature;

              private int instances;

              @Override
              protected void preRun() throws Exception
              {
                BulkAddDialog dialog = new BulkAddDialog(page, features);
                if (dialog.open() == BulkAddDialog.OK)
                {
                  feature = dialog.getFeature();
                  instances = dialog.getInstances();
                }
                else
                {
                  cancel();
                }
              }

              @SuppressWarnings("unchecked")
              @Override
              protected void doRun() throws Exception
              {
                List<EObject> children = new ArrayList<EObject>();
                for (int i = 0; i < instances; i++)
                {
                  EObject child = EcoreUtil.create(feature.getEReferenceType());
                  children.add(child);
                }

                List<EObject> list = (EList<EObject>)object.eGet(feature);
                list.addAll(children);
              }
            });
          }
        }
      }
    }
  }

  /**
   * @ADDED
   */
  protected boolean populateNewRoot(MenuManager menuManager)
  {
    boolean populated = false;
    List<CDOPackage> cdoPackages = Arrays.asList(view.getSession().getPackageManager().getPackages());
    Collections.sort(cdoPackages);

    for (CDOPackage cdoPackage : cdoPackages)
    {
      List<CDOClass> cdoClasses = Arrays.asList(cdoPackage.getConcreteClasses());
      Collections.sort(cdoClasses);
      // TODO Sorting by class name may not have the desired effect if the labels are computed by an ItemProvider!

      if (!cdoClasses.isEmpty())
      {
        MenuManager submenuManager = new MenuManager(cdoPackage.getPackageURI());
        for (CDOClass cdoClass : cdoClasses)
        {
          if (!cdoClass.isResource())
          {
            // TODO Optimize/cache this?
            CreateRootAction action = new CreateRootAction(cdoClass);
            submenuManager.add(action);
            populated = true;
          }
        }

        menuManager.add(submenuManager);
      }
    }

    return populated;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EditingDomainActionBarContributor getActionBarContributor()
  {
    return (EditingDomainActionBarContributor)getEditorSite().getActionBarContributor();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public IActionBars getActionBars()
  {
    return getActionBarContributor().getActionBars();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void disposeGen()
  {
    updateProblemIndication = false;

    ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);

    getSite().getPage().removePartListener(partListener);

    adapterFactory.dispose();

    if (getActionBarContributor().getActiveEditor() == this)
    {
      getActionBarContributor().setActiveEditor(null);
    }

    if (propertySheetPage != null)
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
   * @ADDED
   */
  @Override
  public void dispose()
  {
    updateProblemIndication = false;
    eventHandler.dispose();
    getSite().getPage().removePartListener(partListener);
    adapterFactory.dispose();

    if (getActionBarContributor().getActiveEditor() == this)
    {
      getActionBarContributor().setActiveEditor(null);
    }

    if (propertySheetPage != null)
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
   * Returns whether the outline view should be presented to the user. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected boolean showOutlineView()
  {
    return false;
  }

  /**
   * @ADDED
   */
  protected void fireDirtyPropertyChange()
  {
    try
    {
      getSite().getShell().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            firePropertyChange(IEditorPart.PROP_DIRTY);
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  /**
   * @ADDED
   */
  protected void closeEditor()
  {
    try
    {
      getSite().getShell().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            getSite().getPage().closeEditor(CDOEditor.this, false);
            CDOEditor.this.dispose();
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  /**
   * @ADDED
   */
  public void refreshViewer(final Object element)
  {
    try
    {
      selectionViewer.getControl().getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            if (element == null)
            {
              selectionViewer.refresh(true);
            }
            else
            {
              selectionViewer.refresh(element, true);
            }
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  /**
   * @ADDED
   */
  public static void open(final IWorkbenchPage page, final CDOView view, final String resourcePath)
  {
    Display display = page.getWorkbenchWindow().getShell().getDisplay();
    display.asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          IEditorReference[] references = find(page, view, resourcePath);
          if (references.length != 0)
          {
            IEditorPart editor = references[0].getEditor(true);
            page.activate(editor);
          }
          else
          {
            IEditorInput input = new CDOEditorInput(view, resourcePath);
            page.openEditor(input, EDITOR_ID);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    });
  }

  /**
   * @ADDED
   */
  public static IEditorReference[] find(IWorkbenchPage page, CDOView view, String resourcePath)
  {
    List<IEditorReference> result = new ArrayList<IEditorReference>();
    IEditorReference[] editorReferences = page.getEditorReferences();
    for (IEditorReference editorReference : editorReferences)
    {
      try
      {
        if (ObjectUtil.equals(editorReference.getId(), EDITOR_ID))
        {
          IEditorInput editorInput = editorReference.getEditorInput();
          if (editorInput instanceof CDOEditorInput)
          {
            CDOEditorInput cdoInput = (CDOEditorInput)editorInput;
            if (cdoInput.getView() == view)
            {
              if (resourcePath == null || ObjectUtil.equals(cdoInput.getResourcePath(), resourcePath))
              {
                result.add(editorReference);
              }
            }
          }
        }
      }
      catch (PartInitException ex)
      {
        OM.LOG.error(ex);
      }
    }

    return result.toArray(new IEditorReference[result.size()]);
  }

  /**
   * @ADDED
   */
  public static void refresh(IWorkbenchPage page, CDOView view)
  {
    IEditorReference[] references = find(page, view, null);
    for (IEditorReference reference : references)
    {
      CDOEditor editor = (CDOEditor)reference.getEditor(false);
      if (editor != null)
      {
        editor.refreshViewer(null);
      }
    }
  }

  /**
   * @author Eike Stepper
   * @ADDED
   */
  private final class CreateRootAction extends LongRunningAction
  {
    private CDOClass cdoClass;

    private CreateRootAction(CDOClass cdoClass)
    {
      super(getEditorSite().getPage(), cdoClass.getName(), SharedIcons.getDescriptor(SharedIcons.OBJ_ECLASS));
      this.cdoClass = cdoClass;
    }

    @Override
    protected void doRun() throws Exception
    {
      Resource resource = null;
      IStructuredSelection ssel = (IStructuredSelection)editorSelection;
      if (ssel.isEmpty())
      {
        if (viewerInput instanceof Resource)
        {
          resource = (Resource)viewerInput;
        }
      }
      else if (ssel.size() == 1)
      {
        Object element = ssel.getFirstElement();
        if (element instanceof Resource)
        {
          resource = (Resource)element;
        }
        else if (element instanceof EObject)
        {
          resource = ((EObject)element).eResource();
        }
      }

      if (resource != null)
      {
        InternalCDOObject object = ((CDOTransactionImpl)view).newInstance(cdoClass);
        resource.getContents().add(object.cdoInternalInstance());
      }
    }
  }
}
