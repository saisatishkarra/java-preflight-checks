package com.dataguise.saas.service;

import com.dataguise.saas.dto.DgAgentClusterDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.structures.ErrorConstants;
import com.dg.saas.orch.models.structures.Modules;
import io.swagger.client.model.DgAgentClusterInfo;
import io.swagger.client.model.DgCloudAgentInfo;
import io.swagger.client.model.DgClusterInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class S3LfaService {
    private static Logger logger = Logger.getLogger(S3LfaService.class);

    @Autowired
    private DgSecureRestDriver restDriver;

    /*
This method will return the list of S3 buckets in tree format
 */
    public String getS3BucketsTree(String sourceId, String dirPath) throws BadGatewayException {
        String bucketsTreeInfo = null;
        try {
            bucketsTreeInfo = restDriver.getS3BucketsTree(sourceId, dirPath);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return bucketsTreeInfo;
    }

    /*
    This method will return the list of buckets with its directories, subdirectories and files
    */
    public String getS3BucketsDirInfo(String sourceId, String dirPath) throws BadGatewayException {
        String bucketsDirInfo = null;
        try {
            bucketsDirInfo = restDriver.getS3BucketsDirInfo(sourceId, dirPath);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return bucketsDirInfo;
    }

    /**
     * Method used for creationg the LFA-S3 cluster
     * @param dgAgentClusterDTO
     * @return
     */
    public Integer saveCluster(DgAgentClusterDTO dgAgentClusterDTO) throws BadGatewayException, BadRequestException{
        Integer clusterId=null;
        try {
            DgClusterInfo dgClusterInfo = createClusterInfo(dgAgentClusterDTO,0);
            clusterId = restDriver.saveClusterInfo(dgClusterInfo);

        } catch (DgDrvException e) {
            logger.error("create cluster failed " + e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return clusterId;
    }
    /**
     * Method is used for creating cluster info bean i.e. used in the create cluster call
     * @param dgAgentClusterDTO
     * @return
     */

    private DgClusterInfo createClusterInfo(DgAgentClusterDTO dgAgentClusterDTO, Integer id) {
        DgAgentClusterInfo dgAgentClusterInfo = new DgAgentClusterInfo();
        DgCloudAgentInfo dgCloudAgentInfo = new DgCloudAgentInfo();
        dgAgentClusterInfo.setIsPrimary(true);
        dgAgentClusterInfo.setIsSecondary(false);
        DgClusterInfo dgClusterInfo = new DgClusterInfo();
        dgClusterInfo.setId(id);
        dgClusterInfo.setClusterType(Modules.S3LFA.getModuleType());
        dgClusterInfo.setLocation("Cloud");
        dgClusterInfo.setClusterName(dgAgentClusterDTO.getClusterName());


        List<DgAgentClusterInfo> dgAgentClusterInfoList = new ArrayList<>();
        List<DgCloudAgentInfo> dgCloudAgentInfoList = new ArrayList<>();

        // set details for LFA Agent
        dgAgentClusterInfo.setAgentId(dgAgentClusterDTO.getClusterAgentId());
        dgAgentClusterInfoList.add(dgAgentClusterInfo);

        // set details for S3 Agent
        dgCloudAgentInfo.setAgentId(dgAgentClusterDTO.getCloudAgentId());
        dgCloudAgentInfoList.add(dgCloudAgentInfo);

        dgClusterInfo.setAgentInfo(dgAgentClusterInfoList);
        dgClusterInfo.setCloudAgentInfo(dgCloudAgentInfoList);
        return dgClusterInfo;
    }

    /**
     * Method is used for editing the cluster Info using ID
     * @param dgAgentClusterDTO
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    public Integer editClusterInfo(DgAgentClusterDTO dgAgentClusterDTO, Integer id) throws BadGatewayException, BadRequestException{
        Integer clusterId=null;
        try {
            DgClusterInfo dgClusterInfo = createClusterInfo(dgAgentClusterDTO, id);
            clusterId = restDriver.editClusterInfo(dgClusterInfo,id);

        } catch (DgDrvException e) {
            logger.error("create cluster failed " + e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return clusterId;
    }

    /**
     * Method is used for test the cluster
     * @param clusterId
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public Boolean testCluster(Integer clusterId) throws BadRequestException, BadGatewayException {
        if (clusterId == null) {
            throw new BadRequestException("Cluster" + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }
        boolean testConnectionStatus = false;

            try {
                List<DgClusterInfo> clusterInfoList;
                DgClusterInfo clusterInfo;
                clusterInfoList = restDriver.loadClustersInfo(clusterId);
                clusterInfo=clusterInfoList.get(0);
                Integer lfaAgentId=clusterInfo.getAgentInfo().get(0).getAgentId();
                Integer s3CloudAgentId=clusterInfo.getCloudAgentInfo().get(0).getAgentId();

                if(restDriver.testAgentStatus(lfaAgentId)&&restDriver.testAgentStatus(s3CloudAgentId)){

                    testConnectionStatus=true;
                }
                else{
                    testConnectionStatus=false;
                }

            } catch (DgDrvException e) {
                throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
            }
            return testConnectionStatus;

        }

    /**
     * This method is used for deleting the cluster
     * @param clusterId
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
        public Integer deleteCluster(Integer clusterId) throws BadRequestException, BadGatewayException {
        if(clusterId ==null){
            throw new BadRequestException("Cluster " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }
            try {
                restDriver.deleteClusterInfo(clusterId);
            } catch (DgDrvException e) {
                logger.error("Delete connection failed: " + e);
                throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
            }
            return clusterId;
        }

    /**
     * This method is used to get the details of the cluster by id
      * @param id
     * @return
     * @throws BadGatewayException
     */
    public DgAgentClusterDTO getClusterDetailsById(Integer id) throws BadGatewayException {
        List<DgClusterInfo> clusterInfoList;
        DgAgentClusterDTO dgAgentClusterDTO = new DgAgentClusterDTO();
        try {

            clusterInfoList = restDriver.loadClustersInfo(id);
            DgClusterInfo clusterInfo=clusterInfoList.get(0);
            if(clusterInfo!=null) {
                dgAgentClusterDTO.setCloudAgentId(clusterInfo.getCloudAgentInfo().get(0).getAgentId());
                dgAgentClusterDTO.setClusterAgentId(clusterInfo.getAgentInfo().get(0).getAgentId());
                dgAgentClusterDTO.setClusterId(clusterInfo.getId());
                dgAgentClusterDTO.setClusterName(clusterInfo.getClusterName());
            }

        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return dgAgentClusterDTO;
    }
    }

