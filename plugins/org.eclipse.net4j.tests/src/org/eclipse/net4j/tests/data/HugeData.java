/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.data;

import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public final class HugeData
{
  public static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$

  public static StringTokenizer getTokenizer()
  {
    return new StringTokenizer(getText(), NL);
  }

  public static String[] getArray()
  {
    return getText().split(NL);
  }

  public static byte[] getBytes()
  {
    return getText().getBytes();
  }

  public static String getText()
  {
    return "/**" + NL //$NON-NLS-1$
        + " * COPYRIGHT (C) 2004 - 2008 EIKE STEPPER, GERMANY." + NL //$NON-NLS-1$
        + " * ALL RIGHTS RESERVED. THIS PROGRAM AND THE ACCOMPANYING MATERIALS" + NL //$NON-NLS-1$
        + " * ARE MADE AVAILABLE UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 2.0" + NL //$NON-NLS-1$
        + " * WHICH ACCOMPANIES THIS DISTRIBUTION, AND IS AVAILABLE AT" + NL //$NON-NLS-1$
        + " * HTTPS://WWW.ECLIPSE.ORG/LEGAL/EPL-2.0" + NL + " * " + NL + " * CONTRIBUTORS:" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + " *    EIKE STEPPER - INITIAL API AND IMPLEMENTATION" + NL //$NON-NLS-1$
        + " */" + NL //$NON-NLS-1$
        + "PACKAGE ORG.ECLIPSE.INTERNAL.NET4J.TRANSPORT.CONNECTOR;" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.BUFFER.BUFFERPROVIDER;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CHANNEL.CHANNEL;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CHANNEL.MULTIPLEXER;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.CONNECTOR;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.CONNECTOREXCEPTION;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.CREDENTIALS;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.PROTOCOL;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.PROTOCOLFACTORY;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.UTIL.LIFECYCLE.LIFECYCLELISTENER;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.UTIL.LIFECYCLE.LIFECYCLENOTIFIER;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.NET4J.UTIL.REGISTRY.IREGISTRY;" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.TRANSPORT.CHANNEL.CHANNELIMPL;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.UTIL.STREAM.BUFFERQUEUE;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.UTIL.LIFECYCLE.ABSTRACTCOMPONENT;" + NL //$NON-NLS-1$
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.UTIL.LIFECYCLE.LIFECYCLEUTIL;" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "IMPORT JAVA.UTIL.ARRAYLIST;" + NL + "IMPORT JAVA.UTIL.LIST;" + NL + "IMPORT JAVA.UTIL.QUEUE;" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "IMPORT JAVA.UTIL.CONCURRENT.CONCURRENTLINKEDQUEUE;" + NL + "IMPORT JAVA.UTIL.CONCURRENT.COUNTDOWNLATCH;" //$NON-NLS-1$ //$NON-NLS-2$
        + NL + "IMPORT JAVA.UTIL.CONCURRENT.EXECUTORSERVICE;" + NL + "IMPORT JAVA.UTIL.CONCURRENT.TIMEUNIT;" + NL + "" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "/**" + NL + " * @AUTHOR EIKE STEPPER" + NL + " */" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "PUBLIC ABSTRACT CLASS ABSTRACTCONNECTOR EXTENDS ABSTRACTLIFECYCLE IMPLEMENTS CONNECTOR, CHANNELMULTIPLEXER" //$NON-NLS-1$
        + NL + "{" + NL + "  PRIVATE STATIC FINAL CHANNELIMPL NULL_CHANNEL = NEW CHANNELIMPL(NULL);" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PRIVATE CONNECTORCREDENTIALS CREDENTIALS;" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "  PRIVATE IREGISTRY<STRING, PROTOCOLFACTORY> PROTOCOLFACTORYREGISTRY;" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "  PRIVATE BUFFERPROVIDER BUFFERPROVIDER;" + NL + "" + NL + "  /**" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "   * AN OPTIONAL EXECUTOR TO BE USED BY THE {@LINK CHANNEL}S TO PROCESS THEIR" + NL //$NON-NLS-1$
        + "   * {@LINK CHANNELIMPL#RECEIVEQUEUE} INSTEAD OF THE CURRENT THREAD. IF NOT" + NL //$NON-NLS-1$
        + "   * <CODE>NULL</CODE> THE SENDER AND THE RECEIVER PEERS BECOME DECOUPLED." + NL + "   * <P>" + NL + "   */" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "  PRIVATE EXECUTORSERVICE RECEIVEEXECUTOR;" + NL + "" + NL + "  /**" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "   * TODO SYNCHRONIZE ON CHANNELS?" + NL + "   */" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "  PRIVATE LIST<CHANNELIMPL> CHANNELS = NEW ARRAYLIST();" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "  PRIVATE STATE STATE = STATE.DISCONNECTED;" + NL + "" + NL + "  /**" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "   * DON\'T INITIALIZE LAZILY TO CIRCUMVENT SYNCHRONIZATION!" + NL + "   */" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "  PRIVATE QUEUE<STATELISTENER> STATELISTENERS = NEW CONCURRENTLINKEDQUEUE();" + NL + "" + NL + "  /**" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "   * DON\'T INITIALIZE LAZILY TO CIRCUMVENT SYNCHRONIZATION!" + NL + "   */" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "  PRIVATE QUEUE<CHANNELLISTENER> CHANNELLISTENERS = NEW CONCURRENTLINKEDQUEUE();" + NL + "" + NL + "  /**" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "   * IS REGISTERED WITH EACH {@LINK CHANNEL} OF THIS {@LINK CONNECTOR}." + NL + "   * <P>" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "   */" + NL + "  PRIVATE LIFECYCLELISTENER CHANNELLIFECYCLELISTENER = NEW CHANNELLIFECYCLELISTENER();" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "" + NL + "  PRIVATE COUNTDOWNLATCH FINISHEDCONNECTING;" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PRIVATE COUNTDOWNLATCH FINISHEDNEGOTIATING;" + NL + "" + NL + "  PUBLIC ABSTRACTCONNECTOR()" + NL + "  {" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + NL + "  }" + NL + "" + NL + "  PUBLIC EXECUTORSERVICE GETRECEIVEEXECUTOR()" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    RETURN RECEIVEEXECUTOR;" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC VOID SETRECEIVEEXECUTOR(EXECUTORSERVICE RECEIVEEXECUTOR)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    THIS.RECEIVEEXECUTOR = RECEIVEEXECUTOR;" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC IREGISTRY<STRING, PROTOCOLFACTORY> GETPROTOCOLFACTORYREGISTRY()" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    RETURN PROTOCOLFACTORYREGISTRY;" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC VOID SETPROTOCOLFACTORYREGISTRY(IREGISTRY<STRING, PROTOCOLFACTORY> PROTOCOLFACTORYREGISTRY)" + NL //$NON-NLS-1$
        + "  {" + NL + "    THIS.PROTOCOLFACTORYREGISTRY = PROTOCOLFACTORYREGISTRY;" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  PUBLIC VOID ADDSTATELISTENER(STATELISTENER LISTENER)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    STATELISTENERS.ADD(LISTENER);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC VOID REMOVESTATELISTENER(STATELISTENER LISTENER)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    STATELISTENERS.REMOVE(LISTENER);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC VOID ADDCHANNELLISTENER(CHANNELLISTENER LISTENER)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    CHANNELLISTENERS.ADD(LISTENER);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC VOID REMOVECHANNELLISTENER(CHANNELLISTENER LISTENER)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    CHANNELLISTENERS.REMOVE(LISTENER);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC BUFFERPROVIDER GETBUFFERPROVIDER()" + NL + "  {" + NL + "    RETURN BUFFERPROVIDER;" + NL + "  }" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + NL + "" + NL + "  PUBLIC VOID SETBUFFERPROVIDER(BUFFERPROVIDER BUFFERPROVIDER)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    THIS.BUFFERPROVIDER = BUFFERPROVIDER;" + NL + "  }" + NL + "" + NL + "  PUBLIC BOOLEAN ISCLIENT()" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  {" + NL + "    RETURN GETTYPE() == TYPE.CLIENT;" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  PUBLIC BOOLEAN ISSERVER()" + NL + "  {" + NL + "    RETURN GETTYPE() == TYPE.SERVER;" + NL + "  }" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "" + NL + "  PUBLIC CONNECTORCREDENTIALS GETCREDENTIALS()" + NL + "  {" + NL + "    RETURN CREDENTIALS;" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  }" + NL + "" + NL + "  PUBLIC VOID SETCREDENTIALS(CONNECTORCREDENTIALS CREDENTIALS)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    THIS.CREDENTIALS = CREDENTIALS;" + NL + "  }" + NL + "" + NL + "  PUBLIC STATE GETSTATE()" + NL + "  {" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + NL + "    RETURN STATE;" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC VOID SETSTATE(STATE NEWSTATE) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    STATE OLDSTATE = GETSTATE();" + NL + "    IF (NEWSTATE != OLDSTATE)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": SETTING STATE \" + NEWSTATE + \" (WAS \"" + NL //$NON-NLS-1$
        + "          + OLDSTATE.TOSTRING().TOLOWERCASE() + \")\");" + NL + "      STATE = NEWSTATE;" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "      FIRESTATECHANGED(NEWSTATE, OLDSTATE);" + NL + "" + NL + "      SWITCH (NEWSTATE)" + NL + "      {" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + NL + "      CASE DISCONNECTED:" + NL + "        IF (FINISHEDCONNECTING != NULL)" + NL + "        {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "          FINISHEDCONNECTING.COUNTDOWN();" + NL + "          FINISHEDCONNECTING = NULL;" + NL + "        }" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "" + NL + "        IF (FINISHEDNEGOTIATING != NULL)" + NL + "        {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "          FINISHEDNEGOTIATING.COUNTDOWN();" + NL + "          FINISHEDNEGOTIATING = NULL;" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "        }" + NL + "        BREAK;" + NL + "" + NL + "      CASE CONNECTING:" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "        FINISHEDCONNECTING = NEW COUNTDOWNLATCH(1);" + NL //$NON-NLS-1$
        + "        FINISHEDNEGOTIATING = NEW COUNTDOWNLATCH(1);" + NL + "        IF (GETTYPE() == TYPE.SERVER)" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "        {" + NL + "          SETSTATE(STATE.NEGOTIATING);" + NL + "        }" + NL + "        BREAK;" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "" + NL + "      CASE NEGOTIATING:" + NL + "        FINISHEDCONNECTING.COUNTDOWN();" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "        SETSTATE(STATE.CONNECTED); // TODO IMPLEMENT NEGOTIATION" + NL + "        BREAK;" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      CASE CONNECTED:" + NL + "        FINISHEDCONNECTING.COUNTDOWN(); // JUST IN CASE OF SUSPICION" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "        FINISHEDNEGOTIATING.COUNTDOWN();" + NL + "        BREAK;" + NL + "" + NL + "      }" + NL + "    }" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + NL + "  }" + NL + "" + NL + "  PUBLIC BOOLEAN ISCONNECTED()" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    RETURN GETSTATE() == STATE.CONNECTED;" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC VOID CONNECTASYNC() THROWS CONNECTOREXCEPTION" + NL + "  {" + NL + "    TRY" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "      ACTIVATE();" + NL + "    }" + NL + "    CATCH (CONNECTOREXCEPTION EX)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "      THROW EX;" + NL + "    }" + NL + "    CATCH (EXCEPTION EX)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "      THROW NEW CONNECTOREXCEPTION(EX);" + NL + "    }" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  PUBLIC BOOLEAN WAITFORCONNECTION(LONG TIMEOUT) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    STATE STATE = GETSTATE();" + NL + "    IF (STATE == STATE.DISCONNECTED)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      RETURN FALSE;" + NL + "    }" + NL + "" + NL + "    TRY" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": WAITING FOR CONNECTION...\");" + NL //$NON-NLS-1$
        + "      RETURN FINISHEDNEGOTIATING.AWAIT(TIMEOUT, TIMEUNIT.MILLISECONDS);" + NL + "    }" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    CATCH (INTERRUPTEDEXCEPTION EX)" + NL + "    {" + NL + "      RETURN FALSE;" + NL + "    }" + NL + "  }" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + NL + "" + NL + "  PUBLIC BOOLEAN CONNECT(LONG TIMEOUT) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    CONNECTASYNC();" + NL + "    RETURN WAITFORCONNECTION(TIMEOUT);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  PUBLIC CONNECTOREXCEPTION DISCONNECT()" + NL + "  {" + NL + "    EXCEPTION EX = DEACTIVATE();" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    IF (EX == NULL)" + NL + "    {" + NL + "      RETURN NULL;" + NL + "    }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + "    IF (EX INSTANCEOF CONNECTOREXCEPTION)" + NL + "    {" + NL + "      RETURN (CONNECTOREXCEPTION)EX;" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    }" + NL + "" + NL + "    RETURN NEW CONNECTOREXCEPTION(EX);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + "  PUBLIC CHANNEL[] GETCHANNELS()" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    FINAL LIST<CHANNEL> RESULT = NEW ARRAYLIST<CHANNEL>();" + NL + "    SYNCHRONIZED (CHANNELS)" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    {" + NL + "      FOR (FINAL CHANNELIMPL CHANNEL : CHANNELS)" + NL + "      {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "        IF (CHANNEL != NULL_CHANNEL)" + NL + "        {" + NL + "          RESULT.ADD(CHANNEL);" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "        }" + NL + "      }" + NL + "    }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    RETURN RESULT.TOARRAY(NEW CHANNEL[RESULT.SIZE()]);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC CHANNEL OPENCHANNEL() THROWS CONNECTOREXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    RETURN OPENCHANNEL(NULL);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PUBLIC CHANNEL OPENCHANNEL(STRING PROTOCOLID) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    SHORT CHANNELINDEX = FINDFREECHANNELINDEX();" + NL //$NON-NLS-1$
        + "    CHANNELIMPL CHANNEL = CREATECHANNEL(CHANNELINDEX, PROTOCOLID);" + NL //$NON-NLS-1$
        + "    REGISTERCHANNELWITHPEER(CHANNELINDEX, PROTOCOLID);" + NL + "" + NL + "    TRY" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "      CHANNEL.ACTIVATE();" + NL + "    }" + NL + "    CATCH (CONNECTOREXCEPTION EX)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "      THROW EX;" + NL + "    }" + NL + "    CATCH (EXCEPTION EX)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "      THROW NEW CONNECTOREXCEPTION(EX);" + NL + "    }" + NL + "" + NL + "    RETURN CHANNEL;" + NL + "  }" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + NL + "" + NL + "  PUBLIC CHANNELIMPL CREATECHANNEL(SHORT CHANNELINDEX, STRING PROTOCOLID)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    PROTOCOL PROTOCOL = CREATEPROTOCOL(PROTOCOLID);" + NL + "    IF (PROTOCOL == NULL)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": OPENING CHANNEL WITHOUT PROTOCOL\");" + NL + "    }" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    ELSE" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": OPENING CHANNEL WITH PROTOCOL \" + PROTOCOLID);" + NL + "    }" //$NON-NLS-1$ //$NON-NLS-2$
        + NL + "" + NL + "    CHANNELIMPL CHANNEL = NEW CHANNELIMPL(RECEIVEEXECUTOR);" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    CHANNEL.SETCHANNELINDEX(CHANNELINDEX);" + NL + "    CHANNEL.SETMULTIPLEXER(THIS);" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    CHANNEL.SETRECEIVEHANDLER(PROTOCOL);" + NL //$NON-NLS-1$
        + "    CHANNEL.ADDLIFECYCLELISTENER(CHANNELLIFECYCLELISTENER);" + NL + "    ADDCHANNEL(CHANNEL);" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    RETURN CHANNEL;" + NL + "  }" + NL + "" + NL + "  PUBLIC CHANNELIMPL GETCHANNEL(SHORT CHANNELINDEX)" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + NL + "  {" + NL + "    TRY" + NL + "    {" + NL + "      CHANNELIMPL CHANNEL = CHANNELS.GET(CHANNELINDEX);" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + NL + "      IF (CHANNEL == NULL || CHANNEL == NULL_CHANNEL)" + NL + "      {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "        THROW NEW NULLPOINTEREXCEPTION();" + NL + "      }" + NL + "" + NL + "      RETURN CHANNEL;" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    }" + NL + "    CATCH (INDEXOUTOFBOUNDSEXCEPTION EX)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": INVALID CHANNELINDEX \" + CHANNELINDEX);" + NL //$NON-NLS-1$
        + "      RETURN NULL;" + NL + "    }" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  PROTECTED LIST<BUFFERQUEUE> GETCHANNELBUFFERQUEUES()" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    FINAL LIST<BUFFERQUEUE> RESULT = NEW ARRAYLIST<BUFFERQUEUE>();" + NL + "    SYNCHRONIZED (CHANNELS)" //$NON-NLS-1$ //$NON-NLS-2$
        + NL + "    {" + NL + "      FOR (FINAL CHANNELIMPL CHANNEL : CHANNELS)" + NL + "      {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "        IF (CHANNEL != NULL_CHANNEL)" + NL + "        {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "          BUFFERQUEUE BUFFERQUEUE = CHANNEL.GETSENDQUEUE();" + NL + "          RESULT.ADD(BUFFERQUEUE);" //$NON-NLS-1$ //$NON-NLS-2$
        + NL + "        }" + NL + "      }" + NL + "    }" + NL + "" + NL + "    RETURN RESULT;" + NL + "  }" + NL + "" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        + NL + "  PROTECTED SHORT FINDFREECHANNELINDEX()" + NL + "  {" + NL + "    SYNCHRONIZED (CHANNELS)" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    {" + NL + "      INT SIZE = CHANNELS.SIZE();" + NL + "      FOR (SHORT I = 0; I < SIZE; I++)" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      {" + NL + "        IF (CHANNELS.GET(I) == NULL_CHANNEL)" + NL + "        {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "          RETURN I;" + NL + "        }" + NL + "      }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "      CHANNELS.ADD(NULL_CHANNEL);" + NL + "      RETURN (SHORT)SIZE;" + NL + "    }" + NL + "  }" + NL + "" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + NL + "  PROTECTED VOID ADDCHANNEL(CHANNELIMPL CHANNEL)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    SHORT CHANNELINDEX = CHANNEL.GETCHANNELINDEX();" + NL + "    WHILE (CHANNELINDEX >= CHANNELS.SIZE())" //$NON-NLS-1$ //$NON-NLS-2$
        + NL + "    {" + NL + "      CHANNELS.ADD(NULL_CHANNEL);" + NL + "    }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    CHANNELS.SET(CHANNELINDEX, CHANNEL);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PROTECTED VOID REMOVECHANNEL(CHANNELIMPL CHANNEL)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    CHANNEL.REMOVELIFECYCLELISTENER(CHANNELLIFECYCLELISTENER);" + NL //$NON-NLS-1$
        + "    INT CHANNELINDEX = CHANNEL.GETCHANNELINDEX();" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    IOUTIL.OUT().PRINTLN(TOSTRING() + \": REMOVING CHANNEL \" + CHANNELINDEX);" + NL //$NON-NLS-1$
        + "    CHANNELS.SET(CHANNELINDEX, NULL_CHANNEL);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PROTECTED PROTOCOL CREATEPROTOCOL(STRING PROTOCOLID)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    IF (PROTOCOLID == NULL || PROTOCOLID.LENGTH() == 0)" + NL + "    {" + NL + "      RETURN NULL;" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    }" + NL + "" + NL + "    IREGISTRY<STRING, PROTOCOLFACTORY> REGISTRY = GETPROTOCOLFACTORYREGISTRY();" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "    IF (REGISTRY == NULL)" + NL + "    {" + NL + "      RETURN NULL;" + NL + "    }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        + "    PROTOCOLFACTORY FACTORY = REGISTRY.LOOKUP(PROTOCOLID);" + NL + "    IF (FACTORY == NULL)" + NL + "    {" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "      RETURN NULL;" + NL + "    }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    IOUTIL.OUT().PRINTLN(TOSTRING() + \": CREATING PROTOCOL \" + PROTOCOLID);" + NL //$NON-NLS-1$
        + "    RETURN FACTORY.CREATEPROTOCOL();" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "  PROTECTED VOID FIRECHANNELOPENED(CHANNEL CHANNEL)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    FOR (CHANNELLISTENER LISTENER : CHANNELLISTENERS)" + NL + "    {" + NL + "      TRY" + NL + "      {" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + NL + "        LISTENER.NOTIFYCHANNELOPENED(CHANNEL);" + NL + "      }" + NL + "      CATCH (EXCEPTION EX)" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "      {" + NL + "        EX.PRINTSTACKTRACE();" + NL + "      }" + NL + "    }" + NL + "  }" + NL + "" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        + NL + "  PROTECTED VOID FIRECHANNELCLOSING(CHANNEL CHANNEL)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    FOR (CHANNELLISTENER LISTENER : CHANNELLISTENERS)" + NL + "    {" + NL + "      TRY" + NL + "      {" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + NL + "        LISTENER.NOTIFYCHANNELCLOSING(CHANNEL);" + NL + "      }" + NL + "      CATCH (EXCEPTION EX)" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "      {" + NL + "        EX.PRINTSTACKTRACE();" + NL + "      }" + NL + "    }" + NL + "  }" + NL + "" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        + NL + "  PROTECTED VOID FIRESTATECHANGED(STATE NEWSTATE, STATE OLDSTATE)" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    FOR (STATELISTENER LISTENER : STATELISTENERS)" + NL + "    {" + NL + "      TRY" + NL + "      {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "        LISTENER.NOTIFYSTATECHANGED(THIS, NEWSTATE, OLDSTATE);" + NL + "      }" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "      CATCH (EXCEPTION EX)" + NL + "      {" + NL + "        EX.PRINTSTACKTRACE();" + NL + "      }" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    }" + NL + "  }" + NL + "" + NL + "  @OVERRIDE" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  PROTECTED VOID ONACCESSBEFOREACTIVATE() THROWS EXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    SUPER.ONACCESSBEFOREACTIVATE();" + NL + "    IF (BUFFERPROVIDER == NULL)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      THROW NEW ILLEGALSTATEEXCEPTION(\"BUFFERPROVIDER == NULL\");" + NL + "    }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    IF (PROTOCOLFACTORYREGISTRY == NULL)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": (INFO) PROTOCOLFACTORYREGISTRY == NULL\");" + NL + "    }" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "" + NL + "    IF (RECEIVEEXECUTOR == NULL)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": (INFO) RECEIVEEXECUTOR == NULL\");" + NL + "    }" + NL + "  }" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "" + NL + "  @OVERRIDE" + NL + "  PROTECTED VOID ONACTIVATE() THROWS EXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    SUPER.ONACTIVATE();" + NL + "    SETSTATE(STATE.CONNECTING);" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  @OVERRIDE" + NL + "  PROTECTED VOID ONDEACTIVATE() THROWS EXCEPTION" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    SETSTATE(STATE.DISCONNECTED);" + NL + "    FOR (SHORT I = 0; I < CHANNELS.SIZE(); I++)" + NL + "    {" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "      CHANNELIMPL CHANNEL = CHANNELS.GET(I);" + NL + "      IF (CHANNEL != NULL)" + NL + "      {" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "        LIFECYCLEUTIL.DEACTIVATE(CHANNEL);" + NL + "      }" + NL + "    }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "    CHANNELS.CLEAR();" + NL + "    SUPER.ONDEACTIVATE();" + NL + "  }" + NL + "" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "  PROTECTED ABSTRACT VOID REGISTERCHANNELWITHPEER(SHORT CHANNELINDEX, STRING PROTOCOLID)" + NL //$NON-NLS-1$
        + "      THROWS CONNECTOREXCEPTION;" + NL + "" + NL + "  /**" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "   * IS REGISTERED WITH EACH {@LINK CHANNEL} OF THIS {@LINK CONNECTOR}." + NL + "   * <P>" + NL + "   * " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + NL + "   * @AUTHOR EIKE STEPPER" + NL + "   */" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "  PRIVATE FINAL CLASS CHANNELLIFECYCLELISTENER IMPLEMENTS LIFECYCLELISTENER" + NL + "  {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    PUBLIC VOID NOTIFYLIFECYCLEACTIVATED(LIFECYCLENOTIFIER NOTIFIER)" + NL + "    {" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "      CHANNELIMPL CHANNEL = (CHANNELIMPL)NOTIFIER;" + NL + "      FIRECHANNELOPENED(CHANNEL);" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "    }" + NL + "" + NL + "    PUBLIC VOID NOTIFYLIFECYCLEDEACTIVATING(LIFECYCLENOTIFIER NOTIFIER)" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        + "    {" + NL + "      CHANNELIMPL CHANNEL = (CHANNELIMPL)NOTIFIER;" + NL //$NON-NLS-1$ //$NON-NLS-2$
        + "      FIRECHANNELCLOSING(CHANNEL);" + NL + "      REMOVECHANNEL(CHANNEL);" + NL + "    }" + NL + "  }" + NL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "}" + NL; //$NON-NLS-1$
  }
}
