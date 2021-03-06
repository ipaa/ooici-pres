package ooici.pres

import ion.core.messaging.MessagingName
import ion.resource.ListAllQueryDO
import ion.resource.InstrumentRDO
import ion.core.messaging.IonMessage
import ion.core.data.DataObject
import ion.resource.ResourceDO
import ion.resource.DataProductRDO

class LcademoService {

    static transactional = false

	def BootstrapIONService

	def SYSNAME = System.getProperty("ioncore.sysname","Bill");

    def List listAllInstruments() {

	    MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_registry");

		// Create and send message
        ListAllQueryDO listall = new ListAllQueryDO(new InstrumentRDO());
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "find_instrument_instance", listall);

	    def instruments = []

        if (msgin.hasDataObject()) {

        	DataObject dobj = msgin.extractDataObject();
        	List resList = (List) dobj.getAttribute("resources");

	        if(resList != null) {
				for (Iterator it = resList.iterator(); it.hasNext();) {
					ResourceDO resobj = (ResourceDO) it.next();
					instruments << resobj
				}
            }
	        else {
		        println '\n***** RESOURCE LIST IS NULL'
	        }
        }
	    
        BootstrapIONService.baseProcess.ackMessage(msgin);

	    return instruments

    }

	def List listAllDataProducts() {

	    MessagingName dataprodRegSvc = new MessagingName(SYSNAME, "data_product_registry");

		// Create and send message
        ListAllQueryDO listall = new ListAllQueryDO(new DataProductRDO());
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(dataprodRegSvc, "find_data_product", listall);

	    def dataProducts = []

        if (msgin.hasDataObject()) {

        	DataObject dobj = msgin.extractDataObject();
        	List resList = (List) dobj.getAttribute("resources");

	        if(resList != null) {
				for (Iterator it = resList.iterator(); it.hasNext();) {
					ResourceDO resobj = (ResourceDO) it.next();
					dataProducts << resobj
				}
            }
	        else {
		        println '\n***** LcademoService:listAllDataProducts -- RESOURCE LIST IS NULL'
	        }
        }

        BootstrapIONService.baseProcess.ackMessage(msgin);

	    return dataProducts

    }

	def IonMessage startAgent(String instrumentId, String model) {

		MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_management");

		def contentMap= ['instrumentID':instrumentId, 'model':model]

		// Create and send message
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "start_instrument_agent", contentMap);

        BootstrapIONService.baseProcess.ackMessage(msgin);

		return msgin
	}

	def IonMessage getParameter(String instrumentID, String ParameterName) {

		MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_management");

		def commandMap = ['instrumentID':instrumentID,'parameterName':ParameterName]
		def contentMap = ['commandInput':commandMap]

		// Create and send message
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "get_instrument_parameter", contentMap);

        BootstrapIONService.baseProcess.ackMessage(msgin);

		return msgin
	}

	def IonMessage setParameter(String instrumentID, String ParameterName, String ParameterValue) {

		MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_management");

		def commandMap = ['instrumentID':instrumentID, 'parameterName':ParameterName, 'parameterValue':ParameterValue]
		def contentMap = ['commandInput':commandMap]

		// Create and send message
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "set_instrument_parameter", contentMap);

        BootstrapIONService.baseProcess.ackMessage(msgin);

		return msgin
	}

	def IonMessage getInstrumentStatus(String instrumentID) {

		MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_management");

		def commandMap = ['instrumentID':instrumentID]
		def contentMap = ['commandInput':commandMap]

		// Create and send message
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "get_instrument_state", contentMap);

        BootstrapIONService.baseProcess.ackMessage(msgin);

		return msgin
	}

	def createDataProduct(String name, String instrumentId, String dataFormat) {

		MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_management");

		def newDataformatMap = ['name':name, 'instrumentID':instrumentId, 'dataformat':dataFormat]
		def contentMap = ['dataProductInput':newDataformatMap]

		// Create and send message
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "create_new_data_product", contentMap);
        BootstrapIONService.baseProcess.ackMessage(msgin);

	}

	def createInstrument(String name, String model, String manufacturer, String serialNum, String fwVersion) {

		MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_management");

		def newInstrumentMap= ['name':name, 'model':model, 'manufacturer':manufacturer, 'serial_num':serialNum, 'fw_version':fwVersion]
		def contentMap = ['userInput':newInstrumentMap]

		// Create and send message
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "create_new_instrument", contentMap);
        BootstrapIONService.baseProcess.ackMessage(msgin);

	}

	def IonMessage commandInstrument(String instrumentID, String command, String arg0, String arg1) {

		MessagingName instRegSvc = new MessagingName(SYSNAME, "instrument_management");

		def CommandMap= ['instrumentID':instrumentID, 'command':command, 'cmdArg0':arg0, 'cmdArg1':arg1]
		def contentMap = ['commandInput':CommandMap]

		// Create and send message
        IonMessage msgin = BootstrapIONService.baseProcess.rpcSend(instRegSvc, "execute_command", contentMap);

        BootstrapIONService.baseProcess.ackMessage(msgin);

		return msgin
	}
}
