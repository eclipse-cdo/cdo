package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.internal.ui.ContainerItemProvider;
import org.eclipse.net4j.internal.ui.ContainerView;
import org.eclipse.net4j.internal.ui.IElementFilter;
import org.eclipse.net4j.internal.ui.SafeAction;
import org.eclipse.net4j.transport.IAcceptor;
import org.eclipse.net4j.transport.IPluginTransportContainer;
import org.eclipse.net4j.transport.ITransportContainer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;

public class AcceptorsView extends ContainerView
{
  private Action addAcceptorAction = new SafeAction("Add Acceptor", "Add an acceptor", getAddImageDescriptor())
  {
    @Override
    protected void doRun() throws Exception
    {
      IPluginTransportContainer.INSTANCE.getAcceptor("tcp", "0.0.0.0:2036");
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
    manager.add(addAcceptorAction);
    super.fillLocalToolBar(manager);
  }
}