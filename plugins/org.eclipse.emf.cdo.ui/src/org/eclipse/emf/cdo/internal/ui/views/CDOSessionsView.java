package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.emf.internal.cdo.CDOSessionFactory;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IWorkbenchPage;

public class CDOSessionsView extends ContainerView
{
  private OpenSessionAction openSessionAction2036 = new OpenSessionAction(2036);

  private OpenSessionAction openSessionAction2037 = new OpenSessionAction(2037);

  public CDOSessionsView()
  {
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider createContainerItemProvider()
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
    manager.add(openSessionAction2036);
    manager.add(openSessionAction2037);
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

  /**
   * @author Eike Stepper
   */
  private static final class OpenSessionAction extends SafeAction
  {
    private int port;

    private OpenSessionAction(int port)
    {
      super("Open Session " + port, "Open a CDO session on port " + port, getAddImageDescriptor());
      this.port = port;
    }

    @Override
    protected void doRun() throws Exception
    {
      IPluginContainer.INSTANCE.getElement(CDOSessionFactory.PRODUCT_GROUP, CDOProtocolConstants.PROTOCOL_NAME,
          "tcp://127.0.0.1:" + port + "/repo1");
    }
  }
}