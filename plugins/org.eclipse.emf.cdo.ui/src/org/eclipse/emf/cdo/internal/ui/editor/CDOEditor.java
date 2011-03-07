/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.BulkAddDialog;
import org.eclipse.emf.cdo.internal.ui.dialogs.RollbackTransactionDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.CDOEventHandler;
import org.eclipse.emf.cdo.ui.CDOLabelProvider;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.transaction.TransactionException;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.ui.MarkerHelper;
import org.eclipse.emf.common.ui.editor.ProblemEditorPart;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emf.edit.ui.celleditor.AdapterFactoryTreeEditor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.edit.ui.provider.UnwrappingSelectionProvider;
import org.eclipse.emf.edit.ui.util.EditUIMarkerHelper;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

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
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @generated
 */
public class CDOEditor extends MultiPageEditorPart implements IEditingDomainProvider, ISelectionProvider,
    IMenuListener, IViewerProvider, IGotoMarker
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation"; //$NON-NLS-1$

  /**
   * @ADDED
   */
  public static final String EDITOR_ID = "org.eclipse.emf.cdo.ui.CDOEditor"; //$NON-NLS-1$

  /**
   * @ADDED
   */
  private static final Object EMPTY_INPUT = new Object();

  /**
   * @ADDED
   */
  protected CDOView view;

  /**
   * @ADDED
   */
  protected Object viewerInput;

  /**
   * @ADDED
   */
  protected CDOEventHandler eventHandler;

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
                Resource resource = resourceSet.getResource(
                    URI.createPlatformResourceURI(delta.getFullPath().toString(), true), false);
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
                getSite().getPage().closeEditor(CDOEditor.this, false);
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
              if (getSite().getPage().getActiveEditor() == CDOEditor.this)
              {
                handleActivate();
              }
            }
          });
        }
      }
      catch (CoreException exception)
      {
        PluginDelegator.INSTANCE.log(exception);
      }
    }
  };

  private IListener viewTargetListener = new IListener()
  {
    private CDOID inputID;

    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewTargetChangedEvent)
      {
        Object input = selectionViewer.getInput();
        if (input == EMPTY_INPUT)
        {
          if (inputID != null)
          {
            try
            {
              CDOObject object = view.getObject(inputID);
              selectionViewer.setInput(object);
              inputID = null;
            }
            catch (Exception ex)
            {
              // Ignore
            }
          }
        }
        else if (input instanceof EObject)
        {
          CDOObject object = CDOUtil.getCDOObject((EObject)input);
          if (object.cdoInvalid())
          {
            inputID = object.cdoID();
            selectionViewer.setInput(EMPTY_INPUT);
          }
        }
      }
    }
  };

  /**
   * Handles activation of the editor or it's associated views. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void handleActivateGen()
  {
    if (editingDomain.getResourceToReadOnlyMap() != null)
    {
      editingDomain.getResourceToReadOnlyMap().clear();
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
   * Handles activation of the editor or it's associated views. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  protected void handleActivate()
  {
    handleActivateGen();
    setCurrentViewer(selectionViewer);
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
      BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK, "org.eclipse.emf.cdo.ui", 0, null, //$NON-NLS-1$
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
    return MessageDialog.openQuestion(getSite().getShell(), getString("_UI_FileConflict_label"), //$NON-NLS-1$
        getString("_WARN_FileConflict")); //$NON-NLS-1$
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
    MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
    contextMenu.add(new Separator("additions")); //$NON-NLS-1$
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
      view.addListener(viewTargetListener);

      // TODO Check if a CommandStack is needed
      BasicCommandStack commandStack = new BasicCommandStack();
      commandStack.addCommandStackListener(new CommandStackListener()
      {
        public void commandStackChanged(final EventObject event)
        {
          try
          {
            if (getContainer() != null && !getContainer().isDisposed())
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
          }
          catch (RuntimeException ex)
          {
            OM.LOG.error(ex);
          }
        }
      });

      ResourceSet resourceSet = view.getResourceSet();
      editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, resourceSet);

      // This adapter provides the EditingDomain of the Editor
      resourceSet.eAdapters().add(new EditingDomainProviderAdapter());

      String resourcePath = editorInput.getResourcePath();
      if (resourcePath == null)
      {
        viewerInput = resourceSet;
      }
      else
      {
        URI resourceURI = CDOURIUtil.createResourceURI(view, resourcePath);
        viewerInput = resourceSet.getResource(resourceURI, true);
      }

      // resourceSet.eAdapters().add(problemIndicationAdapter);
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
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
      BasicDiagnostic basicDiagnostic = new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.ui", 0, getString( //$NON-NLS-1$
          "_UI_CreateModelError_message", resource.getURI()), new Object[] { exception == null ? (Object)resource //$NON-NLS-1$
          : exception });
      basicDiagnostic.merge(EcoreUtil.computeDiagnostic(resource, true));
      return basicDiagnostic;
    }
    else if (exception != null)
    {
      return new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.ui", 0, getString( //$NON-NLS-1$
          "_UI_CreateModelError_message", resource.getURI()), new Object[] { exception }); //$NON-NLS-1$
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
      setPageText(pageIndex, getString("_UI_SelectionPage_label")); //$NON-NLS-1$

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
      createModel();

      // Create a page for the selection tree view.
      getContainer().setLayoutData(UIUtil.createGridData());
      getContainer().setLayout(UIUtil.createGridLayout(1));
      Composite composite = UIUtil.createGridComposite(getContainer(), 1);
      composite.setLayoutData(UIUtil.createGridData());
      composite.setLayout(UIUtil.createGridLayout(1));
      Tree tree = new Tree(composite, SWT.MULTI | SWT.BORDER);
      tree.setLayoutData(UIUtil.createGridData());

      final Set<CDOID> expandedIDs = new HashSet<CDOID>();
      final boolean sliderAllowed = view.isReadOnly() && view.getSession().getRepositoryInfo().isSupportingAudits();
      if (sliderAllowed)
      {
        createTimeSlider(composite, expandedIDs);
      }

      selectionViewer = new TreeViewer(tree)
      {
        @Override
        public void setSelection(ISelection selection, boolean reveal)
        {
          if (sliderAllowed && selection instanceof IStructuredSelection)
          {
            IStructuredSelection ssel = (IStructuredSelection)selection;
            for (Iterator<?> it = ssel.iterator(); it.hasNext();)
            {
              Object object = it.next();
              if (object instanceof EObject)
              {
                CDOObject cdoObject = CDOUtil.getCDOObject((EObject)object);
                switch (cdoObject.cdoState())
                {
                case TRANSIENT:
                case PREPARED:
                case INVALID:
                case INVALID_CONFLICT:
                  it.remove();
                }
              }
            }
          }

          super.setSelection(selection, reveal);
        }
      };

      selectionViewer.addTreeListener(new ITreeViewerListener()
      {
        public void treeExpanded(TreeExpansionEvent event)
        {
          CDOID id = getID(event.getElement());
          if (id != null)
          {
            expandedIDs.add(id);
          }
        }

        public void treeCollapsed(TreeExpansionEvent event)
        {
          CDOID id = getID(event.getElement());
          if (id != null)
          {
            expandedIDs.remove(id);
          }
        }

        private CDOID getID(Object element)
        {
          if (element instanceof EObject)
          {
            CDOObject object = CDOUtil.getCDOObject((EObject)element);
            return object.cdoID();
          }

          return null;
        }
      });

      setCurrentViewer(selectionViewer);

      selectionViewer.setContentProvider(createContentProvider());
      selectionViewer.setLabelProvider(createLabelProvider());
      selectionViewer.setInput(viewerInput);

      new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);

      createContextMenuFor(selectionViewer);
      int pageIndex = addPage(composite);
      setPageText(pageIndex, getString("_UI_SelectionPage_label")); //$NON-NLS-1$

      setActivePage(0);

      // Ensures that this editor will only display the page's tab
      // area if there are more than one page
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
          if (CDOUtil.isLegacyObject(cdoObject))
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

    getViewer().getControl().addMouseListener(new MouseListener()
    {
      public void mouseDoubleClick(MouseEvent e)
      {
        try
        {
          getSite().getPage().showView("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
        }
        catch (PartInitException ex)
        {
          OM.LOG.error(ex);
        }
      }

      public void mouseDown(MouseEvent e)
      {
        // do nothing
      }

      public void mouseUp(MouseEvent e)
      {
        // do nothing
      }
    });
  }

  /**
   * @ADDED
   */
  protected void createTimeSlider(final Composite composite, final Set<CDOID> expandedIDs)
  {
    final CDOSession session = view.getSession();
    final long startTimeStamp = session.getRepositoryInfo().getCreationTime();
    final long endTimeStamp = session.getLastUpdateTime();

    final int MIN = 0;
    final int MAX = 100000;
    final long absoluteTimeWindowLength = endTimeStamp - startTimeStamp;
    final long scaleFactor = MAX - MIN;
    final double stepSize = (double)absoluteTimeWindowLength / (double)scaleFactor;

    final Group group = new Group(composite, SWT.NONE);
    group.setLayoutData(UIUtil.createEmptyGridData());
    group.setLayout(new FillLayout());
    group.setText(CDOCommonUtil.formatTimeStamp(endTimeStamp));
    group.setVisible(false);

    final Scale scale = new Scale(group, SWT.HORIZONTAL);
    scale.setMinimum(MIN);
    scale.setMaximum(MAX);
    scale.setSelection(MAX);

    scale.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        long value = scale.getSelection();
        long timeStamp = startTimeStamp + Math.round(stepSize * value);

        group.setText(CDOCommonUtil.formatTimeStamp(timeStamp));

        view.setTimeStamp(timeStamp);
        selectionViewer.refresh();
        setExpandedStates();
      }

      private void setExpandedStates()
      {
        for (CDOID id : expandedIDs)
        {
          try
          {
            CDOObject object = view.getObject(id);
            selectionViewer.setExpandedState(object, true);
          }
          catch (Exception ex)
          {
            // Ignore
          }
        }
      }
    });

    IAction action = new Action()
    {
      @Override
      public void run()
      {
        if (group.isVisible())
        {
          group.setVisible(false);
          group.setLayoutData(UIUtil.createEmptyGridData());
          composite.layout();
        }
        else
        {
          group.setVisible(true);
          group.setLayoutData(new GridData(SWT.FILL, 50, true, false));
          composite.layout();
        }
      }
    };

    action.setEnabled(true);
    action.setChecked(false);
    action.setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_SLIDER_ICON));
    action.setToolTipText(Messages.getString("CDOEditor.1")); //$NON-NLS-1$
    getActionBars().getToolBarManager().add(action);
  }

  /**
   * @ADDED
   */
  protected IContentProvider createContentProvider()
  {
    return new AdapterFactoryContentProvider(adapterFactory);
  }

  /**
   * @ADDED
   */
  protected ILabelProvider createLabelProvider()
  {
    return new DecoratingLabelProvider(new CDOLabelProvider(adapterFactory, view, selectionViewer),
        createLabelDecorator());
  }

  /**
   * @ADDED
   */
  protected ILabelDecorator createLabelDecorator()
  {
    return PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  /**
   * If there is just one page in the multi-page editor part, this hides the single tab at the bottom. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  protected void hideTabs()
  {
    if (getPageCount() <= 1)
    {
      setPageText(0, ""); //$NON-NLS-1$
      if (getContainer() != null && !getContainer().isDisposed() && getContainer() instanceof CTabFolder)
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
   * @generated NOT
   */
  protected void showTabs()
  {
    if (getPageCount() > 1)
    {
      setPageText(0, getString("_UI_SelectionPage_label")); //$NON-NLS-1$
      if (getContainer() != null && !getContainer().isDisposed() && getContainer() instanceof CTabFolder)
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
  @SuppressWarnings("rawtypes")
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
   * @generated NOT
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
          contentOutlineViewer.setInput(viewerInput);

          // Make sure our popups work.
          //
          createContextMenuFor(contentOutlineViewer);

          if (!CDOUtil.getResources(editingDomain.getResourceSet()).isEmpty())
          {
            // Select the root object in the view.
            //
            contentOutlineViewer.setSelection(
                new StructuredSelection(CDOUtil.getResources(editingDomain.getResourceSet()).get(0)), true);
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
    Display.getCurrent().asyncExec(null);
    // Save only resources that have actually changed.
    //
    final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
    saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

    IRunnableWithProgress operation = new IRunnableWithProgress()
    {
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
        boolean first = true;
        EList<Resource> resources = CDOUtil.getResources(editingDomain.getResourceSet());
        monitor.beginTask("", resources.size()); //$NON-NLS-1$
        try
        {
          for (Resource resource : resources)
          {
            if ((first || !resource.getContents().isEmpty() || isPersisted(resource))
                && !editingDomain.isReadOnly(resource))
            {
              try
              {
                savedResources.add(resource);
                saveOptions.put(CDOResource.OPTION_SAVE_PROGRESS_MONITOR, new SubProgressMonitor(monitor, 1));
                saveOptions.put(CDOResource.OPTION_SAVE_OVERRIDE_TRANSACTION, view);
                resource.save(saveOptions);
              }
              catch (TransactionException exception)
              {
                OM.LOG.error(exception);
                final Shell shell = getSite().getShell();
                shell.getDisplay().syncExec(new Runnable()
                {
                  public void run()
                  {
                    CDOTransaction transaction = (CDOTransaction)view;
                    String title = Messages.getString("CDOEditor.17"); //$NON-NLS-1$
                    String message = Messages.getString("CDOEditor.18"); //$NON-NLS-1$
                    RollbackTransactionDialog dialog = new RollbackTransactionDialog(getEditorSite().getPage(), title,
                        message, transaction);
                    if (dialog.open() == RollbackTransactionDialog.OK)
                    {
                      transaction.rollback();
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
            else
            {
              monitor.worked(1);
            }
          }
        }
        finally
        {
          monitor.done();
        }
      }
    };

    updateProblemIndication = false;

    try
    {
      // This runs the options, and shows progress.
      //
      new ProgressMonitorDialog(getSite().getShell()).run(true, true, operation);

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
   * @generated NOT
   */
  protected void doSaveAs(URI uri, IEditorInput editorInput)
  {
    throw new UnsupportedOperationException();

    // CDONet4jUtil.getResources(editingDomain.getResourceSet()).get(0).setURI(uri);
    // setInputWithNotify(editorInput);
    // setPartName(editorInput.getName());
    // IProgressMonitor progressMonitor = getActionBars().getStatusLineManager() != null ? getActionBars()
    // .getStatusLineManager().getProgressMonitor() : new NullProgressMonitor();
    // doSave(progressMonitor);
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
          statusLineManager.setMessage(getString("_UI_NoObjectSelected")); //$NON-NLS-1$
          break;
        }
        case 1:
        {
          String text = new AdapterFactoryItemDelegator(adapterFactory).getText(collection.iterator().next());
          statusLineManager.setMessage(getString("_UI_SingleObjectSelected", text)); //$NON-NLS-1$
          break;
        }
        default:
        {
          statusLineManager.setMessage(getString("_UI_MultiObjectSelected", Integer.toString(collection.size()))); //$NON-NLS-1$
          break;
        }
        }
      }
      else
      {
        statusLineManager.setMessage(""); //$NON-NLS-1$
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
    MenuManager submenuManager = new MenuManager(Messages.getString("CDOEditor.23")); //$NON-NLS-1$
    if (populateNewRoot(submenuManager))
    {
      menuManager.insertBefore("edit", submenuManager); //$NON-NLS-1$
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
            menuManager.insertBefore(
                "edit", new LongRunningAction(page, Messages.getString("CDOEditor.26") + SafeAction.INTERACTIVE) //$NON-NLS-1$ //$NON-NLS-2$
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
                  protected void doRun(IProgressMonitor progressMonitor) throws Exception
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
    CDOPackageRegistry packageRegistry = view.getSession().getPackageRegistry();
    for (Map.Entry<String, Object> entry : EMFUtil.getSortedRegistryEntries(packageRegistry))
    {
      IContributionItem item = populateSubMenu(entry.getKey(), entry.getValue(), packageRegistry);
      if (item != null)
      {
        menuManager.add(item);
        populated = true;
      }
    }

    return populated;
  }

  /**
   * @ADDED
   */
  private IContributionItem populateSubMenu(String nsURI, Object value, final CDOPackageRegistry packageRegistry)
  {
    if (value instanceof EPackage)
    {
      EPackage ePackage = (EPackage)value;
      CDOPackageInfo packageInfo = packageRegistry.getPackageInfo(ePackage);
      CDOPackageUnit packageUnit = packageInfo.getPackageUnit();
      if (packageUnit.isResource())
      {
        return null;
      }

      ImageDescriptor imageDescriptor = SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE);
      MenuManager submenuManager = new MenuManager(nsURI, imageDescriptor, nsURI);
      populateSubMenu(ePackage, submenuManager);
      return submenuManager;
    }

    ImageDescriptor imageDescriptor = SharedIcons.getDescriptor(SharedIcons.OBJ_EPACKAGE_UNKNOWN);
    final MenuManager submenuManager = new MenuManager(nsURI, imageDescriptor, nsURI);
    submenuManager.setRemoveAllWhenShown(true);
    submenuManager.add(new Action(Messages.getString("CDOEditor.27")) //$NON-NLS-1$
        {
        });

    submenuManager.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        String nsURI = submenuManager.getMenuText();
        EPackage ePackage = packageRegistry.getEPackage(nsURI);

        if (ePackage != null)
        {
          populateSubMenu(ePackage, submenuManager);
        }
        else
        {
          OM.LOG.warn(MessageFormat.format(Messages.getString("CDOEditor.28"), nsURI)); //$NON-NLS-1$
        }
      }
    });

    return submenuManager;
  }

  /**
   * @ADDED
   */
  private void populateSubMenu(EPackage ePackage, final MenuManager submenuManager)
  {
    List<EObject> objects = new ArrayList<EObject>();
    for (EClassifier eClassifier : ePackage.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;
        if (!eClass.isAbstract() && !eClass.isInterface())
        {
          objects.add(EcoreUtil.create(eClass));
        }
      }
    }

    if (!objects.isEmpty())
    {
      Collections.sort(objects, new Comparator<EObject>()
      {
        public int compare(EObject o1, EObject o2)
        {
          return o1.eClass().getName().compareTo(o2.eClass().getName());
        }
      });

      for (EObject object : objects)
      {
        CreateRootAction action = new CreateRootAction(object);
        submenuManager.add(action);
      }
    }
  }

  /**
   * @ADDED
   */
  protected String getLabelText(Object object)
  {
    try
    {
      IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
      if (labelProvider != null)
      {
        String text = labelProvider.getText(object);
        if (text != null)
        {
          return text;
        }
      }
    }
    catch (Exception ignore)
    {
      ignore.printStackTrace();
    }

    return ""; //$NON-NLS-1$
  }

  /**
   * @ADDED
   */
  protected Object getLabelImage(Object object)
  {
    try
    {
      IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);
      if (labelProvider != null)
      {
        return labelProvider.getImage(object);
      }
    }
    catch (Exception ignore)
    {
      ignore.printStackTrace();
    }

    return null;
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
    view.removeListener(viewTargetListener);

    if (!view.isClosed())
    {
      try
      {
        if (eventHandler != null)
        {
          eventHandler.dispose();
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }

      try
      {
        if (adapterFactory != null)
        {
          adapterFactory.dispose();
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    getSite().getPage().removePartListener(partListener);

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

    if (((CDOEditorInput)getEditorInput()).isViewOwned())
    {
      view.close();
    }

    super.dispose();
  }

  /**
   * Returns whether the outline view should be presented to the user. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  protected boolean showOutlineView()
  {
    return true;
  }

  /**
   * @ADDED
   */
  protected void fireDirtyPropertyChange()
  {
    try
    {
      getSite().getShell().getDisplay().asyncExec(new Runnable()
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
          catch (RuntimeException ignore)
          {
            // Do nothing
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
      // Do nothing
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
          catch (RuntimeException ignore)
          {
            // Do nothing
          }
        }
      });
    }
    catch (RuntimeException ignore)
    {
      // Do nothing
    }
  }

  /**
   * @author Eike Stepper
   * @ADDED
   */
  private final class CreateRootAction extends LongRunningAction
  {
    private EObject object;

    private CreateRootAction(EObject object)
    {
      super(getEditorSite().getPage(), object.eClass().getName(), ExtendedImageRegistry.getInstance()
          .getImageDescriptor(getLabelImage(object)));
      this.object = object;
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
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
        if (object instanceof InternalCDOObject)
        {
          object = ((InternalCDOObject)object).cdoInternalInstance();
        }

        resource.getContents().add(object);
      }
    }
  }

  /**
   * Adapter that provides the current EditingDomain
   * 
   * @since 2.0
   */
  private class EditingDomainProviderAdapter implements Adapter, IEditingDomainProvider
  {
    public boolean isAdapterForType(Object type)
    {
      return type == IEditingDomainProvider.class;
    }

    public EditingDomain getEditingDomain()
    {
      return editingDomain;
    }

    public Notifier getTarget()
    {
      return null;
    }

    public void notifyChanged(Notification notification)
    {
    }

    public void setTarget(Notifier newTarget)
    {
    }
  }
}
