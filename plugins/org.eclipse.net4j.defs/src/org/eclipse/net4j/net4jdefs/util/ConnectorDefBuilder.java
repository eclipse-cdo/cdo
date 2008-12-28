package org.eclipse.net4j.net4jdefs.util;

import org.eclipse.net4j.net4jdefs.BufferProviderDef;
import org.eclipse.net4j.net4jdefs.ConnectorDef;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef;

public class ConnectorDefBuilder
{

  protected BufferProviderDef bufferProviderDef;

  protected ExecutorServiceDef executorServiceDef;

  public ConnectorDefBuilder()
  {
    super();
  }

  public ConnectorDefBuilder bufferProvider(BufferProviderDef bufferProviderDef)
  {
    this.bufferProviderDef = bufferProviderDef;
    return this;
  }

  public ConnectorDefBuilder executorService(ExecutorServiceDef executorServiceDef)
  {
    this.executorServiceDef = executorServiceDef;
    return this;
  }

  public void validate()
  {
    CheckUtil.checkState(bufferProviderDef != null, "bufferProviderDef is not set!");
    CheckUtil.checkState(executorServiceDef != null, "executorServiceDef is not set!");
  }

  public void build(ConnectorDef connectorDef)
  {
    validate();

    connectorDef.setBufferProvider(bufferProviderDef);
    connectorDef.setExecutorService(executorServiceDef);
  }
}
