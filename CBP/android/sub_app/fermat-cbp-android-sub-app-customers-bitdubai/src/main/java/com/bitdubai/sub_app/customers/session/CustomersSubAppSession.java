package com.bitdubai.sub_app.customers.session;

import com.bitdubai.fermat_android_api.layer.definition.wallet.abstracts.AbstractFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.SubAppsSession;
import com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.enums.SubApps;
import com.bitdubai.fermat_api.layer.dmp_module.sub_app_manager.InstalledSubApp;
import com.bitdubai.fermat_pip_api.layer.network_service.subapp_resources.SubAppResourcesProviderManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matias Furszyfer on 2015.07.20..
 */

//TODO: Nelson fijate que el module manager está mal..
public class CustomersSubAppSession extends AbstractFermatSession<InstalledSubApp,CustomerModuleManager,SubAppResourcesProviderManager> {

    /**
     * SubApps type
     */
    SubApps subApps;

    /**
     * Active objects in wallet session
     */
    Map<String, Object> data;

    /**
     * Error manager
     */
    private ErrorManager errorManager;

    /**
     * Customers Module Manager [FALTA MODULE]
     */
    private Object moduleManager;


    /**
     * Create a session for the Wallet Store SubApp
     *
     * @param errorManager             the error manager
     * @param moduleManager the module of this SubApp
     */
    public CustomersSubAppSession(InstalledSubApp subApp, ErrorManager errorManager, CustomerModuleManager moduleManager) {
        super(subApp.getAppPublicKey(), subApp, errorManager, moduleManager, null);
        this.subApps = subApps;
        data = new HashMap<String, Object>();
        this.errorManager = errorManager;
        this.moduleManager = moduleManager;
    }



    /**
     * Store any data you need to hold between the fragments of the sub app
     *
     * @param key    key to reference the object
     * @param object the object yo want to store
     */
    @Override
    public void setData(String key, Object object) {
        data.put(key, object);
    }

    /**
     * Return the data referenced by the key
     *
     * @param key the key to access de data
     * @return the data you want
     */
    @Override
    public Object getData(String key) {
        return data.get(key);
    }

    /**
     * Return the Error Manager
     *
     * @return reference to the Error Manager
     */
    @Override
    public ErrorManager getErrorManager() {
        return errorManager;
    }

    /**
     * Return the Crypto Customer LinkedActorIdentity Module Manager
     *
     * @return reference to the Crypto Customer LinkedActorIdentity Module Manager
     */
    public CustomerModuleManager getModuleManager() {
        return getModuleManager();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomersSubAppSession that = (CustomersSubAppSession) o;

        return subApps == that.subApps;

    }

    @Override
    public int hashCode() {
        return subApps.hashCode();
    }
}
