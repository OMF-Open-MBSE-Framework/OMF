package com.samares_engineering.omf.omf_example_plugin.features.featureexample

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_public_features.apiserver.OMFApiServer
import com.samares_engineering.omf.omf_public_features.apiserver.server.ExtHyperTextServerRouting

class FeatureExample : SimpleFeature("feature example") {

    //Register mapping between the URL and the method to call
    private fun registerRouting() {
        OMFApiServer.getInstance().addRoute("openProject", ExtHyperTextServerRouting.openProject())
        OMFApiServer.getInstance().addRoute("openTWCProject", ExtHyperTextServerRouting.openTWCProject())
        OMFApiServer.getInstance().addRoute("refmodel", ExtHyperTextServerRouting.refModel())
        OMFApiServer.getInstance().addRoute("openSpecification", ExtHyperTextServerRouting.openSpecification())
    }

    //On feature registration, start the server and register the routing
    override fun onRegistering() {
        OMFApiServer.getInstance(getPlugin()).startServer(8999)
        registerRouting()
    }

    //On feature unregistration, stop the server
    override fun onUnregistering() {
        OMFApiServer.getInstance(getPlugin()).stopServer()
    }

}
