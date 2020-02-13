package com.dataguise.saas.service;

import com.dataguise.saas.dto.SourcesDetails;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.source.Source;
import com.dataguise.saas.source.SourceDTO;
import com.dataguise.saas.source.SourceFactory;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.structures.Modules;
import io.swagger.client.model.DgClusterInfo;
import io.swagger.client.model.DgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SourceService {

    @Autowired
    private DgSecureRestDriver restDriver;

    @Autowired
    private SourceFactory sourceFactory;

    private Source source;


    /**
     * This method is used to add a new source
     * @param sourceDTO
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     * @throws InternalServerException
     */
    public Integer saveSource(SourceDTO sourceDTO) throws BadGatewayException,
            BadRequestException, InternalServerException {

        String sourceModule=(sourceDTO.getSourceModule().equals(Modules.DBMS.name()))? Modules.RDS.getModuleType():
                sourceDTO.getSourceModule();
        source= sourceFactory.getSource(sourceModule);
        Integer id=source.add(sourceDTO);
        return id;
    }

    /**
     * This method is used to edit a source by id
     * @param sourceDTO
     * @param id
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     * @throws InternalServerException
     */
    public Integer editSource(SourceDTO sourceDTO, Integer id) throws BadGatewayException,
            BadRequestException, InternalServerException {

        String sourceModule=(sourceDTO.getSourceModule().equals(Modules.DBMS.name()))? Modules.RDS.getModuleType():
                sourceDTO.getSourceModule();
        source= sourceFactory.getSource(sourceModule);
        id=source.edit(id,sourceDTO);
        return id;
    }

    /**
     * This method is used to get all the sources
     * @return
     * @throws BadGatewayException
     */
    public List<SourcesDetails> getAllSources() throws BadGatewayException {
        List<DgConnection> connectionsList;
        List<DgClusterInfo> clusterInfoList;
        List<SourcesDetails> sourceDetailsList = new ArrayList<>();
        try {
            connectionsList = restDriver.getAllConnections();
            clusterInfoList = restDriver.loadClustersInfo(0);
            if(connectionsList!=null && connectionsList.size() > 0){
                for(DgConnection connection: connectionsList){
                    SourcesDetails sourceDetails = new SourcesDetails();
                    sourceDetails.setSourceId(connection.getConnectionId());
                    sourceDetails.setSourceAgent(connection.getConnectionAgent());
                    sourceDetails.setSourceName(connection.getConnectionName());
                    sourceDetails.setSourceType(connection.getConnectionType());
                    sourceDetails.setSourceModule(connection.getModule());
                    sourceDetails.setSourceHostName(connection.getHostNameOrIPAddress());
                    sourceDetails.setSourcePortNumber(connection.getPortNumber());
                    sourceDetails.setSourceDatabases(connection.getDatabase());
                    sourceDetails.setSourceSchemas(connection.getSchemaFilter());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date parsedDate = dateFormat.parse(connection.getCreatets());
                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                    sourceDetails.setCreatedAt(timestamp);
                    sourceDetailsList.add(sourceDetails);
                }
            }
            if(clusterInfoList!=null && clusterInfoList.size() > 0) {
                for( int j = 0; j< clusterInfoList.size(); j++){
                    SourcesDetails sourceDetails = new SourcesDetails();
                    sourceDetails.setSourceId(clusterInfoList.get(j).getId());
                    sourceDetails.setSourceAgent(clusterInfoList.get(j).getClusterType());
                    sourceDetails.setSourceName(clusterInfoList.get(j).getClusterName());
                    sourceDetails.setSourceType(Modules.S3LFA.name());
                    sourceDetails.setSourceModule(Modules.S3LFA.name());
                    sourceDetails.setCreatedAt(clusterInfoList.get(j).getCreatets());
                    sourceDetailsList.add(sourceDetails);

                }
            }

        } catch (DgDrvException | ParseException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        sourceDetailsList  = sourceDetailsList.stream() .sorted(Comparator.comparing(SourcesDetails::getCreatedAt)
                .reversed()) .collect(Collectors.toList());
        return sourceDetailsList;
    }

    /**
     *  Method is used for test the existing source
     * @param id
     * @param sourceModule
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    public Boolean testExistingSource(Integer id, String sourceModule) throws BadGatewayException,
            BadRequestException, InternalServerException {

        sourceModule = sourceModule.equals(Modules.DBMS.name()) ? Modules.RDS.getModuleType() : sourceModule;
        source = sourceFactory.getSource(sourceModule);
        Boolean testConnection = source.test(id);
        return testConnection;
    }

    /**
     * Method is used for delete source
     * @param sourceId
     * @param sourceModule
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     * @throws InternalServerException
     */
    public Integer deleteSource(Integer sourceId, String sourceModule) throws BadGatewayException,
            BadRequestException, InternalServerException {

        sourceModule = sourceModule.equals(Modules.DBMS.name()) ? Modules.RDS.getModuleType() : sourceModule;
        source= sourceFactory.getSource(sourceModule);
        source.delete(sourceId);
        return  sourceId;
    }

    /**
     * This method is used to get the details of the source by id
     * @param sourceModule
     * @param id
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     * @throws InternalServerException
     */
    public SourceDTO getSourceDetails(String sourceModule,Integer id) throws BadGatewayException,
            BadRequestException, InternalServerException{

        sourceModule = sourceModule.equals(Modules.DBMS.name()) ? Modules.RDS.getModuleType() : sourceModule;
        source= sourceFactory.getSource(sourceModule);
        SourceDTO sourceDTO = source.get(id);
        return sourceDTO;
    }

}