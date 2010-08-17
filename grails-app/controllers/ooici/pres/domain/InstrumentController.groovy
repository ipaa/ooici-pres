package ooici.pres.domain

class InstrumentController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	String SYSNAME = System.getProperty("ioncore.sysname","mysys");

	// Spring injected 
	def lcademoService

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {

	    def allInstruments = lcademoService.listAllInstruments()

	    def instruments = []

	    for (i in allInstruments) {

		    def instrument = new Instrument()

		    instrument.registryId = (String)i.getIdentity()
		    instrument.name = (String)i.getAttribute("name")
		    instrument.model = (String)i.getAttribute("model")
		    instrument.manufacturer = (String)i.getAttribute("manufacturer")
		    instrument.serialNum = (String)i.getAttribute("serial_num")
		    instrument.manufacturer = (String)i.getAttribute("manufacturer")
		    instrument.fwVersion = (String)i.getAttribute("fw_version")

		    instruments << instrument

	    }

        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [instruments:instruments, instrumentInstanceTotal: Instrument.count()]
    }

    def create = {
        def instrumentInstance = new Instrument()
        instrumentInstance.properties = params
        return [instrumentInstance: instrumentInstance]
    }

	/**
	 * Forwards to instrument/command.gsp
	 */
	def runcommand = {

		render(view:"command")

	}

	/**
	 * Handles command form submissions
	 */
	def command = {

		def status = lcademoService.commandInstrument(params.instrumentId, params.command, params.arg0, params.arg1)

		render(view:"commandstatus", model:[status:status.getContent(), headers:status.getIonHeaders()])
	}

    def save = {

	    lcademoService.createInstrument(params.name, params.model, params.manufacturer, params.serialNum, params.fwVersion)

        def instrumentInstance = new Instrument(params)
        if (instrumentInstance.save(flush: true)) {
            redirect(action: "show")
        }
        else {
            render(view: "create", model: [instrumentInstance: instrumentInstance])
        }
    }

    def show = {
        def instrumentInstance = Instrument.get(params.id)
        if (!instrumentInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instrument.label', default: 'Instrument'), params.id])}"
            redirect(action: "list")
        }
        else {
            [instrumentInstance: instrumentInstance]
        }
    }

    def edit = {
        def instrumentInstance = Instrument.get(params.id)
        if (!instrumentInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instrument.label', default: 'Instrument'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [instrumentInstance: instrumentInstance]
        }
    }

    def update = {
        def instrumentInstance = Instrument.get(params.id)
        if (instrumentInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (instrumentInstance.version > version) {
                    
                    instrumentInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'instrument.label', default: 'Instrument')] as Object[], "Another user has updated this Instrument while you were editing")
                    render(view: "edit", model: [instrumentInstance: instrumentInstance])
                    return
                }
            }
            instrumentInstance.properties = params
            if (!instrumentInstance.hasErrors() && instrumentInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'instrument.label', default: 'Instrument'), instrumentInstance.id])}"
                redirect(action: "show", id: instrumentInstance.id)
            }
            else {
                render(view: "edit", model: [instrumentInstance: instrumentInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instrument.label', default: 'Instrument'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def instrumentInstance = Instrument.get(params.id)
        if (instrumentInstance) {
            try {
                instrumentInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'instrument.label', default: 'Instrument'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'instrument.label', default: 'Instrument'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'instrument.label', default: 'Instrument'), params.id])}"
            redirect(action: "list")
        }
    }
}
