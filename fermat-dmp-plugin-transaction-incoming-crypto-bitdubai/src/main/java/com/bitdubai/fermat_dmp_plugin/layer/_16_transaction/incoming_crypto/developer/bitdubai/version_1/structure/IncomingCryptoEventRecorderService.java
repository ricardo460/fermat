package com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer._18_transaction.incoming_crypto.Registry;
import com.bitdubai.fermat_api.layer._1_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer._1_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer._3_platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_api.layer._3_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_api.layer._3_platform_service.error_manager.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer._3_platform_service.event_manager.*;
import com.bitdubai.fermat_api.layer._3_platform_service.event_manager.events.IncomingCryptoIdentifiedEvent;
import com.bitdubai.fermat_api.layer._3_platform_service.event_manager.events.IncomingCryptoReceivedEvent;
import com.bitdubai.fermat_api.layer._3_platform_service.event_manager.events.IncomingCryptoReceptionConfirmedEvent;
import com.bitdubai.fermat_api.layer._3_platform_service.event_manager.events.IncomingCryptoReversedEvent;
import com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.event_handlers.IncomingCryptoIdentifiedEventHandler;
import com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.event_handlers.IncomingCryptoReceivedEventHandler;
import com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.event_handlers.IncomingCryptoReceptionConfirmedEventHandler;
import com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.event_handlers.IncomingCryptoReversedEventHandler;
import com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.exceptions.CantSaveEvent;

import com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.exceptions.CantStartServiceException;
import com.bitdubai.fermat_dmp_plugin.layer._16_transaction.incoming_crypto.developer.bitdubai.version_1.interfaces.TransactionService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ciencias on 3/30/15.
 * Modified by Arturo Vallone 25/04/2015
 */

/**
 * Esta es la clase que escucha los eventos relacionados a Incoming Crypto disparados por cualquier plugin que reciba
 * crypto currencies. 
 *
 * Cuando detecta un evento lo escribe en su tabla de base de datos.
 *
 *
 *
 * * * * * * * *
 */

public class IncomingCryptoEventRecorderService implements DealsWithEvents, TransactionService {
    /**
     * IncomingCryptoEventRecorderService member variables.
     */
    private IncomingCryptoRegistry registry;

    /**
     * DealsWithEvents Interface member variables.
     */
    private EventManager eventManager;
    private List<EventListener> listenersAdded = new ArrayList<>();

    /**
     * TransactionService Interface member variables.
     */
    private ServiceStatus serviceStatus = ServiceStatus.CREATED;


    /**
     * Constructor.
     * I will ask for the references I need to do my job since my creation
     */
    public IncomingCryptoEventRecorderService(EventManager eventManager, IncomingCryptoRegistry registry) {
        // TODO: CHECK NULL POINTER EXCEPTIONS
        this.eventManager = eventManager;
        this.registry = registry;
    }

    /**
     * DealWithEvents Interface implementation.
     */
    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * IncomingCryptoEventRecorder Interface implementation.
     */
    public void incomingCryptoIdentified(IncomingCryptoIdentifiedEvent event) throws CantSaveEvent {
        this.registry.saveNewEvent(event.getEventType().name(), event.getSource().name());
    }

    public void incomingCryptoReceived(IncomingCryptoReceivedEvent event)  throws CantSaveEvent {
        this.registry.saveNewEvent(event.getEventType().name(), event.getSource().name());
    }

    public void incomingCryptoReceptionConfirmed(IncomingCryptoReceptionConfirmedEvent event)  throws CantSaveEvent {
        this.registry.saveNewEvent(event.getEventType().name(), event.getSource().name());
    }

    public void incomingCryptoReversed(IncomingCryptoReversedEvent event) throws CantSaveEvent {
        this.registry.saveNewEvent(event.getEventType().name(), event.getSource().name());
    }

    
    /**
     * TransactionService interface implementation.
     */
    @Override
    public void start() throws CantStartServiceException {

        /**
         * I will initialize the handling of com.bitdubai.platform events.
         */
        EventListener eventListener;
        EventHandler eventHandler;

        eventListener = eventManager.getNewListener(EventType.INCOMING_CRYPTO_IDENTIFIED);
        eventHandler = new IncomingCryptoIdentifiedEventHandler();
        ((IncomingCryptoIdentifiedEventHandler) eventHandler).setIncomingCryptoEventRecorderService(this);
        eventListener.setEventHandler(eventHandler);
        eventManager.addListener(eventListener);
        listenersAdded.add(eventListener);

        eventListener = eventManager.getNewListener(EventType.INCOMING_CRYPTO_RECEIVED);
        eventHandler = new IncomingCryptoReceivedEventHandler();
        ((IncomingCryptoReceivedEventHandler) eventHandler).setIncomingCryptoEventRecorderService(this);
        eventListener.setEventHandler(eventHandler);
        eventManager.addListener(eventListener);
        listenersAdded.add(eventListener);

        eventListener = eventManager.getNewListener(EventType.INCOMING_CRYPTO_RECEPTION_CONFIRMED);
        eventHandler = new IncomingCryptoReceptionConfirmedEventHandler();
        ((IncomingCryptoReceptionConfirmedEventHandler) eventHandler).setIncomingCryptoEventRecorder(this);
        eventListener.setEventHandler(eventHandler);
        eventManager.addListener(eventListener);
        listenersAdded.add(eventListener);

        eventListener = eventManager.getNewListener(EventType.INCOMING_CRYPTO_REVERSED);
        eventHandler = new IncomingCryptoReversedEventHandler();
        ((IncomingCryptoReversedEventHandler) eventHandler).setIncomingCryptoEventRecorderService(this);
        eventListener.setEventHandler(eventHandler);
        eventManager.addListener(eventListener);
        listenersAdded.add(eventListener);

        this.serviceStatus = ServiceStatus.STARTED;
        
    }

    @Override
    public void stop() {

        /**
         * I will remove all the event listeners registered with the event manager.
         */
        for (EventListener eventListener : listenersAdded) {
            eventManager.removeListener(eventListener);
        }

        listenersAdded.clear();
        
        this.serviceStatus = ServiceStatus.STOPPED;
        
    }

    @Override
    public ServiceStatus getStatus() {
        return this.serviceStatus;
    }

}