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
package org.eclipse.net4j.tests;

import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public final class HugeData
{
  public static final String NL = System.getProperty("line.separator");

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
    return "/***************************************************************************" + NL
        + " * COPYRIGHT (C) 2004 - 2008 EIKE STEPPER, GERMANY." + NL
        + " * ALL RIGHTS RESERVED. THIS PROGRAM AND THE ACCOMPANYING MATERIALS" + NL
        + " * ARE MADE AVAILABLE UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE V1.0" + NL
        + " * WHICH ACCOMPANIES THIS DISTRIBUTION, AND IS AVAILABLE AT" + NL
        + " * HTTP://WWW.ECLIPSE.ORG/LEGAL/EPL-V10.HTML" + NL + " * " + NL + " * CONTRIBUTORS:" + NL
        + " *    EIKE STEPPER - INITIAL API AND IMPLEMENTATION" + NL
        + " **************************************************************************/" + NL
        + "PACKAGE ORG.ECLIPSE.INTERNAL.NET4J.TRANSPORT.CONNECTOR;" + NL + "" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.BUFFER.BUFFERPROVIDER;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CHANNEL.CHANNEL;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CHANNEL.MULTIPLEXER;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.CONNECTOR;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.CONNECTOREXCEPTION;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.CREDENTIALS;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.PROTOCOL;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.TRANSPORT.CONNECTOR.PROTOCOLFACTORY;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.UTIL.LIFECYCLE.LIFECYCLELISTENER;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.UTIL.LIFECYCLE.LIFECYCLENOTIFIER;" + NL
        + "IMPORT ORG.ECLIPSE.NET4J.UTIL.REGISTRY.IREGISTRY;" + NL + "" + NL
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.TRANSPORT.CHANNEL.CHANNELIMPL;" + NL
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.UTIL.STREAM.BUFFERQUEUE;" + NL
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.UTIL.LIFECYCLE.ABSTRACTCOMPONENT;" + NL
        + "IMPORT ORG.ECLIPSE.INTERNAL.NET4J.UTIL.LIFECYCLE.LIFECYCLEUTIL;" + NL + "" + NL
        + "IMPORT JAVA.UTIL.ARRAYLIST;" + NL + "IMPORT JAVA.UTIL.LIST;" + NL + "IMPORT JAVA.UTIL.QUEUE;" + NL
        + "IMPORT JAVA.UTIL.CONCURRENT.CONCURRENTLINKEDQUEUE;" + NL + "IMPORT JAVA.UTIL.CONCURRENT.COUNTDOWNLATCH;"
        + NL + "IMPORT JAVA.UTIL.CONCURRENT.EXECUTORSERVICE;" + NL + "IMPORT JAVA.UTIL.CONCURRENT.TIMEUNIT;" + NL + ""
        + NL + "/**" + NL + " * @AUTHOR EIKE STEPPER" + NL + " */" + NL
        + "PUBLIC ABSTRACT CLASS ABSTRACTCONNECTOR EXTENDS ABSTRACTLIFECYCLE IMPLEMENTS CONNECTOR, CHANNELMULTIPLEXER"
        + NL + "{" + NL + "  PRIVATE STATIC FINAL CHANNELIMPL NULL_CHANNEL = NEW CHANNELIMPL(NULL);" + NL + "" + NL
        + "  PRIVATE CONNECTORCREDENTIALS CREDENTIALS;" + NL + "" + NL
        + "  PRIVATE IREGISTRY<STRING, PROTOCOLFACTORY> PROTOCOLFACTORYREGISTRY;" + NL + "" + NL
        + "  PRIVATE BUFFERPROVIDER BUFFERPROVIDER;" + NL + "" + NL + "  /**" + NL
        + "   * AN OPTIONAL EXECUTOR TO BE USED BY THE {@LINK CHANNEL}S TO PROCESS THEIR" + NL
        + "   * {@LINK CHANNELIMPL#RECEIVEQUEUE} INSTEAD OF THE CURRENT THREAD. IF NOT" + NL
        + "   * <CODE>NULL</CODE> THE SENDER AND THE RECEIVER PEERS BECOME DECOUPLED." + NL + "   * <P>" + NL + "   */"
        + NL + "  PRIVATE EXECUTORSERVICE RECEIVEEXECUTOR;" + NL + "" + NL + "  /**" + NL
        + "   * TODO SYNCHRONIZE ON CHANNELS?" + NL + "   */" + NL
        + "  PRIVATE LIST<CHANNELIMPL> CHANNELS = NEW ARRAYLIST();" + NL + "" + NL
        + "  PRIVATE STATE STATE = STATE.DISCONNECTED;" + NL + "" + NL + "  /**" + NL
        + "   * DON\'T INITIALIZE LAZILY TO CIRCUMVENT SYNCHRONIZATION!" + NL + "   */" + NL
        + "  PRIVATE QUEUE<STATELISTENER> STATELISTENERS = NEW CONCURRENTLINKEDQUEUE();" + NL + "" + NL + "  /**" + NL
        + "   * DON\'T INITIALIZE LAZILY TO CIRCUMVENT SYNCHRONIZATION!" + NL + "   */" + NL
        + "  PRIVATE QUEUE<CHANNELLISTENER> CHANNELLISTENERS = NEW CONCURRENTLINKEDQUEUE();" + NL + "" + NL + "  /**"
        + NL + "   * IS REGISTERED WITH EACH {@LINK CHANNEL} OF THIS {@LINK CONNECTOR}." + NL + "   * <P>" + NL
        + "   */" + NL + "  PRIVATE LIFECYCLELISTENER CHANNELLIFECYCLELISTENER = NEW CHANNELLIFECYCLELISTENER();" + NL
        + "" + NL + "  PRIVATE COUNTDOWNLATCH FINISHEDCONNECTING;" + NL + "" + NL
        + "  PRIVATE COUNTDOWNLATCH FINISHEDNEGOTIATING;" + NL + "" + NL + "  PUBLIC ABSTRACTCONNECTOR()" + NL + "  {"
        + NL + "  }" + NL + "" + NL + "  PUBLIC EXECUTORSERVICE GETRECEIVEEXECUTOR()" + NL + "  {" + NL
        + "    RETURN RECEIVEEXECUTOR;" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID SETRECEIVEEXECUTOR(EXECUTORSERVICE RECEIVEEXECUTOR)" + NL + "  {" + NL
        + "    THIS.RECEIVEEXECUTOR = RECEIVEEXECUTOR;" + NL + "  }" + NL + "" + NL
        + "  PUBLIC IREGISTRY<STRING, PROTOCOLFACTORY> GETPROTOCOLFACTORYREGISTRY()" + NL + "  {" + NL
        + "    RETURN PROTOCOLFACTORYREGISTRY;" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID SETPROTOCOLFACTORYREGISTRY(IREGISTRY<STRING, PROTOCOLFACTORY> PROTOCOLFACTORYREGISTRY)" + NL
        + "  {" + NL + "    THIS.PROTOCOLFACTORYREGISTRY = PROTOCOLFACTORYREGISTRY;" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID ADDSTATELISTENER(STATELISTENER LISTENER)" + NL + "  {" + NL
        + "    STATELISTENERS.ADD(LISTENER);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID REMOVESTATELISTENER(STATELISTENER LISTENER)" + NL + "  {" + NL
        + "    STATELISTENERS.REMOVE(LISTENER);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID ADDCHANNELLISTENER(CHANNELLISTENER LISTENER)" + NL + "  {" + NL
        + "    CHANNELLISTENERS.ADD(LISTENER);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID REMOVECHANNELLISTENER(CHANNELLISTENER LISTENER)" + NL + "  {" + NL
        + "    CHANNELLISTENERS.REMOVE(LISTENER);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC BUFFERPROVIDER GETBUFFERPROVIDER()" + NL + "  {" + NL + "    RETURN BUFFERPROVIDER;" + NL + "  }"
        + NL + "" + NL + "  PUBLIC VOID SETBUFFERPROVIDER(BUFFERPROVIDER BUFFERPROVIDER)" + NL + "  {" + NL
        + "    THIS.BUFFERPROVIDER = BUFFERPROVIDER;" + NL + "  }" + NL + "" + NL + "  PUBLIC BOOLEAN ISCLIENT()" + NL
        + "  {" + NL + "    RETURN GETTYPE() == TYPE.CLIENT;" + NL + "  }" + NL + "" + NL
        + "  PUBLIC BOOLEAN ISSERVER()" + NL + "  {" + NL + "    RETURN GETTYPE() == TYPE.SERVER;" + NL + "  }" + NL
        + "" + NL + "  PUBLIC CONNECTORCREDENTIALS GETCREDENTIALS()" + NL + "  {" + NL + "    RETURN CREDENTIALS;" + NL
        + "  }" + NL + "" + NL + "  PUBLIC VOID SETCREDENTIALS(CONNECTORCREDENTIALS CREDENTIALS)" + NL + "  {" + NL
        + "    THIS.CREDENTIALS = CREDENTIALS;" + NL + "  }" + NL + "" + NL + "  PUBLIC STATE GETSTATE()" + NL + "  {"
        + NL + "    RETURN STATE;" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID SETSTATE(STATE NEWSTATE) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL
        + "    STATE OLDSTATE = GETSTATE();" + NL + "    IF (NEWSTATE != OLDSTATE)" + NL + "    {" + NL
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": SETTING STATE \" + NEWSTATE + \" (WAS \"" + NL
        + "          + OLDSTATE.TOSTRING().TOLOWERCASE() + \")\");" + NL + "      STATE = NEWSTATE;" + NL
        + "      FIRESTATECHANGED(NEWSTATE, OLDSTATE);" + NL + "" + NL + "      SWITCH (NEWSTATE)" + NL + "      {"
        + NL + "      CASE DISCONNECTED:" + NL + "        IF (FINISHEDCONNECTING != NULL)" + NL + "        {" + NL
        + "          FINISHEDCONNECTING.COUNTDOWN();" + NL + "          FINISHEDCONNECTING = NULL;" + NL + "        }"
        + NL + "" + NL + "        IF (FINISHEDNEGOTIATING != NULL)" + NL + "        {" + NL
        + "          FINISHEDNEGOTIATING.COUNTDOWN();" + NL + "          FINISHEDNEGOTIATING = NULL;" + NL
        + "        }" + NL + "        BREAK;" + NL + "" + NL + "      CASE CONNECTING:" + NL
        + "        FINISHEDCONNECTING = NEW COUNTDOWNLATCH(1);" + NL
        + "        FINISHEDNEGOTIATING = NEW COUNTDOWNLATCH(1);" + NL + "        IF (GETTYPE() == TYPE.SERVER)" + NL
        + "        {" + NL + "          SETSTATE(STATE.NEGOTIATING);" + NL + "        }" + NL + "        BREAK;" + NL
        + "" + NL + "      CASE NEGOTIATING:" + NL + "        FINISHEDCONNECTING.COUNTDOWN();" + NL
        + "        SETSTATE(STATE.CONNECTED); // TODO IMPLEMENT NEGOTIATION" + NL + "        BREAK;" + NL + "" + NL
        + "      CASE CONNECTED:" + NL + "        FINISHEDCONNECTING.COUNTDOWN(); // JUST IN CASE OF SUSPICION" + NL
        + "        FINISHEDNEGOTIATING.COUNTDOWN();" + NL + "        BREAK;" + NL + "" + NL + "      }" + NL + "    }"
        + NL + "  }" + NL + "" + NL + "  PUBLIC BOOLEAN ISCONNECTED()" + NL + "  {" + NL
        + "    RETURN GETSTATE() == STATE.CONNECTED;" + NL + "  }" + NL + "" + NL
        + "  PUBLIC VOID CONNECTASYNC() THROWS CONNECTOREXCEPTION" + NL + "  {" + NL + "    TRY" + NL + "    {" + NL
        + "      ACTIVATE();" + NL + "    }" + NL + "    CATCH (CONNECTOREXCEPTION EX)" + NL + "    {" + NL
        + "      THROW EX;" + NL + "    }" + NL + "    CATCH (EXCEPTION EX)" + NL + "    {" + NL
        + "      THROW NEW CONNECTOREXCEPTION(EX);" + NL + "    }" + NL + "  }" + NL + "" + NL
        + "  PUBLIC BOOLEAN WAITFORCONNECTION(LONG TIMEOUT) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL
        + "    STATE STATE = GETSTATE();" + NL + "    IF (STATE == STATE.DISCONNECTED)" + NL + "    {" + NL
        + "      RETURN FALSE;" + NL + "    }" + NL + "" + NL + "    TRY" + NL + "    {" + NL
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": WAITING FOR CONNECTION...\");" + NL
        + "      RETURN FINISHEDNEGOTIATING.AWAIT(TIMEOUT, TIMEUNIT.MILLISECONDS);" + NL + "    }" + NL
        + "    CATCH (INTERRUPTEDEXCEPTION EX)" + NL + "    {" + NL + "      RETURN FALSE;" + NL + "    }" + NL + "  }"
        + NL + "" + NL + "  PUBLIC BOOLEAN CONNECT(LONG TIMEOUT) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL
        + "    CONNECTASYNC();" + NL + "    RETURN WAITFORCONNECTION(TIMEOUT);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC CONNECTOREXCEPTION DISCONNECT()" + NL + "  {" + NL + "    EXCEPTION EX = DEACTIVATE();" + NL
        + "    IF (EX == NULL)" + NL + "    {" + NL + "      RETURN NULL;" + NL + "    }" + NL + "" + NL
        + "    IF (EX INSTANCEOF CONNECTOREXCEPTION)" + NL + "    {" + NL + "      RETURN (CONNECTOREXCEPTION)EX;" + NL
        + "    }" + NL + "" + NL + "    RETURN NEW CONNECTOREXCEPTION(EX);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC CHANNEL[] GETCHANNELS()" + NL + "  {" + NL
        + "    FINAL LIST<CHANNEL> RESULT = NEW ARRAYLIST<CHANNEL>();" + NL + "    SYNCHRONIZED (CHANNELS)" + NL
        + "    {" + NL + "      FOR (FINAL CHANNELIMPL CHANNEL : CHANNELS)" + NL + "      {" + NL
        + "        IF (CHANNEL != NULL_CHANNEL)" + NL + "        {" + NL + "          RESULT.ADD(CHANNEL);" + NL
        + "        }" + NL + "      }" + NL + "    }" + NL + "" + NL
        + "    RETURN RESULT.TOARRAY(NEW CHANNEL[RESULT.SIZE()]);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC CHANNEL OPENCHANNEL() THROWS CONNECTOREXCEPTION" + NL + "  {" + NL
        + "    RETURN OPENCHANNEL(NULL);" + NL + "  }" + NL + "" + NL
        + "  PUBLIC CHANNEL OPENCHANNEL(STRING PROTOCOLID) THROWS CONNECTOREXCEPTION" + NL + "  {" + NL
        + "    SHORT CHANNELINDEX = FINDFREECHANNELINDEX();" + NL
        + "    CHANNELIMPL CHANNEL = CREATECHANNEL(CHANNELINDEX, PROTOCOLID);" + NL
        + "    REGISTERCHANNELWITHPEER(CHANNELINDEX, PROTOCOLID);" + NL + "" + NL + "    TRY" + NL + "    {" + NL
        + "      CHANNEL.ACTIVATE();" + NL + "    }" + NL + "    CATCH (CONNECTOREXCEPTION EX)" + NL + "    {" + NL
        + "      THROW EX;" + NL + "    }" + NL + "    CATCH (EXCEPTION EX)" + NL + "    {" + NL
        + "      THROW NEW CONNECTOREXCEPTION(EX);" + NL + "    }" + NL + "" + NL + "    RETURN CHANNEL;" + NL + "  }"
        + NL + "" + NL + "  PUBLIC CHANNELIMPL CREATECHANNEL(SHORT CHANNELINDEX, STRING PROTOCOLID)" + NL + "  {" + NL
        + "    PROTOCOL PROTOCOL = CREATEPROTOCOL(PROTOCOLID);" + NL + "    IF (PROTOCOL == NULL)" + NL + "    {" + NL
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": OPENING CHANNEL WITHOUT PROTOCOL\");" + NL + "    }" + NL
        + "    ELSE" + NL + "    {" + NL
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": OPENING CHANNEL WITH PROTOCOL \" + PROTOCOLID);" + NL + "    }"
        + NL + "" + NL + "    CHANNELIMPL CHANNEL = NEW CHANNELIMPL(RECEIVEEXECUTOR);" + NL
        + "    CHANNEL.SETCHANNELINDEX(CHANNELINDEX);" + NL + "    CHANNEL.SETMULTIPLEXER(THIS);" + NL
        + "    CHANNEL.SETRECEIVEHANDLER(PROTOCOL);" + NL
        + "    CHANNEL.ADDLIFECYCLELISTENER(CHANNELLIFECYCLELISTENER);" + NL + "    ADDCHANNEL(CHANNEL);" + NL
        + "    RETURN CHANNEL;" + NL + "  }" + NL + "" + NL + "  PUBLIC CHANNELIMPL GETCHANNEL(SHORT CHANNELINDEX)"
        + NL + "  {" + NL + "    TRY" + NL + "    {" + NL + "      CHANNELIMPL CHANNEL = CHANNELS.GET(CHANNELINDEX);"
        + NL + "      IF (CHANNEL == NULL || CHANNEL == NULL_CHANNEL)" + NL + "      {" + NL
        + "        THROW NEW NULLPOINTEREXCEPTION();" + NL + "      }" + NL + "" + NL + "      RETURN CHANNEL;" + NL
        + "    }" + NL + "    CATCH (INDEXOUTOFBOUNDSEXCEPTION EX)" + NL + "    {" + NL
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": INVALID CHANNELINDEX \" + CHANNELINDEX);" + NL
        + "      RETURN NULL;" + NL + "    }" + NL + "  }" + NL + "" + NL
        + "  PROTECTED LIST<BUFFERQUEUE> GETCHANNELBUFFERQUEUES()" + NL + "  {" + NL
        + "    FINAL LIST<BUFFERQUEUE> RESULT = NEW ARRAYLIST<BUFFERQUEUE>();" + NL + "    SYNCHRONIZED (CHANNELS)"
        + NL + "    {" + NL + "      FOR (FINAL CHANNELIMPL CHANNEL : CHANNELS)" + NL + "      {" + NL
        + "        IF (CHANNEL != NULL_CHANNEL)" + NL + "        {" + NL
        + "          BUFFERQUEUE BUFFERQUEUE = CHANNEL.GETSENDQUEUE();" + NL + "          RESULT.ADD(BUFFERQUEUE);"
        + NL + "        }" + NL + "      }" + NL + "    }" + NL + "" + NL + "    RETURN RESULT;" + NL + "  }" + NL + ""
        + NL + "  PROTECTED SHORT FINDFREECHANNELINDEX()" + NL + "  {" + NL + "    SYNCHRONIZED (CHANNELS)" + NL
        + "    {" + NL + "      INT SIZE = CHANNELS.SIZE();" + NL + "      FOR (SHORT I = 0; I < SIZE; I++)" + NL
        + "      {" + NL + "        IF (CHANNELS.GET(I) == NULL_CHANNEL)" + NL + "        {" + NL
        + "          RETURN I;" + NL + "        }" + NL + "      }" + NL + "" + NL
        + "      CHANNELS.ADD(NULL_CHANNEL);" + NL + "      RETURN (SHORT)SIZE;" + NL + "    }" + NL + "  }" + NL + ""
        + NL + "  PROTECTED VOID ADDCHANNEL(CHANNELIMPL CHANNEL)" + NL + "  {" + NL
        + "    SHORT CHANNELINDEX = CHANNEL.GETCHANNELINDEX();" + NL + "    WHILE (CHANNELINDEX >= CHANNELS.SIZE())"
        + NL + "    {" + NL + "      CHANNELS.ADD(NULL_CHANNEL);" + NL + "    }" + NL + "" + NL
        + "    CHANNELS.SET(CHANNELINDEX, CHANNEL);" + NL + "  }" + NL + "" + NL
        + "  PROTECTED VOID REMOVECHANNEL(CHANNELIMPL CHANNEL)" + NL + "  {" + NL
        + "    CHANNEL.REMOVELIFECYCLELISTENER(CHANNELLIFECYCLELISTENER);" + NL
        + "    INT CHANNELINDEX = CHANNEL.GETCHANNELINDEX();" + NL + "" + NL
        + "    IOUTIL.OUT().PRINTLN(TOSTRING() + \": REMOVING CHANNEL \" + CHANNELINDEX);" + NL
        + "    CHANNELS.SET(CHANNELINDEX, NULL_CHANNEL);" + NL + "  }" + NL + "" + NL
        + "  PROTECTED PROTOCOL CREATEPROTOCOL(STRING PROTOCOLID)" + NL + "  {" + NL
        + "    IF (PROTOCOLID == NULL || PROTOCOLID.LENGTH() == 0)" + NL + "    {" + NL + "      RETURN NULL;" + NL
        + "    }" + NL + "" + NL + "    IREGISTRY<STRING, PROTOCOLFACTORY> REGISTRY = GETPROTOCOLFACTORYREGISTRY();"
        + NL + "    IF (REGISTRY == NULL)" + NL + "    {" + NL + "      RETURN NULL;" + NL + "    }" + NL + "" + NL
        + "    PROTOCOLFACTORY FACTORY = REGISTRY.LOOKUP(PROTOCOLID);" + NL + "    IF (FACTORY == NULL)" + NL + "    {"
        + NL + "      RETURN NULL;" + NL + "    }" + NL + "" + NL
        + "    IOUTIL.OUT().PRINTLN(TOSTRING() + \": CREATING PROTOCOL \" + PROTOCOLID);" + NL
        + "    RETURN FACTORY.CREATEPROTOCOL();" + NL + "  }" + NL + "" + NL
        + "  PROTECTED VOID FIRECHANNELOPENED(CHANNEL CHANNEL)" + NL + "  {" + NL
        + "    FOR (CHANNELLISTENER LISTENER : CHANNELLISTENERS)" + NL + "    {" + NL + "      TRY" + NL + "      {"
        + NL + "        LISTENER.NOTIFYCHANNELOPENED(CHANNEL);" + NL + "      }" + NL + "      CATCH (EXCEPTION EX)"
        + NL + "      {" + NL + "        EX.PRINTSTACKTRACE();" + NL + "      }" + NL + "    }" + NL + "  }" + NL + ""
        + NL + "  PROTECTED VOID FIRECHANNELCLOSING(CHANNEL CHANNEL)" + NL + "  {" + NL
        + "    FOR (CHANNELLISTENER LISTENER : CHANNELLISTENERS)" + NL + "    {" + NL + "      TRY" + NL + "      {"
        + NL + "        LISTENER.NOTIFYCHANNELCLOSING(CHANNEL);" + NL + "      }" + NL + "      CATCH (EXCEPTION EX)"
        + NL + "      {" + NL + "        EX.PRINTSTACKTRACE();" + NL + "      }" + NL + "    }" + NL + "  }" + NL + ""
        + NL + "  PROTECTED VOID FIRESTATECHANGED(STATE NEWSTATE, STATE OLDSTATE)" + NL + "  {" + NL
        + "    FOR (STATELISTENER LISTENER : STATELISTENERS)" + NL + "    {" + NL + "      TRY" + NL + "      {" + NL
        + "        LISTENER.NOTIFYSTATECHANGED(THIS, NEWSTATE, OLDSTATE);" + NL + "      }" + NL
        + "      CATCH (EXCEPTION EX)" + NL + "      {" + NL + "        EX.PRINTSTACKTRACE();" + NL + "      }" + NL
        + "    }" + NL + "  }" + NL + "" + NL + "  @OVERRIDE" + NL
        + "  PROTECTED VOID ONACCESSBEFOREACTIVATE() THROWS EXCEPTION" + NL + "  {" + NL
        + "    SUPER.ONACCESSBEFOREACTIVATE();" + NL + "    IF (BUFFERPROVIDER == NULL)" + NL + "    {" + NL
        + "      THROW NEW ILLEGALSTATEEXCEPTION(\"BUFFERPROVIDER == NULL\");" + NL + "    }" + NL + "" + NL
        + "    IF (PROTOCOLFACTORYREGISTRY == NULL)" + NL + "    {" + NL
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": (INFO) PROTOCOLFACTORYREGISTRY == NULL\");" + NL + "    }" + NL
        + "" + NL + "    IF (RECEIVEEXECUTOR == NULL)" + NL + "    {" + NL
        + "      IOUTIL.OUT().PRINTLN(TOSTRING() + \": (INFO) RECEIVEEXECUTOR == NULL\");" + NL + "    }" + NL + "  }"
        + NL + "" + NL + "  @OVERRIDE" + NL + "  PROTECTED VOID ONACTIVATE() THROWS EXCEPTION" + NL + "  {" + NL
        + "    SUPER.ONACTIVATE();" + NL + "    SETSTATE(STATE.CONNECTING);" + NL + "  }" + NL + "" + NL
        + "  @OVERRIDE" + NL + "  PROTECTED VOID ONDEACTIVATE() THROWS EXCEPTION" + NL + "  {" + NL
        + "    SETSTATE(STATE.DISCONNECTED);" + NL + "    FOR (SHORT I = 0; I < CHANNELS.SIZE(); I++)" + NL + "    {"
        + NL + "      CHANNELIMPL CHANNEL = CHANNELS.GET(I);" + NL + "      IF (CHANNEL != NULL)" + NL + "      {" + NL
        + "        LIFECYCLEUTIL.DEACTIVATE(CHANNEL);" + NL + "      }" + NL + "    }" + NL + "" + NL
        + "    CHANNELS.CLEAR();" + NL + "    SUPER.ONDEACTIVATE();" + NL + "  }" + NL + "" + NL
        + "  PROTECTED ABSTRACT VOID REGISTERCHANNELWITHPEER(SHORT CHANNELINDEX, STRING PROTOCOLID)" + NL
        + "      THROWS CONNECTOREXCEPTION;" + NL + "" + NL + "  /**" + NL
        + "   * IS REGISTERED WITH EACH {@LINK CHANNEL} OF THIS {@LINK CONNECTOR}." + NL + "   * <P>" + NL + "   * "
        + NL + "   * @AUTHOR EIKE STEPPER" + NL + "   */" + NL
        + "  PRIVATE FINAL CLASS CHANNELLIFECYCLELISTENER IMPLEMENTS LIFECYCLELISTENER" + NL + "  {" + NL
        + "    PUBLIC VOID NOTIFYLIFECYCLEACTIVATED(LIFECYCLENOTIFIER NOTIFIER)" + NL + "    {" + NL
        + "      CHANNELIMPL CHANNEL = (CHANNELIMPL)NOTIFIER;" + NL + "      FIRECHANNELOPENED(CHANNEL);" + NL
        + "    }" + NL + "" + NL + "    PUBLIC VOID NOTIFYLIFECYCLEDEACTIVATING(LIFECYCLENOTIFIER NOTIFIER)" + NL
        + "    {" + NL + "      CHANNELIMPL CHANNEL = (CHANNELIMPL)NOTIFIER;" + NL
        + "      FIRECHANNELCLOSING(CHANNEL);" + NL + "      REMOVECHANNEL(CHANNEL);" + NL + "    }" + NL + "  }" + NL
        + "}" + NL;
  }
}
