/*
 * @#CommunicationSupervisorPendingMessagesAgent  - 2016
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.agents;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStopAgentException;
import com.bitdubai.fermat_api.FermatAgent;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.enums.AgentStatus;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.base.AbstractNetworkServiceBase;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.data_base.CommunicationNetworkServiceDatabaseConstants;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.MessagesStatus;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.contents.FermatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The Class <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.agents.CommunicationSupervisorPendingMessagesAgent</code> is
 * responsible to validate is exist pending message to process (incoming or outgoing )
 * <p/>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 05/02/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CommunicationSupervisorPendingMessagesAgent extends FermatAgent {

    /**
     * Represent the networkServiceRoot
     */
    private AbstractNetworkServiceBase networkServiceRoot;

    /**
     * Represent the scheduledThreadPool
     */
    private ScheduledExecutorService scheduledThreadPool;

    /**
     * Represent the scheduledFutures
     */
    private List<ScheduledFuture> scheduledFutures;

    /**
     * Represent the poolConnectionsWaitingForResponse
     */
    private Map<String, PlatformComponentProfile> poolConnectionsWaitingForResponse;

    /**
     * Constructor with parameter
     *
     * @param networkServiceRoot
     */
    public CommunicationSupervisorPendingMessagesAgent(AbstractNetworkServiceBase networkServiceRoot){
        super();
        this.networkServiceRoot                = networkServiceRoot;
        this.status                            = AgentStatus.CREATED;
        this.poolConnectionsWaitingForResponse = new HashMap<>();
        this.scheduledThreadPool               = Executors.newScheduledThreadPool(4);
        this.scheduledFutures                  = new ArrayList<>();
    }

    /**
     * Method that process the pending incoming messages
     */
    private void processPendingIncomingMessage() {

        try {

            /*
             * Read all pending message from database
             */
            Map<String, Object> filters = new HashMap<>();
            filters.put(CommunicationNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_STATUS_COLUMN_NAME, MessagesStatus.NEW_RECEIVED.getCode());
            List<FermatMessage> messages = networkServiceRoot.getCommunicationNetworkServiceConnectionManager().getIncomingMessageDao().findAll(filters);

            /*
             * For all destination in the message request a new connection
             */
            for (FermatMessage fermatMessage: messages) {
                networkServiceRoot.onNewMessagesReceive(fermatMessage);
            }

        }catch (Exception e){
            System.out.println("CommunicationSupervisorPendingMessagesAgent - processPendingIncomingMessage detect a error: "+e.getMessage());
        }
    }

    /**
     * Method that process the pending outgoing messages, in this method
     * validate is pending message to send, and request new connection for
     * the remote agent send the message
     */
    private void processPendingOutgoingMessage(Integer countFail) {

        try {

            /*
             * Read all pending message from database
             */
            Map<String, Object> filters = new HashMap<>();
            filters.put(CommunicationNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_STATUS_COLUMN_NAME, MessagesStatus.PENDING_TO_SEND.getCode());
            filters.put(CommunicationNetworkServiceDatabaseConstants.OUTGOING_MESSAGES_FAIL_COUNT_COLUMN_NAME, countFail);

            List<FermatMessage> messages = networkServiceRoot.getCommunicationNetworkServiceConnectionManager().getOutgoingMessageDao().findAll(filters);

            /*
             * For all destination in the message request a new connection
             */
            for (FermatMessage fermatMessage: messages) {

                if (!poolConnectionsWaitingForResponse.containsKey(fermatMessage.getReceiver())) {
                    if (networkServiceRoot.getCommunicationNetworkServiceConnectionManager().getNetworkServiceLocalInstance(fermatMessage.getReceiver()) == null) {

                        PlatformComponentProfile applicantParticipant = networkServiceRoot.getProfileSenderToRequestConnection(fermatMessage.getSender());

                        PlatformComponentProfile remoteParticipant = networkServiceRoot.getProfileDestinationToRequestConnection(fermatMessage.getReceiver());

                        networkServiceRoot.getCommunicationNetworkServiceConnectionManager().connectTo(applicantParticipant, networkServiceRoot.getNetworkServiceProfile(), remoteParticipant);

                        poolConnectionsWaitingForResponse.put(fermatMessage.getReceiver(), remoteParticipant);

                    }
                }
            }

        } catch (Exception e) {
            System.out.println("CommunicationSupervisorPendingMessagesAgent - processPendingOutgoingMessage detect a error: "+e.getMessage());
        }

    }

    /**
     * (non-javadoc)
     * @see FermatAgent#start()
     */
    @Override
    public void start() throws CantStartAgentException {

        try {

            scheduledFutures.add(scheduledThreadPool.scheduleAtFixedRate(new PendingIncomingMessageProcessorTask(),  15, 15, TimeUnit.SECONDS));
            scheduledFutures.add(scheduledThreadPool.scheduleAtFixedRate(new PendingOutgoingMessageProcessorTask(0), 15, 15, TimeUnit.SECONDS));
            scheduledFutures.add(scheduledThreadPool.scheduleAtFixedRate(new PendingOutgoingMessageProcessorTask(5),  5,  5, TimeUnit.MINUTES));
            scheduledFutures.add(scheduledThreadPool.scheduleAtFixedRate(new PendingOutgoingMessageProcessorTask(10), 1, 1, TimeUnit.HOURS));

            this.status = AgentStatus.STARTED;

        } catch (Exception exception) {
            throw new CantStartAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    /**
     * (non-javadoc)
     * @see FermatAgent#resume()
     */
    public void resume() throws CantStartAgentException {
        try {
            try {

                scheduledThreadPool.scheduleAtFixedRate(new PendingIncomingMessageProcessorTask()  , 15, 15, TimeUnit.SECONDS);
                scheduledThreadPool.scheduleAtFixedRate(new PendingOutgoingMessageProcessorTask(0) , 15, 15, TimeUnit.SECONDS);
                scheduledThreadPool.scheduleAtFixedRate(new PendingOutgoingMessageProcessorTask(5) ,  5,  5, TimeUnit.MINUTES);
                scheduledThreadPool.scheduleAtFixedRate(new PendingOutgoingMessageProcessorTask(10),  1,  1, TimeUnit.HOURS);

                this.status = AgentStatus.STARTED;

            } catch (Exception exception) {
                throw new CantStartAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
            }

        } catch (Exception exception) {

            throw new CantStartAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    /**
     * (non-javadoc)
     * @see FermatAgent#pause()
     */
    public void pause() throws CantStopAgentException {
        try {

            for (ScheduledFuture future: scheduledFutures) {
                future.cancel(Boolean.TRUE);
            }

            this.status = AgentStatus.PAUSED;

        } catch (Exception exception) {

            throw new CantStopAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    /**
     * (non-javadoc)
     * @see FermatAgent#stop()
     */
    public void stop() throws CantStopAgentException {
        try {

            scheduledThreadPool.shutdown();
            this.status = AgentStatus.PAUSED;

        } catch (Exception exception) {

            throw new CantStopAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    /**
     * Notify to the agent that remove a specific connection
     *
     * @param identityPublicKey
     */
    public void removeConnectionWaitingForResponse(String identityPublicKey){
        this.poolConnectionsWaitingForResponse.remove(identityPublicKey);
    }

    /**
     * Notify to the agent that remove all connection
     */
    public void removeAllConnectionWaitingForResponse(){
        this.poolConnectionsWaitingForResponse.clear();
    }


    private class PendingIncomingMessageProcessorTask implements Runnable {

        /**
         * (non-javadoc)
         * @see Runnable#run()
         */
        @Override
        public void run() {
            processPendingIncomingMessage();
        }
    }

    private class PendingOutgoingMessageProcessorTask implements Runnable {

        /**
         * Represent the count fail
         */
        private int countFail;

        /**
         * Constructor with parameters
         * @param countFail
         */
        public PendingOutgoingMessageProcessorTask(int countFail){
            super();
            this.countFail = countFail;
        }

        /**
         * (non-javadoc)
         * @see Runnable#run()
         */
        @Override
        public void run() {
            processPendingOutgoingMessage(countFail);
        }
    }

}