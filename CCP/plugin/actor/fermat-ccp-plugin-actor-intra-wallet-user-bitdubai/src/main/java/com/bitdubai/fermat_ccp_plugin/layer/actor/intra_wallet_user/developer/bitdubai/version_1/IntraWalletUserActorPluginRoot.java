package com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.Plugin;
import com.bitdubai.fermat_api.Service;


import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.developer.LogManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;

import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.dmp_actor.Actor;
import com.bitdubai.fermat_api.layer.dmp_actor.intra_user.exceptions.CantGetIntraUserException;
import com.bitdubai.fermat_api.layer.dmp_actor.intra_user.exceptions.IntraUserNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.enums.ConnectionState;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.exceptions.CantAcceptIntraWalletUserException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.exceptions.CantCancelIntraWalletUserException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.exceptions.CantCreateIntraWalletUserException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.exceptions.CantDenyConnectionException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.exceptions.CantDisconnectIntraWalletUserException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.exceptions.CantGetIntraWalletUsersException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.interfaces.IntraWalletUser;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_wallet_user.interfaces.IntraWalletUserManager;
import com.bitdubai.fermat_api.layer.dmp_network_service.intra_user.interfaces.DealsWithIntraUsersNetworkService;
import com.bitdubai.fermat_api.layer.dmp_network_service.intra_user.interfaces.IntraUserManager;
import com.bitdubai.fermat_api.layer.dmp_network_service.intra_user.interfaces.IntraUserNotification;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.DealsWithPluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogLevel;

import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.database.IntraWalletUserActorDao;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.database.IntraWalletUserActorDeveloperDatabaseFactory;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.event_handlers.IntraWalletUserConnectionAcceptedEventHandlers;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.event_handlers.IntraWalletUserDeniedConnectionEventHandlers;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.event_handlers.IntraWalletUserDisconnectionEventHandlers;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.event_handlers.IntraWalletUserRequestConnectionEventHandlers;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.exceptions.CantAddPendingIntraWalletUserException;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.exceptions.CantGetIntraWalletUsersListException;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.exceptions.CantInitializeIntraWalletUserActorDatabaseException;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.exceptions.CantProcessNotificationsExceptions;
import com.bitdubai.fermat_ccp_plugin.layer.actor.intra_wallet_user.developer.bitdubai.version_1.exceptions.CantUpdateIntraWalletUserConnectionException;
import com.bitdubai.fermat_pip_api.layer.pip_actor.exception.CantGetLogTool;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.UnexpectedAddonsExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.enums.EventType;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.interfaces.DealsWithEvents;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.interfaces.EventManager;
import com.bitdubai.fermat_pip_api.layer.pip_user.device_user.interfaces.DeviceUserManager;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * This plugin manages connections between users of the platform..
 * Provides contact information of Intra User
 * <p/>
 * Created by loui on 22/02/15.
 * modified by Natalia on 11/08/2015
 *
 * @version 1.0
 * @since Java JDK 1.7
 */


public class IntraWalletUserActorPluginRoot implements IntraWalletUserManager, DatabaseManagerForDevelopers, DealsWithErrors, DealsWithEvents, DealsWithIntraUsersNetworkService, LogManagerForDevelopers, DealsWithPluginDatabaseSystem, DealsWithPluginFileSystem, Plugin, Service, Serializable {

    private IntraWalletUserActorDao intraWalletUserActorDao;

    /**
     * DealsWithErrors Interface member variables.
     */
    ErrorManager errorManager;

    /**
     * DealsWithEvents Interface member variables.
     */
    EventManager eventManager;

    /**
     * DealsWithDeviceUsers Interface member variables.
     */
    private DeviceUserManager deviceUserManager;


    List<FermatEventListener> listenersAdded = new ArrayList<>();

    /**
     * DealsWithLogger interface member variable
     */

    static Map<String, LogLevel> newLoggingLevel = new HashMap<String, LogLevel>();

    /**
     * DealsWithIntraWalletUsersNetworkService interface member variable
     */
    IntraUserManager intraUserNetworkServiceManager;

    /**
     * DealsWithPlatformDatabaseSystem Interface member variables.
     */
    PluginDatabaseSystem pluginDatabaseSystem;


    /**
     * FileSystem Interface member variables.
     */
    PluginFileSystem pluginFileSystem;


    /**
     * Plugin Interface member variables.
     */
    UUID pluginId;

    /**
     * Service Interface member variables.
     */
    ServiceStatus serviceStatus = ServiceStatus.CREATED;


    /**
     * ActorIntraWalletUserManager interface implementation.
     */

//TODO: fijarse si esto va
//    @Override
//    public Actor createNewIntraWalletUser(String alias, byte[] profileImage) throws CantCreateIntraWalletUserException {
//        try {
//            DeviceUser loggedUser = deviceUserManager.getLoggedInDeviceUser();
//
//
//            //this.intraUserActorDao.createNewIntraUser(loggedUser.getPublicKey(), alias, "", profileImage, ContactState.CONNECTED);
//
//            //return new IntraUserActorRecord(loggedUser.getPublicKey(), "",alias,profileImage);
//        }
//        catch(CantGetLoggedInDeviceUserException e)
//        {
//            throw new CantCreateIntraWalletUserException("CAN'T CREATE NEW INTRA WALLET USER ACTOR", e, "Error getting current logged in device user", "");
//        }
////        catch (CantAddPendingIntraUserException e) {
////            throw new CantCreateIntraWalletUserException("CAN'T CREATE NEW INTRA WALLET USER ACTOR", e, "Error add intra user on database", "");
////        }  catch (Exception e) {
////            throw new CantCreateIntraWalletUserException("CAN'T CREATE NEW INTRA WALLET USER ACTOR", FermatException.wrapException(e), "", "");
////        }
//        return null;
//    }


    /**
     * That method registers a new intra user in the list
     * managed by this plugin with ContactState PENDING_HIS_ACCEPTANCE until the other intra user
     * accepts the connection request sent also by this method.
     *
     * @param intraUserLoggedInPublicKey The public key of the intra user sending the connection request.
     * @param intraUserToAddName         The name of the intra user to add
     * @param intraUserToAddPublicKey    The public key of the intra user to add
     * @param profileImage               The profile image that the intra user has
     * @throws CantCreateIntraWalletUserException
     */

    @Override
    public void askIntraWalletUserForAcceptance(String intraUserLoggedInPublicKey, String intraUserToAddName, String intraUserToAddPublicKey, byte[] profileImage) throws CantCreateIntraWalletUserException {
        try {
            this.intraWalletUserActorDao.createNewIntraWalletUser(intraUserLoggedInPublicKey, intraUserToAddName, intraUserToAddPublicKey, profileImage, ConnectionState.PENDING_REMOTELY_ACCEPTANCE);
        } catch (CantAddPendingIntraWalletUserException e) {
            throw new CantCreateIntraWalletUserException("CAN'T ADD NEW INTRA USER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantCreateIntraWalletUserException("CAN'T ADD NEW INTRA USER CONNECTION", FermatException.wrapException(e), "", "");
        }

    }


    /**
     * That method takes the information of a connection request, accepts
     * the request and adds the intra user to the list managed by this plugin with ContactState CONTACT.
     *
     * @param intraUserLoggedInPublicKey The public key of the intra user sending the connection request.
     * @param intraUserToAddPublicKey    The public key of the intra user to add
     * @throws CantAcceptIntraWalletUserException
     */

    @Override
    public void acceptIntraWalletUser(String intraUserLoggedInPublicKey, String intraUserToAddPublicKey) throws CantAcceptIntraWalletUserException {
        try {
            this.intraWalletUserActorDao.updateIntraWalletUserConnectionState(intraUserLoggedInPublicKey, intraUserToAddPublicKey, ConnectionState.CONNECTED);
        } catch (CantUpdateIntraWalletUserConnectionException e) {
            throw new CantAcceptIntraWalletUserException("CAN'T ACCEPT INTRA USER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantAcceptIntraWalletUserException("CAN'T ACCEPT INTRA USER CONNECTION", FermatException.wrapException(e), "", "");
        }
    }


    /**
     * That method rejects a connection request from another intra user
     *
     * @param intraUserLoggedInPublicKey The public key of the intra user identity that is the receptor of the request
     * @param intraUserToRejectPublicKey The public key of the intra user that sent the request
     * @throws CantDenyConnectionException
     */
    @Override
    public void denyConnection(String intraUserLoggedInPublicKey, String intraUserToRejectPublicKey) throws CantDenyConnectionException {

        try {
            this.intraWalletUserActorDao.updateIntraWalletUserConnectionState(intraUserLoggedInPublicKey, intraUserToRejectPublicKey, ConnectionState.DENIED_LOCALLY);
        } catch (CantUpdateIntraWalletUserConnectionException e) {
            throw new CantDenyConnectionException("CAN'T DENY INTRA USER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantDenyConnectionException("CAN'T DENY INTRA USER CONNECTION", FermatException.wrapException(e), "", "");
        }
    }

    /**
     * That method disconnect an intra user from the connections registry
     *
     * @param intraUserLoggedInPublicKey     The public key of the intra user identity that is the receptor of the request
     * @param intraUserToDisconnectPublicKey The public key of the intra user to disconnect as connection
     * @throws CantDisconnectIntraWalletUserException
     */
    @Override
    public void disconnectIntraWalletUser(String intraUserLoggedInPublicKey, String intraUserToDisconnectPublicKey) throws CantDisconnectIntraWalletUserException {
        try {
            this.intraWalletUserActorDao.updateIntraWalletUserConnectionState(intraUserLoggedInPublicKey, intraUserToDisconnectPublicKey, ConnectionState.DISCONNECTED_REMOTELY);
        } catch (CantUpdateIntraWalletUserConnectionException e) {
            throw new CantDisconnectIntraWalletUserException("CAN'T CANCEL INTRA USER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantDisconnectIntraWalletUserException("CAN'T CANCEL INTRA USER CONNECTION", FermatException.wrapException(e), "", "");
        }
    }


    /**
     * That method cancels an intra user from the connections registry
     *
     * @param intraUserLoggedInPublicKey The public key of the intra user identity that is the receptor of the request
     * @param intraUserToCancelPublicKey The public key of the intra user to cancel as connection
     * @throws CantCancelIntraWalletUserException
     */
    @Override
    public void cancelIntraWalletUser(String intraUserLoggedInPublicKey, String intraUserToCancelPublicKey) throws CantCancelIntraWalletUserException {
        try {
            this.intraWalletUserActorDao.updateIntraWalletUserConnectionState(intraUserLoggedInPublicKey, intraUserToCancelPublicKey, ConnectionState.CANCELLED);
        } catch (CantUpdateIntraWalletUserConnectionException e) {
            throw new CantCancelIntraWalletUserException("CAN'T CANCEL INTRA USER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantCancelIntraWalletUserException("CAN'T CANCEL INTRA USER CONNECTION", FermatException.wrapException(e), "", "");
        }
    }

    /**
     * That method get the list of all intra users that are connections of the logged in one.
     *
     * @param intraUserLoggedInPublicKey the public key of the intra user logged in
     * @return the list of intra users the logged in intra user has as connections.
     * @throws CantGetIntraWalletUsersException
     */
    @Override
    public List<IntraWalletUser> getAllIntraWalletUsers(String intraUserLoggedInPublicKey, int max, int offset) throws CantGetIntraWalletUsersException {
        try {
            return this.intraWalletUserActorDao.getAllIntraWalletUsers(intraUserLoggedInPublicKey, max, offset);
        } catch (CantGetIntraWalletUsersListException e) {
            throw new CantGetIntraWalletUsersException("CAN'T LIST INTRA USER CONNECTIONS", e, "", "");
        } catch (Exception e) {
            throw new CantGetIntraWalletUsersException("CAN'T LIST INTRA USER CONNECTIONS", FermatException.wrapException(e), "", "");
        }
    }

    public Actor getActorByPublicKey(String actorPublicKey) throws CantGetIntraUserException, IntraUserNotFoundException {

        try {
            //TODO verificar si se usa

         //   ActorIntraUser actor = intraUserActorDao.getIntraUser(actorPublicKey);

            //not found actor
           // if(actor == null)
               // throw new IntraUserNotFoundException("", null, ".","Intra User not found");

           // return new IntraUserActorRecord(actorPublicKey, "",actor.getName(),actor.getProfileImage());

//            return new IntraUserActorRecord("afd0647a-87de-4c56-9bc9-be736e0c5059", "","wallat user",new byte[0]);

       // } catch (com.bitdubai.fermat_dmp_plugin.layer.actor.intra_user.developer.bitdubai.version_1.exceptions.CantGetIntraUserException  e) {
          //  throw new CantGetIntraUserException("", e, ".","Cant Get Intra USer from Data Base");
         } catch (Exception e) {
            throw new CantGetIntraUserException("", FermatException.wrapException(e), "There is a problem I can't identify.", null);
        }
        return null;
    }


    /**
     * That method get the list of all intra users
     * that sent a connection request and are waiting for the acceptance of the logged in one.
     *
     * @param intraUserLoggedInPublicKey the public key of the intra user logged in
     * @return the list of intra users the logged in intra user has as connections.
     * @throws CantGetIntraWalletUsersException
     */

    @Override
    public List<IntraWalletUser> getWaitingYourAcceptanceIntraWalletUsers(String intraUserLoggedInPublicKey, int max, int offset) throws CantGetIntraWalletUsersException {
        try {
            return this.intraWalletUserActorDao.getAllIntraWalletUsers(intraUserLoggedInPublicKey, ConnectionState.PENDING_LOCALLY_ACCEPTANCE, max, offset);
        } catch (CantGetIntraWalletUsersListException e) {
            throw new CantGetIntraWalletUsersException("CAN'T LIST INTRA USER ACCEPTED CONNECTIONS", e, "", "");
        } catch (Exception e) {
            throw new CantGetIntraWalletUsersException("CAN'T LIST INTRA USER ACCEPTED CONNECTIONS", FermatException.wrapException(e), "", "");
        }
    }


    /**
     * That method get  the list of all intra users
     * that the logged in one has sent connections request to and have not been answered yet..
     *
     * @param intraUserLoggedInPublicKey the public key of the intra user logged in
     * @return the list of intra users the logged in intra user has as connections.
     * @throws CantGetIntraWalletUsersException
     */

    @Override
    public List<IntraWalletUser> getWaitingTheirAcceptanceIntraWalletUsers(String intraUserLoggedInPublicKey, int max, int offset) throws CantGetIntraWalletUsersException {
        try {
            return this.intraWalletUserActorDao.getAllIntraWalletUsers(intraUserLoggedInPublicKey, ConnectionState.PENDING_REMOTELY_ACCEPTANCE, max, offset);
        } catch (CantGetIntraWalletUsersListException e) {
            throw new CantGetIntraWalletUsersException("CAN'T LIST INTRA USER PENDING_HIS_ACCEPTANCE CONNECTIONS", e, "", "");
        } catch (Exception e) {
            throw new CantGetIntraWalletUsersException("CAN'T LIST INTRA USER PENDING_HIS_ACCEPTANCE CONNECTIONS", FermatException.wrapException(e), "", "");
        }
    }

    public void receivingIntraWalletUserRequestConnection(String intraUserLoggedInPublicKey, String intraUserToAddName, String intraUserToAddPublicKey, byte[] profileImage) throws CantCreateIntraWalletUserException {
        try {
            this.intraWalletUserActorDao.createNewIntraWalletUser(intraUserLoggedInPublicKey, intraUserToAddName, intraUserToAddPublicKey, profileImage, ConnectionState.PENDING_LOCALLY_ACCEPTANCE);
        } catch (CantAddPendingIntraWalletUserException e) {
            throw new CantCreateIntraWalletUserException("CAN'T ADD NEW INTRA USER REQUEST CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantCreateIntraWalletUserException("CAN'T ADD NEW INTRA USER REQUEST CONNECTION", FermatException.wrapException(e), "", "");
        }

    }

    /**
     * DealsWithEvents Interface implementation.
     */


    @Override
    public void setEventManager(EventManager DealsWithEvents) {
        this.eventManager = DealsWithEvents;
    }

    /**
     * DealsWithErrors Interface implementation.
     */

    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }


    /**
     * DealsWithIntraWalletUsersNetworkService Interface implementation.
     */

    @Override
    public void setIntraUserNetworkServiceManager(IntraUserManager intraUserManager) {

        this.intraUserNetworkServiceManager = intraUserManager;
    }

    /**
     * DealsWithPluginDatabaseSystem interface implementation.
     */
    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;

    }

    /**
     * DealWithPluginFileSystem Interface implementation.
     */
    @Override
    public void setPluginFileSystem(PluginFileSystem pluginFileSystem) {
        this.pluginFileSystem = pluginFileSystem;

    }

    /**
     * Service Interface implementation.
     */
    @Override
    public void start() throws CantStartPluginException {
        try {
            /**
             * I created instance of IntraWalletUserActorDao
             * and initialize Database
             */
            this.intraWalletUserActorDao = new IntraWalletUserActorDao(pluginDatabaseSystem, this.pluginFileSystem, this.pluginId);

            this.intraWalletUserActorDao.initializeDatabase();

            /**
             * I will initialize the handling of com.bitdubai.platform events.
             */

            FermatEventListener fermatEventListener;
            FermatEventHandler fermatEventHandler;


            /**
             * Listener Accepted connection event
             */
            fermatEventListener = eventManager.getNewListener(EventType.INTRA_USER_CONNECTION_ACCEPTED);
            fermatEventHandler = new IntraWalletUserConnectionAcceptedEventHandlers();
            ((IntraWalletUserConnectionAcceptedEventHandlers) fermatEventHandler).setIntraWalletUserManager(this);
            ((IntraWalletUserConnectionAcceptedEventHandlers) fermatEventHandler).setEventManager(eventManager);
            ((IntraWalletUserConnectionAcceptedEventHandlers) fermatEventHandler).setIntraUserManager(this.intraUserNetworkServiceManager);
            fermatEventListener.setEventHandler(fermatEventHandler);
            eventManager.addListener(fermatEventListener);
            listenersAdded.add(fermatEventListener);

            /**
             * Listener Cancelled connection event
             */
            fermatEventListener = eventManager.getNewListener(EventType.INTRA_USER_DISCONNECTION_REQUEST_RECEIVED);
            fermatEventHandler = new IntraWalletUserDisconnectionEventHandlers();
            ((IntraWalletUserDisconnectionEventHandlers) fermatEventHandler).setIntraWalletUserManager(this);
            ((IntraWalletUserDisconnectionEventHandlers) fermatEventHandler).setIntraUserManager(this.intraUserNetworkServiceManager);
            fermatEventListener.setEventHandler(fermatEventHandler);
            eventManager.addListener(fermatEventListener);
            listenersAdded.add(fermatEventListener);

            /**
             * Listener Request connection event
             */
            fermatEventListener = eventManager.getNewListener(EventType.INTRA_USER_REQUESTED_CONNECTION);
            fermatEventHandler = new IntraWalletUserRequestConnectionEventHandlers();
            ((IntraWalletUserRequestConnectionEventHandlers) fermatEventHandler).setIntraWalletUserManager(this);
            ((IntraWalletUserRequestConnectionEventHandlers) fermatEventHandler).setEventManager(this.eventManager);
            ((IntraWalletUserRequestConnectionEventHandlers) fermatEventHandler).setIntraUserManager(this.intraUserNetworkServiceManager);

            fermatEventListener.setEventHandler(fermatEventHandler);
            eventManager.addListener(fermatEventListener);
            listenersAdded.add(fermatEventListener);

            /**
             * Listener Denied connection event
             */
            fermatEventListener = eventManager.getNewListener(EventType.INTRA_USER_CONNECTION_DENIED);
            fermatEventHandler = new IntraWalletUserDeniedConnectionEventHandlers();
            ((IntraWalletUserDeniedConnectionEventHandlers) fermatEventHandler).setActorIntraUserManager(this);
            ((IntraWalletUserDeniedConnectionEventHandlers) fermatEventHandler).setIntraUserManager(this.intraUserNetworkServiceManager);
            fermatEventListener.setEventHandler(fermatEventHandler);

            eventManager.addListener(fermatEventListener);
            listenersAdded.add(fermatEventListener);


            /**
             * I ask the list of pending requests to the Network Service to execute
             */

            this.processNotifications();

            // set plugin status Started
            this.serviceStatus = ServiceStatus.STARTED;


        } catch (CantProcessNotificationsExceptions e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR);

        } catch (CantInitializeIntraWalletUserActorDatabaseException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR);
        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR);
        }

    }

    @Override
    public void pause() {
        this.serviceStatus = ServiceStatus.PAUSED;
    }

    @Override
    public void resume() {
        this.serviceStatus = ServiceStatus.STARTED;
    }

    @Override
    public void stop() {
        this.serviceStatus = ServiceStatus.STOPPED;
    }

    @Override
    public ServiceStatus getStatus() {
        return serviceStatus;
    }

    /**
     * PlugIn Interface implementation.
     */
    @Override
    public void setId(UUID pluginId) {
        this.pluginId = pluginId;
    }


    /**
     * DatabaseManagerForDevelopers Interface implementation.
     */
    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        IntraWalletUserActorDeveloperDatabaseFactory dbFactory = new IntraWalletUserActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return dbFactory.getDatabaseList(developerObjectFactory);


    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        IntraWalletUserActorDeveloperDatabaseFactory dbFactory = new IntraWalletUserActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return dbFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {

        try {
            IntraWalletUserActorDeveloperDatabaseFactory dbFactory = new IntraWalletUserActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
            dbFactory.initializeDatabase();
            return dbFactory.getDatabaseTableContent(developerObjectFactory, developerDatabaseTable);
        } catch (CantInitializeIntraWalletUserActorDatabaseException e) {
            /**
             * The database exists but cannot be open. I can not handle this situation.
             */
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (Exception e) {
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CCP_INTRA_WALLET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
        // If we are here the database could not be opened, so we return an empry list
        return new ArrayList<>();
    }


    @Override
    public List<String> getClassesFullPath() {
        List<String> returnedClasses = new ArrayList<String>();
        returnedClasses.add("IntraWalletUserActorPluginRoot");
        returnedClasses.add("com.bitdubai.fermat_dmp_plugin.layer.actor.intra_user.developer.bitdubai.version_1.structure.ActorIntraWalletUser");

        /**
         * I return the values.
         */
        return returnedClasses;
    }

    @Override
    public void setLoggingLevelPerClass(Map<String, LogLevel> newLoggingLevel) {
        /**
         * Modify by Manuel on 25/07/2015
         * I will wrap all this method within a try, I need to catch any generic java Exception
         */
        try {

            /**
             * I will check the current values and update the LogLevel in those which is different
             */
            for (Map.Entry<String, LogLevel> pluginPair : newLoggingLevel.entrySet()) {
                /**
                 * if this path already exists in the Root.bewLoggingLevel I'll update the value, else, I will put as new
                 */
                if (IntraWalletUserActorPluginRoot.newLoggingLevel.containsKey(pluginPair.getKey())) {
                    IntraWalletUserActorPluginRoot.newLoggingLevel.remove(pluginPair.getKey());
                    IntraWalletUserActorPluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
                } else {
                    IntraWalletUserActorPluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
                }
            }

        } catch (Exception exception) {
            FermatException e = new CantGetLogTool(CantGetLogTool.DEFAULT_MESSAGE, FermatException.wrapException(exception), "setLoggingLevelPerClass: " + IntraWalletUserActorPluginRoot.newLoggingLevel, "Check the cause");
            this.errorManager.reportUnexpectedAddonsException(Addons.EXTRA_USER, UnexpectedAddonsExceptionSeverity.DISABLES_THIS_ADDONS, e);
        }

    }


/**
 * Private methods
 */

    /**
     * Procces the list o f notifications from Intra User Network Services
     * And update intra user actor contact state
     *
     * @throws CantProcessNotificationsExceptions
     */
    private void processNotifications() throws CantProcessNotificationsExceptions {

        try {

            List<IntraUserNotification> intraUserNotificationes = intraUserNetworkServiceManager.getNotifications();


            for (IntraUserNotification notification : intraUserNotificationes) {

                String intraUserSendingPublicKey = notification.getPublicKeyOfTheIntraUserSendingUsANotification();

                String intraUserToConnectPublicKey = notification.getPublicKeyOfTheIntraUserToConnect();

                switch (notification.getNotificationDescriptor()) {
                    case ASKFORACCEPTANCE:

                        this.askIntraWalletUserForAcceptance(intraUserSendingPublicKey, notification.getIntraUserToConnectAlias(), intraUserToConnectPublicKey, notification.getIntraUserToConnectProfileImage());

                    case CANCEL:
                        this.cancelIntraWalletUser(intraUserSendingPublicKey, intraUserToConnectPublicKey);

                    case ACCEPTED:
                        this.acceptIntraWalletUser(intraUserSendingPublicKey, intraUserToConnectPublicKey);
                        /**
                         * fire event "INTRA_USER_CONNECTION_ACCEPTED_NOTIFICATION"
                         */
                        eventManager.raiseEvent(eventManager.getNewEvent(EventType.INTRA_USER_CONNECTION_ACCEPTED_NOTIFICATION));
                        break;
                    case DISCONNECTED:
                        this.disconnectIntraWalletUser("", intraUserSendingPublicKey);
                        break;
                    case RECEIVED:
                        this.receivingIntraWalletUserRequestConnection(intraUserSendingPublicKey, notification.getIntraUserToConnectAlias(), intraUserToConnectPublicKey, notification.getIntraUserToConnectProfileImage());
                        /**
                         * fire event "INTRA_USER_CONNECTION_REQUEST_RECEIVED_NOTIFICATION"
                         */
                        eventManager.raiseEvent(eventManager.getNewEvent(EventType.INTRA_USER_CONNECTION_REQUEST_RECEIVED_NOTIFICATION));
                        break;
                    case DENIED:
                        this.denyConnection(intraUserSendingPublicKey, intraUserToConnectPublicKey);
                        break;
                    default:
                        break;

                }

                /**
                 * I confirm the application in the Network Service
                 */
                intraUserNetworkServiceManager.confirmNotification(intraUserSendingPublicKey, intraUserToConnectPublicKey);
            }


        } catch (CantAcceptIntraWalletUserException e) {
            throw new CantProcessNotificationsExceptions("CAN'T PROCESS NETWORK SERVICE NOTIFICATIONS", e, "", "Error Update Contact State to Accepted");

        } catch (CantDisconnectIntraWalletUserException e) {
            throw new CantProcessNotificationsExceptions("CAN'T PROCESS NETWORK SERVICE NOTIFICATIONS", e, "", "Error Update Contact State to Disconnected");

        } catch (CantDenyConnectionException e) {
            throw new CantProcessNotificationsExceptions("CAN'T PROCESS NETWORK SERVICE NOTIFICATIONS", e, "", "Error Update Contact State to Denied");

        } catch (Exception e) {
            throw new CantProcessNotificationsExceptions("CAN'T PROCESS NETWORK SERVICE NOTIFICATIONS", FermatException.wrapException(e), "", "");

        }
    }

//    @Override
//    public void setDeviceUserManager(DeviceUserManager deviceUserManager) {
//        this.deviceUserManager = deviceUserManager;
//    }
}