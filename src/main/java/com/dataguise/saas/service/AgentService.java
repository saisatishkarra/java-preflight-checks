package com.dataguise.saas.service;


import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.util.Utility;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.builders.AgentBuilder;
import com.dg.saas.orch.models.structures.ErrorConstants;
import com.dg.saas.orch.models.structures.IpRange;
import io.swagger.client.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * The agent-management service
 */
@Service
public class AgentService {

    private static Logger logger = Logger.getLogger(AgentService.class);

    @Autowired
    private DgSecureRestDriver restDriver;


    public Boolean addAgentStruct(List<DgAgentInfoStruct> dgAgentInfoStructList) throws BadRequestException,
            BadGatewayException{
        Boolean agentAdded = false;
        int count =0;
        for(DgAgentInfoStruct agentInfo : dgAgentInfoStructList){
            addAgent(agentInfo);
            count++;
        }
        if(count == dgAgentInfoStructList.size())
            agentAdded = true;

        return agentAdded;
    }



    /**
     * Add a agent
     */
    public Integer addAgent(DgAgentInfoStruct agentInfoStruct)
            throws BadRequestException,BadGatewayException {
        /** Function to add multiple agents, ip ranges and group id mappings for detection agents. **/
        Integer groupId = null;
        Integer agentId = null;
        Integer ipRangeId = null;

        try {
            validateAgent(agentInfoStruct);
            logger.info("Updated Agent info struct: " + agentInfoStruct);
        } catch (BadRequestException e) {
            throw e;
        }


        try {
            if(agentInfoStruct.getAgentType() == AgentBuilder.AgentType.S3Cloud.name() ||
                    agentInfoStruct.getAgentType() == AgentBuilder.AgentType.UnixFiles.name() )
            {
                agentId = restDriver.configureAgent(agentInfoStruct);
            }
            else
            {
                agentId = restDriver.configureAgentWithIpranges(agentInfoStruct);
            }
        } catch(DgDrvException e) {
            logger.error("Error adding agent");
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

        //edit case, this is a temporary fix.
        //We need missing edit agent call at controller end for this.
        //Anyways we have to skip create ipRange and assiging it to agent in case of editing.
        if(agentInfoStruct.getAgentId()!=null){
            return agentId;
        }

        return agentId;
    }


    /**
     * Add a IP Range
     *
     */
    private Integer addIPRange(String ipRangeLabel) throws BadGatewayException {
        Integer ipRangeId = null;
        try {
            Random rand = new Random();
            int  n = rand.nextInt(255) + 1;
            StringBuilder fromAddress = new StringBuilder();
            StringBuilder toAddress = new StringBuilder();
            fromAddress = fromAddress.
                    append(n).append(".").
                    append(n).append(".").
                    append(n).append(".")
                    .append(1);
            toAddress = toAddress.
                    append(n).append(".").
                    append(n).append(".").
                    append(n).append(".")
                    .append(255);
            /** Setting ip range for discover agent **/
            IpRange ipRange = new IpRange(ipRangeLabel, ipRangeLabel,
                    fromAddress.toString(), toAddress.toString());
            ipRangeId = restDriver.createIpRange(ipRange);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return ipRangeId;
    }

    /**
     * Assign IP range to agent
     *
     */
    private Integer mapAgentToIpRange(Integer ipRangeId, Integer primaryAgentId, Integer secondaryAgentId)
            throws BadGatewayException {
        Integer groupId = null;
        try {

            /** Setting group Id for discover agent **/
            groupId = restDriver.assignIpRangetoAgents(ipRangeId, primaryAgentId, secondaryAgentId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return groupId;
    }


    /**
     * Get Agent details
     */
    public List<DgInfraStructureNodeWrapper> getGroupAgentDetails(Optional<Integer> id) throws  BadGatewayException {
        List<DgInfraStructureNodeWrapper> agentGroupDetails;
        List<DgInfraStructureNodeWrapper> filterAgentSourcesList;
        try {
            agentGroupDetails = restDriver.getAgentsInfo();
            List<DgInfraStructureNodeWrapper> agentDetails=restDriver.getAgentDetails();
            agentGroupDetails.addAll(agentDetails);
            /** Filter unique return of the agent details with the help of agentType or agentName**/
            filterAgentSourcesList = agentGroupDetails
                    .stream()
                    .filter(Utility.distinctByKeys(DgInfraStructureNodeWrapper::getAgentName))
                    .filter(agent -> agent.getAgentType().equals(AgentBuilder.AgentType.DBMSDetection.getAgentType())||
                            agent.getAgentType().equals(AgentBuilder.AgentType.DBMSMasker.getAgentType())||
                            agent.getAgentType().equals(AgentBuilder.AgentType.UnixFiles.getAgentType()) ||
                            agent.getAgentType().equals(AgentBuilder.AgentType.S3Cloud.getAgentType()))
                    .collect(Collectors.toList());

        } catch (DgDrvException e) {
            logger.error("Unable to fetch agent details");
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

        if(id.isPresent()) {
            filterAgentSourcesList = filterAgentSourcesList.stream().filter(agent -> agent.getAgentId().equals(id.get()))
                    .collect(Collectors.toList());
        }

        filterAgentSourcesList.sort((obj1, obj2) -> obj2.getAgentId().compareTo(obj1.getAgentId()));
        return filterAgentSourcesList;
    }

    /**
     * Get Agent Status
     */
    public Boolean testAgentStatus(Integer id) throws BadRequestException, BadGatewayException {

        Boolean status = false;

        if(id == null) {
            throw new BadRequestException(ErrorConstants.ERROR_TESTING_AGENT_BY_ID.getErrorMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            status = restDriver.testAgentStatus(id);
        } catch (DgDrvException e) {
            logger.error(e.getMessage());
            return status;
        }
        return status;
    }

    /**
     * Delete Agent with its IPRange
     */
    public Boolean deleteAgent(Integer agentId) throws BadGatewayException {
        Boolean status = false;
        String response = null;
        try {
            response = restDriver.deleteAgent(agentId);
        } catch (DgDrvException e) {
            logger.error("Error deleting agent");
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        if(response!=null && !response.isEmpty()) {
            status = true;
        }
        return status;
    }


    /**
     * Delete IP range
     */
    private Boolean deleteIpRange(Integer ipRangeId) throws BadGatewayException {
        Boolean status;
        try {
            status = restDriver.deleteIpRange(ipRangeId);
        } catch (DgDrvException e) {
            logger.error("Error deleting agent");
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return status;
    }


    /**
     * Filter agentList by groupId
     */
    public List<DgInfraStructureNodeWrapper> filterAgentListByGroupId(Integer groupId) throws  BadGatewayException {
        List<DgInfraStructureNodeWrapper> agentDetails = getGroupAgentDetails(Optional.empty());
        if( groupId!=null && groupId != 0) {
            agentDetails = agentDetails.stream().filter(agent -> agent.getGroupId()!=null &&
                    agent.getGroupId() == groupId)
                    .collect(Collectors.toList());
        }
        return agentDetails;
    }


    private List<DgIPRangeUIAgentName> getAgentMappingDetails(Optional<String> agentName) throws BadGatewayException {
        List<DgIPRangeUIAgentName> mappingList = null;
        try {
            mappingList = restDriver.getIpRangeToAgentMapping();
        } catch (DgDrvException e) {
            logger.error("Unable to get agent mapping details.");
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        if(agentName.isPresent()) {
            mappingList = mappingList.stream().
                    filter(mapping -> mapping.getPrimaryAgentName().equals(agentName.get()))
                    .collect(Collectors.toList());
        }

        return mappingList;
    }

    private void validateAgent(DgAgentInfoStruct agentInfoStruct) throws BadRequestException {

        if(agentInfoStruct.getHostName() == null || agentInfoStruct.getPortNumber() == null) {
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_HOSTNAME_PORT_NUMBER.getErrorMessage()
                    ,HttpStatus.BAD_REQUEST);
        }

        if(agentInfoStruct.getAgentType() == null || (!agentInfoStruct.getAgentType()
                .equalsIgnoreCase(AgentBuilder.AgentType.DBMSDetection.getAgentType()) && !agentInfoStruct.getAgentType()
                .equalsIgnoreCase(AgentBuilder.AgentType.DBMSMasker.getAgentType()) && !agentInfoStruct.getAgentType()
                .equalsIgnoreCase(AgentBuilder.AgentType.UnixFiles.getAgentType()) && !agentInfoStruct.getAgentType()
                .equalsIgnoreCase(AgentBuilder.AgentType.S3Cloud.getAgentType()) )){
            throw new BadRequestException(ErrorConstants.ERROR_INVALID_AGENT_TYPE.getErrorMessage(),HttpStatus.BAD_REQUEST);
        }
        /** Setting default agent name as detection agent. **/

        /** Setting ssl type to be none (0). **/
        if(agentInfoStruct.getSslType()==null)
            agentInfoStruct.setSslType(0);
        /** Setting default agent type to be detection agent. **/
        if(agentInfoStruct.getAgentType().equalsIgnoreCase(AgentBuilder.AgentType.DBMSDetection.getAgentType())) {
            agentInfoStruct.agentType(AgentBuilder.AgentType.DBMSDetection.name());
        }

        if(agentInfoStruct.getAgentType().equalsIgnoreCase(AgentBuilder.AgentType.DBMSMasker.getAgentType())) {
            agentInfoStruct.agentType(AgentBuilder.AgentType.DBMSMasker.name());
        }

        if(agentInfoStruct.getAgentType().equalsIgnoreCase(AgentBuilder.AgentType.S3Cloud.getAgentType())) {
            agentInfoStruct.agentType(AgentBuilder.AgentType.S3Cloud.name());
        }
        if(agentInfoStruct.getAgentType().equalsIgnoreCase(AgentBuilder.AgentType.UnixFiles.getAgentType())) {
            agentInfoStruct.agentType(AgentBuilder.AgentType.UnixFiles.name());
        }
        /** TODO: Make a ping to the specified agent host, port and use protocol based on ssl type. **/

    }

}
