package com.dataguise.saas.controllers;

import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.service.AgentService;
import io.swagger.client.model.DgAgentInfoStruct;
import io.swagger.client.model.DgInfraStructureNodeWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/agent")
public class AgentController {
    private static Logger logger = Logger.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    /**
     * Add a agent.
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Boolean configureAgent(@RequestBody List<DgAgentInfoStruct> agentInfoStructList)
            throws BadGatewayException, BadRequestException {
        return agentService.addAgentStruct(agentInfoStructList);
    }



    @RequestMapping(value = {"/list", "/list/{id}"})
    public List<DgInfraStructureNodeWrapper> getAgentDetails(@PathVariable Optional<Integer> id)
            throws BadGatewayException {
        List<DgInfraStructureNodeWrapper> agentDetails = null;
        agentDetails = agentService.getGroupAgentDetails(id);
        return agentDetails;
    }


    /**
     * Test agent.
     */
    @RequestMapping(value = "/test/{id}", method = RequestMethod.GET)
    public Boolean testAgentStatus(@PathVariable Integer id)
            throws BadGatewayException, BadRequestException {
        return agentService.testAgentStatus(id);
    }
    
    /**
     * Delete agent.
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Boolean deleteAgent(@PathVariable Integer id)
            throws BadGatewayException, BadRequestException {
        return agentService.deleteAgent(id);
    }

}
