/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.ui;

import org.eclipse.emf.cdo.ecore.dependencies.Addressable;
import org.eclipse.emf.cdo.ecore.dependencies.Element;
import org.eclipse.emf.cdo.ecore.dependencies.Link;
import org.eclipse.emf.cdo.ecore.dependencies.Model;
import org.eclipse.emf.cdo.ecore.dependencies.ModelContainer;
import org.eclipse.emf.cdo.ecore.dependencies.bundle.DependenciesPlugin;
import org.eclipse.emf.cdo.ecore.dependencies.provider.DependenciesItemProviderAdapterFactory;
import org.eclipse.emf.cdo.ecore.dependencies.provider.ModelItemProviderForLinkViewer;
import org.eclipse.emf.cdo.ecore.dependencies.provider.URIStyler;
import org.eclipse.emf.cdo.ecore.dependencies.util.WorkspaceScanner;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.ui.widgets.SashComposite;
import org.eclipse.net4j.util.ui.widgets.SashComposite.OrientationChangedEvent;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DelegatingStyledCellLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Eike Stepper
 */
public class DependenciesView extends ViewPart implements ISelectionProvider
{
  public static final String ID = "org.eclipse.emf.cdo.ecore.DependenciesView";

  private static final String ECORE_EDITOR_ID = "org.eclipse.emf.ecore.presentation.EcoreEditorID";

  private static final String XML_EDITOR_ID = "org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart";

  private static final String TEXT_EDITOR_ID = "org.eclipse.ui.DefaultTextEditor";

  private static final String[] FILE_NAME_PATTERNS = { "*.ecore" };

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private final List<ISelectionChangedListener> selectionChangedListeners = new CopyOnWriteArrayList<>();

  private final ISelectionChangedListener currentViewerListener = new ISelectionChangedListener()
  {
    @Override
    public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
    {
      setSelection(selectionChangedEvent.getSelection());
    }
  };

  private final IResourceChangeListener resourceChangeListener = new IResourceChangeListener()
  {
    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
      IResourceDelta delta = event.getDelta();
      if (hasModelImpact(delta))
      {
        modelViewer.getControl().getDisplay().asyncExec(() -> setInput());
      }
    }

    private boolean hasModelImpact(IResourceDelta delta)
    {
      IResource resource = delta.getResource();
      if (resource.getType() == IResource.FILE)
      {
        return "ecore".equals(resource.getFileExtension());
      }

      for (IResourceDelta childDelta : delta.getAffectedChildren())
      {
        if (hasModelImpact(childDelta))
        {
          return true;
        }
      }

      return false;
    }
  };

  private final ComposedAdapterFactory modelAdapterFactory = new ComposedAdapterFactory(EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());

  private final ComposedAdapterFactory linkAdapterFactory = new ComposedAdapterFactory(EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry())
  {
    {
      addAdapterFactory(new DependenciesItemProviderAdapterFactory()
      {
        @Override
        public Adapter createModelAdapter()
        {
          return new ModelItemProviderForLinkViewer(this);
        }
      });
    }
  };

  private final Action calleesAction = new PreferenceAction("Callees", "Callees", DependenciesPlugin.PREF_SHOW_CALLERS, false);

  private final Action callersAction = new PreferenceAction("Callers", "Callers", DependenciesPlugin.PREF_SHOW_CALLERS, true);

  private final Action flatAction = new PreferenceAction("Flat", "Flat", DependenciesPlugin.PREF_SHOW_FLAT, null);

  private final Action sortByDependenciesAction = new PreferenceAction("Sort By Dependencies", "SortByDependencies", //
      DependenciesPlugin.PREF_SORT_BY_DEPENDENCIES, null);

  private final Action showBrokenLinksAction = new PreferenceAction("Show Broken Links", "BrokenLink", //
      DependenciesPlugin.PREF_SHOW_BROKEN_LINKS, null);

  private final Action showGenericsAction = new PreferenceAction("Show Generics", "Generics", //
      DependenciesPlugin.PREF_SHOW_GENERICS, null);

  private final Action layoutVerticalAction = new PreferenceAction("Layout Vertical", "Vertical", //
      DependenciesPlugin.PREF_LAYOUT_VERTICAL, true)
  {
    @Override
    protected void run(boolean value)
    {
      sashComposite.setVertical(value);
    }
  };

  private final Action layoutHorizontalAction = new PreferenceAction("Layout Horizontal", "Horizontal", //
      DependenciesPlugin.PREF_LAYOUT_VERTICAL, false)
  {
    @Override
    protected void run(boolean value)
    {
      sashComposite.setVertical(value);
    }
  };

  private final Action toggleXMLAction = new DefaultAction("Use XML Editor", "Xml", IAction.AS_CHECK_BOX);

  private final Action refreshAction = new DefaultAction("Refresh", "Refresh", IAction.AS_PUSH_BUTTON)
  {
    @Override
    public void run()
    {
      refreshViewers();
    }
  };

  private final Action copyURIAction = new AbstractCopyAction("Copy URI", "Copy")
  {
    @Override
    protected String getText(IStructuredSelection selection)
    {
      StringBuilder builder = new StringBuilder();
      for (Object element : selection)
      {
        if (element instanceof Addressable)
        {
          Addressable addressable = (Addressable)element;

          URI uri = addressable.getUri();
          if (uri != null)
          {
            if (builder.length() != 0)
            {
              builder.append(LINE_SEPARATOR);
            }

            builder.append(uri);
          }
        }
      }

      return builder.toString();
    }
  };

  private final Action copyPathAction = new AbstractCopyAction("Copy Path", "Copy")
  {
    @Override
    protected String getText(IStructuredSelection selection)
    {
      StringBuilder builder = new StringBuilder();
      for (Object element : selection)
      {
        if (element instanceof Model)
        {
          Model model = (Model)element;

          IFile file = model.getFile();
          if (file != null)
          {
            if (builder.length() != 0)
            {
              builder.append(LINE_SEPARATOR);
            }

            builder.append(file.getFullPath());
          }
        }
      }

      return builder.toString();
    }
  };

  private final Action searchAction = new DefaultAction("Search URI", "Search", IAction.AS_PUSH_BUTTON)
  {
    @Override
    public void run()
    {
      try
      {
        Object element = modelViewer.getStructuredSelection().getFirstElement();
        if (element instanceof Addressable)
        {
          String uri = Addressable.getAlphaKey((Addressable)element);
          if (uri.startsWith("platform:/plugin"))
          {
            uri = uri.substring("platform:/plugin".length());
          }
          else if (uri.startsWith("platform:/resource"))
          {
            uri = uri.substring("platform:/resource".length());
          }

          @SuppressWarnings("restriction")
          ISearchQuery query = new org.eclipse.search.internal.ui.text.FileSearchQuery(uri, false, true,
              FileTextSearchScope.newWorkspaceScope(FILE_NAME_PATTERNS, false));
          NewSearchUI.runQueryInBackground(query);
        }
      }
      catch (Throwable ignore)
      {
        //$FALL-THROUGH$
      }
    }
  };

  private final Action doubleClickAction = new Action("Open")
  {
    @Override
    public void run()
    {
      Object element = modelViewer.getStructuredSelection().getFirstElement();

      try
      {
        if (element instanceof Model)
        {
          Model model = (Model)element;
          IEditorInput input = null;

          IFile file = model.getFile();
          if (file != null)
          {
            input = new FileEditorInput(file);
          }

          if (input == null)
          {
            URI uri = model.getUri();
            if (uri != null && uri.isPlatform())
            {
              input = new URIEditorInput(uri);
            }
          }

          if (input != null)
          {
            page.openEditor(input, toggleXMLAction.isChecked() && input instanceof FileEditorInput ? xmlEditorID : ECORE_EDITOR_ID, true,
                IWorkbenchPage.MATCH_INPUT);
            return;
          }
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }

      searchAction.run();
    }
  };

  private IWorkbenchPage page;

  private SashComposite sashComposite;

  private StructuredViewer currentViewer;

  private TreeViewer modelViewer;

  private TableViewer linkViewer;

  private String xmlEditorID;

  private ISelection viewSelection;

  public DependenciesView()
  {
  }

  @Override
  public ISelection getSelection()
  {
    return viewSelection;
  }

  @Override
  public void setSelection(ISelection selection)
  {
    viewSelection = selection;

    for (ISelectionChangedListener listener : selectionChangedListeners)
    {
      listener.selectionChanged(new SelectionChangedEvent(this, selection));
    }

    setStatusLineManager(selection);
  }

  @Override
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.add(listener);
  }

  @Override
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    selectionChangedListeners.remove(listener);
  }

  private void setStatusLineManager(ISelection selection)
  {
    IActionBars bars = getViewSite().getActionBars();

    IStatusLineManager statusLineManager = bars.getStatusLineManager();
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
          String text = new AdapterFactoryItemDelegator(modelAdapterFactory).getText(collection.iterator().next());
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

  @Override
  public void createPartControl(Composite parent)
  {
    page = getViewSite().getPage();

    IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
    xmlEditorID = editorRegistry.findEditor(XML_EDITOR_ID) != null ? XML_EDITOR_ID : TEXT_EDITOR_ID;

    boolean vertical = DependenciesPlugin.PREF_LAYOUT_VERTICAL.getValue();
    sashComposite = new SashComposite(parent, SWT.NONE, 100, 50, false, vertical, false)
    {
      @Override
      protected Control createControl1(Composite parent)
      {
        return createModelViewer(parent);
      }

      @Override
      protected Sash createSash(Composite parent)
      {
        Sash sash = super.createSash(parent);
        sash.setBackground(parent.getShell().getBackground());
        return sash;
      }

      @Override
      protected Control createControl2(Composite parent)
      {
        return createLinkViewer(parent);
      }
    };

    sashComposite.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof OrientationChangedEvent)
        {
          OrientationChangedEvent e = (OrientationChangedEvent)event;
          DependenciesPlugin.PREF_LAYOUT_VERTICAL.setValue(e.isVertical());
        }
      }
    });

    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();

    setInput();
    setCurrentViewer(modelViewer);
    modelViewer.getTree().setFocus();
    getViewSite().setSelectionProvider(this);

    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    workspace.addResourceChangeListener(resourceChangeListener);
  }

  private Control createModelViewer(Composite parent)
  {
    modelViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    modelViewer.setContentProvider(new AdapterFactoryContentProvider(modelAdapterFactory));
    modelViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new AdapterFactoryLabelProvider.StyledLabelProvider(modelAdapterFactory, modelViewer)));
    modelViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = event.getStructuredSelection();
        Object element = selection.getFirstElement();
        linkViewer.setInput(selection.size() == 1 && element instanceof Model ? element : null);
      }
    });

    Tree tree = modelViewer.getTree();
    tree.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusGained(FocusEvent e)
      {
        setCurrentViewer(modelViewer);
      }
    });

    return tree;
  }

  private Control createLinkViewer(Composite parent)
  {
    linkViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    linkViewer.setContentProvider(new AdapterFactoryContentProvider(linkAdapterFactory));

    TableViewerColumn elementViewerColumn = new TableViewerColumn(linkViewer, SWT.NONE);
    elementViewerColumn.getColumn().setText("Element");
    elementViewerColumn.getColumn().setWidth(300);
    elementViewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new LinkLabelProvider()
    {
      @Override
      public StyledString getStyledText(Object element)
      {
        StyledString styledString = new StyledString();

        Link link = (Link)element;
        styledString.append(link.getUri().fragment());

        return styledString;
      }
    }));

    TableViewerColumn referenceViewerColumn = new TableViewerColumn(linkViewer, SWT.NONE);
    referenceViewerColumn.getColumn().setText("Reference");
    referenceViewerColumn.getColumn().setWidth(150);
    referenceViewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new LinkLabelProvider()
    {
      @Override
      public StyledString getStyledText(Object element)
      {
        StyledString styledString = new StyledString();

        Link link = (Link)element;
        styledString.append(link.getReference().getName());

        return styledString;
      }
    }));

    TableViewerColumn targetViewerColumn = new TableViewerColumn(linkViewer, SWT.NONE);
    targetViewerColumn.getColumn().setText("Target");
    targetViewerColumn.getColumn().setWidth(600);
    targetViewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new LinkLabelProvider()
    {
      @Override
      public StyledString getStyledText(Object element)
      {
        Link link = (Link)element;
        Element target = link.getTarget();
        if (target != null)
        {
          URI uri = target.getUri();
          if (uri != null)
          {
            if (link.isBroken())
            {
              return URIStyler.getJFaceStyledURI(uri, URIStyler.MISSING);
            }

            if (!target.getModel().isWorkspace())
            {
              return URIStyler.getJFaceStyledURI(uri, URIStyler.EXTERNAL);
            }

            return URIStyler.getJFaceStyledURI(uri, URIStyler.WORKSPACE);
          }
        }

        return new StyledString();
      }
    }));

    new TableColumnSorter(linkViewer);

    Table table = linkViewer.getTable();
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    table.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusGained(FocusEvent e)
      {
        setCurrentViewer(linkViewer);
      }
    });

    URIStyler.setDefaultFont(table.getFont());

    return table;
  }

  private void setInput()
  {
    ModelContainer container = WorkspaceScanner.scanWorkspace();
    modelViewer.setInput(container);
  }

  private void setCurrentViewer(StructuredViewer viewer)
  {
    if (currentViewer != viewer)
    {
      if (currentViewer != null)
      {
        currentViewer.removeSelectionChangedListener(currentViewerListener);
      }

      if (viewer != null)
      {
        viewer.addSelectionChangedListener(currentViewerListener);
      }

      currentViewer = viewer;
      setSelection(currentViewer == null ? StructuredSelection.EMPTY : currentViewer.getSelection());
    }
  }

  private void refreshViewers()
  {
    modelViewer.refresh();
    linkViewer.refresh();
  }

  @Override
  public void setFocus()
  {
    modelViewer.getControl().setFocus();
  }

  private void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        fillContextMenu(manager);
      }
    });

    Menu menu = menuMgr.createContextMenu(modelViewer.getControl());
    modelViewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, modelViewer);
  }

  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyURIAction);

    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(calleesAction);
    manager.add(callersAction);
    manager.add(new Separator());
    manager.add(flatAction);
    manager.add(sortByDependenciesAction);
    manager.add(new Separator());
    manager.add(showBrokenLinksAction);
    manager.add(showGenericsAction);
    manager.add(new Separator());
    manager.add(searchAction);
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(toggleXMLAction);
    manager.add(new Separator());
    manager.add(copyURIAction);
    manager.add(copyPathAction);
    manager.add(new Separator());
    manager.add(layoutVerticalAction);
    manager.add(layoutHorizontalAction);
    manager.add(new Separator());
    manager.add(refreshAction);
  }

  private void fillContextMenu(IMenuManager manager)
  {
    manager.add(new Separator());
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void hookDoubleClickAction()
  {
    modelViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        doubleClickAction.run();
      }
    });
  }

  private static String getString(String key)
  {
    return DependenciesPlugin.INSTANCE.getString(key);
  }

  private static String getString(String key, Object s1)
  {
    return DependenciesPlugin.INSTANCE.getString(key, new Object[] { s1 });
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class LinkLabelProvider extends ColumnLabelProvider implements IStyledLabelProvider
  {
    public LinkLabelProvider()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private class DefaultAction extends Action
  {
    public DefaultAction(String text, String iconKey, int style)
    {
      super(text, style);
      if (iconKey != null)
      {
        setImageDescriptor(ExtendedImageRegistry.INSTANCE
            .getImageDescriptor(URI.createPlatformPluginURI("org.eclipse.emf.cdo.ecore.dependencies/icons/full/obj16/" + iconKey + ".gif", true)));
      }
    }

    @Override
    public void run()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private class PreferenceAction extends DefaultAction
  {
    private final OMPreference<Boolean> preference;

    private final Boolean value;

    public PreferenceAction(String text, String iconKey, OMPreference<Boolean> preference, Boolean value)
    {
      super(text, iconKey, value != null ? AS_RADIO_BUTTON : AS_CHECK_BOX);
      this.preference = preference;
      this.value = value;

      boolean pref = preference.getValue();
      if (value != null)
      {
        setChecked(pref == value);
      }
      else
      {
        setChecked(pref);
      }
    }

    @Override
    public final void run()
    {
      boolean checked = isChecked();
      if (value != null)
      {
        if (checked)
        {
          preference.setValue(value);
          run(value);
        }
      }
      else
      {
        preference.setValue(checked);
        run(checked);
      }
    }

    protected void run(boolean value)
    {
      refreshViewers();
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class AbstractCopyAction extends DefaultAction
  {
    public AbstractCopyAction(String text, String iconKey)
    {
      super(text, iconKey, AS_PUSH_BUTTON);
    }

    @Override
    public void run()
    {
      String text = getText(modelViewer.getStructuredSelection());

      Clipboard clipboard = new Clipboard(modelViewer.getControl().getDisplay());
      clipboard.setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
      clipboard.dispose();
    }

    protected abstract String getText(IStructuredSelection selection);
  }
}
