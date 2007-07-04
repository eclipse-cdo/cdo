package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.IAcceptor;
import org.eclipse.net4j.IPluginTransportContainer;
import org.eclipse.net4j.ITransportContainer;
import org.eclipse.net4j.ui.actions.SafeAction;
import org.eclipse.net4j.ui.views.ContainerItemProvider;
import org.eclipse.net4j.ui.views.ContainerView;
import org.eclipse.net4j.ui.views.IElementFilter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;

public class AcceptorsView extends ContainerView
{
  private Action addAcceptorAction2036 = new SafeAction("Add Acceptor 2036", "Add an acceptor for port 2036",
      getAddImageDescriptor())
  {
    @Override
    protected void doRun() throws Exception
    {
      IPluginTransportContainer.INSTANCE.getAcceptor("tcp", "0.0.0.0:2036");
    }
  };

  private Action addAcceptorAction2037 = new SafeAction("Add Acceptor 2037", "Add an acceptor for port 2037",
      getAddImageDescriptor())
  {
    @Override
    protected void doRun() throws Exception
    {
      IPluginTransportContainer.INSTANCE.getAcceptor("tcp", "0.0.0.0:2037");
    }
  };

  public AcceptorsView()
  {
  }

  @Override
  protected ITransportContainer getContainer()
  {
    return IPluginTransportContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider createContainerItemProvider()
  {
    return new Net4jItemProvider(new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof IAcceptor;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(addAcceptorAction2036);
    manager.add(addAcceptorAction2037);
    super.fillLocalToolBar(manager);
  }
}