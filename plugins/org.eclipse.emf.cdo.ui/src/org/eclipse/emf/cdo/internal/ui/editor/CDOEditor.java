/*
 * Copyright (c) 2007-2016, 2019-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 *    Christian W. Damus (CEA LIST) - bug 418452, bug 399487
 */
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.CDOAdapterFactoryContentProvider;
import org.eclipse.emf.cdo.internal.ui.CDOContentProvider;
import org.eclipse.emf.cdo.internal.ui.RunnableViewerRefresh;
import org.eclipse.emf.cdo.internal.ui.actions.TransactionalBackgroundAction;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.BulkAddDialog;
import org.eclipse.emf.cdo.internal.ui.dialogs.RollbackTransactionDialog;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectClassDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.CDOEditorInput2;
import org.eclipse.emf.cdo.ui.CDOEditorInput3;
import org.eclipse.emf.cdo.ui.CDOEventHandler;
import org.eclipse.emf.cdo.ui.CDOInvalidRootAgent;
import org.eclipse.emf.cdo.ui.CDOLabelProvider;
import org.eclipse.emf.cdo.ui.CDOTopicProvider;
import org.eclipse.emf.cdo.ui.CDOTreeExpansionAgent;
import org.eclipse.emf.cdo.ui.DecoratingStyledLabelProvider;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ValidationException;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ui.SafeTreeViewer;
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
import org.eclipse.emf.common.ui.URIEditorInput;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
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
import org.eclipse.emf.edit.ui.provider.DelegatingStyledCellLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.edit.ui.provider.UnwrappingSelectionProvider;
import org.eclipse.emf.edit.ui.util.EditUIUtil;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IInputProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
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
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eike Stepper
 * @generated not
 */
public class CDOEditor extends MultiPageEditorPart implements IEditingDomainProvider, ISelectionProvider, IMenuListener, IViewerProvider, CDOTopicProvider
{
  /**
   * The filters for file extensions supported by the editor.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<String> FILE_EXTENSION_FILTERS = prefixExtensions(
      Arrays.asList(PluginDelegator.INSTANCE.getString("_UI_CDOEditorFilenameExtensions").split("\\s*,\\s*")), "*.");

  /**
   * Returns a new unmodifiable list containing prefixed versions of the extensions in the given list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static List<String> prefixExtensions(List<String> extensions, String prefix)
  {
    List<String> result = new ArrayList<>();
    for (String extension : extensions)
    {
      result.add(prefix + extension);
    }
    return Collections.unmodifiableList(result);
  }

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOEditor.class);

  private static final Field PROPERTY_SHEET_PAGE_VIEWER_FIELD = getPropertySheetPageViewerField();

  private static final Field CONTENT_OUTLINE_PAGE_VIEWER_FIELD = getContentOutlinePageViewerField();

  private static final boolean SHOW_BULK_ADD_ACTION = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.ui.editor.SHOW_BULK_ADD_ACTION");

  private static final boolean LOG_EXCEPTIONS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.ui.editor.LOG_EXCEPTIONS");

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
  protected CDOInvalidRootAgent invalidRootAgent;

  /**
   * @ADDED
   */
  protected CDOTreeExpansionAgent treeExpansionAgent;

  /**
   * @ADDED
   */
  protected CDOEventHandler eventHandler;

  /**
   * @ADDED
   */
  protected CDOObjectHandler objectHandler = new CDOObjectHandler()
  {
    @Override
    public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
    {
      if (object == viewerInput && newState == CDOState.INVALID)
      {
        closeEditor();
      }
    }
  };

  /**
   * This keeps track of the editing domain that is used to track all changes to the model.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
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
   * This is the content outline page.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected IContentOutlinePage contentOutlinePage;

  /**
   * This is a kludge...
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected IStatusLineManager contentOutlineStatusLineManager;

  /**
   * This is the content outline page's viewer.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected TreeViewer contentOutlineViewer;

  /**
   * This is the property sheet page.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected List<PropertySheetPage> propertySheetPages = new ArrayList<>();

  /**
   * This is the viewer that shadows the selection in the content outline.
   * The parent relation must be correctly defined for this to work.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected TreeViewer selectionViewer;

  /**
   * This keeps track of the active content viewer, which may be either one of the viewers in the pages or the content outline viewer.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Viewer currentViewer;

  /**
   * This listens to which ever viewer is active.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ISelectionChangedListener selectionChangedListener;

  /**
   * This keeps track of all the {@link org.eclipse.jface.viewers.ISelectionChangedListener}s that are listening to this editor.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Collection<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();

  /**
   * This keeps track of the selection of the editor as a whole.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ISelection editorSelection = StructuredSelection.EMPTY;

  /**
   * This listens for when the outline becomes active
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected IPartListener partListener = new IPartListener()
  {
    @Override
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
        if (propertySheetPages.contains(((PropertySheet)p).getCurrentPage()))
        {
          getActionBarContributor().setActiveEditor(CDOEditor.this);
          handleActivate();
        }
      }
      else if (p == CDOEditor.this)
      {
        if (pagesCreated.get())
        {
          handleActivate();
        }
      }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart p)
    {
      // Ignore.
    }

    @Override
    public void partClosed(IWorkbenchPart p)
    {
      // Ignore.
    }

    @Override
    public void partDeactivated(IWorkbenchPart p)
    {
      // Ignore.
    }

    @Override
    public void partOpened(IWorkbenchPart p)
    {
      // Ignore.
    }
  };

  /**
   * Resources that have been removed since last activation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Resource> removedResources = new ArrayList<>();

  /**
   * Resources that have been changed since last activation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Resource> changedResources = new ArrayList<>();

  /**
   * Resources that have been saved.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Collection<Resource> savedResources = new ArrayList<>();

  /**
   * Map to store the diagnostic associated with a resource.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Map<Resource, Diagnostic> resourceToDiagnosticMap = new LinkedHashMap<>();

  /**
   * Controls whether the problem indication should be updated.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected boolean updateProblemIndication = true;

  /**
   * Adapter used to update the problem indication when resources are demanded loaded.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
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
              @Override
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
      resourceToDiagnosticMap.remove(target);
      if (updateProblemIndication)
      {
        getSite().getShell().getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            updateProblemIndication();
          }
        });
      }
    }
  };

  protected final AtomicBoolean pagesCreated = new AtomicBoolean();

  protected final ConcurrentArray<Listener> topicListeners = new ConcurrentArray<>()
  {
    @Override
    protected Listener[] newArray(int length)
    {
      return new Listener[length];
    }
  };

  protected Topic currentTopic;

  /**
   * Handles activation of the editor or it's associated views.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void handleActivateGen()
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
   * Handles what to do with changed resources on activation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * Updates the problems indication with the information described in the specified diagnostic.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void updateProblemIndication()
  {
    if (updateProblemIndication)
    {
      BasicDiagnostic diagnostic = new BasicDiagnostic(Diagnostic.OK, "org.eclipse.emf.cdo.ui", 0, null, new Object[] { editingDomain.getResourceSet() });
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
    }
  }

  /**
   * Shows a dialog that asks if conflicting changes should be discarded.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected boolean handleDirtyConflict()
  {
    return MessageDialog.openQuestion(getSite().getShell(), getString("_UI_FileConflict_label"), getString("_WARN_FileConflict"));
  }

  /**
   * This creates a model editor.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOEditor()
  {
    super();
    initializeEditingDomain();
  }

  /**
   * This sets up the editing domain for the model editor.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
      @Override
      public void commandStackChanged(final EventObject event)
      {
        getContainer().getDisplay().asyncExec(new Runnable()
        {
          @Override
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
    editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, new HashMap<>());
  }

  /**
   * @ADDED
   */
  protected void initializeEditingDomain()
  {
    adapterFactory = createAdapterFactory(true);
  }

  /**
   * This is here for the listener to be able to call it.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void firePropertyChange(int action)
  {
    super.firePropertyChange(action);
  }

  /**
   * This sets the selection into whichever viewer is active.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
        @Override
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EditingDomain getEditingDomain()
  {
    return editingDomain;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public class ReverseAdapterFactoryContentProvider extends AdapterFactoryContentProvider
  {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public ReverseAdapterFactoryContentProvider(AdapterFactory adapterFactory)
    {
      super(adapterFactory);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
          @Override
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
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public Viewer getViewer()
  {
    return currentViewer;
  }

  /**
   * This creates a context menu for the viewer and adds a listener as well registering the menu for extension. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
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

    configureDND(viewer);
  }

  protected void configureDND(StructuredViewer viewer)
  {
    int dndOperations = getDNDOperations();
    Transfer[] transfers = getDNDTransfers();
    viewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(viewer));
    viewer.addDropSupport(dndOperations, transfers, new EditingDomainViewerDropAdapter(editingDomain, viewer));
  }

  protected int getDNDOperations()
  {
    return DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
  }

  protected Transfer[] getDNDTransfers()
  {
    return new Transfer[] { LocalTransfer.getInstance(), LocalSelectionTransfer.getTransfer() };
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
   * <!-- begin-user-doc -->
   * @deprecated Not called.
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  public void createModel()
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

  protected AdapterFactoryEditingDomain createEditingDomain(BasicCommandStack commandStack, ResourceSet resourceSet)
  {
    return new AdapterFactoryEditingDomain(adapterFactory, commandStack, resourceSet);
  }

  /**
   * Returns a diagnostic describing the errors and warnings listed in the resource
   * and the specified exception (if any).
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Diagnostic analyzeResourceProblems(Resource resource, Exception exception)
  {
    if (!resource.getErrors().isEmpty() || !resource.getWarnings().isEmpty())
    {
      BasicDiagnostic basicDiagnostic = new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.ui", 0,
          getString("_UI_CreateModelError_message", resource.getURI()), new Object[] { exception == null ? (Object)resource : exception });
      basicDiagnostic.merge(EcoreUtil.computeDiagnostic(resource, true));
      return basicDiagnostic;
    }
    else if (exception != null)
    {
      return new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.ui", 0, getString("_UI_CreateModelError_message", resource.getURI()),
          new Object[] { exception });
    }
    else
    {
      return Diagnostic.OK_INSTANCE;
    }
  }

  /**
   * This is the method used by the framework to install your own controls.
   * <!-- begin-user-doc -->
   * @deprecated Not called.
   * <!-- end-user-doc -->
   *
   * @generated
   */
  @Deprecated
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
        @Override
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
      @Override
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
    CDOEditorInput editorInput = (CDOEditorInput)getEditorInput();

    CDOView view = editorInput.getView();
    String resourcePath = editorInput.getResourcePath();
    IEditingDomainProvider domainProvider = AdapterUtil.adapt(editorInput, IEditingDomainProvider.class);

    if (view != null)
    {
      CDOID objectID = editorInput instanceof CDOEditorInput2 ? ((CDOEditorInput2)editorInput).getObjectID() : null;
      createPages(view, resourcePath, objectID, domainProvider);
    }
    else if (editorInput instanceof CDOEditorInput3)
    {
      Display display = getSite().getShell().getDisplay();

      Label progressLabel = new Label(getContainer(), SWT.NONE);
      progressLabel.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
      progressLabel.setText("Operation in progress...");

      addPage(0, progressLabel);
      setActivePage(0);

      Job.create("Open view", monitor -> {
        ProgressMonitorWrapper monitorWrapper = new ProgressMonitorWrapper(monitor)
        {
          @Override
          public void beginTask(String name, int totalWork)
          {
            display.asyncExec(() -> progressLabel.setText(name));
            super.beginTask(name, totalWork);
          }

          @Override
          public void setTaskName(String name)
          {
            display.asyncExec(() -> progressLabel.setText(name));
            super.setTaskName(name);
          }
        };

        try
        {
          CDOView newView = ((CDOEditorInput3)editorInput).openView(monitorWrapper);
          if (newView != null)
          {
            UIUtil.asyncExec(display, () -> {
              hideTabs();
              removePage(0);

              CDOID objectID = editorInput instanceof CDOEditorInput2 ? ((CDOEditorInput2)editorInput).getObjectID() : null;
              createPages(newView, resourcePath, objectID, domainProvider);

              handleActivate();

              CDOActionBarContributor actionBarContributor = (CDOActionBarContributor)getActionBarContributor();
              actionBarContributor.activate();

              IContentOutlinePage contentOutlinePage = getContentOutlinePage();
              if (contentOutlinePage instanceof CDOContentOutlinePage)
              {
                ((CDOContentOutlinePage)contentOutlinePage).setView(newView);
              }

              firePropertyChange(IEditorPart.PROP_TITLE);
            });
          }

          return Status.OK_STATUS;
        }
        catch (Throwable ex)
        {
          return OM.BUNDLE.getStatus(ex);
        }
      }).schedule();
    }
  }

  protected void createPages(CDOView view, String resourcePath, CDOID objectID, IEditingDomainProvider domainProvider)
  {
    try
    {
      this.view = view;

      ResourceSet resourceSet = view.getResourceSet();
      resourceSet.eAdapters().add(new EditingDomainProviderAdapter());

      invalidRootAgent = new CDOInvalidRootAgent(view)
      {
        @Override
        protected Object getRootFromUI()
        {
          if (selectionViewer != null)
          {
            return selectionViewer.getInput();
          }

          return null;
        }

        @Override
        protected void setRootToUI(Object root)
        {
          if (selectionViewer != null)
          {
            selectionViewer.setInput(root);
          }
        }

        @Override
        protected void closeUI()
        {
          closeEditor();
        }
      };

      CommandStack commandStack;

      if (domainProvider != null && domainProvider.getEditingDomain() instanceof AdapterFactoryEditingDomain)
      {
        editingDomain = (AdapterFactoryEditingDomain)domainProvider.getEditingDomain();
        commandStack = editingDomain.getCommandStack();

        AdapterFactory editingDomainAdapterFactory = editingDomain.getAdapterFactory();
        if (editingDomainAdapterFactory instanceof ComposedAdapterFactory)
        {
          adapterFactory = (ComposedAdapterFactory)editingDomainAdapterFactory;
        }
        else
        {
          adapterFactory.addAdapterFactory(editingDomainAdapterFactory);
        }
      }
      else
      {
        commandStack = new BasicCommandStack();
        editingDomain = createEditingDomain((BasicCommandStack)commandStack, resourceSet);
      }

      commandStack.addCommandStackListener(new CommandStackListener()
      {
        @Override
        public void commandStackChanged(final EventObject event)
        {
          Composite container = getContainer();
          if (!container.isDisposed())
          {
            container.getDisplay().asyncExec(new Runnable()
            {
              @Override
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
        }
      });

      if (resourcePath == null)
      {
        viewerInput = resourceSet;
      }
      else
      {
        if (CDOIDUtil.isNull(objectID))
        {
          URI resourceURI = CDOURIUtil.createResourceURI(view, resourcePath);
          viewerInput = resourceSet.getResource(resourceURI, true);
        }
        else
        {
          InternalCDOObject inputObject = (InternalCDOObject)view.getObject(objectID);
          viewerInput = inputObject.cdoInternalInstance();
        }

        if (!view.isReadOnly())
        {
          view.addObjectHandler(objectHandler);
        }
      }

      Tree tree = new Tree(getContainer(), SWT.MULTI);
      selectionViewer = new SafeTreeViewer(tree, ex -> handleException(ex));
      setCurrentViewer(selectionViewer);

      selectionViewer.setContentProvider(createContentProvider());
      selectionViewer.setLabelProvider(createLabelProvider(selectionViewer));
      selectionViewer.setInput(viewerInput);

      getSite().getShell().getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            IContentProvider contentProvider = selectionViewer.getContentProvider();
            if (contentProvider instanceof IStructuredContentProvider)
            {
              IStructuredContentProvider structuredContentProvider = (IStructuredContentProvider)contentProvider;

              Object[] elements = structuredContentProvider.getElements(viewerInput);
              if (elements != null && elements.length != 0)
              {
                setSelectionToViewer(Collections.singleton(elements[0]));
              }
            }
          }
          catch (Exception ex)
          {
            //$FALL-THROUGH$
          }
        }
      });

      new AdapterFactoryTreeEditor(selectionViewer.getTree(), adapterFactory);

      createContextMenuFor(selectionViewer);
      int pageIndex = addPage(tree);
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
        protected void viewTargetChanged(CDOBranchPoint branchPoint)
        {
          setCurrentTopic(createTopic());
        }

        @Override
        protected void objectInvalidated(InternalCDOObject cdoObject)
        {
          if (CDOUtil.isLegacyObject(cdoObject))
          {
            CDOStateMachine.INSTANCE.read(cdoObject);
          }

          for (PropertySheetPage propertySheetPage : propertySheetPages)
          {
            List<?> objects = null;

            try
            {
              // Bug 453211: ExtendedPropertySheetPage.getInput() is only available since EMF 2.9.
              if (propertySheetPage instanceof ExtendedPropertySheetPage)
              {
                ExtendedPropertySheetPage page = (ExtendedPropertySheetPage)propertySheetPage;
                objects = page.getInput();
              }
            }
            catch (NoSuchMethodError ex)
            {
              if (PROPERTY_SHEET_PAGE_VIEWER_FIELD == null)
              {
                throw ex;
              }

              //$FALL-THROUGH$
            }

            if (objects == null)
            {
              Object value = ReflectUtil.getValue(PROPERTY_SHEET_PAGE_VIEWER_FIELD, propertySheetPage);
              if (value instanceof IInputProvider)
              {
                IInputProvider inputProvider = (IInputProvider)value;
                Object input = inputProvider.getInput();
                if (input instanceof Object[])
                {
                  objects = Arrays.asList((Object[])input);
                }
              }
            }

            if (objects != null)
            {
              for (Object object : objects)
              {
                if (object == cdoObject)
                {
                  propertySheetPage.refresh();
                  break;
                }
              }
            }
          }
        }

        @Override
        protected void viewConflict(CDOObject conflictingObject, boolean firstConflict)
        {
          refreshViewer(conflictingObject);
        }

        @Override
        protected void viewPermissionsChanged(Map<CDOID, Pair<CDOPermission, CDOPermission>> permissionChanges)
        {
          refreshViewer(null);
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

      setCurrentTopic(createTopic());
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }

    if (view.isReadOnly())
    {
      treeExpansionAgent = new CDOTreeExpansionAgent(view, selectionViewer);
    }

    getViewer().getControl().addMouseListener(new MouseListener()
    {
      @Override
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

      @Override
      public void mouseDown(MouseEvent e)
      {
        // do nothing
      }

      @Override
      public void mouseUp(MouseEvent e)
      {
        // do nothing
      }
    });

    pagesCreated.set(true);
  }

  /**
   * @ADDED
   */
  protected IContentProvider createContentProvider()
  {
    return new CDOContentProvider<CDOView>()
    {
      private final CDOAdapterFactoryContentProvider delegate = new CDOAdapterFactoryContentProvider(adapterFactory);

      @Override
      public void inputChanged(Viewer newViewer, Object oldInput, Object newInput)
      {
        super.inputChanged(newViewer, oldInput, newInput);
        delegate.inputChanged(newViewer, oldInput, newInput);
      }

      @Override
      protected Object adapt(Object target, Object type)
      {
        return adapterFactory.adapt(target, type);
      }

      @Override
      protected Object[] modifyChildren(Object parent, Object[] children)
      {
        return children;
      }

      @Override
      protected ITreeContentProvider getContentProvider(Object object)
      {
        return delegate;
      }

      @Override
      protected RunnableViewerRefresh getViewerRefresh()
      {
        return delegate.getViewerRefresh();
      }

      @Override
      protected boolean isContext(Object object)
      {
        return false;
      }

      @Override
      protected ContextState getContextState(CDOView view)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      protected void openContext(CDOView view)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      protected void closeContext(CDOView view)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      protected Object getRootObject(CDOView view)
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * @ADDED
   * @deprecated As of 4.10 use {@link #createLabelProvider(TreeViewer)}.
   */
  @Deprecated
  protected ILabelProvider createLabelProvider()
  {
    return createLabelProvider(selectionViewer);
  }

  /**
   * @ADDED
   * @since 4.10
   */
  protected ILabelProvider createLabelProvider(TreeViewer viewer)
  {
    CDOLabelProvider provider = new CDOLabelProvider(adapterFactory, view, viewer);
    ILabelDecorator decorator = createLabelDecorator();

    return new DelegatingStyledCellLabelProvider(new DecoratingStyledLabelProvider(provider, decorator)
    {
      @Override
      protected void handleException(Exception ex)
      {
        CDOEditor.this.handleException(ex);
      }
    });
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
      Composite container = getContainer();
      if (container instanceof CTabFolder && !container.isDisposed())
      {
        Point point = container.getSize();
        Rectangle clientArea = container.getClientArea();
        container.setSize(point.x, 2 * point.y - clientArea.height - clientArea.y);
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
      Composite container = getContainer();
      if (container instanceof CTabFolder && !container.isDisposed())
      {
        Point point = container.getSize();
        Rectangle clientArea = container.getClientArea();
        container.setSize(point.x, clientArea.height + clientArea.y);
      }
    }
  }

  /**
   * This is used to track the active viewer.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Class<T> key)
  {
    if (key.equals(IContentOutlinePage.class))
    {
      return (T)(showOutlineView() ? getContentOutlinePage() : null);
    }
    else if (key.equals(IPropertySheetPage.class))
    {
      return (T)getPropertySheetPage();
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
      contentOutlinePage = new CDOContentOutlinePage();

      // Listen to selection so that we can handle it is a special way.
      //
      contentOutlinePage.addSelectionChangedListener(new ISelectionChangedListener()
      {
        // This ensures that we handle selections correctly.
        //
        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
          handleContentOutlineSelection(event.getSelection());
        }
      });
    }

    return contentOutlinePage;
  }

  /**
   * This accesses a cached version of the property sheet.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public IPropertySheetPage getPropertySheetPageGen()
  {
    PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage(editingDomain)
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
    propertySheetPages.add(propertySheetPage);

    return propertySheetPage;
  }

  /**
   * This accesses a cached version of the property sheet. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public IPropertySheetPage getPropertySheetPage()
  {
    PropertySheetPage propertySheetPage = new ExtendedPropertySheetPage(editingDomain)
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
        CDOEditor.this.setSelectionToViewer(selection);
        CDOEditor.this.setFocus();
      }

      @Override
      public void setActionBars(IActionBars actionBars)
      {
        super.setActionBars(actionBars);
        getActionBarContributor().shareGlobalActions(this, actionBars);
      }

      @Override
      public void selectionChanged(IWorkbenchPart part, ISelection selection)
      {
        try
        {
          super.selectionChanged(part, selection);
        }
        catch (Exception ex)
        {
          handleException(ex);
          super.selectionChanged(part, StructuredSelection.EMPTY);
        }
      }
    };

    propertySheetPage.setPropertySourceProvider(new AdapterFactoryContentProvider(adapterFactory));
    propertySheetPages.add(propertySheetPage);

    return propertySheetPage;
  }

  /**
   * This deals with how we want selection in the outliner to affect the other views.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
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

        ArrayList<Object> selectionList = new ArrayList<>();
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
    if (view != null)
    {
      return view.isDirty();
    }

    return false;
  }

  /**
   * This is for implementing {@link IEditorPart} and simply saves the model file.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public void doSaveGen(IProgressMonitor progressMonitor)
  {
    // Save only resources that have actually changed.
    //
    final Map<Object, Object> saveOptions = new HashMap<>();
    saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

    // Do the work within an operation because this is a long running activity that modifies the workbench.
    //
    IRunnableWithProgress operation = new IRunnableWithProgress()
    {
      // This is the method that gets invoked when the operation runs.
      //
      @Override
      public void run(IProgressMonitor monitor)
      {
        // Save the resources to the file system.
        //
        boolean first = true;
        for (Resource resource : editingDomain.getResourceSet().getResources())
        {
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
    final Map<Object, Object> saveOptions = new HashMap<>();
    saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

    IRunnableWithProgress operation = new IRunnableWithProgress()
    {
      @Override
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
        boolean first = true;
        EList<Resource> resources = CDOUtil.getResources(editingDomain.getResourceSet());
        try
        {
          for (Resource resource : resources)
          {
            if ((first || !resource.getContents().isEmpty() || isPersisted(resource)) && !editingDomain.isReadOnly(resource))
            {
              try
              {
                savedResources.add(resource);
                if (resource instanceof CDOResource)
                {
                  CDOView resourceView = ((CDOResource)resource).cdoView();
                  if (resourceView == view || resourceView.isReadOnly())
                  {
                    continue;
                  }
                }

                resource.save(saveOptions);
              }
              catch (Exception exception)
              {
                OM.LOG.error(exception);
                resourceToDiagnosticMap.put(resource, analyzeResourceProblems(resource, exception));
              }

              first = false;
            }
          }

          if (!view.isReadOnly())
          {
            final CDOTransaction transaction = (CDOTransaction)view;

            try
            {
              transaction.commit(monitor);
            }
            catch (final Exception exception)
            {
              OM.LOG.error(exception);
              final Shell shell = getSite().getShell();
              shell.getDisplay().syncExec(new Runnable()
              {
                @Override
                public void run()
                {
                  String title = Messages.getString("CDOEditor.17"); //$NON-NLS-1$
                  String message;
                  if (exception instanceof ValidationException)
                  {
                    message = MessageFormat.format(Messages.getString("CDOEditor.19"), //$NON-NLS-1$
                        exception.getLocalizedMessage());
                  }
                  else
                  {
                    message = Messages.getString("CDOEditor.18"); //$NON-NLS-1$
                  }

                  RollbackTransactionDialog dialog = new RollbackTransactionDialog(getEditorSite().getPage(), title, message, transaction);
                  if (dialog.open() == RollbackTransactionDialog.OK)
                  {
                    transaction.rollback();
                  }
                }
              });
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
   * This returns whether something has been persisted to the URI of the specified resource.
   * The implementation uses the URI converter from the editor's resource set to try to open an input stream.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * This also changes the editor's input.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void doSaveAs()
  {
    String[] filters = FILE_EXTENSION_FILTERS.toArray(new String[FILE_EXTENSION_FILTERS.size()]);
    String[] files = EditorEditorAdvisor.openFilePathDialog(getSite().getShell(), SWT.SAVE, filters);
    if (files.length > 0)
    {
      URI uri = URI.createFileURI(files[0]);
      doSaveAs(uri, new URIEditorInput(uri));
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
  }

  /**
   * This is called during startup.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void initGen(IEditorSite site, IEditorInput editorInput)
  {
    setSite(site);
    setInputWithNotify(editorInput);
    setPartName(editorInput.getName());
    site.setSelectionProvider(this);
    site.getPage().addPartListener(partListener);
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

  protected void setCurrentTopic(Topic topic)
  {
    if (!Objects.equals(currentTopic, topic))
    {
      if (currentTopic != null)
      {
        for (Listener listener : topicListeners.get())
        {
          listener.topicRemoved(this, currentTopic);
        }
      }

      currentTopic = topic;

      if (currentTopic != null)
      {
        for (Listener listener : topicListeners.get())
        {
          listener.topicAdded(this, currentTopic);
        }
      }
    }
  }

  protected Topic createTopic()
  {
    if (viewerInput instanceof CDOResource)
    {
      return createTopic((CDOResource)viewerInput);
    }

    if (viewerInput instanceof EObject)
    {
      CDOObject cdoObject = CDOUtil.getCDOObject((EObject)viewerInput);
      if (cdoObject != null)
      {
        return createTopic(cdoObject.cdoResource());
      }
    }

    if (view != null)
    {
      EList<Resource> resources = view.getResourceSet().getResources();
      if (!resources.isEmpty())
      {
        Resource resource = resources.get(0);

        if (resource instanceof CDOResource)
        {
          return createTopic((CDOResource)resource);
        }
      }
    }

    return null;
  }

  protected Topic createTopic(CDOResource resource)
  {
    String path = resource.getPath();
    CDOBranch branch = view.getBranch();
    long timeStamp = view.getTimeStamp();

    String id = "org.eclipse.emf.cdo.resource" + path + "/" + branch.getID() + "/" + timeStamp;
    Image image = getTitleImage();
    String text = path + "  [" + branch.getName() + "/" + CDOCommonUtil.formatTimeStamp(timeStamp) + "]";
    String description = text;

    return new Topic(view.getSession(), id, image, text, description);
  }

  @Override
  public Topic[] getTopics()
  {
    if (currentTopic != null)
    {
      return new Topic[] { currentTopic };
    }

    return new Topic[0];
  }

  @Override
  public void addTopicListener(Listener listener)
  {
    topicListeners.add(listener);
  }

  @Override
  public void removeTopicListener(Listener listener)
  {
    topicListeners.remove(listener);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFocus()
  {
    getControl(getActivePage()).setFocus();
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.add(listener);
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.remove(listener);
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to return this editor's overall selection.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ISelection getSelection()
  {
    return editorSelection;
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ISelectionProvider} to set this editor's overall selection.
   * Calling this result will notify the listeners.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setSelection(ISelection selection)
  {
    editorSelection = selection;

    for (ISelectionChangedListener listener : selectionChangedListeners)
    {
      try
      {
        listener.selectionChanged(new SelectionChangedEvent(this, selection));
      }
      catch (Exception ex)
      {
        handleException(ex);
      }
    }

    setStatusLineManager(selection);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated NOT
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
          Object element = collection.iterator().next();
          AdapterFactoryItemDelegator adapterFactoryItemDelegator = new AdapterFactoryItemDelegator(adapterFactory);
          String text = null;

          try
          {
            text = adapterFactoryItemDelegator.getText(element);
          }
          catch (Exception ex)
          {
            handleException(ex);
            text = null;
          }

          try
          {
            if (StringUtil.isEmpty(text) && element instanceof EObject)
            {
              EObject eObject = (EObject)element;
              EClass eClass = eObject.eClass();
              text = adapterFactoryItemDelegator.getText(eClass);
            }
          }
          catch (Exception ignore)
          {
            //$FALL-THROUGH$
          }

          if (StringUtil.isEmpty(text))
          {
            text = element.getClass().getSimpleName();
          }

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
   * This implements {@link org.eclipse.jface.action.IMenuListener} to help fill the context menus with contributions from the Edit menu.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void menuAboutToShowGen(IMenuManager menuManager)
  {
    ((IMenuListener)getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
  }

  /**
   * @ADDED
   */
  @Override
  public void menuAboutToShow(IMenuManager menuManager)
  {
    menuAboutToShowGen(menuManager);

    final IWorkbenchPage page = getSite().getPage();
    MenuManager submenuManager = new MenuManager(Messages.getString("CDOEditor.23")); //$NON-NLS-1$

    if (showNewRootMenu())
    {
      NewRootMenuPopulator populator = new NewRootMenuPopulator(view.getSession().getPackageRegistry())
      {
        @Override
        protected IAction createAction(EObject object)
        {
          return new CreateRootAction(object);
        }
      };

      if (populator.populateMenu(submenuManager))
      {
        menuManager.insertBefore("edit", submenuManager); //$NON-NLS-1$
      }
    }

    Resource resource = getSelectedResource();
    if (resource instanceof CDOResource)
    {
      final CDOResource parent = (CDOResource)resource;
      String text = Messages.getString("CDOEditor.29") + SafeAction.INTERACTIVE;

      submenuManager.add(new Separator());
      submenuManager.add(new TransactionalBackgroundAction(page, text, null, null, parent)
      {
        private EObject newObject;

        @Override
        protected void preRun() throws Exception
        {
          SelectClassDialog dialog = new SelectClassDialog(page, "New Root Object", "Select a package and a class for the new root object.");

          if (dialog.open() == SelectClassDialog.OK)
          {
            EClass eClass = dialog.getSelectedClass();
            newObject = EcoreUtil.create(eClass);
          }
          else
          {
            cancel();
          }
        }

        @Override
        protected void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor) throws Exception
        {
          EList<EObject> contents = parent.getContents();
          contents.add(newObject);
        }
      });
    }

    IStructuredSelection sel = (IStructuredSelection)editorSelection;
    if (sel.size() == 1)
    {
      Object element = sel.getFirstElement();
      if (element instanceof EObject)
      {
        final EObject object = (EObject)element;
        final List<EReference> features = new ArrayList<>();
        for (EReference containment : object.eClass().getEAllContainments())
        {
          if (containment.isMany())
          {
            features.add(containment);
          }
        }

        if (showBulkAddMenu() && !features.isEmpty())
        {
          menuManager.insertBefore("edit", //$NON-NLS-1$
              new LongRunningAction(page, Messages.getString("CDOEditor.26") + SafeAction.INTERACTIVE) //$NON-NLS-1$
              {
                protected EReference feature;

                protected int instances;

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
                  List<EObject> children = new ArrayList<>();
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

  protected boolean showNewRootMenu()
  {
    return true;
  }

  protected boolean showBulkAddMenu()
  {
    return SHOW_BULK_ADD_ACTION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EditingDomainActionBarContributor getActionBarContributor()
  {
    return (EditingDomainActionBarContributor)getEditorSite().getActionBarContributor();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public IActionBars getActionBars()
  {
    return getActionBarContributor().getActionBars();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void disposeGen()
  {
    updateProblemIndication = false;

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
   * @ADDED
   */
  @Override
  public void dispose()
  {
    updateProblemIndication = false;

    if (treeExpansionAgent != null)
    {
      treeExpansionAgent.dispose();
      treeExpansionAgent = null;
    }

    if (invalidRootAgent != null)
    {
      invalidRootAgent.dispose();
      invalidRootAgent = null;
    }

    if (view != null && !view.isClosed())
    {
      try
      {
        if (objectHandler != null)
        {
          view.removeObjectHandler(objectHandler);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }

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

    for (PropertySheetPage propertySheetPage : propertySheetPages)
    {
      propertySheetPage.dispose();
    }

    if (contentOutlinePage != null)
    {
      contentOutlinePage.dispose();
    }

    if (((CDOEditorInput)getEditorInput()).isViewOwned())
    {
      LifecycleUtil.deactivate(view);
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
        @Override
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
        @Override
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
        @Override
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

  protected Resource getSelectedResource()
  {
    IStructuredSelection ssel = (IStructuredSelection)editorSelection;
    if (ssel.isEmpty())
    {
      if (viewerInput instanceof Resource)
      {
        return (Resource)viewerInput;
      }
    }
    else if (ssel.size() == 1)
    {
      Object element = ssel.getFirstElement();
      if (element instanceof Resource)
      {
        return (Resource)element;
      }

      if (element instanceof EObject)
      {
        return ((EObject)element).eResource();
      }
    }

    return null;
  }

  protected void handleException(Exception ex)
  {
    if (LOG_EXCEPTIONS)
    {
      OM.LOG.error(ex);
    }
    else if (TRACER.isEnabled())
    {
      TRACER.trace(ex);
    }
  }

  /**
   * @ADDED
   */
  public static ComposedAdapterFactory createAdapterFactory(boolean reflective)
  {
    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    if (reflective)
    {
      adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    }

    return adapterFactory;
  }

  /**
   * @ADDED
   */
  public static String getLabelText(AdapterFactory adapterFactory, Object object)
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
      //$FALL-THROUGH$
    }

    if (object != null)
    {
      String text = object.getClass().getSimpleName();
      if (text.endsWith("Impl"))
      {
        text = text.substring(0, text.length() - "Impl".length());
      }

      return text;
    }

    return ""; //$NON-NLS-1$
  }

  /**
   * @ADDED
   */
  public static Object getLabelImage(AdapterFactory adapterFactory, Object object)
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
      //$FALL-THROUGH$
    }

    return null;
  }

  /**
   * This looks up a string in the plugin's plugin.properties file.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated NOT
   */
  protected String getString(String key)
  {
    return PluginDelegator.INSTANCE.getString(key);
  }

  /**
   * This looks up a string in plugin.properties, making a substitution.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated NOT
   */
  protected String getString(String key, Object s1)
  {
    return PluginDelegator.INSTANCE.getString(key, new Object[] { s1 });
  }

  private static Field getPropertySheetPageViewerField()
  {
    try
    {
      return ReflectUtil.getField(PropertySheetPage.class, "viewer");
    }
    catch (Throwable ex)
    {
      return null;
    }
  }

  private static Field getContentOutlinePageViewerField()
  {
    try
    {
      return ReflectUtil.getField(ContentOutlinePage.class, "treeViewer");
    }
    catch (Throwable ex)
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CDOContentOutlinePage extends ContentOutlinePage
  {
    private CDOInvalidRootAgent invalidRootAgent;

    public CDOContentOutlinePage()
    {
    }

    public void setView(CDOView view)
    {
      // If the view can be switched to historical times let an InvalidRootAgent handle detached inputs.
      if (invalidRootAgent == null && view.isReadOnly())
      {
        invalidRootAgent = new CDOInvalidRootAgent(view)
        {
          @Override
          protected Object getRootFromUI()
          {
            return contentOutlineViewer.getInput();
          }

          @Override
          protected void setRootToUI(Object root)
          {
            contentOutlineViewer.setInput(root);
          }

          @Override
          protected void closeUI()
          {
            closeEditor();
          }
        };
      }

      if (contentOutlineViewer != null)
      {
        if (!CDOUtil.getResources(editingDomain.getResourceSet()).isEmpty())
        {
          // Select the root object in the view.
          //
          contentOutlineViewer.setSelection(new StructuredSelection(CDOUtil.getResources(editingDomain.getResourceSet()).get(0)), true);
        }
      }
    }

    @Override
    public void createControl(Composite parent)
    {
      if (CONTENT_OUTLINE_PAGE_VIEWER_FIELD == null)
      {
        super.createControl(parent);
      }
      else
      {
        TreeViewer treeViewer = new SafeTreeViewer(parent, getTreeStyle(), ex -> handleException(ex));
        treeViewer.addSelectionChangedListener(this);
        ReflectUtil.setValue(CONTENT_OUTLINE_PAGE_VIEWER_FIELD, this, treeViewer);
      }

      // super.createControl(parent);
      contentOutlineViewer = getTreeViewer();
      contentOutlineViewer.addSelectionChangedListener(this);

      // Set up the tree viewer.
      //
      contentOutlineViewer.setContentProvider(createContentProvider());
      contentOutlineViewer.setLabelProvider(createLabelProvider(contentOutlineViewer));

      try
      {
        contentOutlineViewer.setInput(viewerInput);
      }
      catch (Exception ex)
      {
        contentOutlineViewer.setInput(null);
        return;
      }

      CDOView view = CDOEditor.this.view;
      if (view != null)
      {
        setView(view);
      }

      // Make sure our popups work.
      //
      createContextMenuFor(contentOutlineViewer);
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

    @Override
    public void dispose()
    {
      if (invalidRootAgent != null)
      {
        invalidRootAgent.dispose();
        invalidRootAgent = null;
      }

      super.dispose();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class NewRootMenuPopulator
  {
    private final CDOPackageRegistry packageRegistry;

    public NewRootMenuPopulator(CDOPackageRegistry packageRegistry)
    {
      this.packageRegistry = packageRegistry;
    }

    public boolean populateMenu(IMenuManager menuManager)
    {
      boolean populated = false;
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

    protected IContributionItem populateSubMenu(String nsURI, Object value, final CDOPackageRegistry packageRegistry)
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
        @Override
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

    protected void populateSubMenu(EPackage ePackage, final IMenuManager submenuManager)
    {
      List<EObject> objects = new ArrayList<>();
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
          @Override
          public int compare(EObject o1, EObject o2)
          {
            return o1.eClass().getName().compareTo(o2.eClass().getName());
          }
        });

        for (EObject object : objects)
        {
          IAction action = createAction(object);
          submenuManager.add(action);
        }
      }
    }

    protected abstract IAction createAction(EObject object);
  }

  /**
   * @author Eike Stepper
   * @ADDED
   */
  protected class CreateRootAction extends LongRunningAction
  {
    protected EObject newObject;

    protected CreateRootAction(EObject object)
    {
      super(getEditorSite().getPage(), object.eClass().getName(),
          ExtendedImageRegistry.getInstance().getImageDescriptor(getLabelImage(adapterFactory, object)));
      newObject = object;
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      Resource resource = getSelectedResource();
      if (resource != null)
      {
        if (newObject instanceof InternalCDOObject)
        {
          newObject = ((InternalCDOObject)newObject).cdoInternalInstance();
        }

        // TODO Use a command!
        InternalEList<EObject> contents = (InternalEList<EObject>)resource.getContents();
        contents.addUnique(newObject);

        setSelectionToViewer(Collections.singleton(newObject));
      }
    }
  }

  /**
   * Adapter that provides the current EditingDomain
   *
   * @since 2.0
   */
  protected class EditingDomainProviderAdapter implements Adapter, IEditingDomainProvider
  {
    @Override
    public boolean isAdapterForType(Object type)
    {
      return type == IEditingDomainProvider.class;
    }

    @Override
    public EditingDomain getEditingDomain()
    {
      return editingDomain;
    }

    @Override
    public Notifier getTarget()
    {
      return null;
    }

    @Override
    public void notifyChanged(Notification notification)
    {
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
    }
  }
}
