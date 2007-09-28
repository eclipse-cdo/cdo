package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.actions.OpenSessionAction;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

public class CDOSessionsView extends ContainerView
{
  private OpenSessionAction openSessionAction;

  public CDOSessionsView()
  {
  }

  @Override
  public void createPartControl(Composite parent)
  {
    openSessionAction = new OpenSessionAction(getViewSite().getPage());
    super.createPartControl(parent);
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new CDOItemProvider(getSite().getPage(), new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(openSessionAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (object instanceof CDOViewHistory.Entry)
    {
      CDOViewHistory.Entry entry = (CDOViewHistory.Entry)object;
      IWorkbenchPage page = getViewSite().getPage();
      CDOView view = entry.getView();
      String resourcePath = entry.getResourcePath();
      CDOEditor.open(page, view, resourcePath);
    }
    else
    {
      super.doubleClicked(object);
    }
  }
}