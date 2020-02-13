package com.dataguise.saas.source;

import com.dataguise.saas.dto.ConnectionDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.service.ConnectionService;
import io.swagger.client.model.DatabaseMetadata;
import io.swagger.client.model.DgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Component("RDS")
public class ConnectionSource implements Source {


    /** The connection service. */
    @Autowired
    private ConnectionService connectionService;

    @Override
    public Integer add(SourceDTO sourceDTO) throws BadGatewayException, BadRequestException, InternalServerException {

        ConnectionDTO connectionDTO=new ConnectionDTO();
        connectionDTO.setConnection(sourceDTO.getConnection());
        connectionDTO.setAgentHostName(sourceDTO.getAgentHostName());
        connectionDTO.setAgentName(sourceDTO.getAgentName());
        connectionDTO.setAutoSchedule(sourceDTO.getAutoSchedule());
        Integer id=connectionService.addConnection(connectionDTO);
        return id;
    }

    @Override
    public Integer edit(Integer id, SourceDTO sourceDTO) throws BadGatewayException, BadRequestException, InternalServerException {
        ConnectionDTO connectionDTO = new ConnectionDTO();
        connectionDTO.setConnection(sourceDTO.getConnection());
        connectionDTO.setAutoSchedule(sourceDTO.getAutoSchedule());
        Integer connId= connectionService.editConnection(id, connectionDTO);
        return connId;
    }

    @Override
    public boolean test(Integer id) throws BadRequestException, BadGatewayException {
        Boolean connectionSuccessful = connectionService.testExistingConnection(id);
        return connectionSuccessful;
    }

    @Override
    public Integer delete(Integer id) throws BadRequestException, BadGatewayException{
        Integer connectionId = connectionService.deleteConnection(id);
        return connectionId;
    }

    @Override
    public SourceDTO get(Integer id) throws BadGatewayException, BadRequestException, InternalServerException {
       SourceDTO sourceDTO=new SourceDTO();
       List<ConnectionDTO> connectionDTOList=connectionService.getConnections(Optional.of(id));
       ConnectionDTO connectionDTO=connectionDTOList.get(0);
       sourceDTO.setAgentHostName(connectionDTO.getAgentHostName());
       sourceDTO.setAgentName(connectionDTO.getAgentName());
       sourceDTO.setConnection(connectionDTO.getConnection());
       sourceDTO.setSourceType(connectionDTO.getConnection().getConnectionType());
       sourceDTO.setSourceModule(connectionDTO.getConnection().getModule());
       return sourceDTO;

    }

}
