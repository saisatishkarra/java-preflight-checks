package com.dataguise.saas.source;

import com.dataguise.saas.dto.DgAgentClusterDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.service.S3LfaService;
import com.dg.saas.orch.models.structures.Modules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("S3LFA")
public class FileSource implements Source{

    /** The ____________ service. */
    @Autowired
    private S3LfaService s3LfaService;

    @Override
    public Integer add(SourceDTO sourceDTO) throws BadGatewayException, BadRequestException {
        DgAgentClusterDTO dgAgentClusterDTO = sourceDTO.getCluster();
        Integer id = s3LfaService.saveCluster(dgAgentClusterDTO);
        return id;
    }

    @Override
    public Integer edit(Integer id, SourceDTO sourceDTO) throws BadGatewayException, BadRequestException{
        DgAgentClusterDTO dgAgentClusterDTO = sourceDTO.getCluster();
        Integer clusterId = s3LfaService.editClusterInfo(dgAgentClusterDTO, id);
        return clusterId;

    }

    @Override
    public boolean test(Integer id) throws BadGatewayException, BadRequestException {
        Boolean testConnectionStatus = s3LfaService.testCluster(id);
        return testConnectionStatus;

    }

    @Override
    public Integer delete(Integer id) throws BadGatewayException, BadRequestException {
        Integer clusterId= s3LfaService.deleteCluster(id);
        return clusterId;
    }

    @Override
    public SourceDTO get(Integer id) throws BadGatewayException, BadRequestException, InternalServerException {
        SourceDTO sourceDTO=new SourceDTO();
        DgAgentClusterDTO dgAgentClusterDTO= s3LfaService.getClusterDetailsById(id);
        sourceDTO.setSourceModule(Modules.S3LFA.getModuleType());
        sourceDTO.setCluster(dgAgentClusterDTO);
        sourceDTO.setSourceModule(Modules.S3LFA.name());
        return  sourceDTO;
    }

}
